package com.unknown.audio.midi.smf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.unknown.util.io.BEInputStream;
import com.unknown.util.io.BEOutputStream;
import com.unknown.util.io.FourCC;
import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public abstract class Chunk {
	private int id;

	protected Chunk(int id) {
		this.id = id;
	}

	protected abstract int size();

	protected abstract void write(WordOutputStream out) throws IOException;

	protected abstract void read(WordInputStream in, int size) throws IOException;

	public void write(OutputStream out) throws IOException {
		int sz = size();
		WordOutputStream w = new BEOutputStream(out);
		w.write32bit(id);
		w.write32bit(sz);
		long pos = w.tell();
		write(w);
		long pos2 = w.tell();
		long diff = pos2 - pos;
		if(diff != sz) {
			throw new IOException("invalid size of chunk " + FourCC.ascii(id) + ": expected " + sz +
					", was " + diff);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Chunk> T read(InputStream in) throws IOException {
		WordInputStream w = new BEInputStream(in);
		int id = w.read32bit();

		Chunk c = null;
		switch(id) {
		case MThd.ID:
			c = new MThd();
			break;
		case MTrk.ID:
			c = new MTrk();
			break;
		}

		if(c == null) {
			throw new IOException("Unknown chunk " + FourCC.ascii(id));
		}

		int size = w.read32bit();
		long pos = w.tell();
		c.read(w, size);
		long pos2 = w.tell();
		long diff = pos2 - pos;
		if(diff != size) {
			throw new IOException("invalid size of chunk " + FourCC.ascii(id));
		}

		return (T) c;
	}
}
