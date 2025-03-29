package com.unknown.audio.synth.osc;

public class WavetableOscillator extends Oscillator {
	private double phi;
	private double inc;
	private float[] table;

	public WavetableOscillator(int sampleRate, float[] table) {
		super(sampleRate);
		this.table = table;
	}

	@Override
	public void setPitch(double f) {
		inc = (f / sampleRate) * table.length;
	}

	@Override
	public void setParameter(int id, int value) {
		// nothing
	}

	public void setWavetable(float[] wavetable) {
		this.table = wavetable;
	}

	@Override
	public float process() {
		phi = (phi + inc) % table.length;
		int i = (int) phi;
		int i1 = (i + 1) % table.length;
		double t = phi % 1;
		double s1 = table[i];
		double s2 = table[i1];
		return (float) (s1 + (s2 - s1) * t);
	}
}
