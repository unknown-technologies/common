package com.unknown.util.io.tar;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TarEntry {
	public static final int TYPE_REGULAR = '0';
	public static final int TYPE_LINK = '1';
	public static final int TYPE_SYMLINK = '2';
	public static final int TYPE_CHARDEV = '3';
	public static final int TYPE_BLOCKDEV = '4';
	public static final int TYPE_DIRECTORY = '5';
	public static final int TYPE_FIFO = '6';
	public static final int TYPE_PERF = '7';

	private BlockHeader header;
	private byte[] data;

	private static boolean zero(byte[] buf) {
		for(byte b : buf) {
			if(b != 0) {
				return false;
			}
		}
		return true;
	}

	public TarEntry(InputStream in) throws IOException {
		this(in, false);
	}

	public TarEntry(InputStream in, boolean metadataOnly) throws IOException {
		byte[] block = new byte[512];
		int n = readall(in, block);

		if(n == -1) {
			header = null;
			data = null;
			return;
		}

		if(n != block.length) {
			throw new IOException("Unexpected end");
		}

		if(zero(block)) {
			header = null;
			data = null;
		} else {
			try(InputStream hdr = new ByteArrayInputStream(block)) {
				header = new BlockHeader();
				header.read(hdr);
			}

			if(metadataOnly) {
				in.skip(header.size);
				long remainder = 512 - (header.size % 512);
				if(remainder != 512) {
					byte[] buf = new byte[(int) remainder];
					n = readall(in, buf);
					if(n != buf.length) {
						throw new IOException("Unexpected end");
					}
				}
			} else {
				assert header.size == (int) header.size;
				data = new byte[(int) header.size];
				n = readall(in, data);
				if(n != data.length) {
					throw new IOException("Unexpected end");
				}
				long remainder = 512 - (header.size % 512);
				if(remainder != 512) {
					byte[] buf = new byte[(int) remainder];
					n = readall(in, buf);
					if(n != buf.length) {
						throw new IOException("Unexpected end");
					}
				}
			}
		}
	}

	private static int readall(InputStream in, byte[] buf) throws IOException {
		int read = 0;
		int n;
		while((n = in.read(buf, read, buf.length - read)) != -1 && read < buf.length) {
			read += n;
		}
		return read;
	}

	public boolean isEOF() {
		return header == null && data == null;
	}

	private void checkNotEOF() {
		if(isEOF()) {
			throw new IllegalStateException();
		}
	}

	public String getName() {
		checkNotEOF();
		return header.name;
	}

	public byte[] getData() {
		checkNotEOF();
		return data;
	}

	public long getSize() {
		checkNotEOF();
		return header.size;
	}

	public String getOwnerName() {
		checkNotEOF();
		return header.uname;
	}

	public String getGroupName() {
		checkNotEOF();
		return header.gname;
	}

	public long getOwnerId() {
		checkNotEOF();
		return header.uid;
	}

	public long getGroupId() {
		checkNotEOF();
		return header.gid;
	}

	public long getMode() {
		return header.mode;
	}

	public String getLinkname() {
		return header.linkname;
	}

	public int getType() {
		checkNotEOF();
		return header.typeflag;
	}

	@Override
	public String toString() {
		return "TarEntry[" + (isEOF() ? "EOF" : header.toString()) + "]";
	}
}
