package com.unknown.audio.synth.osc;

public abstract class Oscillator {
	protected final int sampleRate;

	protected Oscillator(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public abstract void setPitch(double f);

	public abstract void setParameter(int id, int value);

	public abstract float process();

	public void reset() {
		// nothing
	}
}
