package com.unknown.audio.io;

import java.io.IOException;

public class BufferDataFile implements DataFile {
	private byte[] data;
	private int pos;

	public BufferDataFile(byte[] data) {
		this.data = data;
	}

	public void close() throws IOException {
	}

	public long length() throws IOException {
		return data.length;
	}

	public void seek(long offset) throws IOException {
		assert offset == (int) offset;
		if(offset < 0 || offset >= length()) {
			throw new IllegalArgumentException();
		}
		this.pos = (int) offset;
	}

	public int read() throws IOException {
		if(pos == length()) {
			return -1;
		}
		return Byte.toUnsignedInt(data[pos++]);
	}

	public int read(byte[] buf) throws IOException {
		for(int i = 0; i < buf.length; i++) {
			int x = read();
			if(x == -1) {
				return i;
			} else {
				buf[i] = (byte) x;
			}
		}
		return buf.length;
	}

}
