package com.unknown.audio.synth.fx;

public abstract class Effect {
	protected final int sampleRate;
	protected final int channels;
	protected float mix;

	protected Effect(int sampleRate, int channels) {
		this.sampleRate = sampleRate;
		this.channels = channels;
		mix = 0.5f;
	}

	public void setMix(float mix) {
		this.mix = mix;
	}

	protected abstract float[] process(float[] input);

	public final void process(float[] output, float[] input) {
		float[] fx = process(input);
		for(int i = 0; i < channels; i++) {
			output[i] = fx[i] * mix + (1.0f - mix) * input[i];
		}
	}
}
