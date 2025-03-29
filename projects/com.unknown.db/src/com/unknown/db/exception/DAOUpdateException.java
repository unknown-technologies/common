package com.unknown.db.exception;

import com.unknown.util.exception.ExceptionId;

public class DAOUpdateException extends DAOAppException {
	private static final long serialVersionUID = 2945948025266055182L;

	public DAOUpdateException() {
	}

	public DAOUpdateException(ExceptionId id, String s) {
		super(id, s);
	}

	public DAOUpdateException(String s) {
		super(s);
	}

	public DAOUpdateException(Throwable throwable) {
		super(throwable);
	}

	public DAOUpdateException(String s, Throwable throwable) {
		super(s, throwable);
	}
}
