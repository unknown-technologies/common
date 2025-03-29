package com.unknown.db.exception;

import com.unknown.util.exception.BaseRuntimeException;
import com.unknown.util.exception.ExceptionId;

public class DAOFactoryException extends BaseRuntimeException {
	private static final long serialVersionUID = 1L;

	public DAOFactoryException(ExceptionId id) {
		super(id);
	}

	public DAOFactoryException(ExceptionId id, Throwable t) {
		super(id, t);
	}
}
