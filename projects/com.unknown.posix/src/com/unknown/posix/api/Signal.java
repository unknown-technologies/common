package com.unknown.posix.api;

public class Signal {
	// @formatter:off
	public static final int _NSIG         = 64;
	public static final int _NSIG_BPW     = 64;
	public static final int _NSIG_WORDS   = (_NSIG / _NSIG_BPW);

	public static final int SIGHUP        =  1;
	public static final int SIGINT        =  2;
	public static final int SIGQUIT       =  3;
	public static final int SIGILL        =  4;
	public static final int SIGTRAP       =  5;
	public static final int SIGABRT       =  6;
	public static final int SIGIOT        =  6;
	public static final int SIGBUS        =  7;
	public static final int SIGFPE        =  8;
	public static final int SIGKILL       =  9;
	public static final int SIGUSR1       = 10;
	public static final int SIGSEGV       = 11;
	public static final int SIGUSR2       = 12;
	public static final int SIGPIPE       = 13;
	public static final int SIGALRM       = 14;
	public static final int SIGTERM       = 15;
	public static final int SIGSTKFLT     = 16;
	public static final int SIGCHLD       = 17;
	public static final int SIGCONT       = 18;
	public static final int SIGSTOP       = 19;
	public static final int SIGTSTP       = 20;
	public static final int SIGTTIN       = 21;
	public static final int SIGTTOU       = 22;
	public static final int SIGURG        = 23;
	public static final int SIGXCPU       = 24;
	public static final int SIGXFSZ       = 25;
	public static final int SIGVTALRM     = 26;
	public static final int SIGPROF       = 27;
	public static final int SIGWINCH      = 28;
	public static final int SIGIO         = 29;
	public static final int SIGPOLL       = SIGIO;
	/*
	public static final int SIGLOST       = 29
	*/
	public static final int SIGPWR        = 30;
	public static final int SIGSYS        = 31;
	public static final int SIGUNUSED     = 31;

	/* These should not be considered constants from userland.  */
	public static final int SIGRTMIN      = 32;
	public static final int SIGRTMAX      = _NSIG;

	public static final int SIG_BLOCK     = 0; /* for blocking signals */
	public static final int SIG_UNBLOCK   = 1; /* for unblocking signals */
	public static final int SIG_SETMASK   = 2; /* for setting the signal mask */

	public static final int SIGEV_SIGNAL    = 0; /* notify via signal */
	public static final int SIGEV_NONE      = 1; /* other notification: meaningless */
	public static final int SIGEV_THREAD    = 2; /* deliver via thread creation */
	public static final int SIGEV_THREAD_ID = 4; /* deliver to thread */
	// @formatter:on

	private static final String[] SIGNALS = {
			/* 00 */ "0",
			/* 01 */ "SIGHUP",
			/* 02 */ "SIGINT",
			/* 03 */ "SIGQUIT",
			/* 04 */ "SIGILL",
			/* 05 */ "SIGTRAP",
			/* 06 */ "SIGABRT",
			/* 07 */ "SIGBUS",
			/* 08 */ "SIGFPE",
			/* 09 */ "SIGKILL",
			/* 10 */ "SIGUSR1",
			/* 11 */ "SIGSEGV",
			/* 12 */ "SIGUSR2",
			/* 13 */ "SIGPIPE",
			/* 14 */ "SIGALRM",
			/* 15 */ "SIGTERM",
			/* 16 */ "SIGSTKFLT",
			/* 17 */ "SIGCHLD",
			/* 18 */ "SIGCONT",
			/* 19 */ "SIGSTOP",
			/* 20 */ "SIGTSTP",
			/* 21 */ "SIGTTIN",
			/* 22 */ "SIGTTOU",
			/* 23 */ "SIGURG",
			/* 24 */ "SIGXCPU",
			/* 25 */ "SIGXFSZ",
			/* 26 */ "SIGVTALRM",
			/* 27 */ "SIGPROF",
			/* 28 */ "SIGWINCH",
			/* 29 */ "SIGIO",
			/* 30 */ "SIGPWR",
			/* 31 */ "SIGSYS"
	};

	private static final String[] SIGPROCMASK_HOW = {
			/* 0 */ "SIG_BLOCK",
			/* 1 */ "SIG_UNBLOCK",
			/* 2 */ "SIG_SETMASK"
	};

	public static String sigprocmaskHow(int how) {
		if(how >= 0 && how < SIGPROCMASK_HOW.length) {
			return SIGPROCMASK_HOW[how];
		} else {
			return Integer.toString(how);
		}
	}

	public static String toString(int signal) {
		if(signal >= 0 && signal < SIGNALS.length) {
			return SIGNALS[signal];
		} else {
			return Integer.toString(signal);
		}
	}

	public static String sigev(int sigev_notify) {
		switch(sigev_notify) {
		case SIGEV_SIGNAL:
			return "SIGEV_SIGNAL";
		case SIGEV_NONE:
			return "SIGEV_NONE";
		case SIGEV_THREAD:
			return "SIGEV_THREAD";
		case SIGEV_THREAD_ID:
			return "SIGEV_THREAD_ID";
		default:
			return Integer.toString(sigev_notify);
		}
	}
}
