package com.unknown.audio.midi.smf;

import java.io.IOException;

import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public class MThd extends Chunk {
	public static final int ID = 0x4D546864; // "MThd"

	private short format;
	private short tracks;
	private short division;

	public MThd() {
		super(ID);
	}

	public int getFormat() {
		return Short.toUnsignedInt(format);
	}

	public int getTracks() {
		return Short.toUnsignedInt(tracks);
	}

	void setTracks(short tracks) {
		this.tracks = tracks;

		if(tracks < 2) {
			format = 0;
		} else {
			format = 1;
		}
	}

	public int getDivision() {
		return Short.toUnsignedInt(division);
	}

	public int getPPQ() {
		if(division < 0) {
			throw new IllegalStateException("division is not PPQ");
		}
		return division;
	}

	public void setPPQ(int ppqn) {
		division = (short) ppqn;

		if(division < 0) {
			throw new IllegalArgumentException("invalid PPQN");
		}
	}

	public void setSMPTE(int frame, int resolution) {
		division = (short) ((-frame << 8) | (resolution & 0xFF));
	}

	@Override
	public int size() {
		return 6;
	}

	@Override
	protected void read(WordInputStream in, int size) throws IOException {
		if(size != 6) {
			throw new IOException("invalid size " + size + " for chunk MThd");
		}

		format = in.read16bit();
		tracks = in.read16bit();
		division = in.read16bit();
	}

	@Override
	protected void write(WordOutputStream out) throws IOException {
		out.write16bit(format);
		out.write16bit(tracks);
		out.write16bit(division);
	}

	@Override
	public String toString() {
		String div;
		if(division < 0) {
			int frames = -(division >> 8);
			int resolution = division & 0xFF;
			div = "[" + frames + ":" + resolution + "]";
		} else {
			div = division + "ppq";
		}

		return "MThd[format=" + format + ",tracks=" + tracks + ",division=" + div + "]";
	}
}
