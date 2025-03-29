package com.unknown.posix.vfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.unknown.posix.api.Errno;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.Utimbuf;
import com.unknown.posix.api.io.Fcntl;
import com.unknown.posix.api.io.Stat;
import com.unknown.util.BitTest;

public class VFS {
	private VFSDirectory directory;
	private Map<String, VFSFileSystem> mounts;

	public VFS() {
		directory = new TmpfsDirectory(this, "", 0, 0, 0755); // "/" folder
		mounts = new HashMap<>();
	}

	public String realpath(String path, String at, String cwd) throws PosixException {
		return resolvePath(getPath(resolve(path, at), cwd), cwd);
	}

	private String resolvePath(String path, String cwd) throws PosixException {
		if(path.equals("") || path.equals(".")) {
			return cwd;
		}
		String[] parts = path.split("/");
		VFSDirectory dir = directory;

		StringBuilder result = new StringBuilder();
		int i = 0;
		for(String part : parts) {
			i++;
			VFSEntry entry = dir.get(part);
			if(entry == null) {
				throw new PosixException(Errno.ENOENT);
			} else if(entry instanceof VFSDirectory) {
				dir = (VFSDirectory) entry;
				result.append('/').append(part);
			} else if(entry instanceof VFSSymlink) {
				VFSSymlink link = (VFSSymlink) entry;

				// resolve link
				String linkpath = link.readlink();
				if(linkpath.startsWith("/")) {
					result = new StringBuilder(linkpath);
					entry = get(getPath(realpath(linkpath, result.toString(), cwd), cwd), cwd);
				} else {
					throw new AssertionError("relative symlinks not yet supported");
				}
				if(entry instanceof VFSDirectory) {
					dir = (VFSDirectory) entry;
				} else { // VFSFile, VFSSpecialFile
					if(i != parts.length) {
						throw new PosixException(Errno.ENOTDIR);
					} else {
						dir = (VFSDirectory) entry;
						return result.toString();
					}
				}
			} else { // VFSFile, VFSSpecialFile
				if(i != parts.length) {
					throw new PosixException(Errno.ENOTDIR);
				} else {
					result.append('/').append(part);
					return result.toString();
				}
			}
		}
		return result.toString();
	}

	private <T extends VFSEntry> T find(String path, String cwd) throws PosixException {
		return find(path, true, cwd);
	}

	private <T extends VFSEntry> T find(String path, boolean resolve, String cwd) throws PosixException {
		return find(path, resolve, directory, cwd);
	}

	@SuppressWarnings("unchecked")
	private <T extends VFSEntry> T find(String path, boolean resolve, VFSDirectory root, String cwd)
			throws PosixException {
		assert !path.startsWith("/");
		if(path.equals("")) {
			return (T) root;
		}
		String[] parts = path.split("/");
		StringBuilder pathBuf = new StringBuilder();
		VFSDirectory dir = root;
		int i = 0;
		for(String part : parts) {
			i++;
			VFSEntry entry = dir.get(part);
			if(entry == null) {
				throw new PosixException(Errno.ENOENT);
			} else if(entry instanceof VFSDirectory) {
				dir = (VFSDirectory) entry;
			} else if(entry instanceof VFSSymlink) {
				VFSSymlink link = (VFSSymlink) entry;
				if(!resolve && (i == parts.length) && !path.endsWith("/")) {
					return (T) link;
				}
				// resolve link
				String linkpath = link.readlink();
				if(linkpath.startsWith("/")) {
					entry = find(getPath(linkpath, cwd), resolve, root, cwd);
				} else {
					while(entry instanceof VFSSymlink && !linkpath.contains("/")) {
						entry = entry.getParent().get(linkpath);
						if(entry instanceof VFSSymlink) {
							linkpath = ((VFSSymlink) entry).readlink();
						}
					}
					// TODO: use VFS to resolve symlinks
					while(entry instanceof VFSSymlink) {
						entry = getat(link, link.readlink(), pathBuf.toString());
					}
				}
				if(entry instanceof VFSDirectory) {
					dir = (VFSDirectory) entry;
				} else { // VFSFile, VFSSpecialFile
					if(i != parts.length) {
						throw new PosixException(Errno.ENOTDIR);
					} else {
						return (T) entry;
					}
				}
			} else { // VFSFile, VFSSpecialFile
				if(i != parts.length) {
					throw new PosixException(Errno.ENOTDIR);
				} else {
					return (T) entry;
				}
			}
			pathBuf.append('/');
			pathBuf.append(part);
		}
		return (T) dir;
	}

	public void unlink(String path, String cwd) throws PosixException {
		String dirname = dirname(path);
		String filename = basename(path);
		VFSDirectory dir = getDirectory(dirname, cwd);
		dir.unlink(filename);
	}

	public void rmdir(String path, String cwd) throws PosixException {
		String filename = basename(path);
		String dirname = dirname(path);
		VFSDirectory dir = getDirectory(dirname, cwd);
		VFSEntry file = dir.get(filename);
		if(!(file instanceof VFSDirectory)) {
			throw new PosixException(Errno.ENOTDIR);
		}
		VFSDirectory fdir = (VFSDirectory) file;
		if(fdir.isEmpty()) {
			dir.unlink(filename);
		} else {
			throw new PosixException(Errno.ENOTEMPTY);
		}
	}

	public static String normalize(String path) {
		String[] parts = Arrays.stream(path.split("/"))
				.filter((x) -> x.length() > 0)
				.filter((x) -> !x.equals("."))
				.toArray(String[]::new);
		List<String> normalized = new ArrayList<>();
		for(String part : parts) {
			if(part.equals("..")) {
				if(!normalized.isEmpty()) {
					normalized.remove(normalized.size() - 1);
				}
			} else {
				normalized.add(part);
			}
		}
		String result = normalized.stream().collect(Collectors.joining("/"));
		// force symlink resolution in paths like symlink/, symlink/., and symlink/..
		if(path.endsWith("/") || path.endsWith("/.") || path.endsWith("/..")) {
			result += "/";
		}
		if(path.startsWith("/") && !result.equals("/")) {
			return "/" + result;
		} else {
			return result;
		}
	}

	public static String dirname(String path) {
		String normalized = normalize(path);
		String[] parts = normalized.split("/");
		if(parts.length == 1) {
			if(normalized.charAt(0) == '/') {
				return "/";
			} else {
				return ".";
			}
		}
		String result = Stream.of(parts)
				.limit(parts.length - 1)
				.filter((x) -> x.length() > 0)
				.collect(Collectors.joining("/"));
		if(normalized.charAt(0) == '/') {
			return "/" + result;
		} else {
			return result;
		}
	}

	public static String basename(String path) {
		String[] parts = normalize(path).split("/");
		return parts[parts.length - 1];
	}

	public static String resolve(String path, String at) {
		if(path.startsWith("/")) {
			return normalize(path);
		} else {
			return normalize(at + "/" + path);
		}
	}

	private static String getPath(String path, String cwd) {
		String normalized = resolve(path, cwd);
		if(normalized.startsWith("/")) {
			return normalized.substring(1);
		} else {
			return normalized;
		}
	}

	public List<VFSEntry> list(String path, String cwd) throws PosixException {
		VFSEntry entry = find(getPath(path, cwd), cwd);
		if(entry == null) {
			return Collections.emptyList();
		} else if(entry instanceof VFSDirectory) {
			return ((VFSDirectory) entry).readdir();
		} else {
			return Arrays.asList(entry);
		}
	}

	public <T extends VFSEntry> T get(String path, String cwd) throws PosixException {
		return find(getPath(path, cwd), cwd);
	}

	public <T extends VFSEntry> T get(String path, String cwd, boolean resolve) throws PosixException {
		return find(getPath(path, cwd), resolve, cwd);
	}

	@SuppressWarnings("unchecked")
	public <T extends VFSEntry> T getat(VFSSymlink symlink, String path, String cwd)
			throws PosixException {
		if(path.startsWith("/")) {
			return find(getPath(path, cwd), cwd);
		} else {
			VFSDirectory dir = symlink.getParent();
			String[] parts = path.split("/");
			int i = 0;
			for(String part : parts) {
				i++;
				switch(part) {
				case ".":
					break;
				case "..":
					dir = dir.getParent();
					break;
				default:
					VFSEntry entry = dir.get(part);
					if(entry == null) {
						throw new PosixException(Errno.ENOENT);
					} else if(entry instanceof VFSDirectory) {
						dir = (VFSDirectory) entry;
					} else if(entry instanceof VFSSymlink) {
						if(i == parts.length) {
							return (T) entry;
						} else {
							VFSSymlink s = (VFSSymlink) entry;
							return getat(s, s.readlink(), cwd);
						}
					} else if(i != parts.length) {
						throw new PosixException(Errno.ENOTDIR);
					} else {
						return (T) entry;
					}
				}
			}
			return (T) dir;
		}
	}

	public VFSDirectory getDirectory(String path, String cwd) throws PosixException {
		VFSEntry entry = find(getPath(path, cwd), cwd);
		if(entry instanceof VFSDirectory) {
			return (VFSDirectory) entry;
		} else {
			throw new PosixException(Errno.ENOTDIR);
		}
	}

	public VFSFile getFile(String path, String cwd) throws PosixException {
		VFSEntry entry = find(getPath(path, cwd), cwd);
		if(entry instanceof VFSFile) {
			return (VFSFile) entry;
		} else {
			throw new PosixException(Errno.EISDIR);
		}
	}

	public VFSDirectory mkdir(String path, long uid, long gid, long permissions, String cwd) throws PosixException {
		String filename = basename(path);
		String dirname = dirname(path);
		VFSDirectory dir = getDirectory(dirname, cwd);
		return dir.mkdir(filename, uid, gid, permissions);
	}

	public VFSFile mkfile(String path, long uid, long gid, long permissions, String cwd) throws PosixException {
		String filename = basename(path);
		String dirname = dirname(path);
		VFSDirectory dir = getDirectory(dirname, cwd);
		return dir.mkfile(filename, uid, gid, permissions);
	}

	public VFSSymlink symlink(String path, long uid, long gid, long permissions, String target, String cwd)
			throws PosixException {
		String filename = basename(path);
		String dirname = dirname(path);
		VFSDirectory dir = getDirectory(dirname, cwd);
		return dir.symlink(filename, uid, gid, permissions, target);
	}

	public VFSEntry hardlink(String path, long uid, long gid, long permissions, String target, String cwd)
			throws PosixException {
		String filename = basename(path);
		String dirname = dirname(path);
		VFSDirectory dir = getDirectory(dirname, cwd);
		VFSEntry tgt = get(target, cwd);
		return dir.hardlink(filename, uid, gid, permissions, tgt);
	}

	public com.unknown.posix.api.io.Stream open(String path, int flags, int mode, String cwd)
			throws PosixException {
		if(BitTest.test(flags, Fcntl.O_CREAT)) {
			try {
				VFSEntry entry = get(path, cwd);
				if(entry instanceof VFSFile) {
					if(BitTest.test(flags, Fcntl.O_EXCL)) {
						throw new PosixException(Errno.EEXIST);
					} else {
						return ((VFSFile) entry).open(flags, mode);
					}
				} else if(entry instanceof VFSDirectory) {
					throw new PosixException(Errno.EISDIR); // TODO
				} else {
					throw new RuntimeException("This is a strange VFS entry: " + entry);
				}
			} catch(PosixException e) {
				if(e.getErrno() == Errno.ENOENT) {
					VFSFile file = mkfile(path, 0, 0, mode, cwd);
					return file.open(flags, mode);
				} else {
					throw e;
				}
			}
		} else {
			VFSEntry entry = get(path, cwd);
			if(entry instanceof VFSFile) {
				return ((VFSFile) entry).open(flags, mode);
			} else if(entry instanceof VFSDirectory) {
				if(BitTest.test(flags, Fcntl.O_DIRECTORY)) {
					return ((VFSDirectory) entry).open(flags, mode);
				} else {
					throw new PosixException(Errno.EISDIR);
				}
			} else {
				throw new RuntimeException("This is a strange VFS entry: " + entry);
			}
		}
	}

	public String readlink(String path, String cwd) throws PosixException {
		VFSEntry entry = get(path, cwd, false);
		if(entry instanceof VFSSymlink) {
			VFSSymlink link = (VFSSymlink) entry;
			return link.readlink();
		} else {
			throw new PosixException(Errno.EINVAL);
		}
	}

	public void stat(String path, Stat buf, String cwd) throws PosixException {
		VFSEntry entry = get(path, cwd);
		entry.stat(buf);
	}

	public void chown(String path, long owner, long group, String cwd) throws PosixException {
		VFSEntry entry = get(path, cwd);
		entry.chown(owner, group);
	}

	public void chmod(String path, int mode, String cwd) throws PosixException {
		VFSEntry entry = get(path, cwd);
		entry.chmod(mode);
	}

	public void utime(String path, Utimbuf times, String cwd) throws PosixException {
		VFSEntry entry = get(path, cwd);
		entry.utime(times);
	}

	public void mount(String path, VFSFileSystem fs, String cwd) throws PosixException {
		VFSDirectory dir = getDirectory(path, cwd);
		if(dir.hasMountpoint()) {
			throw new PosixException(Errno.EBUSY);
		}
		dir.mount(fs.createMountPoint(this, path));
		mounts.put(path, fs);
	}

	public void umount(String path, String cwd) throws PosixException {
		VFSDirectory dir = getDirectory(path, cwd);
		// TODO: handle case of unmounting parent fs
		if(dir == directory) {
			throw new PosixException(Errno.EBUSY);
		}
		if(!dir.hasMountpoint()) {
			throw new PosixException(Errno.EINVAL);
		}
		dir.umount();
		mounts.remove(path);
	}

	@Override
	public String toString() {
		return "VFS[" + directory + "]";
	}
}
