package com.unknown.xml;

import com.unknown.util.exception.ExceptionId;

public class Messages {
	// @formatter:off
	public static final ExceptionId START_DOCUMENT	= new ExceptionId("CEVX0001W", "Cannot start document");
	public static final ExceptionId END_DOCUMENT	= new ExceptionId("CEVX0002W", "Cannot end document");
	public static final ExceptionId START_ELEMENT	= new ExceptionId("CEVX0003W", "Cannot start element");
	public static final ExceptionId END_ELEMENT	= new ExceptionId("CEVX0004W", "Cannot end element");
	public static final ExceptionId START_PREFIXMAP	= new ExceptionId("CEVX0005W", "Cannot start prefix mapping");
	public static final ExceptionId END_PREFIXMAP	= new ExceptionId("CEVX0006W", "Cannot end prefix mapping");
	public static final ExceptionId PROCESSING_INSN	= new ExceptionId("CEVX0007W", "Cannot handle processing instruction");
	public static final ExceptionId TEXT		= new ExceptionId("CEVX0008W", "Cannot handle text");
	public static final ExceptionId UNEXPECTED_TEXT	= new ExceptionId("CEVX0009W", "Unexpected text '{0}'");
	// @formatter:on
}
