package com.unknown.net.shownet;

public class Scrambler {
	private static final int[] KEY = { 0xB92FF3CC, 0x75B36AF8, 0x5DEE4E5A, 0xF11090AB };
	private final int[] lfsr;

	public Scrambler() {
		lfsr = new int[KEY.length];
		init();
	}

	public void init() {
		for(int i = 0; i < lfsr.length; i++) {
			lfsr[i] = KEY[i];
		}
	}

	public int next() {
		int tmp = lfsr[0] ^ (lfsr[0] << 11);
		lfsr[0] = lfsr[1];
		lfsr[1] = lfsr[2];
		lfsr[2] = lfsr[3];
		lfsr[3] ^= (lfsr[3] >>> 19) ^ tmp ^ (tmp >>> 8);
		return lfsr[3];
	}

	public int scramble(int word) {
		return word ^ next();
	}
}
