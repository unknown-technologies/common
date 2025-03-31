package com.unknown.util.io.tar;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class TarInputStream implements Closeable {
	private InputStream in;

	public TarInputStream(InputStream in) {
		this.in = in;
	}

	public TarEntry next() throws IOException {
		return next(false);
	}

	public TarEntry next(boolean metadataOnly) throws IOException {
		TarEntry entry = new TarEntry(in, metadataOnly);
		if(entry.isEOF()) {
			return null;
		} else {
			return entry;
		}
	}

	@Override
	public void close() throws IOException {
		in.close();
	}
}
