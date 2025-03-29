package com.unknown.math;

public class Logarithm {
	public static int log2(int x) {
		if(x == 0) {
			return 0;
		}
		return 31 - Integer.numberOfLeadingZeros(x);
	}

	public static int log2(long x) {
		if(x == 0) {
			return 0;
		}
		return 63 - Long.numberOfLeadingZeros(x);
	}
}
