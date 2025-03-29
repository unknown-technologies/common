package com.unknown.audio;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.unknown.util.io.Endianess;
import com.unknown.util.ui.Oscilloscope;

public class AudioInOscilloscope {
	public static void main(String[] args) throws Exception {
		if(args.length != 0 && args.length != 1) {
			System.out.println("Usage: AudioInOscilloscope [port-name]");
			System.exit(1);
		}

		Oscilloscope osc = new Oscilloscope();
		osc.setTimeDivision(0.001);
		osc.setVoltageDivision(0.1);
		osc.setSampleRate(96000);
		osc.setTriggerRisingEdge(true);
		osc.setTriggerLevel(0);
		osc.setTriggerVisible(true);

		JTextField divX = new JTextField(Double.toString(osc.getTimeDivision()));
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

		JTextField divY = new JTextField(Double.toString(osc.getVoltageDivision()));
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

		JTextField triggerLevel = new JTextField(Double.toString(osc.getTriggerLevel()));
		triggerLevel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String value = triggerLevel.getText().trim();
				try {
					float val = Float.parseFloat(value);
					osc.setTriggerLevel(val);
					osc.trigger();
				} catch(NumberFormatException ex) {
					// nothing
				}
			}
		});

		JPanel south = new JPanel(new GridLayout(1, 3));
		south.add(divX);
		south.add(divY);
		south.add(triggerLevel);

		JFrame frame = new JFrame("Oscilloscope");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(BorderLayout.CENTER, osc);
		frame.getContentPane().add(BorderLayout.SOUTH, south);
		frame.setVisible(true);

		Thread t = new Thread(() -> {
			try {
				feed(osc, args.length > 0 ? args[0] : null);
			} catch(LineUnavailableException e) {
				e.printStackTrace();
			}
		});
		t.setName("Recording Thread");
		t.setDaemon(true);
		t.start();
	}

	private static void feed(Oscilloscope osc, String device) throws LineUnavailableException {
		Info[] mixers = AudioSystem.getMixerInfo();
		Info input = null;
		for(Info info : mixers) {
			Mixer mixer = AudioSystem.getMixer(info);
			if(mixer.getTargetLineInfo().length == 0) {
				continue;
			}
			System.out.println(info.getName() + ": " + info.getDescription());
			if(info.getName().equals(device)) {
				input = info;
			}
		}
		if(input == null) {
			System.out.println("Warning: input port not found");
		}

		// @formatter:off
		int channels = 1;
		int sampleRate = osc.getSampleRate();
		AudioFormat format = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,	// encoding
				sampleRate,				// sample rate
				16,					// bit/sample
				channels,				// channels
				2 * channels,
				sampleRate,
				true					// big-endian
		);
		// @formatter:on

		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		TargetDataLine line = null;
		if(input != null) {
			if(!AudioSystem.getMixer(input).isLineSupported(info)) {
				throw new LineUnavailableException("Line matching " + info + " not supported");
			}

			line = AudioSystem.getTargetDataLine(format, input);
		} else {
			if(!AudioSystem.isLineSupported(info)) {
				throw new LineUnavailableException("Line matching " + info + " not supported");
			}

			line = AudioSystem.getTargetDataLine(format);
		}

		int bufferSize = 8192;
		line.open(format, bufferSize);
		osc.setSampleRate((int) line.getFormat().getSampleRate());
		byte[] buf = new byte[bufferSize];
		float[] signal = new float[buf.length / 2];
		line.start();
		while(true) {
			int n = line.read(buf, 0, buf.length);
			for(int i = 0; i < n / 2; i++) {
				signal[i] = Endianess.get16bitBE(buf, 2 * i) / (float) Short.MAX_VALUE;
			}
			osc.setSignal(signal);
		}
	}
}
