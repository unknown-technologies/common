package com.unknown.posix.vfs;

import java.util.List;

import com.unknown.posix.api.Posix;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.Utimbuf;
import com.unknown.posix.api.io.Stat;

public class ProcessVFS {
	private final VFS vfs;
	private final Posix posix;
	private String cwd;

	public ProcessVFS(VFS vfs, Posix posix) {
		this.vfs = vfs;
		this.posix = posix;
		cwd = "/";
	}

	public VFS getVFS() {
		return vfs;
	}

	public void chdir(String path) throws PosixException {
		Posix.setThreadContext(posix);
		String normalized = VFS.resolve(path, cwd);
		getDirectory(normalized); // throw ENOENT/ENOTDIR if necessary
		cwd = normalized;
	}

	public String getcwd() {
		return cwd;
	}

	public String realpath(String path) throws PosixException {
		Posix.setThreadContext(posix);
		return vfs.realpath(path, cwd, cwd);
	}

	public String realpath(String path, String at) throws PosixException {
		Posix.setThreadContext(posix);
		return vfs.realpath(path, at, cwd);
	}

	public void unlink(String path) throws PosixException {
		Posix.setThreadContext(posix);
		vfs.unlink(path, cwd);
	}

	public String resolve(String path) {
		Posix.setThreadContext(posix);
		return VFS.resolve(path, cwd);
	}

	public List<VFSEntry> list(String path) throws PosixException {
		Posix.setThreadContext(posix);
		return vfs.list(path, cwd);
	}

	public <T extends VFSEntry> T get(String path) throws PosixException {
		Posix.setThreadContext(posix);
		return vfs.get(path, cwd);
	}

	public <T extends VFSEntry> T get(String path, boolean resolve) throws PosixException {
		Posix.setThreadContext(posix);
		return vfs.get(path, cwd, resolve);
	}

	public <T extends VFSEntry> T getat(VFSSymlink entry, String path) throws PosixException {
		Posix.setThreadContext(posix);
		return vfs.getat(entry, path, cwd);
	}

	public VFSDirectory getDirectory(String path) throws PosixException {
		Posix.setThreadContext(posix);
		return vfs.getDirectory(path, cwd);
	}

	public VFSFile getFile(String path) throws PosixException {
		Posix.setThreadContext(posix);
		return vfs.getFile(path, cwd);
	}

	public VFSDirectory mkdir(String path, long uid, long gid, long permissions) throws PosixException {
		Posix.setThreadContext(posix);
		return vfs.mkdir(path, uid, gid, permissions, cwd);
	}

	public VFSFile mkfile(String path, long uid, long gid, long permissions) throws PosixException {
		Posix.setThreadContext(posix);
		return vfs.mkfile(path, uid, gid, permissions, cwd);
	}

	public VFSSymlink symlink(String path, long uid, long gid, long permissions, String target)
			throws PosixException {
		Posix.setThreadContext(posix);
		return vfs.symlink(path, uid, gid, permissions, target, cwd);
	}

	public void rmdir(String path) throws PosixException {
		Posix.setThreadContext(posix);
		vfs.rmdir(path, cwd);
	}

	public com.unknown.posix.api.io.Stream open(String path, int flags, int mode) throws PosixException {
		Posix.setThreadContext(posix);
		return vfs.open(path, flags, mode, cwd);
	}

	public String readlink(String path) throws PosixException {
		Posix.setThreadContext(posix);
		return vfs.readlink(path, cwd);
	}

	public void stat(String path, Stat buf) throws PosixException {
		Posix.setThreadContext(posix);
		vfs.stat(path, buf, cwd);
	}

	public void chown(String path, long owner, long group) throws PosixException {
		Posix.setThreadContext(posix);
		vfs.chown(path, owner, group, cwd);
	}

	public void chmod(String path, int mode) throws PosixException {
		Posix.setThreadContext(posix);
		vfs.chmod(path, mode, cwd);
	}

	public void utime(String path, Utimbuf times) throws PosixException {
		Posix.setThreadContext(posix);
		vfs.utime(path, times, cwd);
	}

	public void mount(String path, VFSFileSystem fs) throws PosixException {
		Posix.setThreadContext(posix);
		vfs.mount(path, fs, cwd);
	}

	public void umount(String path) throws PosixException {
		Posix.setThreadContext(posix);
		vfs.umount(path, cwd);
	}
}
