package com.unknown.audio.test.dsp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.audio.dsp.FFT;

public class FFTTest {
	@Test
	public void test() {
		final int SZ = 1024 * 1024;
		double[] r1 = new double[SZ];
		double[] i1 = new double[SZ];
		double[] r2 = new double[SZ];
		double[] i2 = new double[SZ];
		for(int j = 0; j < SZ; j++) {
			r1[j] = r2[j] = Math.random();
			i1[j] = i2[j] = Math.random();
		}
		FFT.fft(r2, i2, FFT.FORWARD);
		FFT.fft(r2, i2, FFT.REVERSE);
		double err = 0;
		for(int j = 0; j < SZ; j++) {
			err += Math.abs(r1[j] - r2[j] / SZ) + Math.abs(i1[j] - i2[j] / SZ);
		}
		assertEquals(0, err, 0.00001);
	}
}
