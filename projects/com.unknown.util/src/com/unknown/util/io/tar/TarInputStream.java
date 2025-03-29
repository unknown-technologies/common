package com.unknown.util.io.tar;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TarInputStream implements AutoCloseable {
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

	public static void main(String[] args) throws Throwable {
		if(args.length != 1) {
			System.out.println("usage: TarInputStream archive.tar");
			return;
		}
		try(InputStream in = new FileInputStream(args[0])) {
			TarInputStream tar = new TarInputStream(in);
			TarEntry entry;
			while((entry = tar.next()) != null) {
				System.out.println(entry);
				// System.out.println("===============");
				// System.out.println(new String(entry.getData()));
				// System.out.println("===============");
			}
		}
	}
}
