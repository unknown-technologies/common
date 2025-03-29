package com.unknown.audio.emu.ei;

/*
 * Format:
 * u8 lower[0xE000];
 * u8 upper[0xE000];
 */
public class EIBank {
	private final EISound lower;
	private final EISound upper;

	public EIBank(byte[] bank) {
		lower = new EISound(bank, 0, false);
		upper = new EISound(bank, 16 * 3584, true);
	}

	public EISound getLower() {
		return lower;
	}

	public EISound getUpper() {
		return upper;
	}
}
