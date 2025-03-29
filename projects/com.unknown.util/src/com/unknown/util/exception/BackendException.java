package com.unknown.util.exception;

public class BackendException extends BaseException {
	private static final long serialVersionUID = -3139561569628222483L;

	public static ExceptionId DEFAULT_ID = Messages.UNKNOWN_BACKEND;

	public BackendException() {
		this(DEFAULT_ID);
	}

	public BackendException(String s) {
		this(DEFAULT_ID, s);
	}

	public BackendException(Throwable t) {
		this(DEFAULT_ID, t);
	}

	public BackendException(String s, Throwable t) {
		this(DEFAULT_ID, s, t);
	}

	public BackendException(ExceptionId id) {
		super(id);
	}

	public BackendException(ExceptionId id, String s) {
		super(id, s);
	}

	public BackendException(ExceptionId id, Throwable throwable) {
		super(id, throwable);
	}

	public BackendException(ExceptionId id, String s, Throwable throwable) {
		super(id, s, throwable);
	}
}
