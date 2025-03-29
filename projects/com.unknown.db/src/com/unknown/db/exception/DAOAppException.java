package com.unknown.db.exception;

import com.unknown.db.Messages;
import com.unknown.util.exception.BaseException;
import com.unknown.util.exception.ExceptionId;

public class DAOAppException extends BaseException {
	private static final long serialVersionUID = 5242673527889061338L;

	public DAOAppException() {
		super(Messages.DAO_UNKNOWN);
	}

	public DAOAppException(String s) {
		super(Messages.DAO_UNKNOWN, s);
	}

	public DAOAppException(Throwable throwable) {
		super(Messages.DAO_UNKNOWN, throwable);
	}

	public DAOAppException(String s, Throwable throwable) {
		super(Messages.DAO_UNKNOWN, s, throwable);
	}

	public DAOAppException(ExceptionId id) {
		super(id);
	}

	public DAOAppException(ExceptionId id, String s) {
		super(id, s);
	}

	public DAOAppException(ExceptionId id, Throwable t) {
		super(id, t);
	}

	public DAOAppException(ExceptionId id, String s, Throwable t) {
		super(id, s, t);
	}
}
