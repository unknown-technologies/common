package com.unknown.audio.midi.smf;

import java.io.IOException;

import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public class KeySignatureEvent extends MetaEvent {
	public static final byte TYPE = 0x59;

	private byte sf;
	private byte mi;

	public KeySignatureEvent(long time) {
		super(time, TYPE);
	}

	public KeySignatureEvent(long time, WordInputStream in) throws IOException {
		super(time, TYPE);

		byte len = in.read8bit();
		if(len != 2) {
			throw new IOException("Invalid length: " + Byte.toUnsignedInt(len));
		}

		sf = in.read8bit();
		mi = in.read8bit();
	}

	@Override
	protected int getDataSize() {
		return 3;
	}

	@Override
	protected void writeContent(WordOutputStream out) throws IOException {
		out.write8bit((byte) 0x02);
		out.write8bit(sf);
		out.write8bit(mi);
	}
}
