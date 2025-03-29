package com.unknown.db;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.TimeZone;

import org.w3c.dom.Document;

import com.unknown.db.exception.DAOSQLException;

public interface DatabaseResultReader {
	boolean next() throws DAOSQLException;

	int getInt(String name) throws DAOSQLException;

	int getInt(int index) throws DAOSQLException;

	Integer getIntObject(String name) throws DAOSQLException;

	Integer getIntObject(int index) throws DAOSQLException;

	BigDecimal getBigDecimal(String name) throws DAOSQLException;

	BigDecimal getBigDecimal(int index) throws DAOSQLException;

	long getLong(String name) throws DAOSQLException;

	long getLong(int index) throws DAOSQLException;

	Long getLongObject(String name) throws DAOSQLException;

	Long getLongObject(int index) throws DAOSQLException;

	short getShort(String name) throws DAOSQLException;

	short getShort(int index) throws DAOSQLException;

	Short getShortObject(String name) throws DAOSQLException;

	Short getShortObject(int index) throws DAOSQLException;

	float getFloat(String name) throws DAOSQLException;

	float getFloat(int index) throws DAOSQLException;

	Float getFloatObject(String name) throws DAOSQLException;

	Float getFloatObject(int index) throws DAOSQLException;

	double getDouble(String name) throws DAOSQLException;

	double getDouble(int index) throws DAOSQLException;

	Double getDoubleObject(String name) throws DAOSQLException;

	Double getDoubleObject(int index) throws DAOSQLException;

	boolean getBoolean(String name) throws DAOSQLException;

	boolean getBoolean(int index) throws DAOSQLException;

	Boolean getBooleanObject(String name) throws DAOSQLException;

	Boolean getBooleanObject(int index) throws DAOSQLException;

	String getString(String name) throws DAOSQLException;

	String getString(int index) throws DAOSQLException;

	Date getTimestamp(String name) throws DAOSQLException;

	Date getTimestamp(String name, TimeZone timeZone)
			throws DAOSQLException;

	Date getTimestamp(int index) throws DAOSQLException;

	Date getTimestamp(int index, TimeZone timeZone) throws DAOSQLException;

	Timestamp getSQLTimestamp(String name) throws DAOSQLException;

	Timestamp getSQLTimestamp(String name, TimeZone timeZone)
			throws DAOSQLException;

	Timestamp getSQLTimestamp(int index) throws DAOSQLException;

	Timestamp getSQLTimestamp(int index, TimeZone timeZone)
			throws DAOSQLException;

	Time getTime(String name) throws DAOSQLException;

	Time getTime(String name, TimeZone timeZone) throws DAOSQLException;

	Time getTime(int index) throws DAOSQLException;

	Time getTime(int index, TimeZone timeZone) throws DAOSQLException;

	Date getDate(String name) throws DAOSQLException;

	Date getDate(String name, TimeZone timeZone) throws DAOSQLException;

	Date getDate(int index) throws DAOSQLException;

	Date getDate(int index, TimeZone timeZone) throws DAOSQLException;

	byte[] getBytes(String name) throws DAOSQLException;

	byte[] getBytes(int index) throws DAOSQLException;

	Object getObject(String name) throws DAOSQLException;

	Object getObject(int index) throws DAOSQLException;

	SQLXML getSQLXML(int index) throws DAOSQLException;

	SQLXML getSQLXML(String name) throws DAOSQLException;

	Document getXML(int index) throws DAOSQLException;

	Document getXML(String name) throws DAOSQLException;

	InputStream getBinaryStream(String name) throws DAOSQLException;

	InputStream getBinaryStream(int index) throws DAOSQLException;

	long getGeneratedLongKey(String sequenceTableName) throws DAOSQLException;

	boolean wasNull() throws DAOSQLException;
}
