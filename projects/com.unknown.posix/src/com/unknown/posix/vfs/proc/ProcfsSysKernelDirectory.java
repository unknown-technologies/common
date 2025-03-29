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

public class ProcfsSysKernelDirectory extends VFSDirectory {
	private final Date ctime;

	public ProcfsSysKernelDirectory(VFSDirectory parent, String path, long uid, long gid, long permissions) {
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

	private VFSEntry getDomainname() {
		Posix posix = Posix.getThreadContext();
		return new ProcfsDynamicReadOnlyFile(this, "domainname", 0, 0, 0444,
				() -> posix.getUname().domainname + '\n');
	}

	private VFSEntry getHostname() {
		Posix posix = Posix.getThreadContext();
		return new ProcfsDynamicReadOnlyFile(this, "hostname", 0, 0, 0444,
				() -> posix.getUname().nodename + '\n');
	}

	private VFSEntry getOSRelease() {
		Posix posix = Posix.getThreadContext();
		return new ProcfsDynamicReadOnlyFile(this, "osrelease", 0, 0, 0444,
				() -> posix.getUname().release + '\n');
	}

	private VFSEntry getOSType() {
		return new ProcfsDynamicReadOnlyFile(this, "ostype", 0, 0, 0444, () -> "Linux\n");
	}

	private VFSEntry getVersion() {
		Posix posix = Posix.getThreadContext();
		return new ProcfsDynamicReadOnlyFile(this, "version", 0, 0, 0444,
				() -> posix.getUname().version + '\n');
	}

	@Override
	protected VFSEntry getEntry(String name) throws PosixException {
		switch(name) {
		case "domainname":
			return getDomainname();
		case "hostname":
			return getHostname();
		case "osrelease":
			return getOSRelease();
		case "ostype":
			return getOSType();
		case "version":
			return getVersion();
		default:
			throw new PosixException(Errno.ENOENT);
		}
	}

	@Override
	protected List<VFSEntry> list() throws PosixException {
		List<VFSEntry> result = new ArrayList<>();
		result.add(getDomainname());
		result.add(getHostname());
		result.add(getOSRelease());
		result.add(getOSType());
		result.add(getVersion());
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
