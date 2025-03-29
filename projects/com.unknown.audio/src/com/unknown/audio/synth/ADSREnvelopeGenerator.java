package com.unknown.audio.synth;

public class ADSREnvelopeGenerator extends EnvelopeGenerator {
	private final int sampleRate;
	private float sustain;

	private int segment = 0;
	private int sample = 0;
	private float value = 0;

	private int attackSamples;
	private int decaySamples;
	private int releaseSamples;

	private float releaseValue;

	public ADSREnvelopeGenerator(int sampleRate, float attack, float decay, float sustain, float release) {
		this.sampleRate = sampleRate;

		setADSR(attack, decay, sustain, release);

		segment = 3;
		sample = releaseSamples;
	}

	public void setADSR(float attack, float decay, float sustain, float release) {
		this.sustain = sustain;

		attackSamples = (int) (attack * sampleRate);
		decaySamples = (int) (decay * sampleRate);
		releaseSamples = (int) (release * sampleRate);
	}

	@Override
	public void on() {
		sample = (int) (value * attackSamples);
		segment = 0;
	}

	@Override
	public void off() {
		sample = 0;
		segment = 3;
		releaseValue = value;
	}

	@Override
	public float process() {
		switch(segment) {
		case 0: // attack
			if(sample >= attackSamples) {
				sample -= attackSamples;
				segment = 1;
				// fall through
			} else {
				value = sample / (float) attackSamples;
				break;
			}
		case 1: // decay
			if(sample >= decaySamples) {
				sample -= decaySamples;
				segment = 2;
				// fall through
			} else {
				value = sustain + (1 - (sample / (float) decaySamples)) * (1 - sustain);
				break;
			}
		case 2: // sustain
			value = sustain;
			break;
		case 3: // release
			if(sample >= releaseSamples) {
				value = 0;
			} else {
				value = releaseValue - (sample / (float) releaseSamples) * releaseValue;
			}
			break;
		}

		sample++;
		return value;
	}
}
