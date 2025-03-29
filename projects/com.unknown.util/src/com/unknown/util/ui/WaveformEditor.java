package com.unknown.util.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;
import com.unknown.util.ui.event.CursorListener;

@SuppressWarnings("serial")
public class WaveformEditor extends JComponent {
	private static final Logger log = Trace.create(WaveformEditor.class);
	private static final int CURSOR_COUNT = 8;

	private static final Color TRANSLUCENT = new Color(0, true);
	private static final Color OVERLAY_BACKGROUND = new Color(0, 0, 0, 192);

	private int divisions = 20;
	private double velocityScale = 10;
	private double velocityThreshold = 10;
	private boolean useVelocityThreshold = false;

	private double beamIntensity = 1.0;

	private int sampleRate;
	private double timeDivision;
	private double scale;

	private double defaultTimeDivision = 0.1;
	private double defaultVoltageDivision = 0.1;

	private double offset;
	private float[] samples;

	private Color sampleColor;
	private Color gridColor;
	private Color signalColor;
	private int signalColorRGB;
	private Color signalColorAlt;
	private Color textColor;

	private int sampleSize = 3;

	private int cursorLoopStart = -1;
	private int cursorLoopEnd = -1;

	private boolean loopEditMode;
	private boolean loopEditEnd;
	private boolean overlayVisible;

	private final boolean[] cursorTimeVisible = new boolean[CURSOR_COUNT];
	private final boolean[] cursorLevelVisible = new boolean[CURSOR_COUNT];
	private final boolean[] cursorEditable = new boolean[CURSOR_COUNT];
	private final float[] cursorLevel = new float[CURSOR_COUNT];
	private final double[] cursorTime = new double[CURSOR_COUNT];
	private final Color[] cursorColor = new Color[CURSOR_COUNT];
	private final String[] cursorName = new String[CURSOR_COUNT];

	private int cursorSelectionDistance = 3;
	private int selectedCursor = -1;

	@SuppressWarnings("unchecked") private List<CursorListener>[] cursorListeners = new ArrayList[CURSOR_COUNT];

	private WaveformRenderer renderer;
	private WaveformRenderer rendererAlt;

	public WaveformEditor() {
		samples = new float[0];

		renderer = new WaveformRenderer(this);
		rendererAlt = new WaveformRenderer(this);

		setSampleRate(44100);
		setTimeDivision(defaultTimeDivision);
		setVoltageDivision(defaultVoltageDivision);
		setOffset(0);

		setBackground(Color.BLACK);
		setForeground(Color.GREEN);

		setGridColor(new Color(0, 64, 0));
		setSignalColor(Color.CYAN);
		setSignalColorAlt(new Color(235, 183, 14));
		setSampleColor(Color.WHITE);
		setTextColor(Color.GREEN);

		setOverlayVisible(true);
		setLoopEditMode(false);

		for(int i = 0; i < 8; i++) {
			setCursorTimeVisible(i, false);
			setCursorLevelVisible(i, false);
			setCursorLevel(i, 0);
			setCursorTime(i, 0);
			setCursorColor(i, Color.YELLOW);
			setCursorEditable(i, false);
			setCursorName(i, null);
			cursorListeners[i] = new ArrayList<>();
		}

		MouseController mouse = new MouseController();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		addMouseWheelListener(mouse);
	}

	public int getSampleSize() {
		return sampleSize;
	}

	public void setLogicAnalyzer(boolean value) {
		useVelocityThreshold = value;
		renderer.setDirty();
		rendererAlt.setDirty();
		repaint();
	}

	public boolean useVelocityThreshold() {
		return useVelocityThreshold;
	}

	public double getVelocityThreshold() {
		return velocityThreshold;
	}

	public double getVelocityScale() {
		return velocityScale;
	}

	public void setGridColor(Color color) {
		gridColor = color;
		repaint();
	}

	public Color getGridColor() {
		return gridColor;
	}

	public void setCursorColor(int cursor, Color color) {
		cursorColor[cursor] = color;
		repaint();
	}

	public Color getCursorColor(int cursor) {
		return cursorColor[cursor];
	}

	public void setCursorName(int cursor, String name) {
		cursorName[cursor] = name;
		repaint();
	}

	public String getCursorName(int cursor) {
		return cursorName[cursor];
	}

	public void setSignalColor(Color color) {
		signalColor = color;
		signalColorRGB = color.getRGB() & 0x00FFFFFF;
		renderer.setSignalColor(color);
		repaint();
	}

	public Color getSignalColor() {
		return signalColor;
	}

	public void setSignalColorAlt(Color color) {
		signalColorAlt = color;
		rendererAlt.setSignalColor(color);
		repaint();
	}

	public Color getSignalColorAlt() {
		return signalColorAlt;
	}

	public void setSampleColor(Color color) {
		sampleColor = color;
		renderer.setSampleColor(color);
		rendererAlt.setSampleColor(color);
		repaint();
	}

	public Color getSampleColor() {
		return sampleColor;
	}

	public void setTextColor(Color color) {
		textColor = color;
		repaint();
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setCursorLevel(int cursor, float value) {
		cursorLevel[cursor] = value;
		if(cursorLevelVisible[cursor]) {
			repaint();
		}
	}

	public void setCursorTime(int cursor, double value) {
		cursorTime[cursor] = value;

		if(cursor == cursorLoopStart || cursor == cursorLoopEnd) {
			updateLoopSignal();
		}

		if(cursorTimeVisible[cursor]) {
			repaint();
		}
	}

	public float getCursorLevel(int cursor) {
		return cursorLevel[cursor];
	}

	public double getCursorTime(int cursor) {
		return cursorTime[cursor];
	}

	public void setCursorLevelVisible(int cursor, boolean visible) {
		cursorLevelVisible[cursor] = visible;
		repaint();
	}

	public boolean isCursorLevelVisible(int cursor) {
		return cursorLevelVisible[cursor];
	}

	public void setCursorTimeVisible(int cursor, boolean visible) {
		cursorTimeVisible[cursor] = visible;
		repaint();
	}

	public boolean isCursorTimeVisible(int cursor) {
		return cursorTimeVisible[cursor];
	}

	public void setCursorEditable(int cursor, boolean editable) {
		cursorEditable[cursor] = editable;
	}

	public boolean isCursorEditable(int cursor) {
		return cursorEditable[cursor];
	}

	public void setCursorSelectionDistance(int distance) {
		cursorSelectionDistance = distance;
	}

	public int getCursorSelectionDistance() {
		return cursorSelectionDistance;
	}

	public void addCursorListener(int cursor, CursorListener listener) {
		cursorListeners[cursor].add(listener);
	}

	public void removeCursorListener(int cursor, CursorListener listener) {
		cursorListeners[cursor].remove(listener);
	}

	protected void fireCursorTimeChanged(int cursor, double time) {
		for(CursorListener listener : cursorListeners[cursor]) {
			try {
				listener.cursorTimeChanged(cursor, time);
			} catch(Throwable t) {
				log.log(Levels.ERROR, "Error while executing CursorListener: " + t, t);
			}
		}

		if(cursor == cursorLoopStart || cursor == cursorLoopEnd) {
			updateLoopSignal();
		}
	}

	protected void fireCursorLevelChanged(int cursor, float level) {
		for(CursorListener listener : cursorListeners[cursor]) {
			try {
				listener.cursorLevelChanged(cursor, level);
			} catch(Throwable t) {
				log.log(Levels.ERROR, "Error while executing CursorListener: " + t, t);
			}
		}
	}

	public void setCursorLoopStart(int cursor) {
		cursorLoopStart = cursor;
	}

	public int getCursorLoopStart() {
		return cursorLoopStart;
	}

	public void setCursorLoopEnd(int cursor) {
		cursorLoopEnd = cursor;
	}

	public int getCursorLoopEnd() {
		return cursorLoopEnd;
	}

	private void updateLoopSignal() {
		// adjust alt waveform offset
		double start = getCursorTime(cursorLoopStart);
		double end = getCursorTime(cursorLoopEnd);
		double off;
		if(loopEditEnd) {
			off = (end - start) * sampleRate;
		} else {
			off = (start - end) * sampleRate;
		}
		rendererAlt.setSampleOffset(-off);
	}

	public void setOverlayVisible(boolean visible) {
		overlayVisible = visible;
		repaint();
	}

	public boolean isOverlayVisible() {
		return overlayVisible;
	}

	public void setLoopEditMode(boolean enabled) {
		loopEditMode = enabled;
		if(enabled) {
			updateLoopSignal();
		}
		repaint();
	}

	public boolean isLoopEditMode() {
		return loopEditMode;
	}

	public void setLoopEditEnd(boolean enabled) {
		loopEditEnd = enabled;
		updateLoopSignal();
		repaint();
	}

	public boolean isLoopEditEnd() {
		return loopEditEnd;
	}

	public boolean isLoopEditModeAvailable() {
		return cursorLoopStart != -1 && cursorLoopEnd != -1;
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

		double visibleSamples = (timeDivision * divisions) * sampleRate;
		double startSample = offset * sampleRate;

		double yscale = tickDistanceY / scale;

		double samplesPerPixel = visibleSamples / width;

		double offsetX = offset / timeDivision;
		double offsetDivX = offsetX - Math.floor(offsetX);

		g.setColor(gridColor);
		for(int i = 0; i <= divisions; i++) {
			int x = (int) Math.round(tickDistanceX * (i - offsetDivX));
			g.drawLine(x, 0, x, height);
		}

		for(int i = 0; i <= (divisions / 2); i++) {
			int y = (int) Math.round(tickDistanceY * i);
			g.drawLine(0, cy - y, width, cy - y);
			g.drawLine(0, cy + y, width, cy + y);
		}

		// TODO: implement time cursor

		// grid
		g.setColor(getForeground());
		g.drawLine(0, cy, width, cy);
		g.drawLine(0, 0, 0, height);

		// ticks: X axis
		for(int i = 0; i <= divisions; i++) {
			int x = (int) Math.round(tickDistanceX * (i - offsetDivX));
			g.drawLine(x, cy - 5, x, cy + 5);
		}

		// ticks: Y axis
		for(int i = 0; i <= (divisions / 2); i++) {
			int y = (int) Math.round(tickDistanceY * i);
			g.drawLine(0, cy - y, 5, cy - y);
			g.drawLine(0, cy + y, 5, cy + y);
		}

		renderer.paint(g);

		if(loopEditMode) {
			rendererAlt.paint(g);
		}

		// draw cursor levels
		for(int cursor = 0; cursor < cursorLevelVisible.length; cursor++) {
			if(cursorLevelVisible[cursor]) {
				g.setColor(getForeground());

				int y = (int) Math.round(cursorLevel[cursor] * yscale);
				for(int i = 0; i < width / 2; i += 20) {
					g.drawLine(cx + i - 3, cy - y, cx + i + 3, cy - y);
					g.drawLine(cx - i + 3, cy - y, cx - i - 3, cy - y);
				}
			}
		}

		// draw cursor times
		for(int cursor = 0; cursor < cursorTimeVisible.length; cursor++) {
			if(cursorTimeVisible[cursor] && cursorTime[cursor] >= offset &&
					cursorTime[cursor] <= (offset + timeDivision * divisions)) {
				double currentSample = cursorTime[cursor] * sampleRate;
				int pos = (int) ((currentSample - startSample) / samplesPerPixel);
				g.setColor(getCursorColor(cursor));
				g.drawLine(pos, 0, pos, height);

				if(cursor == selectedCursor) {
					g.drawLine(pos - 1, 0, pos - 1, height);
					g.drawLine(pos + 1, 0, pos + 1, height);
				}
			}
		}

		// draw text overlay
		if(overlayVisible) {
			String[] overlay = new String[selectedCursor == -1 ? 2 : 3];
			overlay[0] = "H: " + getTimeDivisionString();
			overlay[1] = "V: " + getVoltageDivision();

			if(selectedCursor != -1) {
				String name = getCursorName(selectedCursor);
				if(name == null) {
					overlay[2] = "Cursor " + (selectedCursor + 1);
				} else {
					overlay[2] = "Cursor: " + name;
				}
			}

			renderOverlay(g, 2, 2, overlay);
			renderOverlayRight(g, 2, new String[] { "Sample Rate: " + sampleRate + "Hz" });
		}
	}

	private void renderOverlay(Graphics g, int x, int y, String[] text) {
		int maxlen = 0;
		for(String line : text) {
			int len = line.length();
			if(len > maxlen) {
				maxlen = len;
			}
		}

		g.setColor(OVERLAY_BACKGROUND);
		g.fillRect(x, y, maxlen * ADM3AFont.WIDTH + 6, text.length * ADM3AFont.HEIGHT + 6);

		for(int i = 0; i < text.length; i++) {
			ADM3AFont.render(g, x + 3, y + 1 + (i + 1) * ADM3AFont.HEIGHT, textColor, TRANSLUCENT, text[i]);
		}
	}

	private void renderOverlayRight(Graphics g, int y, String[] text) {
		int maxlen = 0;
		for(String line : text) {
			int len = line.length();
			if(len > maxlen) {
				maxlen = len;
			}
		}

		int x = getWidth() - (maxlen * ADM3AFont.WIDTH + 9);

		g.setColor(OVERLAY_BACKGROUND);
		g.fillRect(x, y, maxlen * ADM3AFont.WIDTH + 6, text.length * ADM3AFont.HEIGHT + 6);

		for(int i = 0; i < text.length; i++) {
			ADM3AFont.render(g, x + 3, y + 1 + (i + 1) * ADM3AFont.HEIGHT, textColor, TRANSLUCENT, text[i]);
		}
	}

	private static double round1(double value) {
		return Math.round(value * 10.0) / 10.0;
	}

	public String getTimeDivisionString() {
		double time = timeDivision;
		if(time > 1) {
			return Double.toString(time) + "s";
		} else if(time > 1e-3) {
			double ms = round1(time * 1e3);
			return Double.toString(ms) + "ms";
		} else if(time > 1e-6) {
			double ms = round1(time * 1e6);
			return Double.toString(ms) + "us";
		} else if(time > 1e-9) {
			double ms = round1(time * 1e9);
			return Double.toString(ms) + "ns";
		} else {
			double ms = round1(time * 1e12);
			return Double.toString(ms) + "ps";
		}
	}

	public Color getSignalColor(double intensity) {
		int i = (int) (intensity * beamIntensity * 255.0);
		if(i > 255) {
			i = 255;
		}
		return new Color(signalColor.getRed(), signalColor.getGreen(), signalColor.getBlue(), i);
	}

	public int getSignalColorRGB(double intensity) {
		int i = (int) (intensity * beamIntensity * 255.0);
		if(i > 255) {
			i = 255;
		}
		return signalColorRGB | (i << 24);
	}

	public float getSample(double sample) {
		if(sample < 0 || sample > samples.length - 1) {
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
			float d = (float) (sample - first);
			return val1 + k * d;
		}
	}

	public float getNearestSample(double sample) {
		if(sample < 0 || sample > samples.length - 1) {
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
		renderer.setDirty();
		rendererAlt.setDirty();
		repaint();
	}

	public double getTimeDivision() {
		return timeDivision;
	}

	public void setVoltageDivision(double voltageDivision) {
		if(voltageDivision < 0.0001) {
			return;
		} else if(voltageDivision > 10) {
			return;
		}
		scale = voltageDivision;
		renderer.setDirty();
		rendererAlt.setDirty();
		repaint();
	}

	public double getVoltageDivision() {
		return scale;
	}

	public int getDivisions() {
		return divisions;
	}

	public void setDefaultDivision(double voltage, double time) {
		defaultVoltageDivision = voltage;
		defaultTimeDivision = time;
	}

	public void setSignal(float[] signal) {
		samples = signal;
		renderer.setDirty();
		rendererAlt.setDirty();
		repaint();
	}

	public float[] getSignal() {
		return samples;
	}

	public void setOffset(double offset) {
		double limit = getScreenStartTime();
		if(limit < 0) {
			this.offset = 0;
		} else if(offset < 0) {
			this.offset = 0;
		} else if(offset > limit) {
			this.offset = limit;
		} else {
			this.offset = offset;
		}
		renderer.setDirty();
		rendererAlt.setDirty();
		repaint();
	}

	public double getOffset() {
		return offset;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
		renderer.setDirty();
		rendererAlt.setDirty();
		repaint();
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setBeamIntensity(double intensity) {
		beamIntensity = intensity;
		renderer.setDirty();
		rendererAlt.setDirty();
		repaint();
	}

	public double getBeamIntensity() {
		return beamIntensity;
	}

	public double getScreenStartTime() {
		return samples.length / (double) sampleRate - timeDivision * divisions;
	}

	public double getMinTimeDivision() {
		return 0.25f / sampleRate;
	}

	public double getMaxTimeDivision() {
		return samples.length / (double) sampleRate;
	}

	public void fitTimeDivision() {
		double div = getTimeDivision();
		int width = getWidth();

		double samplesPerPixel = (div * divisions * sampleRate) / width;
		double length = samples.length / samplesPerPixel;

		if(length < width) {
			// we see more than the sample, try to decrease time division
			while(div > getMinTimeDivision()) {
				samplesPerPixel = ((div / 2) * divisions * sampleRate) / width;
				length = samples.length / samplesPerPixel;
				if(length > width) {
					break;
				} else {
					div /= 2;
				}
			}
		} else {
			// we can't even see the entire sample, try to increase time divison
			while(div < getMaxTimeDivision()) {
				samplesPerPixel = (div * divisions * sampleRate) / width;
				length = samples.length / samplesPerPixel;
				if(length < width) {
					break;
				} else {
					div *= 2;
				}
			}
		}

		if(div >= getMinTimeDivision() && div <= getMaxTimeDivision()) {
			setTimeDivision(div);
		}
	}

	private double pixelsToSample(double px) {
		int width = getWidth();
		double samplesPerPixel = ((timeDivision * divisions) * sampleRate) / width;
		return offset * sampleRate + px * samplesPerPixel;
	}

	private double pixelsToTime(double px) {
		return pixelsToSample(px) / sampleRate;
	}

	private int timeToPixels(double time) {
		int width = getWidth();
		double visibleSamples = (timeDivision * divisions) * sampleRate;
		double startSample = offset * sampleRate;

		double samplesPerPixel = visibleSamples / width;
		double sample = time * sampleRate;
		return (int) ((sample - startSample) / samplesPerPixel);
	}

	public int getSelectedCursor(int x) {
		double time = pixelsToTime(x);
		int nearest = -1;
		double nearestTime = Double.MAX_VALUE;

		for(int i = 0; i < cursorTime.length; i++) {
			if(!isCursorTimeVisible(i) || !isCursorEditable(i)) {
				continue;
			}

			double diff = Math.abs(cursorTime[i] - time);
			if(diff < nearestTime) {
				nearestTime = diff;
				nearest = i;
			}
		}

		// check if we are close enough
		if(nearest != -1) {
			double t = getCursorTime(nearest);

			int px = timeToPixels(t);
			if(Math.abs(x - px) < cursorSelectionDistance) {
				return nearest;
			}
		}

		return -1;
	}

	private class MouseController extends MouseAdapter {
		private int x;
		private double startOffset;
		private int cursor;
		private double startTime;

		private void updateCursorSelection(MouseEvent e) {
			int id = getSelectedCursor(e.getX());
			if(selectedCursor != id) {
				selectedCursor = id;
				repaint();
			}

			// update mouse cursor
			if(id != -1) {
				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			} else {
				setCursor(Cursor.getDefaultCursor());
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			x = e.getX();
			startOffset = offset;
			cursor = getSelectedCursor(x);

			if(e.getButton() == MouseEvent.BUTTON1) {
				if(cursor != -1) {
					startTime = cursorTime[cursor];
				}
			} else if(e.getButton() == MouseEvent.BUTTON3) {
				JMenuItem reset = new JMenuItem("Reset");
				reset.setMnemonic('R');
				reset.addActionListener(ev -> {
					setTimeDivision(defaultTimeDivision);
					setVoltageDivision(defaultVoltageDivision);
					setOffset(0);
				});

				JMenuItem fit = new JMenuItem("Fit");
				fit.setMnemonic('F');
				fit.addActionListener(ev -> {
					fitTimeDivision();
					setVoltageDivision(defaultVoltageDivision);
					setOffset(0);
				});

				JCheckBoxMenuItem loopedit = new JCheckBoxMenuItem("Loop Edit Mode");
				loopedit.setSelected(isLoopEditMode());
				loopedit.setEnabled(isLoopEditModeAvailable());
				loopedit.setMnemonic('L');
				loopedit.addActionListener(ev -> {
					setLoopEditMode(loopedit.isSelected());
				});

				JCheckBoxMenuItem loopend = new JCheckBoxMenuItem("Loop Edit End");
				loopend.setSelected(isLoopEditEnd());
				loopend.setEnabled(isLoopEditModeAvailable());
				loopend.setMnemonic('E');
				loopend.addActionListener(ev -> {
					setLoopEditEnd(loopend.isSelected());
				});

				JCheckBoxMenuItem overlays = new JCheckBoxMenuItem("Show Overlays");
				overlays.setSelected(isOverlayVisible());
				overlays.setMnemonic('O');
				overlays.addActionListener(ev -> {
					setOverlayVisible(overlays.isSelected());
				});

				JPopupMenu menu = new JPopupMenu();
				menu.add(reset);
				menu.add(fit);
				menu.addSeparator();
				menu.add(loopedit);
				menu.add(loopend);
				menu.addSeparator();
				menu.add(overlays);

				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON1) {
				if(cursor != -1) {
					// final update
					int px = e.getX();
					int moved = px - x;
					double dt = pixelsToTime(moved) - offset;
					double time = startTime + dt;
					if(time > samples.length / (double) sampleRate) {
						time = samples.length / (double) sampleRate;
					} else if(time < 0) {
						time = 0;
					}
					cursorTime[cursor] = time;
					fireCursorTimeChanged(cursor, time);
					repaint();
				}
			}

			updateCursorSelection(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			updateCursorSelection(e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			updateCursorSelection(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			updateCursorSelection(e);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
				// left mouse button
				int px = e.getX();
				int moved = px - x;

				if(cursor != -1) {
					// move cursor
					double dt = pixelsToTime(moved) - offset;
					double time = startTime + dt;
					if(time > samples.length / (double) sampleRate) {
						time = samples.length / (double) sampleRate;
					} else if(time < 0) {
						time = 0;
					}
					cursorTime[cursor] = time;
					fireCursorTimeChanged(cursor, time);
					repaint();
				}
			} else if((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
				// middle mouse button
				int px = e.getX();
				int moved = px - x;
				int width = getWidth();
				double samplesPerPixel = ((timeDivision * divisions) * sampleRate) / width;
				double movedSamples = moved * samplesPerPixel;
				if(getScreenStartTime() < 0) {
					setOffset(0);
				} else {
					setOffset(startOffset - movedSamples / sampleRate);
				}
			}
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();

			if((e.getModifiersEx() & MouseWheelEvent.CTRL_DOWN_MASK) != 0) {
				double div = getVoltageDivision() * Math.pow(2.0, notches);
				setVoltageDivision(div);
			} else {
				double div = getTimeDivision() * Math.pow(2.0, notches);
				int px = e.getX();
				int width = getWidth();

				double samplesPerPixel = ((timeDivision * divisions) * sampleRate) / width;
				double cursorSample = offset * sampleRate + px * samplesPerPixel;

				samplesPerPixel = ((div * divisions) * sampleRate) / width;
				double off = (cursorSample - px * samplesPerPixel) / sampleRate;

				if(div >= getMinTimeDivision() && div <= getMaxTimeDivision()) {
					setTimeDivision(div);
					setOffset(off);
				}
			}
		}
	}
}
