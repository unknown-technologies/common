package com.unknown.audio.midi.smf;

import java.io.IOException;

import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public class EndOfTrackEvent extends MetaEvent {
	public static final byte TYPE = 0x2F;

	public EndOfTrackEvent(long time) {
		super(time, TYPE);
	}

	public EndOfTrackEvent(long time, WordInputStream in) throws IOException {
		super(time, TYPE);
		byte len = in.read8bit();
		if(len != 0) {
			throw new IOException("invalid length of end of track marker");
		}
	}

	@Override
	protected int getDataSize() {
		return 1;
	}

	@Override
	protected void writeContent(WordOutputStream out) throws IOException {
		out.write8bit((byte) 0);
	}
}
