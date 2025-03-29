package com.unknown.audio.midi.smf;

import java.io.IOException;

import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public class TimeSignatureEvent extends MetaEvent {
	public static final byte TYPE = 0x58;

	private byte numerator;
	private byte denominator;
	private byte clocks;
	private byte quarter;

	public TimeSignatureEvent(long time) {
		super(time, TYPE);
	}

	public TimeSignatureEvent(long time, WordInputStream in) throws IOException {
		super(time, TYPE);

		byte len = in.read8bit();
		if(len != 0x04) {
			throw new IOException("Invalid length: " + Byte.toUnsignedInt(len));
		}

		numerator = in.read8bit();
		denominator = in.read8bit();
		clocks = in.read8bit();
		quarter = in.read8bit();
	}

	public byte getNumerator() {
		return numerator;
	}

	public byte getDenominator() {
		return denominator;
	}

	public byte getClocks() {
		return clocks;
	}

	public byte getQuarter() {
		return quarter;
	}

	@Override
	protected int getDataSize() {
		return 5;
	}

	@Override
	protected void writeContent(WordOutputStream out) throws IOException {
		out.write8bit((byte) 0x04);
		out.write8bit(numerator);
		out.write8bit(denominator);
		out.write8bit(clocks);
		out.write8bit(quarter);
	}
}
