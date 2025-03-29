package com.unknown.posix.vfs;

import java.util.Arrays;
import java.util.Date;

import com.unknown.posix.api.Errno;
import com.unknown.posix.api.PosixException;

public class TmpfsFileData {
	byte[] data;
	Date atime;
	Date mtime;
	Date ctime;

	long uid;
	long gid;
	long permissions;

	int nlink = 0;

	public TmpfsFileData(long uid, long gid, long permissions) {
		this.uid = uid;
		this.gid = gid;
		this.permissions = permissions;

		atime = new Date();
		mtime = atime;
		ctime = atime;
		data = new byte[0];
	}

	public void setContent(byte[] data) {
		mtime = new Date();
		this.data = data;
	}

	public byte[] getContent() {
		return data;
	}

	int read(int pos, byte[] buf, int offset, int length) {
		int len = length;
		if(pos + length > data.length) {
			len = data.length - pos;
		}
		if(len < 0) {
			return 0;
		}
		System.arraycopy(data, pos, buf, offset, len);
		return len;
	}

	void append(byte[] buf, int offset, int length) {
		mtime = new Date();
		int pos = data.length;
		data = Arrays.copyOf(data, data.length + length);
		System.arraycopy(buf, offset, data, pos, length);
	}

	void insert(int pos, byte[] buf, int offset, int length) {
		mtime = new Date();
		byte[] oldData = data;
		data = Arrays.copyOf(data, data.length + length);
		System.arraycopy(buf, offset, data, pos, length);
		System.arraycopy(oldData, pos, data, pos + length, oldData.length - pos);
	}

	void truncate(int length) throws PosixException {
		if(length < 0) {
			throw new PosixException(Errno.EINVAL);
		}
		this.data = Arrays.copyOf(data, length);
	}

}
