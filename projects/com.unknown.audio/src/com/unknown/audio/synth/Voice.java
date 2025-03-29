package com.unknown.audio.synth;

public abstract class Voice {
	protected final int sampleRate;
	protected float tuning = 0.0f;

	protected Voice(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public void setTuning(float tuning) {
		this.tuning = tuning;
	}

	public abstract void on();

	public abstract void off();

	public abstract void setVelocity(int velocity);

	public abstract void setPitch(double f);

	public abstract void setParameter(int id, int value);

	public abstract float process();
}
