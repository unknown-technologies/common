package com.unknown.math.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.math.Matrix;

public class MatrixTest {
	public static final double EPS = 1e-5;

	@Test
	public void min1() {
		double[][] mtx = { { 1, 7, 2 }, { 5, 9, 3 }, { 7, 9, 1 } };
		Matrix ref = new Matrix(Determinante.mat_part1l(mtx, 0, 0));
		Matrix min = new Matrix(mtx).minor(0, 0);
		assertEquals(ref, min);
	}

	@Test
	public void det1() {
		double[][] mtx = { { 1, 7 }, { 5, 9 } };
		double ref = Determinante.det(mtx);
		double det = new Matrix(mtx).det();
		assertEquals(-26, ref, EPS);
		assertEquals(ref, det, EPS);
	}

	@Test
	public void det2() {
		double[][] mtx = { { 1, 7, 2 }, { 5, 9, 3 }, { 7, 9, 1 } };
		double ref = Determinante.det(mtx);
		double det = new Matrix(mtx).det();
		assertEquals(58, ref, EPS);
		assertEquals(ref, det, EPS);
	}
}
