package com.unknown.posix.api.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.unknown.posix.api.Errno;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.PosixPointer;
import com.unknown.posix.api.io.Fcntl;
import com.unknown.posix.api.io.Iovec;
import com.unknown.util.BitTest;
import com.unknown.util.io.Endianess;
import com.unknown.util.log.Trace;

public class DatagramSocketStream extends NetworkStream {
	private static final Logger log = Trace.create(DatagramSocketStream.class);

	private DatagramChannel socket;

	public DatagramSocketStream() throws PosixException {
		try {
			socket = DatagramChannel.open();
		} catch(IOException e) {
			log.log(Level.WARNING, "Cannot create socket: " + e.getMessage(), e);
			throw new PosixException(Errno.ENOMEM);
		}
	}

	@Override
	public void setFlags(int flags) throws PosixException {
		try {
			if(BitTest.test(flags, Fcntl.O_NONBLOCK) && socket.isBlocking()) {
				socket.configureBlocking(false);
			} else if(!BitTest.test(flags, Fcntl.O_NONBLOCK) && !socket.isBlocking()) {
				socket.configureBlocking(true);
			}
		} catch(IOException e) {
			log.log(Level.WARNING, "Error while changing stream flags: " + e.getMessage(), e);
			throw new PosixException(Errno.EIO);
		}
		super.setFlags(flags);
	}

	@Override
	public int setsockopt(int level, int option_name, int option_value) throws PosixException {
		try {
			switch(level) {
			case Socket.SOL_SOCKET:
				switch(option_name) {
				case Socket.SO_KEEPALIVE:
					socket.setOption(StandardSocketOptions.SO_KEEPALIVE, option_value != 0);
					return 0;
				case Socket.SO_REUSEADDR:
					socket.setOption(StandardSocketOptions.SO_REUSEADDR, option_value != 0);
					return 0;
				default:
					throw new PosixException(Errno.EINVAL);
				}
			default:
				throw new PosixException(Errno.ENOPROTOOPT);
			}
		} catch(UnsupportedOperationException e) {
			throw new PosixException(Errno.ENOPROTOOPT);
		} catch(IllegalArgumentException e) {
			throw new PosixException(Errno.EINVAL);
		} catch(ClosedChannelException e) {
			throw new PosixException(Errno.EIO);
		} catch(IOException e) {
			log.log(Level.INFO, "I/O error while setting socket option: " + e.getMessage(), e);
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public int connect(PosixPointer address, int addressLen) throws PosixException {
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
			SocketAddress remote = new InetSocketAddress(remoteAddr, port);
			try {
				socket.connect(remote);
				return 0;
			} catch(AlreadyConnectedException e) {
				throw new PosixException(Errno.EISCONN);
			} catch(ConnectionPendingException e) {
				throw new PosixException(Errno.EALREADY);
			} catch(UnsupportedAddressTypeException e) {
				throw new PosixException(Errno.EAFNOSUPPORT);
			} catch(ClosedChannelException e) {
				throw new PosixException(Errno.EBADF);
			} catch(IOException e) {
				throw new PosixException(Errno.EIO);
			}
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
			SocketAddress remote = new InetSocketAddress(remoteAddr, port);
			try {
				socket.connect(remote);
				return 0;
			} catch(AlreadyConnectedException e) {
				throw new PosixException(Errno.EISCONN);
			} catch(ConnectionPendingException e) {
				throw new PosixException(Errno.EALREADY);
			} catch(UnsupportedAddressTypeException e) {
				throw new PosixException(Errno.EAFNOSUPPORT);
			} catch(ClosedChannelException e) {
				throw new PosixException(Errno.EBADF);
			} catch(IOException e) {
				throw new PosixException(Errno.EIO);
			}
		} else {
			throw new PosixException(Errno.EAFNOSUPPORT);
		}
	}

	@Override
	public int bind(PosixPointer address, int addressLen) throws PosixException {
		Sockaddr saddr = new Sockaddr();
		saddr.read(address);
		if(saddr.sa_family == Socket.AF_INET) {
			SockaddrIn addr = new SockaddrIn();
			addr.read(address);
			if(addr.sa_family != Socket.AF_INET) {
				throw new PosixException(Errno.EAFNOSUPPORT);
			}
			byte[] localAddrBytes = new byte[4];
			Endianess.set32bitBE(localAddrBytes, 0, addr.sin_addr);
			InetAddress localAddr = null;
			try {
				localAddr = InetAddress.getByAddress(localAddrBytes);
			} catch(UnknownHostException e) {
				throw new PosixException(Errno.EADDRNOTAVAIL);
			}
			int port = addr.sin_port;
			SocketAddress local = new InetSocketAddress(localAddr, port);
			try {
				socket.bind(local);
				return 0;
			} catch(AlreadyConnectedException e) {
				throw new PosixException(Errno.EISCONN);
			} catch(ConnectionPendingException e) {
				throw new PosixException(Errno.EALREADY);
			} catch(UnsupportedAddressTypeException e) {
				throw new PosixException(Errno.EAFNOSUPPORT);
			} catch(ClosedChannelException e) {
				throw new PosixException(Errno.EBADF);
			} catch(IOException e) {
				throw new PosixException(Errno.EIO);
			}
		} else if(saddr.sa_family == Socket.AF_INET6) {
			SockaddrIn6 addr = new SockaddrIn6();
			addr.read(address);
			if(addr.sa_family != Socket.AF_INET6) {
				throw new PosixException(Errno.EAFNOSUPPORT);
			}
			InetAddress localAddr = null;
			try {
				localAddr = InetAddress.getByAddress(addr.sin6_addr);
			} catch(UnknownHostException e) {
				throw new PosixException(Errno.EADDRNOTAVAIL);
			}
			int port = addr.sin6_port;
			SocketAddress local = new InetSocketAddress(localAddr, port);
			try {
				socket.bind(local);
				return 0;
			} catch(AlreadyConnectedException e) {
				throw new PosixException(Errno.EISCONN);
			} catch(ConnectionPendingException e) {
				throw new PosixException(Errno.EALREADY);
			} catch(UnsupportedAddressTypeException e) {
				throw new PosixException(Errno.EAFNOSUPPORT);
			} catch(ClosedChannelException e) {
				throw new PosixException(Errno.EBADF);
			} catch(IOException e) {
				throw new PosixException(Errno.EIO);
			}
		} else {
			throw new PosixException(Errno.EAFNOSUPPORT);
		}
	}

	@Override
	public int listen(int backlog) throws PosixException {
		throw new PosixException(Errno.EOPNOTSUPP);
	}

	@Override
	public int read(byte[] buf, int offset, int length) throws PosixException {
		ByteBuffer b = ByteBuffer.wrap(buf, offset, length);
		try {
			int result = socket.read(b);
			if(result > 0) {
				return result;
			} else if(result == 0) {
				throw new PosixException(Errno.EAGAIN);
			} else {
				// -1 in Java = eof
				return 0;
			}
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public int write(byte[] buf, int offset, int length) throws PosixException {
		ByteBuffer b = ByteBuffer.wrap(buf, offset, length);
		try {
			return socket.write(b);
		} catch(NotYetConnectedException e) {
			throw new PosixException(Errno.ECONNRESET);
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public long send(PosixPointer buffer, long length, int flags) throws PosixException {
		// TODO: use flags
		return write(buffer, (int) length);
	}

	@Override
	public long recv(PosixPointer buffer, long length, int flags) throws PosixException {
		// TODO: use flags
		return read(buffer, (int) length);
	}

	@Override
	public long sendmsg(Msghdr message, int flags) throws PosixException {
		int len = 0;
		for(int i = 0; i < message.msg_iovlen; i++) {
			len += message.msg_iov[i].iov_len;
		}
		ByteBuffer buf = ByteBuffer.allocate(len);
		for(int i = 0; i < message.msg_iovlen; i++) {
			Iovec iov = message.msg_iov[i];
			PosixPointer ptr = iov.iov_base;
			for(int n = 0; n < iov.iov_len; n++) {
				buf.put(ptr.getI8());
				ptr = ptr.add(1);
			}
		}
		buf.flip();
		try {
			return socket.write(buf);
		} catch(NotYetConnectedException e) {
			throw new PosixException(Errno.ECONNRESET);
		} catch(NoRouteToHostException e) {
			throw new PosixException(Errno.EHOSTUNREACH);
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public RecvResult recvfrom(PosixPointer buffer, long length, int flags) throws PosixException {
		// TODO: use the flags
		ByteBuffer buf = ByteBuffer.allocate((int) length);
		RecvResult result = new RecvResult();
		try {
			SocketAddress addr = socket.receive(buf);
			result.length = buf.position();

			// transfer buffer
			buf.flip();
			PosixPointer ptr = buffer;
			for(int i = 0; i < result.length; i++) {
				ptr.setI8(buf.get());
				ptr = ptr.add(1);
			}

			if(addr instanceof InetSocketAddress) {
				result.sa = getSockaddr(addr);
			}
		} catch(PortUnreachableException e) {
			throw new PosixException(Errno.ECONNREFUSED);
		} catch(NoRouteToHostException e) {
			throw new PosixException(Errno.EHOSTUNREACH);
		} catch(IOException e) {
			log.log(Level.WARNING, "I/O error: " + e.getMessage(), e);
			throw new PosixException(Errno.EIO);
		}
		return result;
	}

	@Override
	public long recvmsg(Msghdr message, int flags) throws PosixException {
		if(flags != 0 && flags != Socket.MSG_TRUNC) {
			throw new PosixException(Errno.EOPNOTSUPP);
		}

		int length = 0;
		for(Iovec iov : message.msg_iov) {
			length += iov.iov_len;
		}

		ByteBuffer buf = ByteBuffer.allocate(length);
		try {
			SocketAddress addr = socket.receive(buf);
			length = buf.position();

			// transfer buffer
			buf.flip();
			int iovid = 0;
			int iovpos = 0;
			PosixPointer ptr = message.msg_iov[iovid].iov_base;
			for(int i = 0; i < length; i++) {
				ptr.setI8(buf.get());
				ptr = ptr.add(1);
				iovpos++;
				if(iovpos >= message.msg_iov[iovid].iov_len) {
					iovid++;
					iovpos = 0;
					ptr = message.msg_iov[iovid].iov_base;
				}
			}

			message.msg_name = getSockaddr(addr);
			message.msg_control = null;
			message.msg_flags = 0;
			return length;
		} catch(PortUnreachableException e) {
			throw new PosixException(Errno.ECONNREFUSED);
		} catch(NoRouteToHostException e) {
			throw new PosixException(Errno.EHOSTUNREACH);
		} catch(IOException e) {
			log.log(Level.WARNING, "I/O error: " + e.getMessage(), e);
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public Sockaddr getsockname() throws PosixException {
		try {
			SocketAddress addr = socket.getLocalAddress();
			Sockaddr sa = getSockaddr(addr);
			if(sa == null) {
				throw new PosixException(Errno.EOPNOTSUPP);
			} else {
				return sa;
			}
		} catch(ClosedChannelException e) {
			throw new PosixException(Errno.EINVAL);
		} catch(IOException e) {
			log.log(Level.WARNING, "I/O error: " + e.getMessage(), e);
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public Sockaddr getpeername() throws PosixException {
		try {
			SocketAddress addr = socket.getRemoteAddress();
			Sockaddr sa = getSockaddr(addr);
			if(sa == null) {
				throw new PosixException(Errno.EOPNOTSUPP);
			} else {
				return sa;
			}
		} catch(ClosedChannelException e) {
			throw new PosixException(Errno.EINVAL);
		} catch(IOException e) {
			log.log(Level.WARNING, "I/O error: " + e.getMessage(), e);
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public int shutdown(int how) throws PosixException {
		switch(how) {
		case Socket.SHUT_RD:
		case Socket.SHUT_WR:
		case Socket.SHUT_RDWR:
			throw new PosixException(Errno.ENOTCONN);
		default:
			throw new PosixException(Errno.EINVAL);
		}
	}

	@Override
	public int close() throws PosixException {
		try {
			socket.close();
			return 0;
		} catch(IOException e) {
			throw new PosixException(Errno.EIO);
		}
	}

	@Override
	public DatagramChannel getChannel() {
		return socket;
	}
}
