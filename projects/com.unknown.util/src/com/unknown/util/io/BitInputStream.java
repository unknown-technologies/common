package com.unknown.util.io;

public class BitInputStream {
	private byte[] data;
	private int offset;

	public BitInputStream(byte[] data) {
		this.data = data;
		offset = 0;
	}

	public boolean readBit() {
		int i = offset / 8;
		int off = offset % 8;
		offset++;
		byte b = data[i];
		return ((b >> (7 - off)) & 0x01) != 0;
	}

	private int b() {
		return readBit() ? 1 : 0;
	}

	public byte read4bit() {
		byte b = (byte) ((b() << 3) | (b() << 2) | (b() << 1) | b());
		return b;
	}

	public byte read8bit() {
		byte b = (byte) ((read4bit() << 4) | read4bit());
		return b;
	}
}
