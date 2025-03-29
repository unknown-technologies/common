package com.unknown.math.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.math.EM;
import com.unknown.math.Gaussian;

public class EMTest {
	public static final double EPS = 0.001;

	@Test
	public void test1() {
		double[] data = { 5, 5, 5, 5, 5 };
		Gaussian[] gaussians = EM.em(data, 2);
		assertEquals(5, gaussians[0].mu(), EPS);
		assertEquals(5, gaussians[1].mu(), EPS);
	}

	@Test
	public void test2() {
		double[] data = { 4, 5, 6, 5, 5 };
		Gaussian[] gaussians = EM.em(data, 2);
		assertEquals(5, gaussians[0].mu(), EPS);
		assertEquals(5, gaussians[1].mu(), EPS);
	}
}
