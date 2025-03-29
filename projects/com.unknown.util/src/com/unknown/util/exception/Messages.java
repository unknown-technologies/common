package com.unknown.util.exception;

public class Messages {
	// @formatter:off
	public static final ExceptionId UNKNOWN		= new ExceptionId("CEVU0001E", "Unknown error");
	public static final ExceptionId UNKNOWN_BACKEND	= new ExceptionId("CEVU0002E", "Unknown backend error");

	public static final ExceptionId XALAN_NO_ENVCK	= new ExceptionId("CEVU0003I", "EnvironmentCheck class of xalan was not found. Environment check skipped!");
	public static final ExceptionId XALAN_CKFAIL	= new ExceptionId("CEVU0004W", "xalan environment check failed");
	public static final ExceptionId XALAN_ENVCK_OK	= new ExceptionId("CEVU0005I", "xalan environment is not ok");
	public static final ExceptionId XALAN_ENVCK_NOK	= new ExceptionId("CEVU0006W", "xalan environment is ok");
	public static final ExceptionId XML_NO_PINDENT	= new ExceptionId("CEVU0007W", "Cannot set indent for prettyprint");
	public static final ExceptionId XML_INDENT_FAIL	= new ExceptionId("CEVU0008W", "Cannot set indent-amount property: unknown XSLT processor");
	public static final ExceptionId XML_XSLT_PROC	= new ExceptionId("CEVU0009I", "Using XSLT processor {0} (transformer factory is {1})");
	public static final ExceptionId XML_FORMAT_FAIL	= new ExceptionId("CEVU0010W", "Error formatting XML");
	public static final ExceptionId XML_CLOSESTREAM	= new ExceptionId("CEVU0011W", "Error closing stream");

	public static final ExceptionId NO_RESOURCE	= new ExceptionId("CEVU0012W", "Resource \"{1}\" not found for class {0}");

	public static final ExceptionId POOL_EXEC_FAIL	= new ExceptionId("CEVU0013W", "Error executing task");

	public static final ExceptionId GET_UNSAFE_FAIL	= new ExceptionId("CEVU0014E", "Error retrieving Unsafe instance");
	// @formatter:on
}
