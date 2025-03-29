package com.unknown.util.io.tar;

import java.io.IOException;
import java.io.InputStream;

public class BlockHeader {
	public String name;
	public long mode;
	public long uid;
	public long gid;
	public long size;
	public long mtime;
	public long chksum;
	public int typeflag;
	public String linkname;
	public String magic;
	public String version;
	public String uname;
	public String gname;
	public long devmajor;
	public long devminor;
	public String prefix;

	private static String readString(InputStream in, int length) throws IOException {
		byte[] buf = new byte[length];
		int n = in.read(buf);
		if(n != buf.length) {
			throw new IOException("Unexpected end");
		}
		int len = n;
		for(int i = 0; i < n; i++) {
			if(buf[i] == 0) {
				len = i;
				break;
			}
		}
		return new String(buf, 0, len).trim();
	}

	private static long readNumber(InputStream in, int length) throws IOException {
		String str = readString(in, length);
		return Long.parseUnsignedLong(str, 8);
	}

	private static long readNumber(InputStream in, int length, long fallback) throws IOException {
		String str = readString(in, length);
		if(str.length() == 0) {
			return fallback;
		} else {
			return Long.parseUnsignedLong(str, 8);
		}
	}

	private static void skip(InputStream in, int length) throws IOException {
		byte[] buf = new byte[length];
		int n = in.read(buf);
		if(n != length) {
			throw new IOException("Unexpected end");
		}
	}

	private static int readType(InputStream in) throws IOException {
		int ch = in.read();
		if(ch == -1) {
			throw new IOException("Unexpected end");
		}
		if(ch == 0) {
			return '0';
		} else {
			return ch;
		}
	}

	public void read(InputStream in) throws IOException {
		name = readString(in, 100);
		mode = readNumber(in, 8);
		uid = readNumber(in, 8);
		gid = readNumber(in, 8);
		size = readNumber(in, 12);
		mtime = readNumber(in, 12);
		chksum = readNumber(in, 8);
		typeflag = readType(in);
		linkname = readString(in, 100);
		magic = readString(in, 6);
		version = readString(in, 2);
		uname = readString(in, 32);
		gname = readString(in, 32);
		devmajor = readNumber(in, 8, 0);
		devminor = readNumber(in, 8, 0);
		prefix = readString(in, 155);
		skip(in, 12);
	}

	@Override
	public String toString() {
		return String.format(
				"TARHeader[name='%s',prefix='%s',type=%s,uid=%s(%s),gid=%s(%s),size=%s,linkname='%s']",
				name, prefix, typeflag, uname, uid, gname, gid, size, linkname);
	}
}
