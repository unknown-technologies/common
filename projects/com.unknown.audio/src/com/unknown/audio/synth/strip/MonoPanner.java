package com.unknown.audio.synth.strip;

public class MonoPanner extends Panner {
	private float pan = 0;

	public float getPanning() {
		return pan;
	}

	public void setPanning(float pan) {
		this.pan = pan;
	}

	@Override
	public float[] process(float[] input) {
		assert input.length == 1;
		float[] out = new float[2];
		float volLeft = 1.0f - (pan + 1.0f) / 2.0f;
		float volRight = (pan + 1.0f) / 2.0f;
		out[0] = input[0] * volLeft;
		out[1] = input[0] * volRight;
		return out;
	}
}
