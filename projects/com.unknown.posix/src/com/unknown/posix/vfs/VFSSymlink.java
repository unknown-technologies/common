package com.unknown.posix.vfs;

import java.util.Date;

import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.io.Stat;
import com.unknown.posix.api.io.Statx;
import com.unknown.util.BitTest;

public abstract class VFSSymlink extends VFSEntry {
	private Date atime;
	private Date mtime;
	private Date ctime;

	protected VFSSymlink(VFSDirectory parent, String path, long uid, long gid, long permissions) {
		super(parent, path, uid, gid, permissions);
		atime = new Date();
		mtime = atime;
		ctime = atime;
	}

	public abstract String readlink() throws PosixException;

	@Override
	public long size() throws PosixException {
		return readlink().length();
	}

	@Override
	public void atime(Date time) throws PosixException {
		this.atime = time;
	}

	@Override
	public Date atime() throws PosixException {
		return atime;
	}

	@Override
	public void mtime(Date time) throws PosixException {
		this.mtime = time;
	}

	@Override
	public Date mtime() throws PosixException {
		return mtime;
	}

	@Override
	public void ctime(Date time) throws PosixException {
		this.ctime = time;
	}

	@Override
	public Date ctime() throws PosixException {
		return ctime;
	}

	@Override
	public void stat(Stat buf) throws PosixException {
		super.stat(buf);
		buf.st_nlink = 1;
		buf.st_mode |= Stat.S_IFLNK;
	}

	@Override
	public void statx(int mask, Statx buf) throws PosixException {
		super.statx(mask, buf);
		if(BitTest.test(mask, Stat.STATX_NLINK)) {
			buf.stx_nlink = 1;
			buf.stx_mask |= Stat.STATX_NLINK;
		}
		if(BitTest.test(mask, Stat.STATX_TYPE)) {
			buf.stx_mode |= Stat.S_IFLNK;
			buf.stx_mask |= Stat.STATX_TYPE;
		}
	}
}
