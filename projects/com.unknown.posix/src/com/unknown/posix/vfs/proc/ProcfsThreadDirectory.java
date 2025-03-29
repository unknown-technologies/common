package com.unknown.posix.vfs.proc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.unknown.posix.api.Errno;
import com.unknown.posix.api.Posix;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.io.Statx;
import com.unknown.posix.vfs.VFSDirectory;
import com.unknown.posix.vfs.VFSEntry;
import com.unknown.posix.vfs.VFSFile;
import com.unknown.posix.vfs.VFSSymlink;

public class ProcfsThreadDirectory extends VFSDirectory {
	private final long pid;
	private final Date ctime;

	public ProcfsThreadDirectory(VFSDirectory parent, long pid, int tid, long uid, long gid, long permissions) {
		super(parent, Integer.toString(tid), uid, gid, permissions);
		this.pid = pid;
		ctime = new Date();
	}

	private Posix getPosix() {
		return Posix.getThreadContext().getGlobalState().getProcess(pid);
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

	private VFSEntry getCwd() {
		Posix posix = getPosix();
		return new ProcfsSymlink(this, "cwd", 0, 0, 0777, posix.getcwd());
	}

	private VFSEntry getExe() {
		Posix posix = getPosix();
		return new ProcfsSymlink(this, "exe", 0, 0, 0777, posix.getExecfn());
	}

	private VFSEntry getRoot() {
		return new ProcfsSymlink(this, "root", 0, 0, 0777, "/");
	}

	private VFSEntry getMaps() {
		return new ProcfsPidMaps(this, "maps", 0, 0, 0444, pid);
	}

	@Override
	protected VFSEntry getEntry(String name) throws PosixException {
		switch(name) {
		case "cwd":
			return getCwd();
		case "exe":
			return getExe();
		case "maps":
			return getMaps();
		case "root":
			return getRoot();
		default:
			throw new PosixException(Errno.ENOENT);
		}
	}

	@Override
	protected List<VFSEntry> list() throws PosixException {
		List<VFSEntry> result = new ArrayList<>();
		result.add(getCwd());
		result.add(getExe());
		result.add(getMaps());
		result.add(getRoot());
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
