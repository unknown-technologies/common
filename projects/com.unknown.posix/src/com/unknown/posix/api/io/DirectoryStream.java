package com.unknown.posix.api.io;

import com.unknown.posix.api.Errno;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.PosixPointer;

public abstract class DirectoryStream extends Stream {
	@Override
	public int read(byte[] buf, int offset, int length) throws PosixException {
		throw new PosixException(Errno.EISDIR);
	}

	@Override
	public int write(byte[] buf, int offset, int length) throws PosixException {
		throw new PosixException(Errno.EISDIR);
	}

	@Override
	public int pread(byte[] buf, int offset, int length, long fileOffset) throws PosixException {
		throw new PosixException(Errno.EISDIR);
	}

	@Override
	public int pwrite(byte[] buf, int offset, int length, long fileOffset) throws PosixException {
		throw new PosixException(Errno.EISDIR);
	}

	@Override
	public void ftruncate(long size) throws PosixException {
		throw new PosixException(Errno.EINVAL);
	}

	public abstract long getdents(PosixPointer ptr, long count, int size) throws PosixException;
}
