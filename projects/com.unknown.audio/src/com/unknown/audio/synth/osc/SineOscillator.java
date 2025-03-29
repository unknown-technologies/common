package com.unknown.audio.synth.osc;

public class SineOscillator extends Oscillator {
	private double phi;
	private double inc;

	public SineOscillator(int sampleRate) {
		super(sampleRate);
	}

	@Override
	public void setPitch(double f) {
		inc = 2 * Math.PI * f / sampleRate;
	}

	@Override
	public void setParameter(int id, int value) {
		// nothing
	}

	@Override
	public float process() {
		phi = Math.IEEEremainder(phi + inc, 2 * Math.PI);
		return (float) Math.sin(phi);
	}
}
