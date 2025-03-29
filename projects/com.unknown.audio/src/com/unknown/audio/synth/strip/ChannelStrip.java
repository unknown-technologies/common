package com.unknown.audio.synth.strip;

import com.unknown.audio.synth.fx.Effect;

public class ChannelStrip {
	private float volume = 1.0f;
	private Effect[] effects;
	private Panner panner;

	public ChannelStrip(Panner panner) {
		this.panner = panner;
	}

	public void setEffects(Effect[] effects) {
		this.effects = effects;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public void process(float[] out, float[] input) {
		// process panner
		float[] data = panner.process(input);
		assert data.length == out.length;

		// process effects
		if(effects != null) {
			for(Effect effect : effects) {
				effect.process(data, data);
			}
		}

		// mix into bus
		for(int i = 0; i < out.length; i++) {
			out[i] += data[i] * volume;
		}
	}
}
