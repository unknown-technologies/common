package com.unknown.posix.vfs.proc;

import java.util.Date;
import java.util.function.Supplier;

import com.unknown.posix.api.Errno;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.io.Statx;
import com.unknown.posix.api.io.Stream;
import com.unknown.posix.vfs.VFSDirectory;
import com.unknown.posix.vfs.VFSFile;

public class ProcfsDynamicReadOnlyFile extends VFSFile {
	private final Supplier<String> src;
	private final Date ctime;

	public ProcfsDynamicReadOnlyFile(VFSDirectory parent, String path, long uid, long gid, long permissions,
			Supplier<String> src) {
		super(parent, path, uid, gid, permissions);
		this.src = src;
		ctime = new Date();
	}

	@Override
	protected Stream open(boolean read, boolean write) throws PosixException {
		if(write) {
			throw new PosixException(Errno.EPERM);
		}
		String content = src.get();
		if(content == null) {
			throw new PosixException(Errno.EIO);
		}
		byte[] data = content.getBytes();
		return new ProcfsReadOnlyStream(this, data);
	}

	@Override
	public long size() throws PosixException {
		return 0;
	}

	@Override
	public void atime(Date time) throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	@Override
	public void mtime(Date time) throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	@Override
	public void ctime(Date time) throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	@Override
	public Date atime() throws PosixException {
		return ctime;
	}

	@Override
	public Date mtime() throws PosixException {
		return ctime;
	}

	@Override
	public Date ctime() throws PosixException {
		return ctime;
	}

	@Override
	public void statx(int mask, Statx buf) throws PosixException {
		super.statx(mask, buf);
		buf.stx_dev_major = 0;
		buf.stx_dev_minor = 5;
	}
}
