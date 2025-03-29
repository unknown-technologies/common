package com.unknown.audio.test.analysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.audio.analysis.Autoloop;

public class AutoloopTest {
	@Test
	public void testEstimate1() {
		float[][] data = { { 0, 0, 0, 0, 0, 0, 0, 0 }, { 1, 1, 1, 1, 1, 1, 1, 1 } };
		float err = Autoloop.estimate(data, 0, 4, 4);
		assertEquals(0.0, err, 0.000001);
	}

	@Test
	public void testEstimate2() {
		float[][] data = { { 0, 1, 0, 1, 0, 1, 0, 1 }, { 1, 0, 0, 1, 1, 0, 0, 1 } };
		float err = Autoloop.estimate(data, 0, 4, 4);
		assertEquals(0.0, err, 0.000001);
	}

	@Test
	public void testEstimate3() {
		float[][] data = { { 0, 1, 0, 1, 0, 1, 0, 1 }, { 1, 0, 0, 1, 1, 0, 0, 1 } };
		float err = Autoloop.estimate(data, 1, 4, 4);
		assertEquals(6.0, err, 0.000001);
	}

	@Test
	public void testEstimate4() {
		float[][] data = { { 0, 1, 0, 1, 0, 1, 0, 1 }, { 1, 0, 0, 1, 0, 0, 1, 0 } };
		float err = Autoloop.estimate(data, 0, 4, 4);
		assertEquals(3.0, err, 0.000001);
	}

	@Test
	public void testEstimate5() {
		float[][] data = { { 0, 1, 0, 1, 0, 0, 0, 0 }, { 1, 0, 1, 0, 0, 0, 0, 0 } };
		float err = Autoloop.estimate(data, 0, 4, 4);
		assertEquals(4.0, err, 0.000001);
	}
}
