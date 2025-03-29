package com.unknown.posix.vfs.proc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.unknown.posix.api.Errno;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.io.Statx;
import com.unknown.posix.vfs.VFSDirectory;
import com.unknown.posix.vfs.VFSEntry;
import com.unknown.posix.vfs.VFSFile;
import com.unknown.posix.vfs.VFSSymlink;

public class ProcfsSysDirectory extends VFSDirectory {
	private final Date ctime;

	public ProcfsSysDirectory(VFSDirectory parent, String path, long uid, long gid, long permissions) {
		super(parent, path, uid, gid, permissions);
		ctime = new Date();
	}

	@Override
	protected void create(VFSEntry file) throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	@Override
	protected VFSDirectory createDirectory(String name, long uid, long gid, long permissions)
			throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	@Override
	protected VFSFile createFile(String name, long uid, long gid, long permissions) throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	@Override
	protected VFSSymlink createSymlink(String name, long uid, long gid, long permissions, String target)
			throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	@Override
	protected VFSEntry createHardlink(String name, long uid, long gid, long permissions, VFSEntry target)
			throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	@Override
	protected void delete(String name) throws PosixException {
		throw new PosixException(Errno.EPERM);
	}

	private VFSEntry getKernel() {
		return new ProcfsSysKernelDirectory(this, "kernel", 0, 0, 0555);
	}

	@Override
	protected VFSEntry getEntry(String name) throws PosixException {
		switch(name) {
		case "kernel":
			return getKernel();
		default:
			throw new PosixException(Errno.ENOENT);
		}
	}

	@Override
	protected List<VFSEntry> list() throws PosixException {
		List<VFSEntry> result = new ArrayList<>();
		result.add(getKernel());
		return result;
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
