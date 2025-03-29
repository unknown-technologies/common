package com.unknown.vm.power.isa;

public class Rotate {
	public static long bit(int i) {
		return 1L << (63 - i);
	}

	public static long mask(int mstart, int mstop) {
		long m = 0;
		if(mstart == mstop) {
			return bit(mstart);
		}
		if(mstart < mstop) {
			for(int i = mstart; i <= mstop; i++) {
				m |= bit(i);
			}
		} else {
			for(int i = mstart; i <= 63; i++) {
				m |= bit(i);
			}
			for(int i = 0; i <= mstop; i++) {
				m |= bit(i);
			}
		}
		return m;
	}

	public static int rotl(int x, int n) {
		int shift = n & 0x1f;
		return (x << shift) | (x >>> (32 - shift));
	}
}
