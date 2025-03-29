package com.unknown.audio.dsp;

public class Differentiator {
	private double lastSample = 0;

	public double process(double sample) {
		double diff = sample - lastSample;
		lastSample = sample;
		return diff;
	}

	public static float[] process(float[] samples) {
		float min = Float.MAX_VALUE;
		float max = -Float.MAX_VALUE;
		float[] result = new float[samples.length];
		float lastSample = 0;
		for(int i = 0; i < samples.length; i++) {
			float sample = samples[i];
			result[i] = sample - lastSample;
			lastSample = sample;
			if(result[i] > max) {
				max = result[i];
			} else if(result[i] < min) {
				min = result[i];
			}
		}
		float scale = 1.0f / Math.max(-min, max);
		for(int i = 0; i < result.length; i++) {
			result[i] *= scale;
		}
		return result;
	}

	public static float[] processEnhanced(float[] samples) {
		float min = Float.MAX_VALUE;
		float max = -Float.MAX_VALUE;
		float[] result = new float[samples.length];
		float lastSample = 0;
		for(int i = 0; i < samples.length; i++) {
			float sample = samples[i];
			float diff = sample - lastSample;
			result[i] = Math.signum(diff) * diff * diff;
			lastSample = sample;
			if(result[i] > max) {
				max = result[i];
			} else if(result[i] < min) {
				min = result[i];
			}
		}
		float scale = 1.0f / Math.max(-min, max);
		for(int i = 0; i < result.length; i++) {
			result[i] *= scale;
		}
		return result;
	}
}
