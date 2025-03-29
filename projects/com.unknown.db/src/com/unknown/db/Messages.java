package com.unknown.db;

import com.unknown.util.exception.ExceptionId;

public class Messages {
	// @formatter:off
	public static final ExceptionId NO_DAO_CLASS		= new ExceptionId("CEVDB0001E", "Unable to create DAO: no class specified");
	public static final ExceptionId NO_DAO_CTOR		= new ExceptionId("CEVDB0002E", "Unable to create DAO: constructor (DAOFactory) is missing");
	public static final ExceptionId DAO_INST_ERROR		= new ExceptionId("CEVDB0003E", "Error while instantiating DAO");
	public static final ExceptionId ID_TRANSFORM_INIT_FAIL	= new ExceptionId("CEVDB0004E", "Error initializing identity transformer");
	public static final ExceptionId PS_STILL_OPEN		= new ExceptionId("CEVDB0005W", "Prepared statement is still open");
	public static final ExceptionId DAO_CLOSE_DB_FAIL	= new ExceptionId("CEVDB0006W", "Error while closing database connection: {3}");
	public static final ExceptionId DAO_CLOSE_RS_FAIL	= new ExceptionId("CEVDB0007W", "Error while closing result set: {3}");
	public static final ExceptionId DAO_CLOSE_STMT_FAIL	= new ExceptionId("CEVDB0008W", "Error while closing statement: {3}");
	public static final ExceptionId DAO_LEAK_CON		= new ExceptionId("CEVDB0009W", "Connection has not been closed in {0}");
	public static final ExceptionId DAO_LEAK_STMT		= new ExceptionId("CEVDB0010W", "Statement has not been closed in {0}");
	public static final ExceptionId DAO_LEAK_RS		= new ExceptionId("CEVDB0011W", "Result set has not been closed in {0}");
	public static final ExceptionId LPS_NO_PREPARE		= new ExceptionId("CEVDB0012W", "Executing statement without prepare");
	public static final ExceptionId LPS_LEAK		= new ExceptionId("CEVDB0013W", "Statement not closed");
	public static final ExceptionId LPS_CLOSE_STMT_FAIL	= new ExceptionId("CEVDB0014W", "Error while closing statement");
	public static final ExceptionId LPS_ISCLOSED_FAIL	= new ExceptionId("CEVDB0015W", "Error while querying status");

	public static final ExceptionId DAO_UNKNOWN		= new ExceptionId("CEVDB0016E", "Unknown DAO Exception {4}");
	public static final ExceptionId DAOSQL_UNKNOWN		= new ExceptionId("CEVDB0017E", "Unknown SQL Exception {4}");
	public static final ExceptionId DAO_PREPARE_FAILED	= new ExceptionId("CEVDB0018E", "Unable to prepare statement '{2}': {3}");
	public static final ExceptionId DAO_GET_GENKEYS_FAILED	= new ExceptionId("CEVDB0019E", "Could not retrieve generated key: {3}");
	public static final ExceptionId DAO_GET_GENKEYS_NORES	= new ExceptionId("CEVDB0020E", "Could not retrieve generated key: no result");
	public static final ExceptionId DAO_NEXT_FAILED		= new ExceptionId("CEVDB0021E", "next() failed: {4}");
	public static final ExceptionId DAO_EXECUTE_FAILED	= new ExceptionId("CEVDB0022E", "execute() failed for statement {2}: {3}");
	public static final ExceptionId DAO_EXECUTEQ_FAILED	= new ExceptionId("CEVDB0023E", "executeQuery() failed for statement {2}: {3}");
	public static final ExceptionId DAO_ZERO_UPDATE		= new ExceptionId("CEVDB0024E", "execute() updated zero rows");
	public static final ExceptionId DAOSQL_INTEGRITY	= new ExceptionId("CEVDB0025E", "SQL Integrity Constraint Exception {4}");
	public static final ExceptionId DAO_EXECUTE_INTEGRITY	= new ExceptionId("CEVDB0026E", "execute() failed with integrity constraint violation for statement {2}: {3}");
	// @formatter:on
}
