package com.unknown.net.shownet;

import java.io.IOException;

@SuppressWarnings("serial")
public class TimeoutException extends IOException {
	public TimeoutException() {
		super();
	}

	public TimeoutException(String msg) {
		super(msg);
	}

	public TimeoutException(Throwable t) {
		super(t);
	}

	public TimeoutException(String msg, Throwable t) {
		super(msg, t);
	}
}
