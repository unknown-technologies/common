package com.unknown.posix.vfs.proc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.unknown.posix.api.Errno;
import com.unknown.posix.api.Posix;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.vfs.VFS;
import com.unknown.posix.vfs.VFSDirectory;
import com.unknown.posix.vfs.VFSEntry;
import com.unknown.posix.vfs.VFSFile;
import com.unknown.posix.vfs.VFSSymlink;

public class ProcfsRoot extends VFSDirectory {
	private final Date creationTime;

	public ProcfsRoot(VFS vfs, String path, long uid, long gid, long permissions) {
		super(vfs, path, uid, gid, permissions);
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

	private VFSEntry getProcessDirectory(long id) throws PosixException {
		Posix posix = Posix.getThreadContext();
		if(posix.getGlobalState().getProcess(id) == null) {
			throw new PosixException(Errno.ENOENT);
		}
		return new ProcfsProcessDirectory(this, id, 0, 0, 0555);
	}

	private VFSEntry getFilesystems() {
		return new ProcfsDynamicReadOnlyFile(this, "filesystems", 0, 0, 0444,
				() -> Posix.getThreadContext().getFilesystems());
	}

	@Override
	protected VFSEntry getEntry(String name) throws PosixException {
		Posix posix = Posix.getThreadContext();
		switch(name) {
		case "self":
			// only PID 1 exists for now
			return new ProcfsSymlink(this, "self", 0, 0, 0777, Long.toString(posix.__getpid()));
		case "thread-self":
			return new ProcfsSymlink(this, "thread-self", 0, 0, 0777,
					posix.__getpid() + "/task/" + Posix.getTid());
		case "sys":
			return new ProcfsSysDirectory(this, "sys", 0, 0, 0555);
		case "filesystems":
			return getFilesystems();
		default:
			try {
				int tid = Integer.parseInt(name);
				return getProcessDirectory(tid);
			} catch(NumberFormatException e) {
				// nothing
			}
			throw new PosixException(Errno.ENOENT);
		}
	}

	@Override
	protected List<VFSEntry> list() throws PosixException {
		Posix posix = Posix.getThreadContext();
		List<VFSEntry> result = new ArrayList<>();
		long[] pids = posix.getGlobalState().getProcesses().stream().mapToLong(x -> x).sorted().toArray();
		for(long pid : pids) {
			result.add(getProcessDirectory(pid));
		}
		result.add(new ProcfsSysDirectory(this, "sys", 0, 0, 0555));
		result.add(getFilesystems());
		result.add(new ProcfsSymlink(this, "self", 0, 0, 0777, Long.toString(posix.__getpid())));
		result.add(new ProcfsSymlink(this, "thread-self", 0, 0, 0777,
				posix.__getpid() + "/task/" + Posix.getTid()));
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
}
