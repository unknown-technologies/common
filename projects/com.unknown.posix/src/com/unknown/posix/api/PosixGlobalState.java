package com.unknown.posix.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.unknown.posix.vfs.PosixFileSystem;
import com.unknown.posix.vfs.VFS;
import com.unknown.posix.vfs.VFSFileSystem;

public class PosixGlobalState {
	private final VFS vfs;

	private final Map<Long, Posix> processes;

	private final Map<String, PosixFileSystem<? extends VFSFileSystem>> filesystems;

	public PosixGlobalState() {
		vfs = new VFS();
		processes = new HashMap<>();
		filesystems = new HashMap<>();
	}

	public VFS getVFS() {
		return vfs;
	}

	public void killed(long pid) {
		processes.remove(pid);
	}

	public void spawned(Posix posix) {
		processes.put(posix.__getpid(), posix);
	}

	public Collection<Long> getProcesses() {
		return processes.keySet();
	}

	public Posix getProcess(long pid) {
		return processes.get(pid);
	}

	public void addFilesystem(PosixFileSystem<? extends VFSFileSystem> fs) {
		filesystems.put(fs.getType(), fs);
	}

	public PosixFileSystem<? extends VFSFileSystem> getFilesystem(String type) {
		return filesystems.get(type);
	}

	public Collection<PosixFileSystem<? extends VFSFileSystem>> getFilesystems() {
		return filesystems.values();
	}
}
