package com.unknown.db.exception;

import java.sql.SQLException;

import com.unknown.db.Messages;
import com.unknown.util.exception.ExceptionId;

public class DAOSQLIntegrityConstraintViolationException extends DAOSQLException {
	private static final long serialVersionUID = 8576527540544063105L;

	public DAOSQLIntegrityConstraintViolationException(ExceptionId id) {
		super(id);
	}

	public DAOSQLIntegrityConstraintViolationException(String s) {
		super(Messages.DAOSQL_INTEGRITY, s);
	}

	public DAOSQLIntegrityConstraintViolationException(SQLException e) {
		super(Messages.DAOSQL_INTEGRITY, e);
	}

	public DAOSQLIntegrityConstraintViolationException(SQLException e, String statement) {
		super(Messages.DAOSQL_INTEGRITY, e, statement);
	}

	public DAOSQLIntegrityConstraintViolationException(ExceptionId id, SQLException e, String statement) {
		super(id, e, statement);
	}

	public DAOSQLIntegrityConstraintViolationException(ExceptionId id, SQLException e, String statement,
			Object... args) {
		super(id, e, statement, args);
	}

	public DAOSQLIntegrityConstraintViolationException(ExceptionId id, SQLException e, Object... args) {
		super(id, e, args);
	}
}
