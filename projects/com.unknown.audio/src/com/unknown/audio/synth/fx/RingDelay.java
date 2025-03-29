package com.unknown.audio.synth.fx;

public class RingDelay extends Effect {
	private final float[][] history;
	private final float[] times;
	private final float maxTime;
	private final int[] read;
	private final int[] write;
	private final float[] feedback;

	public RingDelay(int sampleRate, int channels, float[] times) {
		super(sampleRate, channels);
		this.times = new float[channels];
		maxTime = max(times);
		history = new float[channels][(int) (sampleRate * maxTime)];
		read = new int[channels];
		write = new int[channels];
		feedback = new float[channels];

		for(int i = 0; i < channels; i++) {
			feedback[i] = 0.3f;
		}

		setTimes(times);
	}

	private static float max(float[] values) {
		float max = 0;
		for(float val : values) {
			if(val > max) {
				max = val;
			}
		}
		return max;
	}

	public void setTimes(float[] times) {
		if(times.length != channels) {
			throw new IllegalArgumentException("invalid number of delay times");
		}
		float max = max(times);
		if(max > maxTime) {
			throw new IllegalArgumentException(
					"invalid max time: " + max + " [sample buffer only holds " + maxTime + "]");
		}
		for(int i = 0; i < channels; i++) {
			this.times[i] = times[i];
			int samples = (int) (times[i] * sampleRate);
			read[i] = write[i] - samples;
			if(read[i] < 0) {
				read[i] += history[i].length;
			}
		}
	}

	public void setFeedback(float[] feedback) {
		if(feedback.length != channels) {
			throw new IllegalArgumentException("invalid number of delay times");
		}
		for(int i = 0; i < channels; i++) {
			this.feedback[i] = feedback[i];
		}
	}

	@Override
	protected float[] process(float[] input) {
		float[] out = new float[channels];
		for(int i = 0; i < channels; i++) {
			out[i] = history[i][read[i]++];
			read[i] %= history[i].length;
		}
		for(int i = 0; i < channels; i++) {
			int wr = (i + 1) % channels;
			history[i][write[i]++] = input[wr] * (1.0f - feedback[i]) + feedback[i] * out[wr];
			write[i] %= history[i].length;
		}
		return out;
	}
}
