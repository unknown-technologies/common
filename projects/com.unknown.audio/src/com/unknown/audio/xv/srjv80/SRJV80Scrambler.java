package com.unknown.audio.xv.srjv80;

import com.unknown.audio.xv.Scrambler;

public class SRJV80Scrambler extends Scrambler {
	@Override
	public int scrambleAddress(int addr) {
		// @formatter:off
		return    (addr & 0x00000001) << 2
			| (addr & 0x00000002) >>> 1
			| (addr & 0x00000004) << 1
			| (addr & 0x00000008) << 1
			| (addr & 0x00000010) >>> 3
			| (addr & 0x00000020) << 4
			| (addr & 0x00000040) << 7
			| (addr & 0x00000080) << 3
			| (addr & 0x00000100) << 10
			| (addr & 0x00000200) << 8
			| (addr & 0x00000400) >>> 4
			| (addr & 0x00000800) << 4
			| (addr & 0x00001000) >>> 1
			| (addr & 0x00002000) << 3
			| (addr & 0x00004000) >>> 6
			| (addr & 0x00008000) >>> 10
			| (addr & 0x00010000) >>> 4
			| (addr & 0x00020000) >>> 10
			| (addr & 0x00040000) >>> 4
			| (addr & 0xFFF80000);
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
}
