package com.unknown.audio.midi.smf;

import java.io.IOException;

import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public class ProprietaryEvent extends MetaEvent {
	public static final byte TYPE = 0x7F;

	private byte[] data;

	public ProprietaryEvent(long time) {
		super(time, TYPE);
	}

	public ProprietaryEvent(long time, WordInputStream in) throws IOException {
		super(time, TYPE);

		int len = VariableInt.read(in);
		data = in.read(len);
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	@Override
	protected int getDataSize() {
		return VariableInt.length(data.length) + data.length;
	}

	@Override
	protected void writeContent(WordOutputStream out) throws IOException {
		VariableInt.write(data.length, out);
		out.write(data);
	}
}
