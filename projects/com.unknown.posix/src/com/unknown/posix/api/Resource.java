package com.unknown.posix.api;

public class Resource {
	// @formatter:off
	public static final int RLIMIT_CPU            = 0;      /* CPU time in sec */
	public static final int RLIMIT_FSIZE          = 1;      /* Maximum filesize */
	public static final int RLIMIT_DATA           = 2;      /* max data size */
	public static final int RLIMIT_STACK          = 3;      /* max stack size */
	public static final int RLIMIT_CORE           = 4;      /* max core file size */
	public static final int RLIMIT_RSS            = 5;      /* max resident set size */
	public static final int RLIMIT_NPROC          = 6;      /* max number of processes */
	public static final int RLIMIT_NOFILE         = 7;      /* max number of open files */
	public static final int RLIMIT_MEMLOCK        = 8;      /* max locked-in-memory address space */
	public static final int RLIMIT_AS             = 9;      /* address space limit */
	public static final int RLIMIT_LOCKS          = 10;     /* maximum file locks held */
	public static final int RLIMIT_SIGPENDING     = 11;     /* max number of pending signals */
	public static final int RLIMIT_MSGQUEUE       = 12;     /* maximum bytes in POSIX mqueues */
	public static final int RLIMIT_NICE           = 13;     /* max nice prio allowed to raise to
	                                           0-39 for nice level 19 .. -20 */
	public static final int RLIMIT_RTPRIO         = 14;     /* maximum realtime priority */
	public static final int RLIMIT_RTTIME         = 15;     /* timeout for RT tasks in us */
	public static final int RLIM_NLIMITS          = 16;

	/*
	 * SuS says limits have to be unsigned.
	 * Which makes a ton more sense anyway.
	 *
	 * Some architectures override this (for compatibility reasons):
	 */
	public static final long RLIM_INFINITY        = ~0L;
	// @formatter:on

	private static final String[] RESOURCES = {
			/* 00 */ "RLIMIT_CPU",
			/* 01 */ "RLIMIT_FSIZE",
			/* 02 */ "RLIMIT_DATA",
			/* 03 */ "RLIMIT_STACK",
			/* 04 */ "RLIMIT_CORE",
			/* 05 */ "RLIMIT_RSS",
			/* 06 */ "RLIMIT_NPROC",
			/* 07 */ "RLIMIT_NOFILE",
			/* 08 */ "RLIMIT_MEMLOCK",
			/* 09 */ "RLIMIT_AS",
			/* 10 */ "RLIMIT_LOCKS",
			/* 11 */ "RLIMIT_SIGPENDING",
			/* 12 */ "RLIMIT_MSGQUEUE",
			/* 13 */ "RLIMIT_NICE",
			/* 14 */ "RLIMIT_RTPRIO",
			/* 15 */ "RLIMIT_RTTIME"
	};

	public static String toString(int resource) {
		if(resource >= 0 && resource < RESOURCES.length) {
			return RESOURCES[resource];
		} else {
			return Integer.toString(resource);
		}
	}
}
