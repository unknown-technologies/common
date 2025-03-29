package com.unknown.audio.io;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessDataFile implements DataFile {
	private final RandomAccessFile file;

	public RandomAccessDataFile(String filename, String mode) throws IOException {
		file = new RandomAccessFile(filename, mode);
	}

	@Override
	public void close() throws IOException {
		file.close();
	}

	@Override
	public long length() throws IOException {
		return file.length();
	}

	@Override
	public void seek(long pos) throws IOException {
		file.seek(pos);
	}

	@Override
	public int read() throws IOException {
		return file.read();
	}

	@Override
	public int read(byte[] buf) throws IOException {
		return file.read(buf);
	}
}
