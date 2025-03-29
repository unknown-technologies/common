package com.unknown.posix.api.linux;

public class Ptrace {
	public static final int PTRACE_TRACEME = 0;
	public static final int PTRACE_PEEKTEXT = 1;
	public static final int PTRACE_PEEKDATA = 2;
	public static final int PTRACE_PEEKUSER = 3;
	public static final int PTRACE_POKETEXT = 4;
	public static final int PTRACE_POKEDATA = 5;
	public static final int PTRACE_POKEUSER = 6;
	public static final int PTRACE_CONT = 7;
	public static final int PTRACE_KILL = 8;
	public static final int PTRACE_SINGLESTEP = 9;
	public static final int PTRACE_GETREGS = 12;
	public static final int PTRACE_SETREGS = 13;
	public static final int PTRACE_GETFPREGS = 14;
	public static final int PTRACE_SETFPREGS = 15;
	public static final int PTRACE_ATTACH = 16;
	public static final int PTRACE_DETACH = 17;
	public static final int PTRACE_GETFPXREGS = 18;
	public static final int PTRACE_SETFPXREGS = 19;
	public static final int PTRACE_SYSCALL = 24;
	public static final int PTRACE_GET_THREAD_AREA = 25;
	public static final int PTRACE_SET_THREAD_AREA = 26;
	public static final int PTRACE_ARCH_PRCTL = 30;
	public static final int PTRACE_SYSEMU = 31;
	public static final int PTRACE_SYSEMU_SINGLESTEP = 32;
	public static final int PTRACE_SINGLEBLOCK = 33;
	public static final int PTRACE_SETOPTIONS = 0x4200;
	public static final int PTRACE_GETEVENTMSG = 0x4201;
	public static final int PTRACE_GETSIGINFO = 0x4202;
	public static final int PTRACE_SETSIGINFO = 0x4203;
	public static final int PTRACE_GETREGSET = 0x4204;
	public static final int PTRACE_SETREGSET = 0x4205;
	public static final int PTRACE_SEIZE = 0x4206;
	public static final int PTRACE_INTERRUPT = 0x4207;
	public static final int PTRACE_LISTEN = 0x4208;
	public static final int PTRACE_PEEKSIGINFO = 0x4209;
	public static final int PTRACE_GETSIGMASK = 0x420a;
	public static final int PTRACE_SETSIGMASK = 0x420b;
	public static final int PTRACE_SECCOMP_GET_FILTER = 0x420c;
	public static final int PTRACE_SECCOMP_GET_METADATA = 0x420d;
}
