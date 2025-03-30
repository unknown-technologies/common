package com.unknown.posix.api.io;

import java.util.logging.Logger;

import com.unknown.posix.api.Errno;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.Timespec;
import com.unknown.posix.api.Timeval;
import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;

public class Clock {
	private static final Logger log = Trace.create(Clock.class);

	// @formatter:off
	public static final int CLOCK_REALTIME                = 0;
	public static final int CLOCK_MONOTONIC               = 1;
	public static final int CLOCK_PROCESS_CPUTIME_ID      = 2;
	public static final int CLOCK_THREAD_CPUTIME_ID       = 3;
	public static final int CLOCK_MONOTONIC_RAW           = 4;
	public static final int CLOCK_REALTIME_COARSE         = 5;
	public static final int CLOCK_MONOTONIC_COARSE        = 6;
	public static final int CLOCK_BOOTTIME                = 7;
	public static final int CLOCK_REALTIME_ALARM          = 8;
	public static final int CLOCK_BOOTTIME_ALARM          = 9;
	public static final int CLOCK_SGI_CYCLE               = 10;     /* Hardware specific */
	public static final int CLOCK_TAI                     = 11;
	// @formatter:on

	private static final String[] CLOCK_NAMES = {
			/* 00 */ "CLOCK_REALTIME",
			/* 01 */ "CLOCK_MONOTONIC",
			/* 02 */ "CLOCK_PROCESS_CPUTIME_ID",
			/* 03 */ "CLOCK_THREAD_CPUTIME_ID",
			/* 04 */ "CLOCK_MONOTONIC_RAW",
			/* 05 */ "CLOCK_REALTIME_COARSE",
			/* 06 */ "CLOCK_MONOTONIC_COARSE",
			/* 07 */ "CLOCK_BOOTTIME",
			/* 08 */ "CLOCK_REALTIME_ALARM",
			/* 09 */ "CLOCK_BOOTTIME_ALARM",
			/* 10 */ "CLOCK_SGI_CYCLE",
			/* 11 */ "CLOCK_TAI"
	};

	private final double timeScale = getTimeScaleFromProperty();

	private long startTime = System.nanoTime();
	private long startUnixTime = System.currentTimeMillis();

	private static double getTimeScaleFromProperty() {
		String scale = System.getProperty("posix.clock.scale");
		if(scale != null) {
			try {
				return Double.parseDouble(scale);
			} catch(NumberFormatException e) {
				log.log(Levels.WARNING, "Invalid value for time scale", e);
				return 1.0;
			}
		} else {
			return 1.0;
		}
	}

	public static String getClockName(int clk_id) {
		if(clk_id >= 0 && clk_id < CLOCK_NAMES.length) {
			return CLOCK_NAMES[clk_id];
		} else {
			return Integer.toString(clk_id);
		}
	}

	public long getTimestamp(long timestamp) {
		if(timeScale != 1.0) {
			return (long) ((timestamp - startUnixTime) * timeScale + startUnixTime);
		} else {
			return timestamp;
		}
	}

	public long getTimestamp() {
		return getTimestamp(System.currentTimeMillis());
	}

	public int clock_getres(int clk_id, Timespec tp) throws PosixException {
		switch(clk_id) {
		case CLOCK_REALTIME:
		case CLOCK_REALTIME_COARSE:
		case CLOCK_MONOTONIC:
		case CLOCK_MONOTONIC_COARSE:
		case CLOCK_PROCESS_CPUTIME_ID:
		case CLOCK_THREAD_CPUTIME_ID:
			if(tp != null) {
				tp.tv_sec = 0;
				tp.tv_nsec = 1;
			}
			break;
		default:
			throw new PosixException(Errno.EINVAL);
		}
		return 0;
	}

	public int clock_gettime(int clk_id, Timespec tp) throws PosixException {
		switch(clk_id) {
		case CLOCK_REALTIME:
		case CLOCK_REALTIME_COARSE: {
			long t = getTimestamp();
			tp.tv_sec = t / 1000;
			tp.tv_nsec = (t % 1000) * 1000000;
			break;
		}
		case CLOCK_MONOTONIC:
		case CLOCK_MONOTONIC_COARSE: {
			long t = System.nanoTime();
			if(timeScale != 1.0) {
				t = (long) ((t - startTime) * timeScale + startTime);
			}
			tp.tv_sec = t / 1000000000L;
			tp.tv_nsec = (t % 1000000000L);
			break;
		}
		case CLOCK_PROCESS_CPUTIME_ID:
		case CLOCK_THREAD_CPUTIME_ID: {
			long t = System.nanoTime() - startTime;
			if(timeScale != 1.0) {
				t = (long) ((t - startTime) * timeScale + startTime);
			}
			tp.tv_sec = t / 1000000000L;
			tp.tv_nsec = (t % 1000000000L);
			break;
		}
		default:
			throw new PosixException(Errno.EINVAL);
		}
		return 0;
	}

	public int gettimeofday(Timeval tp) {
		long t = getTimestamp();
		tp.tv_sec = t / 1000;
		tp.tv_usec = (t % 1000) * 1000;
		return 0;
	}
}
