package com.unknown.util.io;

import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream extends OutputStream {
	private OutputStream out;

	private byte word;
	private int idx;

	public BitOutputStream(OutputStream out) {
		this.out = out;
		word = 0;
		idx = 0;
	}

	@Override
	public void write(int data) throws IOException {
		flush();
		out.write(data);
	}

	private void flushBits() throws IOException {
		if(idx != 0) {
			out.write(word);
			idx = 0;
			word = 0;
		}
	}

	public void writeBit(boolean bit) throws IOException {
		if(bit) {
			word |= 1 << (7 - idx);
		}
		idx++;
		if(idx == 8) {
			flushBits();
		}
	}

	@Override
	public void flush() throws IOException {
		flushBits();
		out.flush();
	}

	@Override
	public void close() throws IOException {
		if(out != null) {
			flush();
		}
		out.close();
		out = null;
	}
}
