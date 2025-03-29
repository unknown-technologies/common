package com.unknown.util.io;

public class NibbleInputStream {
	private byte[] data;
	private int nibble;

	public NibbleInputStream(byte[] data) {
		this.data = data;
		nibble = 0;
	}

	public byte read4bit() {
		int offset = nibble / 2;
		byte b = data[offset];
		try {
			if(nibble % 2 == 0)
				return (byte) ((b >> 4) & 0x0F);
			else
				return (byte) (b & 0x0F);
		} finally {
			nibble++;
		}
	}

	public byte read8bit() {
		if(nibble % 2 != 0)
			throw new IllegalStateException("not aligned");
		try {
			return data[nibble / 2];
		} finally {
			nibble += 2;
		}
	}
}
