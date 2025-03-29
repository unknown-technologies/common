package com.unknown.audio.test.analysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.audio.analysis.SpectrogramHelper;

public class SpectrogramHelperTest {
	@Test
	public void test1() {
		double[] spectrum = { 0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.1, 0.11, 0.12, 0.13, 0.14,
				0.15, 0.16 };
		double bin0 = 7;
		double bin1 = 7;
		double value = SpectrogramHelper.getValue(spectrum, bin0, bin1, false);
		assertEquals(0.08, value, 1e-12);
	}

	@Test
	public void test2() {
		double[] spectrum = { 0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.1, 0.11, 0.12, 0.13, 0.14,
				0.15, 0.16 };
		double bin0 = 15;
		double bin1 = 15;
		double value = SpectrogramHelper.getValue(spectrum, bin0, bin1, false);
		assertEquals(0.16, value, 1e-12);
	}

	@Test
	public void test3() {
		double[] spectrum = { 0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.1, 0.11, 0.12, 0.13, 0.14,
				0.15, 0.16 };
		double bin0 = 0;
		double bin1 = 0;
		double value = SpectrogramHelper.getValue(spectrum, bin0, bin1, false);
		assertEquals(0.01, value, 1e-12);
	}

	@Test
	public void test4() {
		double[] spectrum = { 0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.1, 0.11, 0.12, 0.13, 0.14,
				0.15, 0.16 };
		double bin0 = 5;
		double bin1 = 5;
		double value = SpectrogramHelper.getValue(spectrum, bin0, bin1, false);
		assertEquals(0.06, value, 1e-12);
	}

	@Test
	public void test5() {
		double[] spectrum = { 0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.1, 0.11, 0.12, 0.13, 0.14,
				0.15, 0.16 };
		double bin0 = 5;
		double bin1 = 6;
		double value = SpectrogramHelper.getValue(spectrum, bin0, bin1, false);
		assertEquals(0.07, value, 1e-12);
	}
}
