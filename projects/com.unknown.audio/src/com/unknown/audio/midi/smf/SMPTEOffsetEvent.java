package com.unknown.audio.midi.smf;

import java.io.IOException;

import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public class SMPTEOffsetEvent extends MetaEvent {
	public static final byte TYPE = 0x54;

	private byte hr;
	private byte mn;
	private byte se;
	private byte fr;
	private byte ff;

	public SMPTEOffsetEvent(long time) {
		super(time, TYPE);
	}

	public SMPTEOffsetEvent(long time, WordInputStream in) throws IOException {
		super(time, TYPE);

		byte len = in.read8bit();
		if(len != 5) {
			throw new IOException("Invalid length: " + Byte.toUnsignedInt(len));
		}

		hr = in.read8bit();
		mn = in.read8bit();
		se = in.read8bit();
		fr = in.read8bit();
		ff = in.read8bit();
	}

	@Override
	protected int getDataSize() {
		return 6;
	}

	@Override
	protected void writeContent(WordOutputStream out) throws IOException {
		out.write8bit((byte) 0x05);
		out.write8bit(hr);
		out.write8bit(mn);
		out.write8bit(se);
		out.write8bit(fr);
		out.write8bit(ff);
	}

}
