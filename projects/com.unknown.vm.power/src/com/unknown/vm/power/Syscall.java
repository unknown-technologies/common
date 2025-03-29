package com.unknown.vm.power;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.unknown.posix.api.Errno;
import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;
import com.unknown.vm.posix.PosixEnvironment;
import com.unknown.vm.posix.SyscallException;

public class Syscall {
	public static final int SYS_exit = 1;
	public static final int SYS_read = 3;
	public static final int SYS_write = 4;
	public static final int SYS_open = 5;
	public static final int SYS_close = 6;
	public static final int SYS_waitpid = 7;
	public static final int SYS_unlink = 10;
	public static final int SYS_chdir = 12;
	public static final int SYS_chmod = 15;
	public static final int SYS_lseek = 19;
	public static final int SYS_getpid = 20;
	public static final int SYS_mount = 21;
	public static final int SYS_umount = 22;
	public static final int SYS_setuid = 23;
	public static final int SYS_getuid = 24;
	public static final int SYS_alarm = 27;
	public static final int SYS_utime = 30;
	public static final int SYS_access = 33;
	public static final int SYS_mkdir = 39;
	public static final int SYS_dup = 41;
	public static final int SYS_times = 43;
	public static final int SYS_brk = 45;
	public static final int SYS_setgid = 46;
	public static final int SYS_getgid = 47;
	public static final int SYS_geteuid = 49;
	public static final int SYS_getegid = 50;
	public static final int SYS_umount2 = 52;
	public static final int SYS_ioctl = 54;
	public static final int SYS_umask = 60;
	public static final int SYS_dup2 = 63;
	public static final int SYS_getppid = 64;
	public static final int SYS_getrusage = 77;
	public static final int SYS_gettimeofday = 78;
	public static final int SYS_settimeofday = 79;
	public static final int SYS_symlink = 83;
	public static final int SYS_readlink = 85;
	public static final int SYS_mmap = 90;
	public static final int SYS_munmap = 91;
	public static final int SYS_ftruncate = 93;
	public static final int SYS_socketcall = 102;
	public static final int SYS_wait4 = 114;
	public static final int SYS_sysinfo = 116;
	public static final int SYS_clone = 120;
	public static final int SYS_uname = 122;
	public static final int SYS_mprotect = 125;
	public static final int SYS__llseek = 140;
	public static final int SYS_getdents = 141;
	public static final int SYS_readv = 145;
	public static final int SYS_writev = 146;
	public static final int SYS_poll = 167;
	public static final int SYS_rt_sigaction = 173;
	public static final int SYS_rt_sigprocmask = 174;
	public static final int SYS_rt_sigpending = 175;
	public static final int SYS_chown = 181;
	public static final int SYS_getcwd = 182;
	public static final int SYS_sigaltstack = 185;
	public static final int SYS_sendfile = 186;
	public static final int SYS_ugetrlimit = 190;
	public static final int SYS_mmap2 = 192;
	public static final int SYS_stat64 = 195; // 32-bit only
	public static final int SYS_lstat64 = 196; // 32-bit only
	public static final int SYS_fstat64 = 197; // 32-bit only
	public static final int SYS_getdents64 = 202;
	public static final int SYS_fcntl64 = 204; // 32-bit only
	public static final int SYS_gettid = 207;
	public static final int SYS_sendfile64 = 226; // 32-bit only
	public static final int SYS_exit_group = 234;
	public static final int SYS_clock_gettime = 246;
	public static final int SYS_clock_getres = 247;
	public static final int SYS_fadvise64_64 = 254; // 32-bit only
	public static final int SYS_tgkill = 250;
	public static final int SYS_openat = 286;
	public static final int SYS_fstatat64 = 291; // 32-bit only
	public static final int SYS_unlinkat = 292;
	public static final int SYS_symlinkat = 295;
	public static final int SYS_dup3 = 316;
	public static final int SYS_prlimit64 = 325;
	public static final int SYS_socket = 326;
	public static final int SYS_bind = 327;
	public static final int SYS_connect = 328;
	public static final int SYS_getsockname = 331;
	public static final int SYS_getpeername = 332;
	public static final int SYS_send = 334;
	public static final int SYS_recv = 336;
	public static final int SYS_recvfrom = 337;
	public static final int SYS_shutdown = 338;
	public static final int SYS_setsockopt = 339;
	public static final int SYS_recvmsg = 342;
	public static final int SYS_sendmmsg = 349;
	public static final int SYS_statx = 383;

	private static final Logger log = Trace.create(Syscall.class);

	private PosixEnvironment posix;
	private PowerState state;

	public Syscall(PowerState state, PosixEnvironment posix) {
		this.state = state;
		this.posix = posix;
	}

	private long brk(long addr) {
		if(addr == 0) {
			long brk = state.getMemory().brk();
			if(posix.isStrace()) {
				log.log(Level.INFO, () -> String.format("brk(NULL) = 0x%016x", brk));
			}
			if(posix.getTrace() != null) {
				try {
					posix.getTrace().brk(addr, brk);
				} catch(IOException e) {
					log.log(Levels.ERROR, "Failed to write brk event: " + e.getMessage(), e);
					posix.setTrace(null);
				}
			}
			return brk;
		} else {
			long newbrk = state.ppc64 ? addr : Integer.toUnsignedLong((int) addr);
			long brk = state.getMemory().brk(newbrk);
			if(posix.isStrace()) {
				log.log(Level.INFO, () -> String.format("brk(0x%016x) = 0x%016x", newbrk, brk));
			}
			if(posix.getTrace() != null) {
				try {
					posix.getTrace().brk(addr, brk);
				} catch(IOException e) {
					log.log(Levels.ERROR, "Failed to write brk event: " + e.getMessage(), e);
					posix.setTrace(null);
				}
			}
			return brk;
		}
	}

	public long execute(int nr, long a1, long a2, long a3, long a4, long a5, long a6, long a7)
			throws SyscallException {
		if(!state.ppc64) {
			return execute32(nr, a1, a2, a3, a4, a5, a6, a7);
		} else {
			return execute64(nr, a1, a2, a3, a4, a5, a6, a7);
		}
	}

	@SuppressWarnings("unused")
	public long execute32(int nr, long a1, long a2, long a3, long a4, long a5, long a6, long a7)
			throws SyscallException {
		switch(nr) {
		case SYS_exit:
			posix.exit((int) a1);
			return 0;
		case SYS_exit_group:
			posix.exit_group((int) a1);
			return 0;
		case SYS_read:
			return posix.read((int) a1, a2, Integer.toUnsignedLong((int) a3));
		case SYS_write:
			return posix.write((int) a1, a2, Integer.toUnsignedLong((int) a3));
		case SYS_open:
			return posix.open(a1, (int) a2, (int) a3);
		case SYS_close:
			return posix.close((int) a1);
		case SYS_unlink:
			return posix.unlink(a1);
		case SYS_chdir:
			return posix.chdir(a1);
		case SYS_chmod:
			return posix.chmod(a1, (int) a2);
		case SYS_getpid:
			return posix.getpid();
		case SYS_mount:
			return posix.mount(a1, a2, a3, a4, a5);
		case SYS_umount:
			return posix.umount(a1);
		case SYS_setuid:
			return posix.setuid((int) a1);
		case SYS_getuid:
			return posix.getuid();
		case SYS_utime:
			return posix.utime(a1, a2);
		case SYS_access:
			return posix.access(a1, (int) a2);
		case SYS_mkdir:
			return posix.mkdir(a1, (int) a2);
		case SYS_dup:
			return posix.dup((int) a1);
		case SYS_times:
			return posix.times32(a1);
		case SYS_brk:
			return brk(a1);
		case SYS_setgid:
			return posix.setgid((int) a1);
		case SYS_getgid:
			return posix.getgid();
		case SYS_geteuid:
			return posix.geteuid();
		case SYS_getegid:
			return posix.getegid();
		case SYS_umount2:
			return posix.umount2(a1, (int) a2);
		case SYS_ioctl:
			return posix.ioctl((int) a1, Integer.toUnsignedLong((int) a2), a3);
		case SYS_dup2:
			return posix.dup2((int) a1, (int) a2);
		case SYS_gettimeofday:
			return posix.gettimeofday32(a1, a2);
		case SYS_symlink:
			return posix.symlink(a1, a2);
		case SYS_readlink:
			return posix.readlink(a1, a2, (int) a3);
		case SYS_mmap:
			return posix.mmap(a1, Integer.toUnsignedLong((int) a2), (int) a3, (int) a4, (int) a5,
					Integer.toUnsignedLong((int) a6));
		case SYS_munmap:
			return posix.munmap(a1, Integer.toUnsignedLong((int) a2));
		case SYS_ftruncate:
			return posix.ftruncate((int) a1, (int) a2);
		case SYS_socketcall:
			return posix.socketcall((int) a1, a2);
		case SYS_sysinfo:
			return posix.sysinfo32(a1);
		case SYS_uname:
			return posix.uname(a1);
		case SYS_mprotect:
			return posix.mprotect(a1, Integer.toUnsignedLong((int) a2), (int) a3);
		case SYS__llseek:
			return posix._llseek((int) a1, (int) a2, (int) a3, a4, (int) a5);
		case SYS_getdents:
			return posix.getdents_32((int) a1, a2, (int) a3);
		case SYS_readv:
			return posix.readv32((int) a1, a2, (int) a3);
		case SYS_writev:
			return posix.writev32((int) a1, a2, (int) a3);
		case SYS_poll:
			return posix.poll(a1, (int) a2, (int) a3);
		case SYS_rt_sigaction:
			return posix.sigaction32((int) a1, a2, a3);
		case SYS_rt_sigprocmask:
			return posix.sigprocmask32((int) a1, a2, a3, (int) a4);
		case SYS_chown:
			return posix.chown(a1, (int) a2, (int) a3);
		case SYS_getcwd:
			return posix.getcwd(a1, (int) a2);
		case SYS_sigaltstack:
			return posix.sigaltstack32(a1, a2);
		case SYS_sendfile:
			return posix.sendfile32((int) a1, (int) a2, a3, (int) a4);
		case SYS_ugetrlimit:
			return posix.getrlimit32((int) a1, a2);
		case SYS_mmap2:
			return posix.mmap2(a1, Integer.toUnsignedLong((int) a2), (int) a3, (int) a4, (int) a5,
					Integer.toUnsignedLong((int) a6));
		case SYS_stat64:
			return posix.stat64(a1, a2);
		case SYS_lstat64:
			return posix.lstat64(a1, a2);
		case SYS_fstat64:
			return posix.fstat64((int) a1, a2);
		case SYS_getdents64:
			return posix.getdents64((int) a1, a2, (int) a3);
		case SYS_fcntl64:
			return posix.fcntl64((int) a1, (int) a2, a3);
		case SYS_sendfile64:
			return posix.sendfile64((int) a1, (int) a2, a3, Integer.toUnsignedLong((int) a4));
		case SYS_clock_gettime:
			return posix.clock_gettime32((int) a1, a2);
		case SYS_clock_getres:
			return posix.clock_getres32((int) a1, a2);
		case SYS_tgkill:
			System.err.printf("tgkill(%d, %d, %d)\n", (int) a1, (int) a2, (int) a3);
			posix.exit_group(128 + (int) a3);
		case SYS_openat:
			return posix.openat((int) a1, a2, (int) a3, (int) a4);
		case SYS_fstatat64:
			return posix.fstatat64((int) a1, a2, a3, (int) a4);
		case SYS_unlinkat:
			return posix.unlinkat((int) a1, a2, (int) a3);
		case SYS_symlinkat:
			return posix.symlinkat(a1, (int) a2, a3);
		case SYS_dup3:
			return posix.dup3((int) a1, (int) a2, (int) a3);
		case SYS_socket:
			return posix.socket((int) a1, (int) a2, (int) a3);
		case SYS_bind:
			return posix.bind((int) a1, a2, (int) a3);
		case SYS_connect:
			return posix.connect((int) a1, a2, (int) a3);
		case SYS_getsockname:
			return posix.getsockname32((int) a1, a2, a3);
		case SYS_getpeername:
			return posix.getpeername32((int) a1, a2, a3);
		case SYS_send:
			return posix.send((int) a1, a2, a3, (int) a4);
		case SYS_recv:
			return posix.recv((int) a1, a2, a3, (int) a4);
		case SYS_recvfrom:
			return posix.recvfrom((int) a1, a2, a3, (int) a4, a5, a6);
		case SYS_shutdown:
			return posix.shutdown((int) a1, (int) a2);
		case SYS_setsockopt:
			return posix.setsockopt((int) a1, (int) a2, (int) a3, a4, (int) a5);
		case SYS_recvmsg:
			return posix.recvmsg32((int) a1, a2, (int) a3);
		case SYS_sendmmsg:
			return posix.sendmmsg32((int) a1, a2, (int) a3, (int) a4);
		case SYS_statx:
			return posix.statx((int) a1, a2, (int) a3, (int) a4, a5);
		default:
			throw new SyscallException(Errno.ENOSYS);
		}
	}

	@SuppressWarnings("unused")
	public long execute64(int nr, long a1, long a2, long a3, long a4, long a5, long a6, long a7)
			throws SyscallException {
		switch(nr) {
		default:
			throw new SyscallException(Errno.ENOSYS);
		}
	}
}
