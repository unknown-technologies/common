package com.unknown.util.test;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.unknown.util.ui.Oscilloscope;

public class OscilloscopeTest {
	// this is an interactive test!
	public static void main(String[] args) {
		Oscilloscope osc = new Oscilloscope();
		osc.setTimeDivision(0.01);
		osc.setVoltageDivision(1);
		osc.setOffset(2.5); // middle
		osc.setSampleRate(48000);
		osc.setTriggerRisingEdge(true);
		osc.setTriggerLevel(0);
		osc.setTriggerVisible(true);

		double fs = 48000;
		float[] signal = new float[(int) (fs * 5)]; // 5s
		double f = 1000; // 1kHz
		for(int i = 0; i < signal.length; i++) {
			double t = i / fs;
			double phi = t * f * 2 * Math.PI;
			// signal[i] = (float) Math.signum(Math.sin(phi));
			signal[i] = (float) Math.sin(phi);
		}
		osc.setSignal(signal);

		JTextField divX = new JTextField("0.01");
		divX.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String value = divX.getText().trim();
				try {
					double val = Double.parseDouble(value);
					osc.setTimeDivision(val);
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
					osc.setVoltageDivision(val);
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
		JTextField triggerLevel = new JTextField("0.0");
		offset.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String value = offset.getText().trim();
				try {
					double val = Double.parseDouble(value);
					osc.setOffset(val);
				} catch(NumberFormatException ex) {
					// nothing
				}
			}
		});
		triggerLevel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String value = triggerLevel.getText().trim();
				try {
					float val = Float.parseFloat(value);
					osc.setTriggerLevel(val);
					osc.trigger();
					offset.setText(Double.toString(osc.getOffset()));
				} catch(NumberFormatException ex) {
					// nothing
				}
			}
		});
		offsetPanel.add(BorderLayout.CENTER, offset);
		offsetPanel.add(BorderLayout.EAST, triggerLevel);

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
			osc.setSignal(signal);
			offset.setText(Double.toString(osc.getOffset()));
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
			osc.setSignal(signal);
			offset.setText(Double.toString(osc.getOffset()));
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

		JFrame frame = new JFrame("Oscilloscope");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(BorderLayout.CENTER, osc);
		frame.getContentPane().add(BorderLayout.SOUTH, south);
		frame.setVisible(true);
	}
}
