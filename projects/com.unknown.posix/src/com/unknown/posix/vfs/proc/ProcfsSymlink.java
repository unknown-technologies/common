package com.unknown.posix.vfs.proc;

import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.io.Statx;
import com.unknown.posix.vfs.VFSDirectory;
import com.unknown.posix.vfs.VFSSymlink;

public class ProcfsSymlink extends VFSSymlink {
	private final String link;

	protected ProcfsSymlink(VFSDirectory parent, String path, long uid, long gid, long permissions, String link) {
		super(parent, path, uid, gid, permissions);
		this.link = link;
	}

	@Override
	public String readlink() throws PosixException {
		return link;
	}

	@Override
	public void statx(int mask, Statx buf) throws PosixException {
		super.statx(mask, buf);
		buf.stx_dev_major = 0;
		buf.stx_dev_minor = 5;
	}
}
