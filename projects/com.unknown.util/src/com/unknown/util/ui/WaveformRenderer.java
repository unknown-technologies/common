package com.unknown.util.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class WaveformRenderer {
	private Color sampleColor;
	private Color signalColor;
	private int signalColorRGB;

	private BufferedImage buffer;
	private boolean dirty;

	private WaveformEditor parent;

	private double sampleOffset;

	public WaveformRenderer(WaveformEditor parent) {
		this.parent = parent;
		sampleOffset = 0;
		buffer = null;
		dirty = true;
	}

	public void setSampleOffset(double offset) {
		sampleOffset = offset;
		dirty = true;
	}

	public double getSampleOffset() {
		return sampleOffset;
	}

	public void setSignalColor(Color color) {
		signalColor = color;
		signalColorRGB = color.getRGB() & 0x00FFFFFF;
		dirty = true;
	}

	public Color getSignalColor() {
		return signalColor;
	}

	public void setSampleColor(Color color) {
		sampleColor = color;
		dirty = true;
	}

	public Color getSampleColor() {
		return sampleColor;
	}

	public Color getSignalColor(double intensity) {
		int i = (int) (intensity * parent.getBeamIntensity() * 255.0);
		if(i > 255) {
			i = 255;
		}
		return new Color(signalColor.getRed(), signalColor.getGreen(), signalColor.getBlue(), i);
	}

	public int getSignalColorRGB(double intensity) {
		int i = (int) (intensity * parent.getBeamIntensity() * 255.0);
		if(i > 255) {
			i = 255;
		}
		return signalColorRGB | (i << 24);
	}

	public void setDirty() {
		dirty = true;
	}

	private void prepare() {
		int width = parent.getWidth();
		int height = parent.getHeight();

		int divisions = parent.getDivisions();
		double timeDivision = parent.getTimeDivision();
		int sampleRate = parent.getSampleRate();
		double scale = parent.getVoltageDivision();
		boolean useVelocityThreshold = parent.useVelocityThreshold();
		double velocityThreshold = parent.getVelocityThreshold();
		double velocityScale = parent.getVelocityScale();
		double offset = parent.getOffset();
		int sampleSize = parent.getSampleSize();

		boolean draw = false;
		if(buffer == null) {
			buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			draw = true;
		} else if(buffer.getWidth() != width || buffer.getHeight() != height) {
			buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			draw = true;
		}

		if(!draw && !dirty) {
			return;
		}

		dirty = false;

		if(parent.getSignal().length == 0) {
			Graphics2D g = buffer.createGraphics();
			g.setBackground(new Color(0, 0, 0, 0));
			g.clearRect(0, 0, width, height);
			g.dispose();
			return;
		}

		int cy = height / 2;

		double tickDistanceY = height / (double) divisions;

		double visibleSamples = (timeDivision * divisions) * sampleRate;
		double startSample = offset * sampleRate + sampleOffset;

		double yscale = tickDistanceY / scale;

		double samplesPerPixel = visibleSamples / width;

		// draw signal
		float last = 0;
		float lastN = 0;

		if(samplesPerPixel <= 1) {
			// variant 1: using lines, because there are more pixels than samples
			Graphics2D g = buffer.createGraphics();
			g.setBackground(new Color(0, 0, 0, 0));
			g.clearRect(0, 0, width, height);

			for(int i = 0; i < width; i++) {
				double start = startSample + i * samplesPerPixel;
				if(start > parent.getSignal().length) {
					continue;
				}

				float value = parent.getSample(start);
				float valueN = parent.getNearestSample(start);
				float valueN1 = parent.getNearestSample(Math.floor(start));
				float valueN2 = parent.getNearestSample(Math.ceil(start));
				if(Float.isNaN(value)) {
					continue;
				}
				if(Float.isNaN(valueN)) {
					valueN = value;
				}
				if(Float.isNaN(valueN1)) {
					valueN1 = valueN;
				}
				if(Float.isNaN(valueN2)) {
					valueN2 = valueN;
				}
				if(i == 0) {
					last = value;
					lastN = valueN;
				}

				int y = (int) Math.round(value * yscale);
				int y1 = (int) Math.round(last * yscale);
				double velocity = Math.abs(value - last) * velocityScale / scale;

				double velocityN = Math.abs(valueN2 - valueN1) * velocityScale / scale;

				if(useVelocityThreshold && velocityN > velocityThreshold) {
					y = (int) Math.round(valueN * yscale);
					y1 = (int) Math.round(lastN * yscale);
					velocity = Math.abs(valueN - lastN) * velocityScale / scale;
					last = valueN;
				} else {
					last = value;
				}
				lastN = valueN;

				double intensity = velocity > 0 ? 1 / velocity : 1;
				if(intensity > 1) {
					intensity = 1;
				}

				g.setColor(getSignalColor(intensity));
				if(y != y1) {
					g.drawLine(i, cy - y1, i, cy - y);
				} else {
					g.drawLine(i, cy - y1, i + 1, cy - y);
				}
			}

			if(samplesPerPixel < (1.0 / 10.0)) {
				// draw individual samples
				int sampleCount = (int) (width * samplesPerPixel) + 1;
				g.setColor(getSampleColor());

				for(int i = 0; i < sampleCount; i++) {
					int sample = (int) (startSample + i);
					if(sample > parent.getSignal().length) {
						break;
					}

					float value = parent.getNearestSample(sample);
					int x = (int) Math
							.round(((int) startSample - startSample + i) / samplesPerPixel);
					int y = (int) Math.round(value * yscale);

					g.drawLine(x - sampleSize, cy - y, x + sampleSize, cy - y);
					g.drawLine(x, cy - y - sampleSize, x, cy - y + sampleSize);
				}
			}

			g.dispose();
		} else {
			// variant 2: using pixels, because there are more samples than pixels
			DataBufferInt buf = (DataBufferInt) buffer.getRaster().getDataBuffer();
			int[] data = buf.getData();

			double[] sum = new double[height];
			for(int i = 0; i < width; i++) {
				double start = startSample + i * samplesPerPixel;
				if(start > parent.getSignal().length) {
					// no more data, clear remaining area
					for(int y = 0; y < height; y++) {
						data[y * width + i] = 0;
					}
					continue;
				}

				for(int n = 0; n < sum.length; n++) {
					sum[n] = 0;
				}
				last = parent.getSample(start);
				{
					last = parent.getSample(start - 1);
					float value = parent.getSample(start);
					if(Float.isNaN(last)) {
						last = value;
					}
					int y = cy - (int) Math.round(value * yscale);
					int y1 = cy - (int) Math.round(last * yscale);
					double velocity = Math.abs(value - last) * velocityScale / scale;
					double intensity = velocity > 0 ? 1 / velocity : 1;
					double contribution = Math.ceil(start) - start;
					if(y1 > y) {
						int tmp = y;
						y = y1;
						y1 = tmp;
					}
					if(y == y1) {
						if(y >= 0 && y < sum.length) {
							sum[y] += intensity * contribution;
						}
					} else {
						for(int n = y1; n < y; n++) {
							if(n >= 0 && n < sum.length) {
								sum[n] += intensity * contribution;
							}
						}
					}
					last = value;
				}
				for(int sample = 1; sample < Math.ceil(samplesPerPixel); sample++) {
					float value = parent.getSample(start + sample);
					int y = cy - (int) Math.round(value * yscale);
					int y1 = cy - (int) Math.round(last * yscale);
					double velocity = Math.abs(value - last) * velocityScale / scale;
					double intensity = velocity > 0 ? 1 / velocity : 1;
					if(y1 > y) {
						int tmp = y;
						y = y1;
						y1 = tmp;
					}
					if(y == y1) {
						if(y >= 0 && y < sum.length) {
							sum[y] += intensity;
						}
					} else {
						for(int n = y1; n < y; n++) {
							if(n >= 0 && n < sum.length) {
								sum[n] += intensity;
							}
						}
					}
					last = value;
				}
				// TODO: float comparison = bug?
				if(samplesPerPixel != Math.floor(samplesPerPixel)) {
					float value = parent.getSample(start + samplesPerPixel);
					int y = cy - (int) Math.round(value * yscale);
					int y1 = cy - (int) Math.round(last * yscale);
					double velocity = Math.abs(value - last) * velocityScale / scale;
					double intensity = velocity > 0 ? 1 / velocity : 1;
					double contribution = samplesPerPixel - Math.floor(samplesPerPixel);
					if(y1 > y) {
						int tmp = y;
						y = y1;
						y1 = tmp;
					}
					if(y == y1) {
						if(y >= 0 && y < sum.length) {
							sum[y] += intensity * contribution;
						}
					} else {
						for(int n = y1; n < y; n++) {
							if(n >= 0 && n < sum.length) {
								sum[n] += intensity * contribution;
							}
						}
					}
					last = value;
				}

				for(int y = 0; y < height; y++) {
					double intensity = sum[y] / samplesPerPixel;
					if(intensity > 1) {
						intensity = 1;
					}
					if(intensity > 0) {
						int color = getSignalColorRGB(intensity);
						data[y * width + i] = color;
						// g.drawLine(i, y, i, y + 1);
					} else {
						data[y * width + i] = 0;
					}
				}
			}
		}
	}

	public void paint(Graphics g) {
		prepare();
		g.drawImage(buffer, 0, 0, parent);
	}
}
