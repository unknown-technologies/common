package com.unknown.posix.vfs;

import java.util.Date;
import java.util.List;

import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.io.Stat;
import com.unknown.posix.api.io.Stream;

public abstract class VFSFileSystem {
	private final String type;
	private final boolean ro;

	public VFSFileSystem(String type, boolean ro) {
		this.type = type;
		this.ro = ro;
	}

	public String getType() {
		return type;
	}

	public boolean isReadOnly() {
		return ro;
	}

	public final VFSDirectory createMountPoint(VFS vfs, String mountPoint) {
		final VFSDirectory root = getRoot();
		return new VFSDirectory(vfs, mountPoint, 0, 0, 0755) {
			@Override
			public Stream opendir(int flags, int mode) throws PosixException {
				return root.open(flags, mode);
			}

			@Override
			public void create(VFSEntry file) throws PosixException {
				root.create(file);
			}

			@Override
			public VFSDirectory createDirectory(String name, long uid, long gid, long permissions)
					throws PosixException {
				return root.createDirectory(name, uid, gid, permissions);
			}

			@Override
			public VFSFile createFile(String name, long uid, long gid, long permissions)
					throws PosixException {
				return root.createFile(name, uid, gid, permissions);
			}

			@Override
			public VFSSymlink createSymlink(String name, long uid, long gid, long permissions,
					String target) throws PosixException {
				return root.createSymlink(name, uid, gid, permissions, target);
			}

			@Override
			public VFSEntry createHardlink(String name, long uid, long gid, long permissions,
					VFSEntry target) throws PosixException {
				return root.createHardlink(name, uid, gid, permissions, target);
			}

			@Override
			public void delete(String name) throws PosixException {
				root.delete(name);
			}

			@Override
			public VFSEntry getEntry(String name) throws PosixException {
				return root.get(name);
			}

			@Override
			public List<VFSEntry> list() throws PosixException {
				return root.list();
			}

			@Override
			public long size() throws PosixException {
				return root.size();
			}

			@Override
			public void atime(Date time) throws PosixException {
				root.atime(time);
			}

			@Override
			public Date atime() throws PosixException {
				return root.atime();
			}

			@Override
			public void mtime(Date time) throws PosixException {
				root.mtime(time);
			}

			@Override
			public Date mtime() throws PosixException {
				return root.mtime();
			}

			@Override
			public void ctime(Date time) throws PosixException {
				root.ctime(time);
			}

			@Override
			public Date ctime() throws PosixException {
				return root.ctime();
			}

			@Override
			public long getUID() throws PosixException {
				return root.getUID();
			}

			@Override
			public long getGID() throws PosixException {
				return root.getGID();
			}

			@Override
			public void stat(Stat buf) throws PosixException {
				root.stat(buf);
			}
		};
	}

	public abstract VFSDirectory getRoot();
}
