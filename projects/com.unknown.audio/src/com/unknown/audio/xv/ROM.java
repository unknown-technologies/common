package com.unknown.audio.xv;

public abstract class ROM {
	private final byte[] data;
	private final Scrambler scrambler;

	protected ROM(int size, Scrambler scrambler) {
		data = new byte[size];
		this.scrambler = scrambler;
	}

	public int getSize() {
		return data.length;
	}

	public byte getByte(int addr) {
		int a = scrambler.scrambleAddress(addr);
		return (byte) scrambler.descrambleData(data[a]);
	}

	public void setByte(int addr, byte word) {
		int a = scrambler.scrambleAddress(addr);
		data[a] = (byte) scrambler.scrambleData(word);
	}

	protected byte[] getHeader() {
		byte[] header = new byte[32];
		System.arraycopy(data, 0, header, 0, header.length);
		return header;
	}

	protected void setHeader(byte[] header) {
		System.arraycopy(header, 0, data, 0, header.length);
	}

	public byte[] getRawData() {
		byte[] raw = new byte[data.length];
		System.arraycopy(data, 0, raw, 0, data.length);
		return raw;
	}

	public void setRawData(byte[] raw) {
		if(raw.length != data.length) {
			throw new IllegalArgumentException("Invalid size");
		}
		System.arraycopy(raw, 0, data, 0, raw.length);
	}

	@Override
	public String toString() {
		byte[] header = getHeader();
		char[] string = new char[header.length];
		for(int i = 0; i < header.length; i++) {
			string[i] = (char) (header[i] & 0xFF);
		}
		return new String(header);
	}
}
