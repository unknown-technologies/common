package com.unknown.posix.api;

import com.unknown.posix.Messages;
import com.unknown.util.exception.BaseException;

public class PosixException extends BaseException {
	private static final long serialVersionUID = 4755386173579073897L;

	public final int errno;

	public PosixException(int errno) {
		super(Messages.errno(errno));
		this.errno = errno;
	}

	public int getErrno() {
		return errno;
	}
}
