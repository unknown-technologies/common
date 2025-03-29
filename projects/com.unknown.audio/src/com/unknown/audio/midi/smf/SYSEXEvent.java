package com.unknown.audio.midi.smf;

import java.io.IOException;

import com.unknown.util.io.WordOutputStream;

public class SYSEXEvent extends Event {
	private byte[] data;

	public SYSEXEvent(long time) {
		super(time);
		data = new byte[0];
	}

	public SYSEXEvent(long time, byte[] data) {
		super(time);

		if(data[0] != (byte) 0xF0) {
			throw new IllegalArgumentException("Invalid status byte");
		}

		if(data[data.length - 1] != (byte) 0xF7) {
			throw new IllegalArgumentException("Invalid end byte");
		}

		this.data = data;
	}

	@Override
	protected int getSize() {
		return data.length;
	}

	public byte[] getData() {
		return data;
	}

	@Override
	protected void writeData(WordOutputStream out) throws IOException {
		if(data[0] != (byte) 0xF0) {
			throw new IOException("Invalid status byte");
		}

		if(data[data.length - 1] != (byte) 0xF7) {
			throw new IOException("Invalid end byte");
		}

		out.write(data);
	}
}
