package com.unknown.audio.synth;

import java.util.function.Supplier;

import com.unknown.audio.synth.osc.Oscillator;

public class SynthVoice1 extends Voice {
	private final Oscillator osc;
	private final EnvelopeGenerator eg;
	private float volume;

	public SynthVoice1(int sampleRate, Supplier<? extends Oscillator> osc,
			Supplier<? extends EnvelopeGenerator> eg) {
		super(sampleRate);
		this.osc = osc.get();
		this.eg = eg.get();
	}

	@Override
	public void on() {
		eg.on();
		osc.reset();
	}

	@Override
	public void off() {
		eg.off();
	}

	@Override
	public void setVelocity(int velocity) {
		volume = velocity / 127.0f;
	}

	@Override
	public void setPitch(double f) {
		osc.setPitch(f + (f * tuning));
	}

	@Override
	public void setParameter(int id, int value) {
		osc.setParameter(id, value);
	}

	@Override
	public float process() {
		float val = osc.process();
		float env = eg.process();
		if(env > 0) {
			return val * volume * env;
		} else {
			return 0;
		}
	}
}
