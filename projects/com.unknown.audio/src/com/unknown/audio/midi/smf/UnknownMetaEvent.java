package com.unknown.audio.midi.smf;

import java.io.IOException;

import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public class UnknownMetaEvent extends MetaEvent {
	private byte[] data;

	public UnknownMetaEvent(long time, byte type) {
		super(time, type);
	}

	public UnknownMetaEvent(long time, byte type, WordInputStream in) throws IOException {
		super(time, type);
		int len = VariableInt.read(in);
		data = new byte[len];
		in.read(len);
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
