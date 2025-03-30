package com.unknown.posix.api.io;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.unknown.util.BitTest;
import com.unknown.util.HexFormatter;

public class Fcntl {
	// @formatter:off
	public static final int O_ACCMODE    =  00000003;
	public static final int O_RDONLY     =  00000000;
	public static final int O_WRONLY     =  00000001;
	public static final int O_RDWR       =  00000002;
	public static final int O_CREAT      =  00000100;        /* not fcntl */
	public static final int O_EXCL       =  00000200;        /* not fcntl */
	public static final int O_NOCTTY     =  00000400;        /* not fcntl */
	public static final int O_TRUNC      =  00001000;        /* not fcntl */
	public static final int O_APPEND     =  00002000;
	public static final int O_NONBLOCK   =  00004000;
	public static final int O_DSYNC      =  00010000;        /* used to be O_SYNC, see below */
	public static final int FASYNC       =  00020000;        /* fcntl, for BSD compatibility */
	public static final int O_DIRECT     =  00400000;        /* direct disk access hint */
	public static final int O_LARGEFILE  =  00200000;
	public static final int O_DIRECTORY  =  00040000;        /* must be a directory */
	public static final int O_NOFOLLOW   =  00100000;        /* don't follow links */
	public static final int O_NOATIME    =  01000000;
	public static final int O_CLOEXEC    =  02000000;        /* set close_on_exec */
	public static final int O_TMPFILE    = 020000000;

	public static final int F_DUPFD         = 0;             /* dup */
	public static final int F_GETFD         = 1;             /* get close_on_exec */
	public static final int F_SETFD         = 2;             /* set/clear close_on_exec */
	public static final int F_GETFL         = 3;             /* get file->f_flags */
	public static final int F_SETFL         = 4;             /* set file->f_flags */
	public static final int F_GETLK         = 5;
	public static final int F_SETLK         = 6;
	public static final int F_SETLKW        = 7;
	public static final int F_SETOWN        = 8;             /* for sockets. */
	public static final int F_GETOWN        = 9;             /* for sockets. */
	public static final int F_SETSIG        = 10;            /* for sockets. */
	public static final int F_GETSIG        = 11;            /* for sockets. */
	public static final int F_GETLK64       = 12;            /*  using 'struct flock64' */
	public static final int F_SETLK64       = 13;
	public static final int F_SETLKW64      = 14;

	public static final int F_SETOWN_EX     = 15;
	public static final int F_GETOWN_EX     = 16;

	public static final int F_GETOWNER_UIDS = 17;

	public static final int F_LINUX_SPECIFIC_BASE = 1024;
	public static final int F_DUPFD_CLOEXEC = F_LINUX_SPECIFIC_BASE + 6;

	public static final int AT_FDCWD        = -100;          /* Special value used to indicate
	                                                            the *at functions should use the
	                                                            current working directory. */

	public static final int AT_SYMLINK_NOFOLLOW   = 0x100;   /* Do not follow symbolic links. */
	public static final int AT_REMOVEDIR          = 0x200;   /* Remove directory instead of
	                                                            unlinking file. */
	public static final int AT_SYMLINK_FOLLOW     = 0x400;   /* Follow symbolic links. */
	public static final int AT_NO_AUTOMOUNT       = 0x800;   /* Suppress terminal automount traversal */
	public static final int AT_EMPTY_PATH         = 0x1000;  /* Allow empty relative pathname */

	public static final int AT_STATX_SYNC_TYPE    = 0x6000;  /* Type of synchronisation required from statx() */
	public static final int AT_STATX_SYNC_AS_STAT = 0x0000;  /* - Do whatever stat() does */
	public static final int AT_STATX_FORCE_SYNC   = 0x2000;  /* - Force the attributes to be sync'd with the server */
	public static final int AT_STATX_DONT_SYNC    = 0x4000;  /* - Don't sync attributes with the server */

	public static final int AT_RECURSIVE          = 0x8000;  /* Apply to the entire subtree */

	public static final int FD_CLOEXEC      = 1;             /* actually anything with low bit set goes */
	// @formatter:on

	public static String flags(int flags) {
		List<String> result = new ArrayList<>();
		int rw = flags & O_ACCMODE;
		switch(rw) {
		case O_RDONLY:
			result.add("O_RDONLY");
			break;
		case O_WRONLY:
			result.add("O_WRONLY");
			break;
		case O_RDWR:
			result.add("O_RDWR");
			break;
		}
		if(BitTest.test(flags, O_CREAT)) {
			result.add("O_CREAT");
		}
		if(BitTest.test(flags, O_EXCL)) {
			result.add("O_EXCL");
		}
		if(BitTest.test(flags, O_NOCTTY)) {
			result.add("O_NOCTTY");
		}
		if(BitTest.test(flags, O_TRUNC)) {
			result.add("O_TRUNC");
		}
		if(BitTest.test(flags, O_APPEND)) {
			result.add("O_APPEND");
		}
		if(BitTest.test(flags, O_NONBLOCK)) {
			result.add("O_NONBLOCK");
		}
		if(BitTest.test(flags, O_DSYNC)) {
			result.add("O_DSYNC");
		}
		if(BitTest.test(flags, FASYNC)) {
			result.add("FASYNC");
		}
		if(BitTest.test(flags, O_DIRECT)) {
			result.add("O_DIRECT");
		}
		if(BitTest.test(flags, O_LARGEFILE)) {
			result.add("O_LARGEFILE");
		}
		if(BitTest.test(flags, O_DIRECTORY)) {
			result.add("O_DIRECTORY");
		}
		if(BitTest.test(flags, O_NOFOLLOW)) {
			result.add("O_NOFOLLOW");
		}
		if(BitTest.test(flags, O_NOATIME)) {
			result.add("O_NOATIME");
		}
		if(BitTest.test(flags, O_CLOEXEC)) {
			result.add("O_CLOEXEC");
		}
		if(BitTest.test(flags, O_TMPFILE)) {
			result.add("O_TMPFILE");
		}
		if(result.size() == 0) {
			return "0";
		} else {
			return result.stream().collect(Collectors.joining("|"));
		}
	}

	public static String fcntl(int cmd) {
		switch(cmd) {
		case Fcntl.F_GETFD:
			return "F_GETFD";
		case Fcntl.F_SETFD:
			return "F_SETFD";
		case Fcntl.F_GETFL:
			return "F_GETFL";
		case Fcntl.F_SETFL:
			return "F_SETFL";
		case Fcntl.F_DUPFD:
			return "F_DUPFD";
		case Fcntl.F_DUPFD_CLOEXEC:
			return "F_DUPFD_CLOEXEC";
		default:
			return Integer.toString(cmd);
		}
	}

	public static String statx(int flags) {
		if(flags == AT_STATX_SYNC_AS_STAT) {
			return "AT_STATX_SYNC_AS_STAT";
		}

		List<String> result = new ArrayList<>();
		int remainder = flags;
		if(BitTest.test(flags, AT_EMPTY_PATH)) {
			result.add("AT_EMPTY_PATH");
			remainder &= ~AT_EMPTY_PATH;
		}
		if(BitTest.test(flags, AT_NO_AUTOMOUNT)) {
			result.add("AT_NO_AUTOMOUNT");
			remainder &= ~AT_NO_AUTOMOUNT;
		}
		if(BitTest.test(flags, AT_SYMLINK_NOFOLLOW)) {
			result.add("AT_SYMLINK_NOFOLLOW");
			remainder &= ~AT_SYMLINK_NOFOLLOW;
		}
		if(BitTest.test(flags, AT_STATX_FORCE_SYNC)) {
			result.add("AT_STATX_FORCE_SYNC");
			remainder &= ~AT_STATX_FORCE_SYNC;
		}
		if(BitTest.test(flags, AT_STATX_DONT_SYNC)) {
			result.add("AT_STATX_DONT_SYNC");
			remainder &= ~AT_STATX_DONT_SYNC;
		}
		if(remainder != 0) {
			result.add("0x" + HexFormatter.tohex(Integer.toUnsignedLong(remainder)));
		}
		if(result.isEmpty()) {
			return "0";
		} else {
			return String.join("|", result);
		}
	}

	public static String fd(int fd) {
		if(fd == AT_FDCWD) {
			return "AT_FDCWD";
		} else {
			return Integer.toString(fd);
		}
	}
}
