package com.unknown.util.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NibbleOutputStream {
	private ByteArrayOutputStream out;
	private boolean lower = false;
	private byte last = 0;

	public NibbleOutputStream() {
		out = new ByteArrayOutputStream();
	}

	public void write4bit(byte data) {
		if(lower) {
			last |= (data & 0x0F);
			out.write(last);
			lower = false;
		} else {
			last = (byte) (data << 4);
			lower = true;
		}
	}

	public void write8bit(byte data) {
		if(lower)
			throw new IllegalStateException("not aligned");
		out.write(data);
	}

	public byte[] getBytes() {
		if(lower)
			throw new IllegalStateException("not aligned");
		try {
			out.flush();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}
}
