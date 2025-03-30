package com.unknown.posix.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Errno {
	// @formatter:off
	public static final int EPERM           =  1;     /* Operation not permitted */
	public static final int ENOENT          =  2;     /* No such file or directory */
	public static final int ESRCH           =  3;     /* No such process */
	public static final int EINTR           =  4;     /* Interrupted system call */
	public static final int EIO             =  5;     /* I/O error */
	public static final int ENXIO           =  6;     /* No such device or address */
	public static final int E2BIG           =  7;     /* Argument list too long */
	public static final int ENOEXEC         =  8;     /* Exec format error */
	public static final int EBADF           =  9;     /* Bad file number */
	public static final int ECHILD          = 10;     /* No child processes */
	public static final int EAGAIN          = 11;     /* Try again */
	public static final int ENOMEM          = 12;     /* Out of memory */
	public static final int EACCES          = 13;     /* Permission denied */
	public static final int EFAULT          = 14;     /* Bad address */
	public static final int ENOTBLK         = 15;     /* Block device required */
	public static final int EBUSY           = 16;     /* Device or resource busy */
	public static final int EEXIST          = 17;     /* File exists */
	public static final int EXDEV           = 18;     /* Cross-device link */
	public static final int ENODEV          = 19;     /* No such device */
	public static final int ENOTDIR         = 20;     /* Not a directory */
	public static final int EISDIR          = 21;     /* Is a directory */
	public static final int EINVAL          = 22;     /* Invalid argument */
	public static final int ENFILE          = 23;     /* File table overflow */
	public static final int EMFILE          = 24;     /* Too many open files */
	public static final int ENOTTY          = 25;     /* Not a typewriter */
	public static final int ETXTBSY         = 26;     /* Text file busy */
	public static final int EFBIG           = 27;     /* File too large */
	public static final int ENOSPC          = 28;     /* No space left on device */
	public static final int ESPIPE          = 29;     /* Illegal seek */
	public static final int EROFS           = 30;     /* Read-only file system */
	public static final int EMLINK          = 31;     /* Too many links */
	public static final int EPIPE           = 32;     /* Broken pipe */
	public static final int EDOM            = 33;     /* Math argument out of domain of func */
	public static final int ERANGE          = 34;     /* Math result not representable */

	public static final int EDEADLK         = 35;     /* Resource deadlock would occur */
	public static final int ENAMETOOLONG    = 36;     /* File name too long */
	public static final int ENOLCK          = 37;     /* No record locks available */

	/*
	 * This error code is special: arch syscall entry code will return
	 * -ENOSYS if users try to call a syscall that doesn't exist. To keep
	 * failures of syscalls that really do exist distinguishable from
	 * failures due to attempts to use a nonexistent syscall, syscall
	 * implementations should refrain from returning -ENOSYS.
	 */
	public static final int ENOSYS          = 38;     /* Invalid system call number */

	public static final int ENOTEMPTY       = 39;     /* Directory not empty */
	public static final int ELOOP           = 40;     /* Too many symbolic links encountered */
	public static final int EWOULDBLOCK     = EAGAIN; /* Operation would block */
	public static final int ENOMSG          = 42;     /* No message of desired type */
	public static final int EIDRM           = 43;     /* Identifier removed */
	public static final int ECHRNG          = 44;     /* Channel number out of range */
	public static final int EL2NSYNC        = 45;     /* Level 2 not synchronized */
	public static final int EL3HLT          = 46;     /* Level 3 halted */
	public static final int EL3RST          = 47;     /* Level 3 reset */
	public static final int ELNRNG          = 48;     /* Link number out of range */
	public static final int EUNATCH         = 49;     /* Protocol driver not attached */
	public static final int ENOCSI          = 50;     /* No CSI structure available */
	public static final int EL2HLT          = 51;     /* Level 2 halted */
	public static final int EBADE           = 52;     /* Invalid exchange */
	public static final int EBADR           = 53;     /* Invalid request descriptor */
	public static final int EXFULL          = 54;     /* Exchange full */
	public static final int ENOANO          = 55;     /* No anode */
	public static final int EBADRQC         = 56;     /* Invalid request code */
	public static final int EBADSLT         = 57;     /* Invalid slot */

	public static final int EDEADLOCK       = 58;     /* File locking deadlock error */

	public static final int EBFONT          = 59;     /* Bad font file format */
	public static final int ENOSTR          = 60;     /* Device not a stream */
	public static final int ENODATA         = 61;     /* No data available */
	public static final int ETIME           = 62;     /* Timer expired */
	public static final int ENOSR           = 63;     /* Out of streams resources */
	public static final int ENONET          = 64;     /* Machine is not on the network */
	public static final int ENOPKG          = 65;     /* Package not installed */
	public static final int EREMOTE         = 66;     /* Object is remote */
	public static final int ENOLINK         = 67;     /* Link has been severed */
	public static final int EADV            = 68;     /* Advertise error */
	public static final int ESRMNT          = 69;     /* Srmount error */
	public static final int ECOMM           = 70;     /* Communication error on send */
	public static final int EPROTO          = 71;     /* Protocol error */
	public static final int EMULTIHOP       = 72;     /* Multihop attempted */
	public static final int EDOTDOT         = 73;     /* RFS specific error */
	public static final int EBADMSG         = 74;     /* Not a data message */
	public static final int EOVERFLOW       = 75;     /* Value too large for defined data type */
	public static final int ENOTUNIQ        = 76;     /* Name not unique on network */
	public static final int EBADFD          = 77;     /* File descriptor in bad state */
	public static final int EREMCHG         = 78;     /* Remote address changed */
	public static final int ELIBACC         = 79;     /* Can not access a needed shared library */
	public static final int ELIBBAD         = 80;     /* Accessing a corrupted shared library */
	public static final int ELIBSCN         = 81;     /* .lib section in a.out corrupted */
	public static final int ELIBMAX         = 82;     /* Attempting to link in too many shared libraries */
	public static final int ELIBEXEC        = 83;     /* Cannot exec a shared library directly */
	public static final int EILSEQ          = 84;     /* Illegal byte sequence */
	public static final int ERESTART        = 85;     /* Interrupted system call should be restarted */
	public static final int ESTRPIPE        = 86;     /* Streams pipe error */
	public static final int EUSERS          = 87;     /* Too many users */
	public static final int ENOTSOCK        = 88;     /* Socket operation on non-socket */
	public static final int EDESTADDRREQ    = 89;     /* Destination address required */
	public static final int EMSGSIZE        = 90;     /* Message too long */
	public static final int EPROTOTYPE      = 91;     /* Protocol wrong type for socket */
	public static final int ENOPROTOOPT     = 92;     /* Protocol not available */
	public static final int EPROTONOSUPPORT = 93;     /* Protocol not supported */
	public static final int ESOCKTNOSUPPORT = 94;     /* Socket type not supported */
	public static final int EOPNOTSUPP      = 95;     /* Operation not supported on transport endpoint */
	public static final int EPFNOSUPPORT    = 96;     /* Protocol family not supported */
	public static final int EAFNOSUPPORT    = 97;     /* Address family not supported by protocol */
	public static final int EADDRINUSE      = 98;     /* Address already in use */
	public static final int EADDRNOTAVAIL   = 99;     /* Cannot assign requested address */
	public static final int ENETDOWN        = 100;    /* Network is down */
	public static final int ENETUNREACH     = 101;    /* Network is unreachable */
	public static final int ENETRESET       = 102;    /* Network dropped connection because of reset */
	public static final int ECONNABORTED    = 103;    /* Software caused connection abort */
	public static final int ECONNRESET      = 104;    /* Connection reset by peer */
	public static final int ENOBUFS         = 105;    /* No buffer space available */
	public static final int EISCONN         = 106;    /* Transport endpoint is already connected */
	public static final int ENOTCONN        = 107;    /* Transport endpoint is not connected */
	public static final int ESHUTDOWN       = 108;    /* Cannot send after transport endpoint shutdown */
	public static final int ETOOMANYREFS    = 109;    /* Too many references: cannot splice */
	public static final int ETIMEDOUT       = 110;    /* Connection timed out */
	public static final int ECONNREFUSED    = 111;    /* Connection refused */
	public static final int EHOSTDOWN       = 112;    /* Host is down */
	public static final int EHOSTUNREACH    = 113;    /* No route to host */
	public static final int EALREADY        = 114;    /* Operation already in progress */
	public static final int EINPROGRESS     = 115;    /* Operation now in progress */
	public static final int ESTALE          = 116;    /* Stale file handle */
	public static final int EUCLEAN         = 117;    /* Structure needs cleaning */
	public static final int ENOTNAM         = 118;    /* Not a XENIX named type file */
	public static final int ENAVAIL         = 119;    /* No XENIX semaphores available */
	public static final int EISNAM          = 120;    /* Is a named type file */
	public static final int EREMOTEIO       = 121;    /* Remote I/O error */
	public static final int EDQUOT          = 122;    /* Quota exceeded */

	public static final int ENOMEDIUM       = 123;    /* No medium found */
	public static final int EMEDIUMTYPE     = 124;    /* Wrong medium type */
	public static final int ECANCELED       = 125;    /* Operation Canceled */
	public static final int ENOKEY          = 126;    /* Required key not available */
	public static final int EKEYEXPIRED     = 127;    /* Key has expired */
	public static final int EKEYREVOKED     = 128;    /* Key has been revoked */
	public static final int EKEYREJECTED    = 129;    /* Key was rejected by service */

	/* for robust mutexes */
	public static final int EOWNERDEAD      = 130;    /* Owner died */
	public static final int ENOTRECOVERABLE = 131;    /* State not recoverable */

	public static final int ERFKILL         = 132;    /* Operation not possible due to RF-kill */

	public static final int EHWPOISON       = 133;    /* Memory page has hardware error */
	// @formatter:on

	private static final Map<Integer, String> names;
	private static final Map<Integer, String> constants;

	static {
		names = new HashMap<>();
		names.put(1, "Operation not permitted");
		names.put(2, "No such file or directory");
		names.put(3, "No such process");
		names.put(4, "Interrupted system call");
		names.put(5, "I/O error");
		names.put(6, "No such device or address");
		names.put(7, "Argument list too long");
		names.put(8, "Exec format error");
		names.put(9, "Bad file number");
		names.put(10, "No child processes");
		names.put(11, "Try again");
		names.put(12, "Out of memory");
		names.put(13, "Permission denied");
		names.put(14, "Bad address");
		names.put(15, "Block device required");
		names.put(16, "Device or resource busy");
		names.put(17, "File exists");
		names.put(18, "Cross-device link");
		names.put(19, "No such device");
		names.put(20, "Not a directory");
		names.put(21, "Is a directory");
		names.put(22, "Invalid argument");
		names.put(23, "File table overflow");
		names.put(24, "Too many open files");
		names.put(25, "Not a typewriter");
		names.put(26, "Text file busy");
		names.put(27, "File too large");
		names.put(28, "No space left on device");
		names.put(29, "Illegal seek");
		names.put(30, "Read-only file system");
		names.put(31, "Too many links");
		names.put(32, "Broken pipe");
		names.put(33, "Math argument out of domain of func");
		names.put(34, "Math result not representable");
		names.put(35, "Resource deadlock would occur");
		names.put(36, "File name too long");
		names.put(37, "No record locks available");
		names.put(38, "Invalid system call number");

		names.put(39, "Directory not empty");
		names.put(40, "Too many symbolic links encountered");
		names.put(42, "No message of desired type");
		names.put(43, "Identifier removed");
		names.put(44, "Channel number out of range");
		names.put(45, "Level 2 not synchronized");
		names.put(46, "Level 3 halted");
		names.put(47, "Level 3 reset");
		names.put(48, "Link number out of range");
		names.put(49, "Protocol driver not attached");
		names.put(50, "No CSI structure available");
		names.put(51, "Level 2 halted");
		names.put(52, "Invalid exchange");
		names.put(53, "Invalid request descriptor");
		names.put(54, "Exchange full");
		names.put(55, "No anode");
		names.put(56, "Invalid request code");
		names.put(57, "Invalid slot");

		names.put(58, "File locking deadlock error");

		names.put(59, "Bad font file format");
		names.put(60, "Device not a stream");
		names.put(61, "No data available");
		names.put(62, "Timer expired");
		names.put(63, "Out of streams resources");
		names.put(64, "Machine is not on the network");
		names.put(65, "Package not installed");
		names.put(66, "Object is remote");
		names.put(67, "Link has been severed");
		names.put(68, "Advertise error");
		names.put(69, "Srmount error");
		names.put(70, "Communication error on send");
		names.put(71, "Protocol error");
		names.put(72, "Multihop attempted");
		names.put(73, "RFS specific error");
		names.put(74, "Not a data message");
		names.put(75, "Value too large for defined data type");
		names.put(76, "Name not unique on network");
		names.put(77, "File descriptor in bad state");
		names.put(78, "Remote address changed");
		names.put(79, "Can not access a needed shared library");
		names.put(80, "Accessing a corrupted shared library");
		names.put(81, ".lib section in a.out corrupted");
		names.put(82, "Attempting to link in too many shared libraries");
		names.put(83, "Cannot exec a shared library directly");
		names.put(84, "Illegal byte sequence");
		names.put(85, "Interrupted system call should be restarted");
		names.put(86, "Streams pipe error");
		names.put(87, "Too many users");
		names.put(88, "Socket operation on non-socket");
		names.put(89, "Destination address required");
		names.put(90, "Message too long");
		names.put(91, "Protocol wrong type for socket");
		names.put(92, "Protocol not available");
		names.put(93, "Protocol not supported");
		names.put(94, "Socket type not supported");
		names.put(95, "Operation not supported on transport endpoint");
		names.put(96, "Protocol family not supported");
		names.put(97, "Address family not supported by protocol");
		names.put(98, "Address already in use");
		names.put(99, "Cannot assign requested address");
		names.put(100, "Network is down");
		names.put(101, "Network is unreachable");
		names.put(102, "Network dropped connection because of reset");
		names.put(103, "Software caused connection abort");
		names.put(104, "Connection reset by peer");
		names.put(105, "No buffer space available");
		names.put(106, "Transport endpoint is already connected");
		names.put(107, "Transport endpoint is not connected");
		names.put(108, "Cannot send after transport endpoint shutdown");
		names.put(109, "Too many references: cannot splice");
		names.put(110, "Connection timed out");
		names.put(111, "Connection refused");
		names.put(112, "Host is down");
		names.put(113, "No route to host");
		names.put(114, "Operation already in progress");
		names.put(115, "Operation now in progress");
		names.put(116, "Stale file handle");
		names.put(117, "Structure needs cleaning");
		names.put(118, "Not a XENIX named type file");
		names.put(119, "No XENIX semaphores available");
		names.put(120, "Is a named type file");
		names.put(121, "Remote I/O error");
		names.put(122, "Quota exceeded");

		names.put(123, "No medium found");
		names.put(124, "Wrong medium type");
		names.put(125, "Operation Canceled");
		names.put(126, "Required key not available");
		names.put(127, "Key has expired");
		names.put(128, "Key has been revoked");
		names.put(129, "Key was rejected by service");

		names.put(130, "Owner died");
		names.put(131, "State not recoverable");

		names.put(132, "Operation not possible due to RF-kill");

		names.put(133, "Memory page has hardware error");

		constants = new HashMap<>();
		constants.put(1, "EPERM");
		constants.put(2, "ENOENT");
		constants.put(3, "ESRCH");
		constants.put(4, "EINTR");
		constants.put(5, "EIO");
		constants.put(6, "ENXIO");
		constants.put(7, "E2BIG");
		constants.put(8, "ENOEXEC");
		constants.put(9, "EBADF");
		constants.put(10, "ECHILD");
		constants.put(11, "EAGAIN");
		constants.put(12, "ENOMEM");
		constants.put(13, "EACCES");
		constants.put(14, "EFAULT");
		constants.put(15, "ENOTBLK");
		constants.put(16, "EBUSY");
		constants.put(17, "EEXIST");
		constants.put(18, "EXDEV");
		constants.put(19, "ENODEV");
		constants.put(20, "ENOTDIR");
		constants.put(21, "EISDIR");
		constants.put(22, "EINVAL");
		constants.put(23, "ENFILE");
		constants.put(24, "EMFILE");
		constants.put(25, "ENOTTY");
		constants.put(26, "ETXTBSY");
		constants.put(27, "EFBIG");
		constants.put(28, "ENOSPC");
		constants.put(29, "ESPIPE");
		constants.put(30, "EROFS");
		constants.put(31, "EMLINK");
		constants.put(32, "EPIPE");
		constants.put(33, "EDOM");
		constants.put(34, "ERANGE");

		constants.put(35, "EDEADLK");
		constants.put(36, "ENAMETOOLONG");
		constants.put(37, "ENOLCK");

		constants.put(38, "ENOSYS");

		constants.put(39, "ENOTEMPTY");
		constants.put(40, "ELOOP");
		constants.put(41, "EWOULDBLOCK");
		constants.put(42, "ENOMSG");
		constants.put(43, "EIDRM");
		constants.put(44, "ECHRNG");
		constants.put(45, "EL2NSYNC");
		constants.put(46, "EL3HLT");
		constants.put(47, "EL3RST");
		constants.put(48, "ELNRNG");
		constants.put(49, "EUNATCH");
		constants.put(50, "ENOCSI");
		constants.put(51, "EL2HLT");
		constants.put(52, "EBADE");
		constants.put(53, "EBADR");
		constants.put(54, "EXFULL");
		constants.put(55, "ENOANO");
		constants.put(56, "EBADRQC");
		constants.put(57, "EBADSLT");
		constants.put(58, "EDEADLOCK");
		constants.put(59, "EBFONT");
		constants.put(60, "ENOSTR");
		constants.put(61, "ENODATA");
		constants.put(62, "ETIME");
		constants.put(63, "ENOSR");
		constants.put(64, "ENONET");
		constants.put(65, "ENOPKG");
		constants.put(66, "EREMOTE");
		constants.put(67, "ENOLINK");
		constants.put(68, "EADV");
		constants.put(69, "ESRMNT");
		constants.put(70, "ECOMM");
		constants.put(71, "EPROTO");
		constants.put(72, "EMULTIHOP");
		constants.put(73, "EDOTDOT");
		constants.put(74, "EBADMSG");
		constants.put(75, "EOVERFLOW");
		constants.put(76, "ENOTUNIQ");
		constants.put(77, "EBADFD");
		constants.put(78, "EREMCHG");
		constants.put(79, "ELIBACC");
		constants.put(80, "ELIBBAD");
		constants.put(81, "ELIBSCN");
		constants.put(82, "ELIBMAX");
		constants.put(83, "ELIBEXEC");
		constants.put(84, "EILSEQ");
		constants.put(85, "ERESTART");
		constants.put(86, "ESTRPIPE");
		constants.put(87, "EUSERS");
		constants.put(88, "ENOTSOCK");
		constants.put(89, "EDESTADDRREQ");
		constants.put(90, "EMSGSIZE");
		constants.put(91, "EPROTOTYPE");
		constants.put(92, "ENOPROTOOPT");
		constants.put(93, "EPROTONOSUPPORT");
		constants.put(94, "ESOCKTNOSUPPORT");
		constants.put(95, "EOPNOTSUPP");
		constants.put(96, "EPFNOSUPPORT");
		constants.put(97, "EAFNOSUPPORT");
		constants.put(98, "EADDRINUSE");
		constants.put(99, "EADDRNOTAVAIL");
		constants.put(100, "ENETDOWN");
		constants.put(101, "ENETUNREACH");
		constants.put(102, "ENETRESET");
		constants.put(103, "ECONNABORTED");
		constants.put(104, "ECONNRESET");
		constants.put(105, "ENOBUFS");
		constants.put(106, "EISCONN");
		constants.put(107, "ENOTCONN");
		constants.put(108, "ESHUTDOWN");
		constants.put(109, "ETOOMANYREFS");
		constants.put(110, "ETIMEDOUT");
		constants.put(111, "ECONNREFUSED");
		constants.put(112, "EHOSTDOWN");
		constants.put(113, "EHOSTUNREACH");
		constants.put(114, "EALREADY");
		constants.put(115, "EINPROGRESS");
		constants.put(116, "ESTALE");
		constants.put(117, "EUCLEAN");
		constants.put(118, "ENOTNAM");
		constants.put(119, "ENAVAIL");
		constants.put(120, "EISNAM");
		constants.put(121, "EREMOTEIO");
		constants.put(122, "EDQUOT");

		constants.put(123, "ENOMEDIUM");
		constants.put(124, "EMEDIUMTYPE");
		constants.put(125, "ECANCELED");
		constants.put(126, "ENOKEY");
		constants.put(127, "EKEYEXPIRED");
		constants.put(128, "EKEYREVOKED");
		constants.put(129, "EKEYREJECTED");

		constants.put(130, "EOWNERDEAD");
		constants.put(131, "ENOTRECOVERABLE");

		constants.put(132, "ERFKILL");

		constants.put(133, "EHWPOISON");
	}

	public static Set<Integer> getErrorNumbers() {
		return Collections.unmodifiableSet(names.keySet());
	}

	public static String toString(int errno) {
		String name = names.get(errno);
		if(name == null) {
			return "Unknown error " + Integer.toString(errno);
		} else {
			return name;
		}
	}

	public static String getConstant(int errno) {
		String name = constants.get(errno);
		if(name == null) {
			return Integer.toString(errno);
		} else {
			return name;
		}
	}
}
