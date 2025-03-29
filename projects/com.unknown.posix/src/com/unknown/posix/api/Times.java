package com.unknown.posix.api;

import com.unknown.posix.api.io.Clock;

public class Times {
	public static final long SC_CLK_TCK = 100;

	private static final long TIMES_FACTOR = 1000000 / Time.CLOCKS_PER_SEC;

	private final Clock clock;

	public Times(Clock clock) {
		this.clock = clock;
	}

	public long times(Tms buffer) throws PosixException {
		if(buffer == null) {
			throw new PosixException(Errno.EFAULT);
		}

		buffer.tms_utime = 0;
		buffer.tms_stime = 0;
		buffer.tms_cutime = 0;
		buffer.tms_cstime = 0;

		long t = clock.getTimestamp(System.currentTimeMillis());
		return t / TIMES_FACTOR;
	}
}
