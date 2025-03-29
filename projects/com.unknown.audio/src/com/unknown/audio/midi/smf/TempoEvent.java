package com.unknown.audio.midi.smf;

import java.io.IOException;

import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public class TempoEvent extends MetaEvent {
	public static final byte TYPE = 0x51;

	private int microTempo;

	public TempoEvent(long time) {
		super(time, TYPE);
	}

	public TempoEvent(long time, int microTempo) {
		this(time);
		this.microTempo = microTempo;
	}

	public TempoEvent(long time, WordInputStream in) throws IOException {
		super(time, TYPE);

		int data = in.read32bit();
		if(((data >> 24) & 0xFF) != 0x03) {
			throw new IOException("Invalid length: " + ((data >> 24) & 0xFF));
		}
		microTempo = data & 0x00FFFFFF;
	}

	public static int getMicroTempo(double bpm) {
		return (int) Math.round(60_000_000 / bpm);
	}

	public static double getBPM(int microTempo) {
		return 60_000_000 / (double) microTempo;
	}

	public double getBPM() {
		return 60_000_000 / (double) microTempo;
	}

	public int getMicroTempo() {
		return microTempo;
	}

	public void setMicroTempo(int microTempo) {
		this.microTempo = microTempo;
	}

	@Override
	protected int getDataSize() {
		return 4;
	}

	@Override
	protected void writeContent(WordOutputStream out) throws IOException {
		out.write32bit(0x03000000 | (microTempo & 0x00FFFFFF));
	}
}
