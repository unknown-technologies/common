package com.unknown.math.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.math.MovingAverage;

public class MovingAverageTest {
	private final static double DELTA = 0.01;

	@Test
	public void testQuantiles() {
		MovingAverage ma = new MovingAverage(20);
		assertEquals(0, ma.getQuantile1(), DELTA);
		assertEquals(0, ma.getMedian(), DELTA);
		assertEquals(0, ma.getQuantile3(), DELTA);
		ma.record(1);
		assertEquals(1, ma.getQuantile1(), DELTA);
		assertEquals(1, ma.getMedian(), DELTA);
		assertEquals(1, ma.getQuantile3(), DELTA);
		ma.record(2);
		assertEquals(1, ma.getQuantile1(), DELTA);
		assertEquals(1.5, ma.getMedian(), DELTA);
		assertEquals(2, ma.getQuantile3(), DELTA);
		ma.record(25);
		assertEquals(1.5, ma.getQuantile1(), DELTA);
		assertEquals(2, ma.getMedian(), DELTA);
		assertEquals(13.5, ma.getQuantile3(), DELTA);
	}

	@Test
	public void testQuantilesFive() {
		MovingAverage ma = new MovingAverage(20);
		ma.record(4);
		ma.record(5);
		ma.record(6);
		ma.record(7);
		ma.record(8);
		assertEquals(5, ma.getQuantile1(), DELTA);
		assertEquals(6, ma.getMedian(), DELTA);
		assertEquals(7, ma.getQuantile3(), DELTA);
	}

	@Test
	public void testQuantilesFour() {
		MovingAverage ma = new MovingAverage(20);
		ma.record(4);
		ma.record(5);
		ma.record(7);
		ma.record(8);
		assertEquals(4.5, ma.getQuantile1(), DELTA);
		assertEquals(6, ma.getMedian(), DELTA);
		assertEquals(7.5, ma.getQuantile3(), DELTA);
	}
}
