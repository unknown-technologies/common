package com.unknown.audio.xv.srx;

import com.unknown.audio.xv.Scrambler;

public class SRXScrambler extends Scrambler {
	@Override
	public int scrambleAddress(int addr) {
		// @formatter:off
		return    (addr & 0x00000002) << 3
			| (addr & 0x00000010) >>> 3
			| (addr & 0x00000020) << 8
			| (addr & 0x00000040) << 1
			| (addr & 0x00000080) << 5
			| (addr & 0x00000100) >>> 3
			| (addr & 0x00000200) << 1
			| (addr & 0x00000400) << 6
			| (addr & 0x00000800) >>> 2
			| (addr & 0x00001000) >>> 6
			| (addr & 0x00002000) >>> 5
			| (addr & 0x00008000) << 2
			| (addr & 0x00010000) >>> 5
			| (addr & 0x00020000) >>> 2
			| (addr & 0xFFFC400D);
		// @formatter:on
	}

	@Override
	public short scrambleData(short word) {
		// @formatter:off
		return (short) (
			  (word & 0x0001) << 2
			| (word & 0x0002) >>> 1
			| (word & 0x0004) << 2
			| (word & 0x0008) << 2
			| (word & 0x0010) << 3
			| (word & 0x0020) << 1
			| (word & 0x0040) >>> 3
			| (word & 0x0080) >>> 6
			| (word & 0x0100) << 2
			| (word & 0x0200) >>> 1
			| (word & 0x0400) << 2
			| (word & 0x0800) << 2
			| (word & 0x1000) << 3
			| (word & 0x2000) << 1
			| (word & 0x4000) >>> 3
			| (word & 0x8000) >>> 6);
		// @formatter:on
	}

	@Override
	public short descrambleData(short word) {
		// @formatter:off
		return (short) (
			  (word & 0x0002) << 6
			| (word & 0x0008) << 3
			| (word & 0x0040) >>> 1
			| (word & 0x0080) >>> 3
			| (word & 0x0020) >>> 2
			| (word & 0x0010) >>> 2
			| (word & 0x0001) << 1
			| (word & 0x0004) >>> 2
			| (word & 0x0200) << 6
			| (word & 0x0800) << 3
			| (word & 0x4000) >>> 1
			| (word & 0x8000) >>> 3
			| (word & 0x2000) >>> 2
			| (word & 0x1000) >>> 2
			| (word & 0x0100) << 1
			| (word & 0x0400) >>> 2);
		// @formatter:on
	}
}
