package com.unknown.audio.meta.riff;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.unknown.util.io.FourCC;
import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;
import com.unknown.util.log.Trace;

public abstract class Chunk {
	public static final int JUNK = 0x4b4e554a; // 'JUNK'

	private static final Logger log = Trace.create(Chunk.class);

	private final int id;

	private static final Map<Integer, Supplier<? extends Chunk>> CHUNKS = new HashMap<>();

	static {
		CHUNKS.put(WaveFormatChunk.MAGIC, () -> new WaveFormatChunk());
		CHUNKS.put(InstrumentChunk.MAGIC, () -> new InstrumentChunk());
		CHUNKS.put(DataChunk.MAGIC, () -> new DataChunk());
		CHUNKS.put(SampleChunk.MAGIC, () -> new SampleChunk());
	}

	protected Chunk(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public abstract int size();

	public final int getChunkSize() {
		return size() + 8;
	}

	protected String getChunkName() {
		return FourCC.fourCC(Integer.reverseBytes(id));
	}

	protected final void write(WordOutputStream out) throws IOException {
		out.write32bit(id);
		out.write32bit(size());
		writeData(out);
		if((size() & 1) != 0) {
			out.write8bit((byte) 0);
		}
	}

	protected abstract void writeData(WordOutputStream out) throws IOException;

	protected final void readChunk(WordInputStream in) throws IOException {
		int size = in.read32bit();
		long start = in.tell();
		readData(in, size);
		try {
			if((size & 1) != 0) {
				in.read8bit();
				size++;
			}
			long end = in.tell();
			if((int) (end - start) != size) {
				throw new IOException("error parsing chunk: invalid size of " + getChunkName() +
						" chunk: " + (end - start) + " vs " + size);
			}
		} catch(EOFException e) {
			// swallow
		}
	}

	protected abstract void readData(WordInputStream in, int size) throws IOException;

	protected static final <T extends Chunk> T read(WordInputStream in) throws IOException {
		int type = in.read32bit();
		@SuppressWarnings("unchecked")
		Supplier<T> clazz = (Supplier<T>) CHUNKS.get(type);
		if(clazz == null) {
			int size = in.read32bit();
			if(type != JUNK) {
				log.info("unknown chunk type " + FourCC.fourCC(Integer.reverseBytes(type)) + " [" +
						size + " bytes]");
			}
			if((size & 1) != 0) {
				size++;
			}
			in.skip(Integer.toUnsignedLong(size));
			return null;
		}

		T chunk = clazz.get();
		chunk.readChunk(in);
		return chunk;
	}
}
