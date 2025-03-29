package com.unknown.posix.vfs.proc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.unknown.posix.api.Errno;
import com.unknown.posix.api.Posix;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.io.Statx;
import com.unknown.posix.vfs.VFSDirectory;
import com.unknown.posix.vfs.VFSEntry;
import com.unknown.posix.vfs.VFSFile;
import com.unknown.posix.vfs.VFSSymlink;

public class ProcfsTasksDirectory extends VFSDirectory {
	private final long pid;
	private final Date creationTime;

	public ProcfsTasksDirectory(VFSDirectory parent, String path, long uid, long gid, long permissions, long pid) {
		super(parent, path, uid, gid, permissions);
		this.pid = pid;
		creationTime = new Date();
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

	private VFSEntry getThreadDirectory(int id) throws PosixException {
		Posix posix = Posix.getThreadContext().getGlobalState().getProcess(pid);
		if(!posix.hasThread(id)) {
			throw new PosixException(Errno.ENOENT);
		}
		return new ProcfsThreadDirectory(this, pid, id, 0, 0, 0755);
	}

	@Override
	protected VFSEntry getEntry(String name) throws PosixException {
		try {
			int tid = Integer.parseInt(name);
			return getThreadDirectory(tid);
		} catch(NumberFormatException e) {
			// nothing
		}
		throw new PosixException(Errno.ENOENT);
	}

	@Override
	protected List<VFSEntry> list() throws PosixException {
		Posix posix = Posix.getThreadContext().getGlobalState().getProcess(pid);
		List<VFSEntry> result = new ArrayList<>();
		List<Integer> tids = posix.getTids().stream().sorted().collect(Collectors.toList());
		for(int tid : tids) {
			result.add(getThreadDirectory(tid));
		}
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
		return creationTime;
	}

	@Override
	public Date mtime() throws PosixException {
		return creationTime;
	}

	@Override
	public Date ctime() throws PosixException {
		return creationTime;
	}

	@Override
	public void statx(int mask, Statx buf) throws PosixException {
		super.statx(mask, buf);
		buf.stx_dev_major = 0;
		buf.stx_dev_minor = 5;
	}
}
