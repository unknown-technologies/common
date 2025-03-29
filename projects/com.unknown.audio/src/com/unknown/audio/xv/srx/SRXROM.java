package com.unknown.audio.xv.srx;

import com.unknown.audio.xv.ROM;

public class SRXROM extends ROM {
	public SRXROM(int size) {
		super(size, new SRXScrambler());
	}

	public SRXROM(byte[] rom) {
		super(rom.length, new SRXScrambler());
		setRawData(rom);
	}
}
