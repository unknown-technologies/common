package com.unknown.math.test;

public class Determinante {
	public static double det2x2(double[][] mat) {
		return mat[0][0] * mat[1][1] -
				mat[0][1] * mat[1][0];
	}

	public static double[][] mat_part1l(double[][] mat,
			int x, int y) {
		double[][] ret = new double[mat.length - 1][mat[0].length - 1];
		int __x = 0;
		int __y;
		for(int _x = 0; _x < mat[0].length; _x++) {
			if(_x == x)
				continue;
			__y = 0;
			for(int _y = 0; _y < mat.length; _y++) {
				if(_y != y) {
					ret[__y][__x] = mat[_y][_x];
					__y++;
				}
			}
			__x++;
		}
		return ret;
	}

	public static double det(double[][] mat) {
		if(mat.length == 0)
			throw new IllegalArgumentException(
					"matrix has no line");
		if(mat[0].length == 0)
			throw new IllegalArgumentException(
					"matrix has no column");
		if(mat.length != mat[0].length)
			throw new IllegalArgumentException(
					"matrix is not square");
		if(mat.length == 2)
			return det2x2(mat);
		double mat_det = 0;
		for(int x = 0; x < mat[0].length; x++) {
			if(x % 2 == 0)
				mat_det += mat[0][x] * det(mat_part1l(mat, x, 0));
			else
				mat_det -= mat[0][x] * det(mat_part1l(mat, x, 0));
		}
		return mat_det;
	}
}
