package com.unknown.db;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;

import com.unknown.db.exception.DAOAppException;
import com.unknown.db.exception.DAOSQLException;
import com.unknown.db.exception.DAOSQLIntegrityConstraintViolationException;
import com.unknown.db.exception.DAOUpdateException;
import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;

public class DAOBase implements DAO {
	private static final Logger log = Trace.create(DAOBase.class);

	private static final String RESULT_SET_IS_NULL = "result set is null";

	private Connection connection = null;
	private PreparedStatement statement = null;
	private ResultSet result = null;
	private String sqlStatement = null;
	private boolean moreRows = false;

	public DAOBase() {
		this(null);
	}

	public DAOBase(Connection connection) {
		this.connection = connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	protected String getDAOStatement(String key) {
		return StatementCache.getStatement(getClass(), key);
	}

	protected String getStatement() {
		if(statement instanceof LoggablePreparedStatement) {
			return ((LoggablePreparedStatement) statement).getStatement();
		} else {
			return sqlStatement;
		}
	}

	@Override
	public String toString() {
		return getStatement();
	}

	protected static PreparedStatement createPreparedStatement(Connection connection, String sql)
			throws SQLException {
		return createPreparedStatement(connection, sql, false);
	}

	protected static PreparedStatement createPreparedStatement(Connection connection, String sql,
			boolean returnGeneratedKeys) throws SQLException {
		if(returnGeneratedKeys) {
			return new LoggablePreparedStatement(connection.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS), sql);
		}

		PreparedStatement statement = connection.prepareStatement(sql);
		// String queryTimeout = getQueryTimeout();
		// statement.setQueryTimeout(queryTimeout);
		return new LoggablePreparedStatement(statement, sql);
	}

	protected void prepareSQL(String sql, boolean returnGeneratedKeys) throws DAOSQLException {
		try {
			if(statement != null) {
				log.warning(Messages.PS_STILL_OPEN.format());
				closeStatementAndResultSet();
			}

			sqlStatement = sql;
			statement = createPreparedStatement(connection, sql, returnGeneratedKeys);
			result = null;
		} catch(SQLException e) {
			throw new DAOSQLException(Messages.DAO_PREPARE_FAILED, e);
		}
	}

	protected void prepareSQL(String sql) throws DAOSQLException {
		prepareSQL(sql, false);
	}

	public Savepoint setSavepoint() throws DAOSQLException {
		try {
			return connection.setSavepoint();
		} catch(SQLException e) {
			throw new DAOSQLException(e);
		}
	}

	public void releaseSavepoint(Savepoint savepoint) throws DAOSQLException {
		if(savepoint != null) {
			try {
				connection.releaseSavepoint(savepoint);
			} catch(SQLException e) {
				throw new DAOSQLException(e);
			}
		}
	}

	public void rollback(Savepoint savepoint) throws DAOSQLException {
		try {
			connection.rollback(savepoint);
		} catch(SQLException e) {
			throw new DAOSQLException(e);
		}
	}

	@Override
	public int getInt(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getInt(name);
		} catch(SQLException e) {
			throw new DAOSQLException("getInt(" + name + ") failed", e);
		}
	}

	@Override
	public int getInt(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getInt(index);
		} catch(SQLException e) {
			throw new DAOSQLException("getInt(" + index + ") failed", e);
		}
	}

	@Override
	public Integer getIntObject(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			int value = result.getInt(name);
			if(result.wasNull()) {
				return null;
			} else {
				return value;
			}
		} catch(SQLException e) {
			throw new DAOSQLException("getIntObject(" + name + ") failed", e);
		}
	}

	@Override
	public Integer getIntObject(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			int value = result.getInt(index);
			if(result.wasNull()) {
				return null;
			} else {
				return value;
			}
		} catch(SQLException e) {
			throw new DAOSQLException("getIntObject(" + index + ") failed", e);
		}
	}

	@Override
	public BigDecimal getBigDecimal(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getBigDecimal(name);
		} catch(SQLException e) {
			throw new DAOSQLException("getBigDecimal(" + name + ") failed", e);
		}
	}

	@Override
	public BigDecimal getBigDecimal(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getBigDecimal(index);
		} catch(SQLException e) {
			throw new DAOSQLException("getBigDecimal(" + index + ") failed", e);
		}
	}

	@Override
	public long getLong(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getLong(name);
		} catch(SQLException e) {
			throw new DAOSQLException("getLong(" + name + ") failed", e);
		}
	}

	@Override
	public long getLong(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getLong(index);
		} catch(SQLException e) {
			throw new DAOSQLException("getLong(" + index + ") failed", e);
		}
	}

	@Override
	public Long getLongObject(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			long value = result.getLong(name);
			if(result.wasNull()) {
				return null;
			} else {
				return value;
			}
		} catch(SQLException e) {
			throw new DAOSQLException("getLongObject(" + name + ") failed", e);
		}
	}

	@Override
	public Long getLongObject(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			long value = result.getLong(index);
			if(result.wasNull()) {
				return null;
			} else {
				return value;
			}
		} catch(SQLException e) {
			throw new DAOSQLException("getLongObject(" + index + ") failed", e);
		}
	}

	@Override
	public short getShort(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getShort(name);
		} catch(SQLException e) {
			throw new DAOSQLException("getShort(" + name + ") failed", e);
		}
	}

	@Override
	public short getShort(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getShort(index);
		} catch(SQLException e) {
			throw new DAOSQLException("getShort(" + index + ") failed", e);
		}
	}

	@Override
	public Short getShortObject(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			short value = result.getShort(name);
			if(result.wasNull()) {
				return null;
			} else {
				return value;
			}
		} catch(SQLException e) {
			throw new DAOSQLException("getShortObject(" + name + ") failed", e);
		}
	}

	@Override
	public Short getShortObject(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			short value = result.getShort(index);
			if(result.wasNull()) {
				return null;
			} else {
				return value;
			}
		} catch(SQLException e) {
			throw new DAOSQLException("getShortObject(" + index + ") failed", e);
		}
	}

	@Override
	public float getFloat(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getFloat(name);
		} catch(SQLException e) {
			throw new DAOSQLException("getFloat(" + name + ") failed", e);
		}
	}

	@Override
	public float getFloat(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getFloat(index);
		} catch(SQLException e) {
			throw new DAOSQLException("getFloat(" + index + ") failed", e);
		}
	}

	@Override
	public Float getFloatObject(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			float value = result.getFloat(name);
			if(result.wasNull()) {
				return null;
			} else {
				return value;
			}
		} catch(SQLException e) {
			throw new DAOSQLException("getFloatObject(" + name + ") failed", e);
		}
	}

	@Override
	public Float getFloatObject(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			float value = result.getFloat(index);
			if(result.wasNull()) {
				return null;
			} else {
				return value;
			}
		} catch(SQLException e) {
			throw new DAOSQLException("getFloatObject(" + index + ") failed", e);
		}
	}

	@Override
	public double getDouble(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getDouble(name);
		} catch(SQLException e) {
			throw new DAOSQLException("getDouble(" + name + ") failed", e);
		}
	}

	@Override
	public double getDouble(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getDouble(index);
		} catch(SQLException e) {
			throw new DAOSQLException("getDouble(" + index + ") failed", e);
		}
	}

	@Override
	public Double getDoubleObject(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			double value = result.getDouble(name);
			if(result.wasNull()) {
				return null;
			} else {
				return value;
			}
		} catch(SQLException e) {
			throw new DAOSQLException("getDoubleObject(" + name + ") failed", e);
		}
	}

	@Override
	public Double getDoubleObject(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			double value = result.getDouble(index);
			if(result.wasNull()) {
				return null;
			} else {
				return value;
			}
		} catch(SQLException e) {
			throw new DAOSQLException("getDoubleObject(" + index + ") failed", e);
		}
	}

	@Override
	public boolean getBoolean(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getBoolean(name);
		} catch(SQLException e) {
			throw new DAOSQLException("getBoolean(" + name + ") failed", e);
		}
	}

	@Override
	public boolean getBoolean(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getBoolean(index);
		} catch(SQLException e) {
			throw new DAOSQLException("getBoolean(" + index + ") failed", e);
		}
	}

	@Override
	public Boolean getBooleanObject(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			boolean value = result.getBoolean(name);
			if(result.wasNull()) {
				return null;
			} else {
				return value;
			}
		} catch(SQLException e) {
			throw new DAOSQLException("getBooleanObject(" + name + ") failed", e);
		}
	}

	@Override
	public Boolean getBooleanObject(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			boolean value = result.getBoolean(index);
			if(result.wasNull()) {
				return null;
			} else {
				return value;
			}
		} catch(SQLException e) {
			throw new DAOSQLException("getBooleanObject(" + index + ") failed", e);
		}
	}

	@Override
	public String getString(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getString(name);
		} catch(SQLException e) {
			throw new DAOSQLException("getString(" + name + ") failed", e);
		}
	}

	@Override
	public String getString(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getString(index);
		} catch(SQLException e) {
			throw new DAOSQLException("getString(" + index + ") failed", e);
		}
	}

	@Override
	public Date getTimestamp(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		return getTimestamp(name, TimeZone.getTimeZone("UTC"));
	}

	@Override
	public Date getTimestamp(String name, TimeZone timeZone) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getTimestamp(name, Calendar.getInstance(timeZone));
		} catch(SQLException e) {
			throw new DAOSQLException("getTimestamp(" + name + ") failed", e);
		}
	}

	@Override
	public Date getTimestamp(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		return getTimestamp(index, TimeZone.getTimeZone("UTC"));
	}

	@Override
	public Date getTimestamp(int index, TimeZone timeZone) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getTimestamp(index, Calendar.getInstance(timeZone));
		} catch(SQLException e) {
			throw new DAOSQLException("getTimestamp(" + index + ") failed", e);
		}
	}

	@Override
	public Timestamp getSQLTimestamp(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getTimestamp(name);
		} catch(SQLException e) {
			throw new DAOSQLException("getSQLTimestamp(" + name + ") failed", e);
		}
	}

	@Override
	public Timestamp getSQLTimestamp(String name, TimeZone timeZone) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getTimestamp(name, Calendar.getInstance(timeZone));
		} catch(SQLException e) {
			throw new DAOSQLException("getSQLTimestamp(" + name + ") failed", e);
		}
	}

	@Override
	public Timestamp getSQLTimestamp(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getTimestamp(index);
		} catch(SQLException e) {
			throw new DAOSQLException("getSQLTimestamp(" + index + ") failed", e);
		}
	}

	@Override
	public Timestamp getSQLTimestamp(int index, TimeZone timeZone) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getTimestamp(index, Calendar.getInstance(timeZone));
		} catch(SQLException e) {
			throw new DAOSQLException("getSQLTimestamp(" + index + ") failed", e);
		}
	}

	@Override
	public Time getTime(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getTime(name);
		} catch(SQLException e) {
			throw new DAOSQLException("getTime(" + name + ") failed", e);
		}
	}

	@Override
	public Time getTime(String name, TimeZone timeZone) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getTime(name, Calendar.getInstance(timeZone));
		} catch(SQLException e) {
			throw new DAOSQLException("getTime(" + name + ") failed", e);
		}
	}

	@Override
	public Time getTime(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getTime(index);
		} catch(SQLException e) {
			throw new DAOSQLException("getTime(" + index + ") failed", e);
		}
	}

	@Override
	public Time getTime(int index, TimeZone timeZone) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getTime(index, Calendar.getInstance(timeZone));
		} catch(SQLException e) {
			throw new DAOSQLException("getTime(" + index + ") failed", e);
		}
	}

	@Override
	public Date getDate(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		return getDate(name, TimeZone.getTimeZone("UTC"));
	}

	@Override
	public Date getDate(String name, TimeZone timeZone) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getDate(name, Calendar.getInstance(timeZone));
		} catch(SQLException e) {
			throw new DAOSQLException("getDate(" + name + ") failed", e);
		}
	}

	@Override
	public Date getDate(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		return getDate(index, TimeZone.getTimeZone("UTC"));
	}

	@Override
	public Date getDate(int index, TimeZone timeZone) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getDate(index, Calendar.getInstance(timeZone));
		} catch(SQLException e) {
			throw new DAOSQLException("getDate(" + index + ") failed", e);
		}
	}

	@Override
	public byte[] getBytes(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getBytes(name);
		} catch(SQLException e) {
			throw new DAOSQLException("getBytes(" + name + ") failed", e);
		}
	}

	@Override
	public byte[] getBytes(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getBytes(index);
		} catch(SQLException e) {
			throw new DAOSQLException("getBytes(" + index + ") failed", e);
		}
	}

	@Override
	public Object getObject(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getObject(name);
		} catch(SQLException e) {
			throw new DAOSQLException("getObject(" + name + ") failed", e);
		}
	}

	@Override
	public Object getObject(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getObject(index);
		} catch(SQLException e) {
			throw new DAOSQLException("getObject(" + index + ") failed", e);
		}
	}

	@Override
	public SQLXML getSQLXML(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getSQLXML(index);
		} catch(SQLException e) {
			throw new DAOSQLException("getSQLXML(" + index + ") failed", e);
		}
	}

	@Override
	public SQLXML getSQLXML(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getSQLXML(name);
		} catch(SQLException e) {
			throw new DAOSQLException("getSQLXML(" + name + ") failed", e);
		}
	}

	@Override
	public Document getXML(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			SQLXML xml = result.getSQLXML(index);
			DOMSource src = xml.getSource(DOMSource.class);
			return (Document) src.getNode();
		} catch(SQLException e) {
			throw new DAOSQLException("getXML(" + index + ") failed", e);
		}
	}

	@Override
	public Document getXML(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			SQLXML xml = result.getSQLXML(name);
			DOMSource src = xml.getSource(DOMSource.class);
			return (Document) src.getNode();
		} catch(SQLException e) {
			throw new DAOSQLException("getXML(" + name + ") failed", e);
		}
	}

	@Override
	public InputStream getBinaryStream(String name) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getBinaryStream(name);
		} catch(SQLException e) {
			throw new DAOSQLException("getBinaryStream(" + name + ") failed", e);
		}
	}

	@Override
	public InputStream getBinaryStream(int index) throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.getBinaryStream(index);
		} catch(SQLException e) {
			throw new DAOSQLException("getBinaryStream(" + index + ") failed", e);
		}
	}

	@Override
	public long getGeneratedLongKey(String sequenceTableName) throws DAOSQLException {
		try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
			if(generatedKeys.next()) {
				return generatedKeys.getLong(1);
			} else {
				throw new DAOSQLException(Messages.DAO_GET_GENKEYS_NORES);
			}
		} catch(SQLException e) {
			throw new DAOSQLException(Messages.DAO_GET_GENKEYS_FAILED, e, getStatement());
		}
	}

	@Override
	public boolean wasNull() throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException(RESULT_SET_IS_NULL);
		}
		try {
			return result.wasNull();
		} catch(SQLException e) {
			throw new DAOSQLException("wasNull() failed", e);
		}
	}

	@Override
	public void close() {
		closeStatementAndResultSet();
	}

	@Override
	public void closeConnection() {
		if(connection != null) {
			try {
				connection.close();
			} catch(SQLException e) {
				log.log(Levels.WARNING, Messages.DAO_CLOSE_DB_FAIL.format(e.getMessage()), e);
			}

			connection = null;
		}
	}

	@Override
	public void closeStatementAndResultSet() {
		if(result != null) {
			try {
				result.close();
			} catch(SQLException e) {
				log.log(Levels.WARNING, Messages.DAO_CLOSE_RS_FAIL.format(e.getMessage()), e);
			}
		}

		if(statement != null) {
			try {
				statement.close();
			} catch(SQLException e) {
				log.log(Levels.WARNING, Messages.DAO_CLOSE_STMT_FAIL.format(e.getMessage()), e);
			}
		}

		statement = null;
		result = null;
		moreRows = false;
	}

	@Override
	public void closeResultSet() {
		if(result != null) {
			try {
				result.close();
			} catch(SQLException e) {
				log.log(Levels.WARNING, Messages.DAO_CLOSE_RS_FAIL.format(e.getMessage()), e);
			}
		}

		result = null;
		moreRows = false;
	}

	@Override
	public void addBatch() throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement available");
		}

		try {
			statement.addBatch();
		} catch(SQLException e) {
			throw new DAOSQLException("addBatch failed: " + e.getMessage(), e);
		}
	}

	@Override
	protected void finalize() {
		if(result != null) {
			log.warning(Messages.DAO_LEAK_RS.format(getClass().getName()));
		}

		if(statement != null) {
			log.warning(Messages.DAO_LEAK_STMT.format(getClass().getName()));
		}

		if(connection != null) {
			log.warning(Messages.DAO_LEAK_CON.format(getClass().getName()));
		}

		closeStatementAndResultSet();
		closeConnection();
	}

	@Override
	public void executeBatch() throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}

		try {
			statement.executeBatch();
		} catch(SQLIntegrityConstraintViolationException e) {
			throw new DAOSQLIntegrityConstraintViolationException(Messages.DAO_EXECUTE_INTEGRITY, e,
					getStatement());
		} catch(SQLException e) {
			throw new DAOSQLException(e, getStatement());
		}
	}

	@Override
	public boolean next() throws DAOSQLException {
		if(result == null) {
			throw new IllegalStateException("Result set is null");
		}

		if(moreRows) {
			try {
				moreRows = result.next();
			} catch(SQLException e) {
				throw new DAOSQLException(Messages.DAO_NEXT_FAILED, e, getStatement());
			}
		}

		return moreRows;
	}

	@Override
	public int execute() throws DAOAppException {
		return execute(false);
	}

	@Override
	public int execute(boolean ignoreZeroCount) throws DAOAppException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}

		try {
			int results = statement.executeUpdate();
			if((results == 0) && !ignoreZeroCount) {
				throw new DAOUpdateException(Messages.DAO_ZERO_UPDATE, getStatement());
			}
			return results;
		} catch(SQLIntegrityConstraintViolationException e) {
			throw new DAOSQLIntegrityConstraintViolationException(Messages.DAO_EXECUTE_INTEGRITY, e,
					getStatement());
		} catch(SQLException e) {
			throw new DAOSQLException(Messages.DAO_EXECUTE_FAILED, e, getStatement());
		}
	}

	@Override
	public void executeQuery() throws DAOAppException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}

		try {
			moreRows = true;
			result = statement.executeQuery();
		} catch(SQLException e) {
			throw new DAOSQLException(Messages.DAO_EXECUTEQ_FAILED, e, getStatement());
		}
	}

	protected void setShort(int index, short value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			statement.setShort(index, value);
		} catch(SQLException e) {
			throw new DAOSQLException("setShort(" + index + ", " + value + ")", e);
		}
	}

	protected void setShort(int index, Short value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			if(value == null) {
				statement.setNull(index, Types.SMALLINT);
			} else {
				statement.setShort(index, value);
			}
		} catch(SQLException e) {
			throw new DAOSQLException("setShort(" + index + ", " + value + ")", e);
		}
	}

	protected void setInt(int index, int value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			statement.setInt(index, value);
		} catch(SQLException e) {
			throw new DAOSQLException("setInt(" + index + ", " + value + ")", e);
		}
	}

	protected void setInt(int index, Integer value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			if(value == null) {
				statement.setNull(index, Types.INTEGER);
			} else {
				statement.setInt(index, value);
			}
		} catch(SQLException e) {
			throw new DAOSQLException("setInt(" + index + ", " + value + ")", e);
		}
	}

	protected void setLong(int index, long value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			statement.setLong(index, value);
		} catch(SQLException e) {
			throw new DAOSQLException("setLong(" + index + ", " + value + ")", e);
		}
	}

	protected void setLong(int index, Long value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			if(value == null) {
				statement.setNull(index, Types.BIGINT);
			} else {
				statement.setLong(index, value);
			}
		} catch(SQLException e) {
			throw new DAOSQLException("setLong(" + index + ", " + value + ")", e);
		}
	}

	protected void setFloat(int index, float value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			statement.setFloat(index, value);
		} catch(SQLException e) {
			throw new DAOSQLException("setFloat(" + index + ", " + value + ")", e);
		}
	}

	protected void setFloat(int index, Float value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			if(value == null) {
				statement.setNull(index, Types.FLOAT);
			} else {
				statement.setFloat(index, value);
			}
		} catch(SQLException e) {
			throw new DAOSQLException("setFloat(" + index + ", " + value + ")", e);
		}
	}

	protected void setDouble(int index, double value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			statement.setDouble(index, value);
		} catch(SQLException e) {
			throw new DAOSQLException("setDouble(" + index + ", " + value + ")", e);
		}
	}

	protected void setDouble(int index, Double value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			if(value == null) {
				statement.setNull(index, Types.DOUBLE);
			} else {
				statement.setDouble(index, value);
			}
		} catch(SQLException e) {
			throw new DAOSQLException("setDouble(" + index + ", " + value + ")", e);
		}
	}

	protected void setBoolean(int index, boolean value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			statement.setInt(index, value ? 1 : 0);
		} catch(SQLException e) {
			throw new DAOSQLException("setBoolean(" + index + ", " + value + ")", e);
		}
	}

	protected void setBoolean(int index, Boolean value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			if(value == null) {
				statement.setNull(index, Types.SMALLINT);
			} else {
				statement.setInt(index, value ? 1 : 0);
			}
		} catch(SQLException e) {
			throw new DAOSQLException("setBoolean(" + index + ", " + value + ")", e);
		}
	}

	protected void setBigDecimal(int index, BigDecimal value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			if(value == null) {
				statement.setNull(index, Types.BIGINT);
			} else {
				statement.setBigDecimal(index, value);
			}
		} catch(SQLException e) {
			throw new DAOSQLException("setBigDecimal(" + index + ", " + value + ")", e);
		}
	}

	protected void setString(int index, String value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			if(value == null) {
				statement.setNull(index, Types.VARCHAR);
			} else {
				statement.setString(index, value);
			}
		} catch(SQLException e) {
			throw new DAOSQLException("setString(" + index + ", " + value + ")", e);
		}
	}

	protected void setTimestamp(int index, Date value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		setTimestamp(index, value, TimeZone.getTimeZone("UTC"));
	}

	protected void setTimestamp(int index, Date value, TimeZone timeZone) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			if(value == null) {
				statement.setNull(index, Types.TIMESTAMP);
			} else {
				if(value instanceof Timestamp) {
					statement.setTimestamp(index, (Timestamp) value);
				} else {
					statement.setTimestamp(index, new Timestamp(value.getTime()),
							Calendar.getInstance(timeZone));
				}
			}
		} catch(SQLException e) {
			throw new DAOSQLException("setTimestamp(" + index + ", " + value + ")", e);
		}
	}

	protected void setDate(int index, Date value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		setDate(index, value, TimeZone.getTimeZone("UTC"));
	}

	protected void setDate(int index, Date value, TimeZone timeZone) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			if(value == null) {
				statement.setNull(index, Types.DATE);
			} else {
				if(value instanceof java.sql.Date) {
					statement.setDate(index, (java.sql.Date) value);
				} else {
					statement.setDate(index, new java.sql.Date(value.getTime()),
							Calendar.getInstance(timeZone));
				}
			}
		} catch(SQLException e) {
			throw new DAOSQLException("setDate(" + index + ", " + value + ")", e);
		}
	}

	protected void setTime(int index, Date value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		setTime(index, value, TimeZone.getTimeZone("UTC"));
	}

	protected void setTime(int index, Date value, TimeZone timeZone) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			if(value == null) {
				statement.setNull(index, Types.TIME);
			} else {
				if(value instanceof java.sql.Time) {
					statement.setTime(index, (java.sql.Time) value);
				} else {
					statement.setTime(index, new java.sql.Time(value.getTime()),
							Calendar.getInstance(timeZone));
				}
			}
		} catch(SQLException e) {
			throw new DAOSQLException("setTime(" + index + ", " + value + ")", e);
		}
	}

	protected void setObject(int index, Object value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			statement.setObject(index, value);
		} catch(SQLException e) {
			throw new DAOSQLException("setObject(" + index + ", " + value + ")", e);
		}
	}

	protected void setSQLXML(int index, SQLXML value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			statement.setSQLXML(index, value);
		} catch(SQLException e) {
			throw new DAOSQLException("setSQLXML(" + index + ", " + value + ")", e);
		}
	}

	protected void setXML(int index, Document value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			SQLXML xml = connection.createSQLXML();
			DOMResult dst = xml.setResult(DOMResult.class);
			dst.setNode(value);
			statement.setSQLXML(index, xml);
		} catch(SQLException e) {
			throw new DAOSQLException("setXML(" + index + ", " + value + ")", e);
		}
	}

	protected void setBytes(int index, byte[] value) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			if(value == null) {
				statement.setNull(index, Types.BLOB);
			} else {
				statement.setBytes(index, value);
			}
		} catch(SQLException e) {
			throw new DAOSQLException("setBytes(" + index + ", /* BLOB */)", e);
		}
	}

	protected void setNull(int index, int type) throws DAOSQLException {
		if(statement == null) {
			throw new IllegalStateException("No statement to execute");
		}
		try {
			statement.setNull(index, type);
		} catch(SQLException e) {
			throw new DAOSQLException("setNull(" + index + ", " + type + ")", e);
		}
	}
}
