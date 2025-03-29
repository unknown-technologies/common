package com.unknown.posix.api;

public class Time {
	public static final long CLOCKS_PER_SEC = 100000;

	public static final long TIMER_ABSTIME = 0x01;

	public static String timerFlags(int flags) {
		if(flags == TIMER_ABSTIME) {
			return "TIMER_ABSTIME";
		} else {
			return Integer.toString(flags);
		}
	}
}
