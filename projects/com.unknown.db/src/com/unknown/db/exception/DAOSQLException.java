package com.unknown.db.exception;

import java.sql.SQLException;

import com.unknown.db.Messages;
import com.unknown.util.exception.ExceptionId;

public class DAOSQLException extends DAOAppException {
	private static final long serialVersionUID = 2323777492893413156L;

	private String statement = null;

	private Object[] args = null;

	public DAOSQLException(ExceptionId id) {
		super(id);
	}

	public DAOSQLException(String s) {
		this(Messages.DAOSQL_UNKNOWN, s);
	}

	public DAOSQLException(SQLException e) {
		this(Messages.DAOSQL_UNKNOWN, e);
	}

	public DAOSQLException(SQLException e, String statement) {
		super(Messages.DAOSQL_UNKNOWN, getExceptionText(e, statement), e);
		this.statement = statement;
		args = args(e, statement);
	}

	public DAOSQLException(String s, SQLException e) {
		super(Messages.DAOSQL_UNKNOWN, s, e);
	}

	public DAOSQLException(ExceptionId id, SQLException e, String statement) {
		super(id, getExceptionText(e, statement), e);
		this.statement = statement;
		args = args(e, statement);
	}

	public DAOSQLException(ExceptionId id, SQLException e, String statement, Object... args) {
		super(id, getExceptionText(e, statement), e);
		this.statement = statement;
		this.args = args(e, statement, args);
	}

	public DAOSQLException(ExceptionId id, SQLException t, Object... args) {
		super(id, t);
		this.args = args;
	}

	protected DAOSQLException(ExceptionId id, String s) {
		super(id, s);
	}

	protected DAOSQLException(ExceptionId id, SQLException t) {
		super(id, getExceptionText(t), t);
	}

	public String getStatement() {
		return statement;
	}

	@Override
	public Object[] getArguments() {
		return args;
	}

	protected static Object[] args(SQLException e) {
		return args(e, null, new Object[0]);
	}

	protected static Object[] args(SQLException e, String statement) {
		return args(e, statement, new Object[0]);
	}

	protected static Object[] args(SQLException e, String statement, Object[] args) {
		Object[] result = new Object[args.length + 5];
		System.arraycopy(args, 0, result, 5, args.length);
		result[0] = "SQLCODE=" + e.getErrorCode();
		result[1] = "SQLSTATE=" + e.getSQLState();
		result[2] = statement;
		result[3] = getExceptionText(e);
		result[4] = getExceptionText(e, statement);
		return result;
	}

	protected static String getExceptionText(SQLException e) {
		return String.format("SQL EXCEPTION: SQLCODE=%s, SQLSTATE=%s: %s",
				e.getErrorCode(), e.getSQLState(), e.getMessage());
	}

	protected static String getExceptionText(SQLException e, String statement) {
		return String.format("SQL EXCEPTION: SQLCODE=%s, SQLSTATE=%s, STATEMENT=%s: %s",
				e.getErrorCode(), e.getSQLState(), statement, e.getMessage());
	}
}
