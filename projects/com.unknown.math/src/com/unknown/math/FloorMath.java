package com.unknown.math;

public class FloorMath {
	// round towards floor
	public static int mod(int x, int y) {
		if(x < 0) {
			int r = -x % y;
			if(r == 0) {
				return r;
			}
			return y - r;
		}
		return x % y;
	}

	public static int div(int x, int y) {
		int result = x / y;
		if(x < 0 && y > 0 && x % y != 0) {
			result--;
		}
		return result;
	}
}
