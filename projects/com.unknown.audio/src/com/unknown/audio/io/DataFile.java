package com.unknown.audio.io;

import java.io.Closeable;
import java.io.IOException;

public interface DataFile extends Closeable {
	void close() throws IOException;

	long length() throws IOException;

	void seek(long pos) throws IOException;

	int read() throws IOException;

	int read(byte[] buf) throws IOException;
}
