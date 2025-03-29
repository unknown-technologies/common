package com.unknown.posix.vfs;

public abstract class PosixFileSystem<T extends VFSFileSystem> {
	private final String type;
	private final boolean nodev;

	protected PosixFileSystem(String type, boolean nodev) {
		this.type = type;
		this.nodev = nodev;
	}

	public final String getType() {
		return type;
	}

	public final boolean isNoDev() {
		return nodev;
	}

	@Override
	public final String toString() {
		if(nodev) {
			return "nodev\t" + type;
		} else {
			return "\t" + type;
		}
	}

	public abstract T create(VFS vfs, String source, String data, boolean ro);
}
