package com.unknown.posix.api;

public class ProcessExitException extends RuntimeException {
	private static final long serialVersionUID = -7483493093129513658L;
	private int code;

	public ProcessExitException(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
