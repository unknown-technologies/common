package com.unknown.audio.synth;

public abstract class VoiceAllocator {
	public static final int NONE = -1;

	public abstract int noteOn(int note);

	public abstract int noteOff(int note);
}
