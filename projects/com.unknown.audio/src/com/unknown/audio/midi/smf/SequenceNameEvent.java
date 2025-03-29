package com.unknown.audio.midi.smf;

import java.io.IOException;

import com.unknown.util.io.WordInputStream;

public class SequenceNameEvent extends MetaStringEvent {
	public static final byte TYPE = 0x03;

	public SequenceNameEvent(long time, String text) {
		super(time, TYPE, text);
	}

	public SequenceNameEvent(long time, WordInputStream in) throws IOException {
		super(time, TYPE, in);
	}

	public String getName() {
		return getContent();
	}

	public void setName(String name) {
		setContent(name);
	}
}
