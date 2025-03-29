package com.unknown.posix.vfs;

import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.io.Stat;
import com.unknown.posix.api.io.Statx;
import com.unknown.util.BitTest;

public abstract class VFSSpecialFile extends VFSFile {
	public static final int CHAR_DEVICE = 0;
	public static final int BLOCK_DEVICE = 1;
	public static final int FIFO = 2;
	public static final int SOCKET = 3;

	private int type;

	public VFSSpecialFile(VFSDirectory parent, String path, long uid, long gid, long permissions, int type) {
		super(parent, path, uid, gid, permissions);
		this.type = type;
	}

	@Override
	public void stat(Stat buf) throws PosixException {
		super.stat(buf);
		int mode = 0;
		switch(type) {
		case CHAR_DEVICE:
			mode = Stat.S_IFCHR;
			break;
		case BLOCK_DEVICE:
			mode = Stat.S_IFBLK;
			break;
		case FIFO:
			mode = Stat.S_IFIFO;
			break;
		case SOCKET:
			mode = Stat.S_IFSOCK;
			break;
		}
		buf.st_mode = (buf.st_mode & ~Stat.S_IFREG) | mode;
	}

	@Override
	public void statx(int mask, Statx buf) throws PosixException {
		super.statx(mask, buf);
		if(BitTest.test(mask, Stat.STATX_TYPE)) {
			int mode = 0;
			switch(type) {
			case CHAR_DEVICE:
				mode = Stat.S_IFCHR;
				break;
			case BLOCK_DEVICE:
				mode = Stat.S_IFBLK;
				break;
			case FIFO:
				mode = Stat.S_IFIFO;
				break;
			case SOCKET:
				mode = Stat.S_IFSOCK;
				break;
			}
			buf.stx_mode = (short) ((buf.stx_mode & ~Stat.S_IFREG) | mode);
			buf.stx_mask |= Stat.STATX_TYPE;
		}
	}
}
