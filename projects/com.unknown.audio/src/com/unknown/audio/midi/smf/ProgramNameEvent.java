package com.unknown.audio.midi.smf;

import java.io.IOException;

import com.unknown.util.io.WordInputStream;

public class ProgramNameEvent extends MetaStringEvent {
	public static final byte TYPE = 0x08;

	public ProgramNameEvent(long time, String text) {
		super(time, TYPE, text);
	}

	public ProgramNameEvent(long time, WordInputStream in) throws IOException {
		super(time, TYPE, in);
	}

	public String getProgramName() {
		return getContent();
	}

	public void setProgramName(String name) {
		setContent(name);
	}
}
