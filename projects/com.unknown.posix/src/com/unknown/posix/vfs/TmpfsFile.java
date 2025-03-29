package com.unknown.posix.vfs;

import java.util.Date;

import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.io.Stream;

public class TmpfsFile extends VFSFile {
	private final TmpfsFileData data;

	public TmpfsFile(VFSDirectory parent, String path, long uid, long gid, long permissions, TmpfsFileData data) {
		super(parent, path, uid, gid, permissions);
		data.atime = new Date();
		this.data = data;
		data.nlink++;
		setDev(0, 33);
	}

	public void setContent(byte[] data) {
		this.data.setContent(data);
	}

	public byte[] getContent() {
		return data.getContent();
	}

	int read(int pos, byte[] buf, int offset, int length) {
		return data.read(pos, buf, offset, length);
	}

	void append(byte[] buf, int offset, int length) {
		data.append(buf, offset, length);
	}

	void insert(int pos, byte[] buf, int offset, int length) {
		data.insert(pos, buf, offset, length);
	}

	void truncate(int length) throws PosixException {
		data.truncate(length);
	}

	TmpfsFileData getData() {
		return data;
	}

	void unlink() {
		data.nlink--;
	}

	@Override
	public int getNlink() {
		return data.nlink;
	}

	@Override
	public Stream open(boolean read, boolean write) throws PosixException {
		data.atime = new Date();
		return new TmpfsFileStream(this, read, write);
	}

	@Override
	public void chmod(int mode) throws PosixException {
		data.permissions = mode;
	}

	@Override
	public void chown(long owner, long group) throws PosixException {
		data.uid = owner;
		data.gid = group;
	}

	@Override
	public long getUID() throws PosixException {
		return data.uid;
	}

	@Override
	public long getGID() throws PosixException {
		return data.gid;
	}

	@Override
	public long getPermissions() throws PosixException {
		return data.permissions;
	}

	@Override
	public long size() {
		return data.data.length;
	}

	@Override
	public void atime(Date time) {
		data.atime = time;
	}

	@Override
	public Date atime() {
		return data.atime;
	}

	@Override
	public void mtime(Date time) {
		data.mtime = time;
	}

	@Override
	public Date mtime() {
		return data.mtime;
	}

	@Override
	public void ctime(Date time) {
		data.ctime = time;
	}

	@Override
	public Date ctime() {
		return data.ctime;
	}
}
