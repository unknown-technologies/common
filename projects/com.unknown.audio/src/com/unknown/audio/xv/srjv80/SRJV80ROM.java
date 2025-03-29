package com.unknown.audio.xv.srjv80;

import com.unknown.audio.xv.ROM;

public class SRJV80ROM extends ROM {
	public SRJV80ROM(int size) {
		super(size, new SRJV80Scrambler());
	}

	public SRJV80ROM(byte[] rom) {
		super(rom.length, new SRJV80Scrambler());
		setRawData(rom);
	}
}
