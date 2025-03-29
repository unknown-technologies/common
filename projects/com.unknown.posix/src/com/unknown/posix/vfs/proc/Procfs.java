package com.unknown.posix.vfs.proc;

import com.unknown.posix.vfs.PosixFileSystem;
import com.unknown.posix.vfs.VFS;
import com.unknown.posix.vfs.VFSFileSystem;

public class Procfs extends VFSFileSystem {
	private ProcfsRoot root;

	public Procfs(VFS vfs) {
		super("procfs", false);
		root = new ProcfsRoot(vfs, "/", 0, 0, 755);
	}

	@Override
	public ProcfsRoot getRoot() {
		return root;
	}

	public static PosixFileSystem<Procfs> getFilesystem() {
		return new PosixFileSystem<>("proc", true) {
			@Override
			public Procfs create(VFS vfs, String source, String data, boolean ro) {
				return new Procfs(vfs);
			}
		};
	}
}
