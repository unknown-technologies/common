package com.unknown.posix.api.linux;

public class Net {
	// @formatter:off
	public static final int SYS_SOCKET      = 1;  /* sys_socket(2)                */
	public static final int SYS_BIND        = 2;  /* sys_bind(2)                  */
	public static final int SYS_CONNECT     = 3;  /* sys_connect(2)               */
	public static final int SYS_LISTEN      = 4;  /* sys_listen(2)                */
	public static final int SYS_ACCEPT      = 5;  /* sys_accept(2)                */
	public static final int SYS_GETSOCKNAME = 6;  /* sys_getsockname(2)           */
	public static final int SYS_GETPEERNAME = 7;  /* sys_getpeername(2)           */
	public static final int SYS_SOCKETPAIR  = 8;  /* sys_socketpair(2)            */
	public static final int SYS_SEND        = 9;  /* sys_send(2)                  */
	public static final int SYS_RECV        = 10; /* sys_recv(2)                  */
	public static final int SYS_SENDTO      = 11; /* sys_sendto(2)                */
	public static final int SYS_RECVFROM    = 12; /* sys_recvfrom(2)              */
	public static final int SYS_SHUTDOWN    = 13; /* sys_shutdown(2)              */
	public static final int SYS_SETSOCKOPT  = 14; /* sys_setsockopt(2)            */
	public static final int SYS_GETSOCKOPT  = 15; /* sys_getsockopt(2)            */
	public static final int SYS_SENDMSG     = 16; /* sys_sendmsg(2)               */
	public static final int SYS_RECVMSG     = 17; /* sys_recvmsg(2)               */
	public static final int SYS_ACCEPT4     = 18; /* sys_accept4(2)               */
	public static final int SYS_RECVMMSG    = 19; /* sys_recvmmsg(2)              */
	public static final int SYS_SENDMMSG    = 20; /* sys_sendmmsg(2)              */
	// @formatter:on
}
