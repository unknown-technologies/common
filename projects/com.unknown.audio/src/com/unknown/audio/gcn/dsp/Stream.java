package com.unknown.audio.gcn.dsp;

import java.io.Closeable;
import java.io.IOException;

public interface Stream extends Closeable {
	public void reset() throws IOException;

	public boolean hasMoreData();

	public byte[] decode() throws IOException;

	public int getChannels();

	public long getSampleRate();

	public void close() throws IOException;
}
