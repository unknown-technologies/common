package com.unknown.posix.vfs.proc;

import java.util.Date;

import com.unknown.posix.api.Errno;
import com.unknown.posix.api.MemoryMapProvider;
import com.unknown.posix.api.Posix;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.io.Statx;
import com.unknown.posix.api.io.Stream;
import com.unknown.posix.vfs.VFSDirectory;
import com.unknown.posix.vfs.VFSFile;

public class ProcfsPidMaps extends VFSFile {
	private final long pid;
	private final Date ctime;

	public ProcfsPidMaps(VFSDirectory parent, String path, long uid, long gid, long permissions, long pid) {
		super(parent, path, uid, gid, permissions);
		this.pid = pid;
		ctime = new Date();
	}

	@Override
	protected Stream open(boolean read, boolean write) throws PosixException {
		Posix posix = Posix.getThreadContext().getGlobalState().getProcess(pid);
		if(write) {
			throw new PosixException(Errno.EPERM);
		}
		MemoryMapProvider provider = posix.getMemoryMapProvider();
		if(provider == null) {
			throw new PosixException(Errno.EIO);
		}
		byte[] data = provider.getMemoryMap();
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
