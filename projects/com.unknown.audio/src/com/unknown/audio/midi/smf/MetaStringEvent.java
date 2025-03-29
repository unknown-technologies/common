package com.unknown.audio.midi.smf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public abstract class MetaStringEvent extends MetaEvent {
	private String text;

	protected MetaStringEvent(long time, byte type) {
		super(time, type);
	}

	protected MetaStringEvent(long time, byte type, String text) {
		super(time, type);
		this.text = text;
	}

	protected MetaStringEvent(long time, byte type, WordInputStream in) throws IOException {
		super(time, type);
		int len = VariableInt.read(in);
		byte[] data = new byte[len];
		in.read(data);
		text = new String(data, StandardCharsets.UTF_8);
	}

	protected String getContent() {
		return text;
	}

	protected void setContent(String text) {
		this.text = text;
	}

	@Override
	protected int getDataSize() {
		byte[] data = text.getBytes(StandardCharsets.UTF_8);
		return VariableInt.length(data.length) + data.length;
	}

	@Override
	protected void writeContent(WordOutputStream out) throws IOException {
		byte[] data = text.getBytes(StandardCharsets.UTF_8);
		VariableInt.write(data.length, out);
		out.write(data);
	}
}
