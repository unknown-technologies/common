package com.unknown.db;

import com.unknown.db.exception.DAOAppException;
import com.unknown.db.exception.DAOSQLException;

public interface DAO extends DatabaseResultReader, AutoCloseable {
	void closeConnection();

	void closeStatementAndResultSet();

	void closeResultSet();

	void addBatch() throws DAOSQLException;

	void executeBatch() throws DAOSQLException;

	@Override
	boolean next() throws DAOSQLException;

	int execute() throws DAOAppException;

	int execute(boolean ignoreZeroCount) throws DAOAppException;

	void executeQuery() throws DAOAppException;
}
