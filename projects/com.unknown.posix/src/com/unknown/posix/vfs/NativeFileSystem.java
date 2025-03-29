package com.unknown.posix.vfs;

import java.nio.file.Paths;

public class NativeFileSystem extends VFSFileSystem {
	private final String path;
	private final VFS vfs;

	public NativeFileSystem(VFS vfs, String path) {
		super("native", false);
		this.vfs = vfs;
		this.path = path;
	}

	@Override
	public VFSDirectory getRoot() {
		return new NativeDirectory(vfs, Paths.get(path));
	}

	public static PosixFileSystem<NativeFileSystem> getFilesystem() {
		return new PosixFileSystem<>("native", true) {
			@Override
			public NativeFileSystem create(VFS vfs, String source, String data, boolean ro) {
				return new NativeFileSystem(vfs, source);
			}
		};
	}
}
