package com.unknown.audio.midi.smf;

import java.io.IOException;

import com.unknown.util.io.WordInputStream;

public class LyricEvent extends MetaStringEvent {
	public static final byte TYPE = 0x05;

	public LyricEvent(long time, String text) {
		super(time, TYPE, text);
	}

	public LyricEvent(long time, WordInputStream in) throws IOException {
		super(time, TYPE, in);
	}

	public String getText() {
		return getContent();
	}

	public void setText(String name) {
		setContent(name);
	}
}
