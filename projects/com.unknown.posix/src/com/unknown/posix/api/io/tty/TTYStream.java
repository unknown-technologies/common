package com.unknown.posix.api.io.tty;

import java.io.InputStream;
import java.io.OutputStream;

import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.PosixPointer;
import com.unknown.posix.api.io.Fcntl;
import com.unknown.posix.api.io.Ioctls;
import com.unknown.posix.api.io.PipeStream;
import com.unknown.posix.api.io.Stat;
import com.unknown.posix.api.io.termios.Termios;

import static com.unknown.posix.api.io.Stat.S_IFCHR;
import static com.unknown.posix.api.io.Stat.S_IRUSR;
import static com.unknown.posix.api.io.Stat.S_IWUSR;
import static com.unknown.posix.api.io.Stat.S_IWGRP;

public class TTYStream extends PipeStream {
	private final Termios termios;
	private final Winsize winsize;

	public TTYStream(InputStream in) {
		super(in);
		termios = Termios.getDefaultTerminal();
		winsize = new Winsize();
		statusFlags = Fcntl.O_RDONLY;
	}

	public TTYStream(OutputStream out) {
		super(out);
		termios = Termios.getDefaultTerminal();
		winsize = new Winsize();
		statusFlags = Fcntl.O_WRONLY;
	}

	public TTYStream(InputStream in, OutputStream out) {
		super(in, out);
		termios = Termios.getDefaultTerminal();
		winsize = new Winsize();
		statusFlags = Fcntl.O_RDWR;
	}

	@Override
	public void stat(Stat buf) throws PosixException {
		super.stat(buf);
		buf.st_mode = S_IFCHR | S_IRUSR | S_IWUSR | S_IWGRP;
	}

	@Override
	public int ioctl(long request, PosixPointer argp) throws PosixException {
		switch((int) request) {
		case Ioctls.TCGETS:
			termios.write(argp);
			return 0;
		case Ioctls.TIOCGWINSZ:
			winsize.write(argp);
			return 0;
		case Ioctls.TIOCSWINSZ:
			winsize.read(argp);
			return 0;
		default:
			return super.ioctl(request, argp);
		}
	}
}
