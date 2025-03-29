package com.unknown.audio.synth.osc;

public class SawOscillator extends Oscillator {
	private double phi;
	private double inc;
	private final float[][] table;

	private float blend;
	private double f0;
	private float sawBlend;

	private int tableID;
	private int otherTableID;
	private static final int LENGTH = 1024;

	private final float flimit;
	private final float fwidth;

	public SawOscillator(int sampleRate) {
		super(sampleRate);
		int tableCount = (int) Math.round(Math.log(LENGTH) / Math.log(2) - 1);
		flimit = sampleRate / 96.0f;
		fwidth = flimit;
		table = new float[tableCount][LENGTH];
		for(int t = 0; t < table.length; t++) {
			double f = (int) Math.pow(2.0, t);
			int K = (int) (LENGTH / (2.0 * f));
			for(int i = 0; i < LENGTH; i++) {
				double sum = 0;
				for(int k = 1; k < K; k++) {
					sum -= 1.0 / k * Math.sin(2 * Math.PI * f * k * i / LENGTH);
				}
				table[t][i] = (float) sum;
			}
		}
	}

	@Override
	public void setPitch(double f) {
		if(f == 0) {
			return;
		} else if(f < 0) {
			throw new IllegalArgumentException("invalid frequency");
		}

		int lastTableID = tableID;

		double ftable = Math.log(f / sampleRate * LENGTH) / Math.log(2);
		tableID = (int) ftable;
		if(tableID < 0) {
			tableID = 0;
			otherTableID = 0;
			blend = 1.0f;
		} else if(tableID == ftable) {
			otherTableID = tableID;
			blend = 1.0f;
		} else if(tableID + 1 < table.length) {
			otherTableID = tableID;
			tableID++;
			blend = (float) (ftable % 1.0);
		} else {
			otherTableID = tableID;
			blend = 1.0f;
		}

		f0 = Math.pow(2.0, tableID);
		inc = (f / sampleRate) * LENGTH / f0;
		assert inc > 0;

		if(lastTableID > tableID) {
			phi *= 2.0;
		} else if(lastTableID < tableID) {
			phi /= 2.0;
		}

		if(f > flimit) {
			sawBlend = 1.0f - (float) ((f - flimit) / fwidth);
			if(sawBlend < 0.0) {
				sawBlend = 0.0f;
			}
		} else {
			sawBlend = 1.0f;
		}
	}

	@Override
	public void setParameter(int id, int value) {
		// nothing
	}

	@Override
	public float process() {
		assert phi >= 0;
		assert inc >= 0;
		assert tableID >= 0 && tableID < table.length : String.format("tableID=%d, table.length=%d", tableID,
				table.length);

		// access current table
		phi = (phi + inc) % LENGTH;
		assert phi >= 0;
		float v1 = table[tableID][(int) phi];
		float v2 = table[tableID][((int) phi + 1) % LENGTH];
		float mix = (float) (phi % 1.0);
		float current = v1 * (1.0f - mix) + v2 * mix;

		// perfect saw tooth
		double sawPhi = (phi * f0 / LENGTH) % 1.0;
		float saw = (float) (sawPhi * 2.0 - 1.0);

		// access other table
		if(otherTableID != tableID) {
			double ophi = (2 * phi) % LENGTH;
			float ov1 = table[otherTableID][(int) ophi];
			float ov2 = table[otherTableID][((int) ophi + 1) % LENGTH];
			float omix = (float) (ophi % 1.0);
			float other = ov1 * (1.0f - omix) + ov2 * omix;

			float wave = current * blend + other * (1.0f - blend);
			return saw * sawBlend + wave * (1.0f - sawBlend);
		} else {
			return saw * sawBlend + current * (1.0f - sawBlend);
		}
	}
}
