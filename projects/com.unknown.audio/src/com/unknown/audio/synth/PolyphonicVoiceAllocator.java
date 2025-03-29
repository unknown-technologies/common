package com.unknown.audio.synth;

public class PolyphonicVoiceAllocator extends VoiceAllocator {
	private static final int OFF = -1;

	private final int voices;

	private int allocated[];
	private int active;

	private int next;

	public PolyphonicVoiceAllocator(int voices) {
		this.voices = voices;
		allocated = new int[voices];
		for(int i = 0; i < allocated.length; i++) {
			allocated[i] = OFF;
		}
		active = 0;
		next = 0;
	}

	@Override
	public int noteOn(int note) {
		if(active >= voices) {
			return NONE;
		}

		for(int i = 0, n = next; i < voices; i++, n = (n + 1) % voices) {
			if(allocated[n] == OFF) {
				allocated[n] = note;
				active++;
				next = (n + 1) % voices;
				return n;
			}
		}

		throw new AssertionError("this should be unreachable");
	}

	@Override
	public int noteOff(int note) {
		for(int i = 0; i < voices; i++) {
			if(allocated[i] == note) {
				allocated[i] = OFF;
				active--;
				return i;
			}
		}

		return NONE;
	}
}
