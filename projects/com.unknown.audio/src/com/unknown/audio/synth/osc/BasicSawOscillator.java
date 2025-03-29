package com.unknown.audio.synth.osc;

public class BasicSawOscillator extends Oscillator {
	private double phi;
	private double inc;

	public BasicSawOscillator(int sampleRate) {
		super(sampleRate);
	}

	@Override
	public void setPitch(double f) {
		inc = f / sampleRate;
	}

	@Override
	public void setParameter(int id, int value) {
		// nothing
	}

	@Override
	public float process() {
		phi = (phi + inc) % 1.0;
		return (float) (phi * 2.0 - 1.0);
	}
}
