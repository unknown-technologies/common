package com.unknown.audio.synth.osc;

public class WavetableSawOscillator extends Oscillator {
	private double phi;
	private double inc;
	private float[][] table;

	private float blend;

	private int tableID;
	private int otherTableID;
	private static final int LENGTH = 512;

	public WavetableSawOscillator(int sampleRate) {
		super(sampleRate);
		int tableCount = (int) Math.round(Math.log(LENGTH) / Math.log(2) - 1);
		System.out.println(tableCount + " tables");
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

		double f0 = Math.pow(2.0, tableID);
		inc = (f / sampleRate) * LENGTH / f0;

		if(lastTableID > tableID) {
			phi *= 2.0;
		} else if(lastTableID < tableID) {
			phi /= 2.0;
		}
	}

	@Override
	public void setParameter(int id, int value) {
		// nothing
	}

	@Override
	public float process() {
		// access current table
		phi = (phi + inc) % LENGTH;
		float v1 = table[tableID][(int) phi];
		float v2 = table[tableID][((int) phi + 1) % LENGTH];
		float mix = (float) (phi % 1.0);
		float current = v1 * (1.0f - mix) + v2 * mix;

		// access other table
		if(otherTableID != tableID) {
			double ophi = (2 * phi) % LENGTH;
			float ov1 = table[otherTableID][(int) ophi];
			float ov2 = table[otherTableID][((int) ophi + 1) % LENGTH];
			float omix = (float) (ophi % 1.0);
			float other = ov1 * (1.0f - omix) + ov2 * omix;

			return current * blend + other * (1.0f - blend);
		} else {
			return current;
		}
	}
}
