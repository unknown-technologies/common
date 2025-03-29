package com.unknown.audio.synth;

import java.util.function.Supplier;

import com.unknown.audio.synth.osc.Oscillator;

public class SynthVoice2 extends Voice {
	private final Oscillator osc1;
	private final Oscillator osc2;
	private final EnvelopeGenerator eg;
	private double detune;
	private double detuneRange = 0.05;
	private float volume;
	private double f;

	public SynthVoice2(int sampleRate, Supplier<? extends Oscillator> osc,
			Supplier<? extends EnvelopeGenerator> eg) {
		super(sampleRate);
		this.eg = eg.get();
		osc1 = osc.get();
		osc2 = osc.get();
		setParameter(1, 0);
	}

	@Override
	public void on() {
		eg.on();
		osc1.reset();
		osc2.reset();
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
		this.f = f;
		setFrequencies(f);
	}

	private void setFrequencies(double f) {
		double detune1 = tuning;
		double detune2 = tuning + detune * detuneRange;
		osc1.setPitch(f + (f * detune1));
		osc2.setPitch(f + (f * detune2));
	}

	@Override
	public void setParameter(int id, int value) {
		if(id == 1) {
			detune = (value + 1) / 128.0;
			setFrequencies(f);
		} else {
			osc1.setParameter(id, value);
			osc2.setParameter(id, value);
		}
	}

	@Override
	public float process() {
		float val1 = osc1.process();
		float val2 = osc2.process();
		float env = eg.process();
		if(env > 0) {
			return (float) ((val1 + val2) * volume * 0.5 * env);
		} else {
			return 0;
		}
	}
}
