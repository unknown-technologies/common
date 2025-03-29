package com.unknown.util.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class Oscilloscope extends JComponent {
	private int divisions = 20;
	private double velocityScale = 10;
	private double velocityThreshold = 10;
	private boolean useVelocityThreshold = false;

	private int sampleRate;
	private double timeDivision;
	private double scale;

	private double offset;
	private float[] samples;
	private float[] oldSamples;

	private Color gridColor;
	private Color signalColor;
	private int signalColorRGB;

	private boolean triggerVisible;
	private boolean triggerRising;
	private float triggerLevel;
	private boolean triggerEnabled = true;

	private boolean dirty;
	private BufferedImage buffer;

	public Oscilloscope() {
		samples = new float[0];

		setSampleRate(44100);
		setTimeDivision(1);
		setVoltageDivision(1);
		setOffset(0);

		setBackground(Color.BLACK);
		setForeground(Color.GREEN);

		setGridColor(new Color(0, 64, 0));
		setSignalColor(Color.CYAN);

		setTriggerVisible(false);
		setTriggerLevel(0);
		setTriggerRisingEdge(true);

		buffer = null;
		dirty = true;
	}

	public void setLogicAnalyzer(boolean value) {
		useVelocityThreshold = value;
		repaint();
	}

	public void setGridColor(Color color) {
		gridColor = color;
	}

	public Color getGridColor() {
		return gridColor;
	}

	public void setSignalColor(Color color) {
		signalColor = color;
		signalColorRGB = color.getRGB() & 0x00FFFFFF;
		dirty = true;
		repaint();
	}

	public Color getSignalColor() {
		return signalColor;
	}

	public void setTriggerLevel(float value) {
		triggerLevel = value;
		if(triggerVisible) {
			repaint();
		}
	}

	public float getTriggerLevel() {
		return triggerLevel;
	}

	public void setTriggerVisible(boolean visible) {
		triggerVisible = visible;
		repaint();
	}

	public boolean isTriggerVisible() {
		return triggerVisible;
	}

	public void setTriggerRisingEdge(boolean rising) {
		triggerRising = rising;
	}

	public boolean isTriggerRisingEdge() {
		return triggerRising;
	}

	public void setTriggerEnabled(boolean enabled) {
		triggerEnabled = enabled;
	}

	public boolean isTriggerEnabled() {
		return triggerEnabled;
	}

	private void prepare() {
		int width = getWidth();
		int height = getHeight();

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

		int cy = height / 2;

		double tickDistanceY = height / (double) divisions;

		double visibleSamples = (timeDivision * divisions) * sampleRate;
		double startSample = (offset - (timeDivision * divisions / 2.0)) * sampleRate;

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
				float value = getSample(start);
				float valueN = getNearestSample(start);
				float valueN1 = getNearestSample(Math.floor(start));
				float valueN2 = getNearestSample(Math.ceil(start));
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

			g.dispose();
		} else {
			// variant 2: using pixels, because there are more samples than pixels
			DataBufferInt buf = (DataBufferInt) buffer.getRaster().getDataBuffer();
			int[] data = buf.getData();

			double[] sum;
			for(int i = 0; i < width; i++) {
				double start = startSample + i * samplesPerPixel;
				sum = new double[height];
				last = getSample(start);
				{
					last = getSample(start - 1);
					float value = getSample(start);
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
					float value = getSample(start + sample);
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
				if(samplesPerPixel != Math.floor(samplesPerPixel)) {
					float value = getSample(start + samplesPerPixel);
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

	@Override
	protected void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();

		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());

		int cx = width / 2;
		int cy = height / 2;

		double tickDistanceX = width / (double) divisions;
		double tickDistanceY = height / (double) divisions;

		double yscale = tickDistanceY / scale;

		g.setColor(gridColor);
		for(int i = 0; i <= (divisions / 2); i++) {
			int x = (int) Math.round(tickDistanceX * i);
			g.drawLine(cx - x, 0, cx - x, height);
			g.drawLine(cx + x, 0, cx + x, height);
		}

		for(int i = 0; i <= (divisions / 2); i++) {
			int y = (int) Math.round(tickDistanceY * i);
			g.drawLine(0, cy - y, width, cy - y);
			g.drawLine(0, cy + y, width, cy + y);
		}

		if(triggerVisible) {
			g.setColor(getForeground());
			int y = (int) Math.round(triggerLevel * yscale);
			for(int i = 0; i < width / 2; i += 20) {
				g.drawLine(cx + i - 3, cy - y, cx + i + 3, cy - y);
				g.drawLine(cx - i + 3, cy - y, cx - i - 3, cy - y);
			}
		}

		prepare();
		g.drawImage(buffer, 0, 0, this);

		g.setColor(getForeground());
		g.drawLine(0, cy, width, cy);
		g.drawLine(cx, 0, cx, height);

		for(int i = 0; i <= (divisions / 2); i++) {
			int x = (int) Math.round(tickDistanceX * i);
			g.drawLine(cx - x, cy - 5, cx - x, cy + 5);
			g.drawLine(cx + x, cy - 5, cx + x, cy + 5);
		}

		for(int i = 0; i <= (divisions / 2); i++) {
			int y = (int) Math.round(tickDistanceY * i);
			g.drawLine(cx - 5, cy - y, cx + 5, cy - y);
			g.drawLine(cx - 5, cy + y, cx + 5, cy + y);
		}
	}

	public Color getSignalColor(double intensity) {
		return new Color(signalColor.getRed(), signalColor.getGreen(), signalColor.getBlue(),
				(int) (intensity * 255.0));
	}

	public int getSignalColorRGB(double intensity) {
		return signalColorRGB | (((int) (intensity * 255.0)) << 24);
	}

	private float getSample(double sample) {
		if(sample < 0 || sample >= samples.length) {
			return Float.NaN;
		} else {
			int first = (int) Math.floor(sample);
			int second = (int) Math.ceil(sample);
			if(first < 0) {
				first = 0;
			}
			if(second >= samples.length) {
				second = samples.length - 1;
			}
			float val1 = samples[first];
			float val2 = samples[second];
			float k = val2 - val1;
			float d = (float) (sample - Math.floor(sample));
			return val1 + k * d;
		}
	}

	private float getNearestSample(double sample) {
		if(sample < 0 || sample >= samples.length) {
			return Float.NaN;
		} else {
			int index = (int) Math.round(sample);
			if(index < 0) {
				index = 0;
			}
			if(index >= samples.length) {
				index = samples.length - 1;
			}
			return samples[index];
		}
	}

	public void setTimeDivision(double timeDivision) {
		this.timeDivision = timeDivision;
		dirty = true;
		repaint();
	}

	public double getTimeDivision() {
		return timeDivision;
	}

	public void setVoltageDivision(double voltageDivision) {
		scale = voltageDivision;
		dirty = true;
		repaint();
	}

	public double getVoltageDivision() {
		return scale;
	}

	public void setSignal(float[] signal) {
		if(oldSamples == null || oldSamples.length != signal.length) {
			oldSamples = new float[signal.length];
			System.arraycopy(signal, 0, oldSamples, 0, signal.length);
		} else {
			System.arraycopy(this.samples, 0, oldSamples, 0, signal.length);
		}
		samples = signal;
		if(triggerEnabled) {
			trigger();
		}
		dirty = true;
		repaint();
	}

	public float[] getSignal() {
		return samples;
	}

	public void setOffset(double offset) {
		this.offset = offset;
		dirty = true;
		repaint();
	}

	public double getOffset() {
		return offset;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
		dirty = true;
		repaint();
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void trigger() {
		if(samples.length < 5) {
			return;
		}

		int c = samples.length / 2;
		int point = -1;
		int[] points = new int[samples.length];
		int count = 0;

		float visibleSamples = (float) (timeDivision * divisions) * sampleRate;
		int limit = samples.length;

		if(triggerRising) {
			for(int i = 0; i < limit / 2; i++) {
				if(samples[c + i - 1] < triggerLevel && samples[c + i] >= triggerLevel) {
					point = c + i;
					points[count++] = point;
				} else if(samples[c - i - 1] < triggerLevel && samples[c - i] >= triggerLevel) {
					point = c - i;
					points[count++] = point;
				}
			}
		} else {
			for(int i = 0; i < limit / 2; i++) {
				if(samples[c + i - 1] >= triggerLevel && samples[c + i] < triggerLevel) {
					point = c + i;
					points[count++] = point;
				} else if(samples[c - i - 1] >= triggerLevel && samples[c - i] < triggerLevel) {
					point = c - i;
					points[count++] = point;
				}
			}
		}

		if(point != -1) {
			double best = c / (double) sampleRate;
			double bestScore = Float.MAX_VALUE;

			for(int i = 0; i < count; i++) {
				point = points[i];

				// compute offset
				double val0 = getNearestSample(point - 1);
				double val1 = getNearestSample(point);
				double k = val1 - val0;
				double d = (triggerLevel - val0) / k;
				d = 1 - d;
				double off = (point - d) / sampleRate;

				double score = estimate(off) *
						(Math.pow(Math.abs(c - point), 1.5) / visibleSamples + 1);
				if(score < bestScore) {
					bestScore = score;
					best = off;
				}
			}

			offset = best;
		} else {
			// center
			setOffset(c / (double) sampleRate);
		}

		dirty = true;
	}

	private float estimate(double off) {
		if(oldSamples == null) {
			return 0;
		}

		double visibleSamples = (timeDivision * divisions) * sampleRate;
		int limit = (int) (visibleSamples / 2);

		float error = 0;
		for(int i = -limit; i <= limit; i++) {
			float a, b;
			int offA = (int) (this.offset * sampleRate + i);
			int offB = (int) (off * sampleRate + i);

			if(offA < 0 || offA >= oldSamples.length) {
				a = 0;
			} else {
				a = oldSamples[offA];
			}

			if(offB < 0 || offB >= samples.length) {
				b = 0;
			} else {
				b = samples[offB];
			}

			float diff = (a - b) * (a - b);
			error += diff;
		}

		return error;
	}
}
