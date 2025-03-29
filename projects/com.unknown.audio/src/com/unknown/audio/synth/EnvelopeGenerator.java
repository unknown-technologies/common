package com.unknown.audio.synth;

public abstract class EnvelopeGenerator {
	public abstract void on();

	public abstract void off();

	public abstract float process();
}
