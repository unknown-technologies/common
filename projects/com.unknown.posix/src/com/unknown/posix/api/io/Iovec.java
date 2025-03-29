package com.unknown.posix.api.io;

import com.unknown.posix.api.PosixPointer;

public class Iovec {
	public final PosixPointer iov_base;
	public final int iov_len;

	public Iovec(PosixPointer iov_base, int iov_len) {
		this.iov_base = iov_base;
		this.iov_len = iov_len;
	}
}
