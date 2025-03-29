package com.unknown.posix.api.net;

import static com.unknown.posix.api.io.Stat.S_IFSOCK;
import static com.unknown.posix.api.io.Stat.S_IRUSR;
import static com.unknown.posix.api.io.Stat.S_IWGRP;
import static com.unknown.posix.api.io.Stat.S_IWUSR;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectableChannel;

import com.unknown.posix.api.Errno;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.PosixPointer;
import com.unknown.posix.api.Timespec;
import com.unknown.posix.api.io.Stat;
import com.unknown.posix.api.io.Statx;
import com.unknown.posix.api.io.StatxTimestamp;
import com.unknown.posix.api.io.Stream;
import com.unknown.util.io.Endianess;

public abstract class NetworkStream extends Stream {
	public abstract int setsockopt(int level, int option_name, int option_value) throws PosixException;

	public abstract int connect(PosixPointer address, int addressLen) throws PosixException;

	public abstract int bind(PosixPointer address, int addressLen) throws PosixException;

	public abstract int listen(int backlog) throws PosixException;

	@Override
	public int pread(byte[] buf, int offset, int length, long fileOffset) throws PosixException {
		throw new PosixException(Errno.ESPIPE);
	}

	@Override
	public int pwrite(byte[] buf, int offset, int length, long fileOffset) throws PosixException {
		throw new PosixException(Errno.ESPIPE);
	}

	@Override
	public long lseek(long offset, int whence) throws PosixException {
		throw new PosixException(Errno.ESPIPE);
	}

	@Override
	public void stat(Stat buf) throws PosixException {
		buf.st_dev = 0; // TODO
		buf.st_ino = 0; // TODO
		buf.st_mode = S_IFSOCK | S_IRUSR | S_IWUSR | S_IWGRP;
		buf.st_nlink = 0; // TODO
		buf.st_uid = 0; // TODO
		buf.st_gid = 0; // TODO
		buf.st_rdev = 0; // TODO
		buf.st_size = 0; // TODO
		buf.st_blksize = 0; // TODO
		buf.st_blocks = 0; // TODO
		buf.st_atim = new Timespec(); // TODO
		buf.st_mtim = new Timespec(); // TODO
		buf.st_ctim = new Timespec(); // TODO
	}

	@Override
	public void statx(int mask, Statx buf) throws PosixException {
		buf.stx_mask = Stat.STATX_INO | Stat.STATX_MODE | Stat.STATX_TYPE | Stat.STATX_NLINK | Stat.STATX_UID |
				Stat.STATX_GID | Stat.STATX_SIZE | Stat.STATX_BLOCKS | Stat.STATX_ATIME |
				Stat.STATX_MTIME | Stat.STATX_CTIME;
		buf.stx_attributes = 0;
		buf.stx_attributes_mask = 0;

		buf.stx_dev_major = 0; // TODO
		buf.stx_dev_minor = 0; // TODO
		buf.stx_ino = 0; // TODO
		buf.stx_mode = (short) (S_IFSOCK | S_IRUSR | S_IWUSR | S_IWGRP);
		buf.stx_nlink = 0; // TODO
		buf.stx_uid = 0; // TODO
		buf.stx_gid = 0; // TODO
		buf.stx_rdev_major = 0; // TODO
		buf.stx_rdev_minor = 0; // TODO
		buf.stx_size = 0; // TODO
		buf.stx_blksize = 0; // TODO
		buf.stx_blocks = 0; // TODO
		buf.stx_atime = new StatxTimestamp(); // TODO
		buf.stx_mtime = new StatxTimestamp(); // TODO
		buf.stx_ctime = new StatxTimestamp(); // TODO
	}

	@Override
	public void ftruncate(long size) throws PosixException {
		throw new PosixException(Errno.EINVAL);
	}

	public abstract SelectableChannel getChannel();

	public abstract long send(PosixPointer buffer, long length, int flags) throws PosixException;

	public abstract long recv(PosixPointer buffer, long length, int flags) throws PosixException;

	public abstract RecvResult recvfrom(PosixPointer buffer, long length, int flags) throws PosixException;

	public abstract long sendmsg(Msghdr message, int flags) throws PosixException;

	public abstract long recvmsg(Msghdr message, int flags) throws PosixException;

	public int sendmmsg(Mmsghdr[] msgvec, int vlen, int flags) throws PosixException {
		for(int i = 0; i < vlen; i++) {
			try {
				msgvec[i].msg_len = (int) sendmsg(msgvec[i].msg_hdr, flags);
			} catch(PosixException e) {
				if(i == 0) {
					throw e;
				} else {
					return i;
				}
			}
		}
		return vlen;
	}

	public abstract Sockaddr getsockname() throws PosixException;

	public abstract Sockaddr getpeername() throws PosixException;

	public abstract int shutdown(int how) throws PosixException;

	protected Sockaddr getSockaddr(SocketAddress addr) {
		if(addr instanceof InetSocketAddress) {
			InetSocketAddress iaddr = (InetSocketAddress) addr;
			byte[] ipaddr = iaddr.getAddress().getAddress();
			if(ipaddr.length == 4) {
				SockaddrIn sin = new SockaddrIn();
				sin.sa_family = Socket.AF_INET;
				sin.sin_addr = Endianess.get32bitBE(iaddr.getAddress().getAddress());
				sin.sin_port = (short) iaddr.getPort();
				return sin;
			} else if(ipaddr.length == 16) {
				SockaddrIn6 sin6 = new SockaddrIn6();
				sin6.sa_family = Socket.AF_INET6;
				sin6.sin6_addr = ipaddr;
				sin6.sin6_port = (short) iaddr.getPort();
				return sin6;
			} else {
				throw new AssertionError("invalid ip address length");
			}
		} else {
			return null;
		}
	}

	protected SocketAddress getSocketAddress(PosixPointer address) throws PosixException {
		Sockaddr saddr = new Sockaddr();
		saddr.read(address);
		if(saddr.sa_family == Socket.AF_INET) {
			SockaddrIn addr = new SockaddrIn();
			addr.read(address);
			if(addr.sa_family != Socket.AF_INET) {
				throw new PosixException(Errno.EAFNOSUPPORT);
			}
			byte[] remoteAddrBytes = new byte[4];
			Endianess.set32bitBE(remoteAddrBytes, 0, addr.sin_addr);
			InetAddress remoteAddr = null;
			try {
				remoteAddr = InetAddress.getByAddress(remoteAddrBytes);
			} catch(UnknownHostException e) {
				throw new PosixException(Errno.EADDRNOTAVAIL);
			}
			int port = addr.sin_port;
			return new InetSocketAddress(remoteAddr, port);
		} else if(saddr.sa_family == Socket.AF_INET6) {
			SockaddrIn6 addr = new SockaddrIn6();
			addr.read(address);
			if(addr.sa_family != Socket.AF_INET6) {
				throw new PosixException(Errno.EAFNOSUPPORT);
			}
			InetAddress remoteAddr = null;
			try {
				remoteAddr = InetAddress.getByAddress(addr.sin6_addr);
			} catch(UnknownHostException e) {
				throw new PosixException(Errno.EADDRNOTAVAIL);
			}
			int port = addr.sin6_port;
			return new InetSocketAddress(remoteAddr, port);
		} else {
			throw new PosixException(Errno.EAFNOSUPPORT);
		}
	}
}
