package com.unknown.posix.vfs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.unknown.posix.api.Dirent;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.io.Stat;
import com.unknown.posix.api.io.Statx;
import com.unknown.posix.api.io.Stream;
import com.unknown.util.BitTest;

public abstract class VFSDirectory extends VFSEntry {
	private VFSDirectory mount;

	public VFSDirectory(VFS vfs, String path, long uid, long gid, long permissions) {
		super(vfs, path, uid, gid, permissions);
		mount = null;
	}

	public VFSDirectory(VFSDirectory parent, String path, long uid, long gid, long permissions) {
		super(parent, path, uid, gid, permissions);
		mount = null;
	}

	public void mount(VFSDirectory fs) {
		mount = fs;
	}

	public void umount() {
		mount = null;
	}

	public boolean hasMountpoint() {
		return mount != null;
	}

	protected abstract void create(VFSEntry file) throws PosixException;

	protected abstract VFSDirectory createDirectory(String name, long uid, long gid, long permissions)
			throws PosixException;

	protected abstract VFSFile createFile(String name, long uid, long gid, long permissions) throws PosixException;

	protected abstract VFSSymlink createSymlink(String name, long uid, long gid, long permissions, String target)
			throws PosixException;

	protected abstract VFSEntry createHardlink(String name, long uid, long gid, long permissions, VFSEntry target)
			throws PosixException;

	protected abstract void delete(String name) throws PosixException;

	protected abstract VFSEntry getEntry(String name) throws PosixException;

	protected abstract List<VFSEntry> list() throws PosixException;

	protected Stream opendir(@SuppressWarnings("unused") int flags, @SuppressWarnings("unused") int mode)
			throws PosixException {
		List<Dirent> entries = new ArrayList<>();
		Dirent self = new Dirent();
		Statx stat = new Statx();
		int mask = Stat.STATX_INO | Stat.STATX_MODE | Stat.STATX_TYPE;
		statx(mask, stat);
		self.d_ino = stat.stx_ino;
		self.d_off = 0;
		self.d_type = Dirent.DT_DIR;
		self.d_name = ".";
		entries.add(self);
		Dirent parent = new Dirent();
		parent.d_ino = self.d_ino;
		parent.d_off = 1;
		parent.d_type = Dirent.DT_DIR;
		parent.d_name = "..";
		entries.add(parent);

		long off = 2;
		for(VFSEntry entry : list()) {
			Dirent dirent = new Dirent();
			entry.statx(mask, stat);
			dirent.d_ino = stat.stx_ino;
			dirent.d_off = off;
			dirent.d_type = Dirent.IFTODT(stat.stx_mode);
			dirent.d_name = entry.getName();
			entries.add(dirent);
			off++;
		}
		return new GenericDirectoryStream(this, entries.iterator());
	}

	public final Stream open(int flags, int mode) throws PosixException {
		if(mount != null) {
			return mount.open(flags, mode);
		} else {
			return opendir(flags, mode);
		}
	}

	public final VFSDirectory mkdir(String name, long uid, long gid, long permissions) throws PosixException {
		if(mount != null) {
			return mount.mkdir(name, uid, gid, permissions);
		} else {
			return createDirectory(name, uid, gid, permissions);
		}
	}

	public final VFSFile mkfile(String name, long uid, long gid, long permissions) throws PosixException {
		if(mount != null) {
			return mount.mkfile(name, uid, gid, permissions);
		} else {
			return createFile(name, uid, gid, permissions);
		}
	}

	public final VFSSymlink symlink(String name, long uid, long gid, long permissions, String target)
			throws PosixException {
		if(mount != null) {
			return mount.createSymlink(name, uid, gid, permissions, target);
		} else {
			return createSymlink(name, uid, gid, permissions, target);
		}
	}

	public final VFSEntry hardlink(String name, long uid, long gid, long permissions, VFSEntry target)
			throws PosixException {
		if(mount != null) {
			return mount.createHardlink(name, uid, gid, permissions, target);
		} else {
			return createHardlink(name, uid, gid, permissions, target);
		}
	}

	public final void unlink(String name) throws PosixException {
		if(mount != null) {
			mount.unlink(name);
		} else {
			delete(name);
		}
	}

	public final List<VFSEntry> readdir() throws PosixException {
		if(mount != null) {
			return mount.readdir();
		} else {
			return list();
		}
	}

	public final VFSEntry get(String name) throws PosixException {
		if(mount != null) {
			return mount.get(name);
		} else {
			return getEntry(name);
		}
	}

	public final boolean isEmpty() throws PosixException {
		if(mount != null) {
			return false;
		}
		return list().isEmpty();
	}

	@Override
	public void stat(Stat buf) throws PosixException {
		super.stat(buf);
		buf.st_mode |= Stat.S_IFDIR;
	}

	@Override
	public void statx(int mask, Statx buf) throws PosixException {
		super.statx(mask, buf);
		if(BitTest.test(mask, Stat.STATX_TYPE)) {
			buf.stx_mode |= Stat.S_IFDIR;
			buf.stx_mask |= Stat.STATX_TYPE;
		}
	}

	@Override
	public String toString() {
		try {
			return "VFSDirectory[" + list().stream()
					.map(Object::toString)
					.collect(Collectors.joining(",")) + "]";
		} catch(PosixException e) {
			return "VFSDirectory[" + getPath() + "]";
		}
	}
}
