package com.unknown.util.test;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.unknown.util.ui.WaveformEditor;

public class WaveformEditorTest {
	// this is an interactive test!
	public static void main(String[] args) {
		WaveformEditor wave = new WaveformEditor();
		wave.setTimeDivision(0.01);
		wave.setVoltageDivision(1);
		wave.setOffset(0);
		wave.setSampleRate(48000);
		wave.setCursorLevel(0, 0);
		wave.setCursorLevel(1, 0);
		wave.setCursorLevelVisible(0, true);
		wave.setCursorLevelVisible(1, true);
		wave.setCursorTime(0, 0);
		wave.setCursorTime(1, 0);
		wave.setCursorTimeVisible(0, true);
		wave.setCursorTimeVisible(1, true);

		double fs = 48000;
		float[] signal = new float[(int) (fs * 5)]; // 5s
		double f = 1000; // 1kHz
		for(int i = 0; i < signal.length; i++) {
			double t = i / fs;
			double phi = t * f * 2 * Math.PI;
			signal[i] = (float) Math.sin(phi);
		}
		wave.setSignal(signal);

		JTextField divX = new JTextField("0.01");
		divX.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String value = divX.getText().trim();
				try {
					double val = Double.parseDouble(value);
					wave.setTimeDivision(val);
				} catch(NumberFormatException ex) {
					// nothing
				}
			}
		});

		JTextField divY = new JTextField("1.0");
		divY.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String value = divY.getText().trim();
				try {
					double val = Double.parseDouble(value);
					wave.setVoltageDivision(val);
				} catch(NumberFormatException ex) {
					// nothing
				}
			}
		});

		JTextField freq = new JTextField("1000");
		JButton sin = new JButton("~");
		JButton rect = new JButton("_||_");

		JPanel buttons = new JPanel();
		buttons.add(freq);
		buttons.add(sin);
		buttons.add(rect);

		JPanel offsetPanel = new JPanel(new BorderLayout());
		JTextField offset = new JTextField("0");
		JTextField cursorLevel = new JTextField("0.0");
		offset.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String value = offset.getText().trim();
				try {
					double val = Double.parseDouble(value);
					wave.setOffset(val);
				} catch(NumberFormatException ex) {
					// nothing
				}
			}
		});
		cursorLevel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String value = cursorLevel.getText().trim();
				try {
					float val = Float.parseFloat(value);
					wave.setCursorLevel(0, val);
					offset.setText(Double.toString(wave.getOffset()));
				} catch(NumberFormatException ex) {
					// nothing
				}
			}
		});
		offsetPanel.add(BorderLayout.CENTER, offset);
		offsetPanel.add(BorderLayout.EAST, cursorLevel);

		sin.addActionListener(e -> {
			double f0 = f;
			try {
				f0 = Double.parseDouble(freq.getText().trim());
			} catch(NumberFormatException ex) {
				// nothing
			}
			for(int i = 0; i < signal.length; i++) {
				double t = i / fs;
				double phi = t * f0 * 2 * Math.PI;
				signal[i] = (float) Math.sin(phi);
			}
			wave.setSignal(signal);
			offset.setText(Double.toString(wave.getOffset()));
		});
		rect.addActionListener(e -> {
			double f0 = f;
			try {
				f0 = Double.parseDouble(freq.getText().trim());
			} catch(NumberFormatException ex) {
				// nothing
			}
			for(int i = 0; i < signal.length; i++) {
				double t = i / fs;
				double phi = t * f0 * 2 * Math.PI;
				signal[i] = (float) Math.signum(Math.sin(phi));
			}
			wave.setSignal(signal);
			offset.setText(Double.toString(wave.getOffset()));
		});

		JPanel inputs = new JPanel(new GridLayout(1, 2));
		inputs.add(divX);
		inputs.add(divY);

		JPanel general = new JPanel(new BorderLayout());
		general.add(BorderLayout.CENTER, inputs);
		general.add(BorderLayout.EAST, buttons);

		JPanel south = new JPanel(new GridLayout(2, 1));
		south.add(general);
		south.add(offsetPanel);

		JFrame frame = new JFrame("WaveformEditor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(BorderLayout.CENTER, wave);
		frame.getContentPane().add(BorderLayout.SOUTH, south);
		frame.setVisible(true);
	}
}
