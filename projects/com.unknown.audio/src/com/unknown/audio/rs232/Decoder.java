package com.unknown.audio.rs232;

import java.io.IOException;
import java.io.InputStream;

import com.unknown.audio.dsp.Differentiator;
import com.unknown.audio.meta.riff.RiffWave;

public class Decoder {
	private final int sampleRate;
	private final int baudRate;
	private final int samplesPerBit;
	private final int waitBeforeSample;
	private final float threshold;

	private boolean idle = true;
	private boolean previous = true;
	private int currentBit = 0;
	private int rx = 0;

	private boolean lastLevel = true;
	private int wait = -1;
	private int samplecnt;

	private Receiver receiver = new Receiver() {
		@Override
		public void receive(int data) {
			if(data < 0x7F) {
				System.out.print((char) data);
			}
		}

		@Override
		public void error(int data) {
			// nothing
		}
	};

	public static interface Receiver {
		void receive(int data);

		void error(int data);
	}

	public Decoder(int sampleRate, int baudRate) {
		this(sampleRate, baudRate, 0.1f);
	}

	public Decoder(int sampleRate, int baudRate, float threshold) {
		this.sampleRate = sampleRate;
		this.baudRate = baudRate;
		this.samplesPerBit = (int) Math.round(sampleRate / (double) baudRate);
		this.waitBeforeSample = samplesPerBit / 2;
		this.threshold = threshold;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public int getBaudRate() {
		return baudRate;
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public Receiver getReceiver() {
		return receiver;
	}

	public float[] integrate(float[] diff) {
		float[] result = new float[diff.length];
		boolean state = true; // idle
		int maxstatic = 10 * samplesPerBit;
		int cnt = 0;
		float value = 0.0f;
		for(int i = 0; i < diff.length; i++) {
			value += diff[i];
			if(value >= threshold) {
				state = true;
				cnt = 0;
			} else if(value <= -threshold) {
				state = false;
				cnt = 0;
			} else {
				cnt++;
			}
			value /= 2.0f;
			if(cnt > maxstatic) {
				result[i] = 1.0f; // idle
			} else {
				result[i] = state ? 1.0f : -1.0f;
			}
		}
		return result;
	}

	public float[] normalize(float[] samples) {
		float[] diff = Differentiator.processEnhanced(samples);
		return integrate(diff);
	}

	public void process(float sample) {
		if(Math.abs(sample) > 0.1) {
			boolean level = sample > 0;
			if(idle && level != lastLevel) { // edge detected
				lastLevel = level;
				wait = waitBeforeSample;
				if(idle) {
					samplecnt = 0;
				}
			} else {
				lastLevel = level;
			}
			if(wait > 0) {
				wait--;
			} else if(wait == 0) {
				process(level);
				wait = samplesPerBit - 1;
			}
		}
		samplecnt++;
		if(!idle && samplecnt > 11 * samplesPerBit) {
			idle = true;
			previous = true;
			lastLevel = true;
		}
	}

	private void process(boolean bit) {
		if(idle) {
			// start bit
			if(previous && !bit) {
				idle = false;
				currentBit = 0;
				rx = 0;
			}
		} else {
			if(currentBit == 8) {
				if(!bit) {
					// Framing error: missing stop bit
					if(receiver != null) {
						receiver.error(rx);
					}
				} else if(receiver != null) {
					receiver.receive(rx);
				}
				idle = true;
			} else {
				rx |= bit ? (1 << currentBit) : 0;
				currentBit++;
			}
		}
		previous = bit;
	}

	public void decode(float[] samples) {
		float[] rs232 = normalize(samples);
		for(float sample : rs232) {
			process(sample);
		}
	}

	public static void decode(InputStream in, int ch, int baud) throws IOException {
		decode(in, ch, baud, false, null);
	}

	public static void decode(InputStream in, int ch, int baud, boolean inverse) throws IOException {
		decode(in, ch, baud, inverse, null);
	}

	public static void decode(InputStream in, int ch, int baud, boolean inverse, Receiver receiver)
			throws IOException {
		RiffWave riff = RiffWave.read(in);
		float[][] samples = riff.getFloatSamples();
		float[] channel = samples[ch];
		if(inverse) {
			for(int i = 0; i < channel.length; i++) {
				channel[i] = -channel[i];
			}
		}
		Decoder decoder = new Decoder(riff.getSampleRate(), baud);
		if(receiver != null) {
			decoder.setReceiver(receiver);
		}
		decoder.decode(channel);
	}
}
