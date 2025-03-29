package com.unknown.posix.vfs;

import java.util.Date;

import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.Timespec;
import com.unknown.posix.api.Utimbuf;
import com.unknown.posix.api.io.Stat;
import com.unknown.posix.api.io.Statx;
import com.unknown.posix.api.io.StatxTimestamp;
import com.unknown.util.BitTest;

public abstract class VFSEntry {
	private String path;
	private long uid;
	private long gid;
	private long permissions;
	private VFSDirectory parent;
	private VFS vfs;

	private int nlink = 1;
	private int dev = 0;

	protected VFSEntry(VFS vfs, String path) {
		this.vfs = vfs;
		if(path.startsWith("/")) {
			this.path = path.substring(1);
		} else {
			this.path = path;
		}
	}

	protected VFSEntry(VFS vfs, String path, long uid, long gid, long permissions) {
		this(vfs, path);
		this.uid = uid;
		this.gid = gid;
		this.permissions = permissions;
	}

	protected VFSEntry(VFSDirectory parent, String path, long uid, long gid, long permissions) {
		this(parent, path);
		this.uid = uid;
		this.gid = gid;
		this.permissions = permissions;
	}

	protected VFSEntry(VFSDirectory parent, String path) {
		this((VFS) null, path);
		this.parent = parent;
	}

	protected VFS getVFS() {
		if(vfs == null && parent != null) {
			return parent.getVFS();
		} else {
			return vfs;
		}
	}

	public VFSDirectory getParent() {
		return parent;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		String[] parts = path.split("/");
		return parts[parts.length - 1];
	}

	public String getEntryPath() {
		int last = path.lastIndexOf("/");
		if(last != -1) {
			return path.substring(0, last);
		} else {
			return "";
		}
	}

	@SuppressWarnings("unused")
	public void chmod(int mode) throws PosixException {
		permissions = mode;
	}

	@SuppressWarnings("unused")
	public void chown(long owner, long group) throws PosixException {
		uid = owner;
		gid = group;
	}

	@SuppressWarnings("unused")
	public long getUID() throws PosixException {
		return uid;
	}

	@SuppressWarnings("unused")
	public long getGID() throws PosixException {
		return gid;
	}

	@SuppressWarnings("unused")
	public long getPermissions() throws PosixException {
		return permissions;
	}

	public void setNlink(int nlink) {
		this.nlink = nlink;
	}

	public int getNlink() {
		return nlink;
	}

	public void setDev(int dev) {
		this.dev = dev;
	}

	public void setDev(int maj, int min) {
		this.dev = maj << 8 | min;
	}

	public abstract long size() throws PosixException;

	public abstract void atime(Date time) throws PosixException;

	public abstract void mtime(Date time) throws PosixException;

	public abstract void ctime(Date time) throws PosixException;

	public abstract Date atime() throws PosixException;

	public abstract Date mtime() throws PosixException;

	public abstract Date ctime() throws PosixException;

	public void utime(Utimbuf times) throws PosixException {
		long atime;
		long mtime;
		if(times == null) {
			atime = new Date().getTime() / 1000;
			mtime = atime;
		} else {
			atime = times.actime;
			mtime = times.modtime;
		}
		atime(new Date(atime * 1000));
		mtime(new Date(mtime * 1000));
	}

	public int getInode() {
		return hashCode();
	}

	public void stat(Stat buf) throws PosixException {
		buf.st_dev = dev; // TODO
		buf.st_ino = getInode(); // TODO
		buf.st_mode = (int) getPermissions(); // TODO
		buf.st_nlink = getNlink(); // TODO
		buf.st_uid = (int) getUID();
		buf.st_gid = (int) getGID();
		buf.st_rdev = dev; // TODO
		buf.st_size = size();
		buf.st_blksize = 4096; // TODO
		buf.st_blocks = (long) Math.ceil(buf.st_size / 512.0); // TODO
		buf.st_atim = new Timespec(atime());
		buf.st_mtim = new Timespec(mtime());
		buf.st_ctim = new Timespec(ctime());
	}

	public void statx(int mask, Statx buf) throws PosixException {
		buf.stx_mask = 0;
		buf.stx_attributes = 0;
		buf.stx_attributes_mask = 0;

		buf.stx_dev_major = (dev >> 8) & 0xFF;
		buf.stx_dev_minor = dev & 0xFF;
		if(BitTest.test(mask, Stat.STATX_INO)) {
			buf.stx_ino = getInode();
			buf.stx_mask |= Stat.STATX_INO;
		}
		if(BitTest.test(mask, Stat.STATX_MODE)) {
			buf.stx_mode = (short) getPermissions();
			buf.stx_mask |= Stat.STATX_MODE;
		}
		if(BitTest.test(mask, Stat.STATX_NLINK)) {
			buf.stx_nlink = getNlink();
			buf.stx_mask |= Stat.STATX_NLINK;
		}
		if(BitTest.test(mask, Stat.STATX_UID)) {
			buf.stx_uid = (int) getUID();
			buf.stx_mask |= Stat.STATX_UID;
		}
		if(BitTest.test(mask, Stat.STATX_GID)) {
			buf.stx_gid = (int) getGID();
			buf.stx_mask |= Stat.STATX_GID;
		}
		buf.stx_rdev_major = (dev >> 8) & 0xFF;
		buf.stx_rdev_minor = dev & 0xFF;
		if(BitTest.test(mask, Stat.STATX_SIZE)) {
			buf.stx_size = size();
			buf.stx_mask |= Stat.STATX_SIZE;
		}
		if(BitTest.test(mask, Stat.STATX_BLOCKS)) {
			buf.stx_blksize = 4096;
			buf.stx_mask |= Stat.STATX_BLOCKS;
		}
		buf.stx_blocks = (long) Math.ceil(buf.stx_size / 512.0);
		if(BitTest.test(mask, Stat.STATX_ATIME)) {
			buf.stx_atime = new StatxTimestamp(atime());
			buf.stx_mask |= Stat.STATX_ATIME;
		}
		if(BitTest.test(mask, Stat.STATX_CTIME)) {
			buf.stx_ctime = new StatxTimestamp(ctime());
			buf.stx_mask |= Stat.STATX_CTIME;
		}
		if(BitTest.test(mask, Stat.STATX_MTIME)) {
			buf.stx_mtime = new StatxTimestamp(mtime());
			buf.stx_mask |= Stat.STATX_MTIME;
		}

	}

	@Override
	public String toString() {
		return "VFSEntry[" + path + "]";
	}
}
