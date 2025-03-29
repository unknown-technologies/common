package com.unknown.posix.vfs;

public class Tmpfs extends VFSFileSystem {
	private TmpfsDirectory root;

	public Tmpfs(VFS vfs) {
		super("tmpfs", false);
		root = new TmpfsDirectory(vfs, "/", 0, 0, 755);
	}

	@Override
	public TmpfsDirectory getRoot() {
		return root;
	}

	public static PosixFileSystem<Tmpfs> getFilesystem() {
		return new PosixFileSystem<>("tmpfs", true) {
			@Override
			public Tmpfs create(VFS vfs, String source, String data, boolean ro) {
				return new Tmpfs(vfs);
			}
		};
	}
}
