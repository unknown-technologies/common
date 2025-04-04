package com.unknown.audio.meta.riff;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.unknown.util.io.FourCC;
import com.unknown.util.io.LEInputStream;
import com.unknown.util.io.LEOutputStream;
import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public abstract class Riff extends Chunk {
	public static final int MAGIC = 0x46464952; // 'RIFF'

	private final int type;

	public Riff(int type) {
		super(MAGIC);
		this.type = type;
	}

	public final void write(OutputStream out) throws IOException {
		int size = size() + 4;
		WordOutputStream wout = new LEOutputStream(out);
		wout.write32bit(MAGIC);
		wout.write32bit(size);
		long start = wout.tell();
		wout.write32bit(type);
		writeData(wout);
		long end = wout.tell();
		if((int) (end - start) != size) {
			throw new IOException("invalid size of RIFF chunk: " + (end - start) + " vs " + size);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Riff> T read(InputStream in) throws IOException {
		WordInputStream win = new LEInputStream(in);
		int magic = win.read32bit();
		if(magic != MAGIC) {
			throw new IOException("not a RIFF file");
		}
		int size = win.read32bit();
		if(size < 4) {
			throw new IOException("invalid size of RIFF chunk: " + size);
		}
		int type = win.read32bit();
		Riff riff;
		switch(type) {
		case RiffWave.MAGIC:
			riff = new RiffWave();
			break;
		default:
			throw new IOException("unknown RIFF type " + FourCC.fourCC(Integer.reverseBytes(type)));
		}
		riff.readData(win, size - 4);
		if(riff.size() > (size - 4)) {
			// check if there is a missing padding byte
			if((riff.size() == (size - 4) + 1) && ((size & 1) == 1)) {
				// everything is fine
				return (T) riff;
			}
			throw new IOException("invalid size of RIFF chunk: " + riff.size() + " vs " + (size - 4));
		}
		return (T) riff;
	}
}
