package com.unknown.platform.serial;

import java.io.Closeable;
import java.io.IOException;

import com.unknown.platform.Platform;

public class RS232 implements Closeable {
	public static final int FORMAT_8N1 = 0;
	public static final int FORMAT_8N1_HWFLOW = 1;

	private long fd;

	private volatile boolean closed = false;

	static {
		Platform.loadNativeLibrary();
	}

	public RS232(String filename, int baudrate, int format) throws IOException {
		closed = false;
		fd = open(filename);
		try {
			configure(fd, baudrate, format);
		} catch(IOException e) {
			close(fd);
			throw e;
		}
	}

	@Override
	public void close() throws IOException {
		if(!closed) {
			closed = true;
			close(fd);
		}
	}

	public int read(byte[] data, int offset, int length) throws IOException {
		if(closed) {
			throw new IOException("serial interface is closed");
		}
		if(data == null) {
			throw new NullPointerException();
		}
		if(offset < 0) {
			throw new IllegalArgumentException("invalid offset");
		}
		if(length < 0) {
			throw new IllegalArgumentException("invalid length");
		}
		if(offset + length > data.length) {
			throw new IllegalArgumentException("invalid offset/length");
		}

		try {
			return read(fd, data, offset, length);
		} catch(IOException e) {
			if(closed) {
				return 0;
			} else {
				throw e;
			}
		}
	}

	public int write(byte[] data, int offset, int length) throws IOException {
		if(closed) {
			throw new IOException("serial interface is closed");
		}
		if(data == null) {
			throw new NullPointerException();
		}
		if(offset < 0) {
			throw new IllegalArgumentException("invalid offset");
		}
		if(length < 0) {
			throw new IllegalArgumentException("invalid length");
		}
		if(offset + length > data.length) {
			throw new IllegalArgumentException("invalid offset/length");
		}

		return write(fd, data, offset, length);
	}

	private native static long open(String filename) throws IOException;

	private native static void close(long fd) throws IOException;

	private native static boolean configure(long fd, int baudrate, int format) throws IOException;

	private native static int read(long fd, byte[] data, int offset, int length) throws IOException;

	private native static int write(long fd, byte[] data, int offset, int length) throws IOException;
}
