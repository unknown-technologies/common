package com.unknown.audio.io;

import java.io.IOException;

public class FileFormatException extends IOException {
	private final static long serialVersionUID = 1L;

	public FileFormatException(String msg) {
		super(msg);
	}
}
