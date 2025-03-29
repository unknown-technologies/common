package com.unknown.posix.test.vfs;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.io.Fcntl;
import com.unknown.posix.api.io.Stat;
import com.unknown.posix.api.io.Stream;
import com.unknown.posix.vfs.NativeFileSystem;
import com.unknown.posix.vfs.ProcessVFS;
import com.unknown.posix.vfs.Tmpfs;
import com.unknown.posix.vfs.VFS;
import com.unknown.posix.vfs.VFSDirectory;
import com.unknown.posix.vfs.VFSEntry;

public class NativeFileSystemTest {
	@Test
	public void testRoot() throws PosixException, IOException {
		NativeFileSystem fs = new NativeFileSystem(null, "/");
		long atime = ((FileTime) Files.getAttribute(Paths.get("/"), "unix:lastAccessTime")).toMillis();
		long mtime = ((FileTime) Files.getAttribute(Paths.get("/"), "unix:lastModifiedTime")).toMillis();
		long ctime = ((FileTime) Files.getAttribute(Paths.get("/"), "unix:ctime")).toMillis();
		VFSDirectory root = fs.getRoot();
		assertEquals(0, root.getUID());
		assertEquals(0, root.getGID());
		assertEquals(mtime, root.mtime().getTime());
		assertEquals(atime, root.atime().getTime());
		assertEquals(ctime, root.ctime().getTime());
		assertEquals(0755, root.getPermissions() & 0777);
	}

	@Test
	public void testProc001() throws PosixException {
		NativeFileSystem fs = new NativeFileSystem(null, "/proc");
		VFSDirectory root = fs.getRoot();
		List<VFSEntry> entries = root.readdir();
		assertEquals(1, entries.stream().filter((x) -> x.getName().equals("cpuinfo")).count());
	}

	@Test
	public void testMount001() throws PosixException, IOException {
		NativeFileSystem fs = new NativeFileSystem(null, "/");
		ProcessVFS vfs = new ProcessVFS(new VFS(), null);
		vfs.mount("/", fs);

		BasicFileAttributes info = Files
				.getFileAttributeView(Paths.get("/proc"), BasicFileAttributeView.class)
				.readAttributes();

		Stat buf = new Stat();
		vfs.stat("/proc", buf);

		assertEquals(info.size(), buf.st_size);
		assertEquals(info.lastModifiedTime().toMillis(), buf.st_mtim.toMillis());

		byte[] ref = Files.readAllBytes(Paths.get("/proc/cmdline"));
		byte[] act = new byte[ref.length];
		Stream in = vfs.open("/proc/cmdline", Fcntl.O_RDONLY, 0);
		assertEquals(act.length, in.read(act, 0, act.length));
		in.close();
		assertArrayEquals(ref, act);
	}

	@Test
	public void testMount002() throws PosixException {
		VFS rawvfs = new VFS();
		ProcessVFS vfs = new ProcessVFS(rawvfs, null);
		NativeFileSystem fs = new NativeFileSystem(rawvfs, "/");
		vfs.mount("/", fs);

		Tmpfs tmpfs = new Tmpfs(rawvfs);
		vfs.mount("/proc", tmpfs);

		List<VFSEntry> entries = vfs.list("/proc");
		assertEquals(0, entries.size());
	}

	@Test
	public void testSymlink001() throws PosixException, IOException {
		NativeFileSystem fs = new NativeFileSystem(null, "/");
		ProcessVFS vfs = new ProcessVFS(new VFS(), null);
		vfs.mount("/", fs);

		byte[] ref = Files.readAllBytes(Paths.get("/proc/self/cmdline"));
		byte[] act = new byte[ref.length];
		Stream in = vfs.open("/proc/self/cmdline", Fcntl.O_RDONLY, 0);
		assertEquals(act.length, in.read(act, 0, act.length));
		in.close();
		assertArrayEquals(ref, act);
	}

	@Test
	public void testSymlink002() throws PosixException, IOException {
		NativeFileSystem fs = new NativeFileSystem(null, "/");
		ProcessVFS vfs = new ProcessVFS(new VFS(), null);
		vfs.mount("/", fs);

		String ref = Files.readSymbolicLink(Paths.get("/proc/self/exe")).toString();
		String act = vfs.readlink("/proc/self/exe");
		assertEquals(ref, act);
	}

	@Test
	public void testSymlink003() throws PosixException, IOException {
		ProcessVFS vfs = new ProcessVFS(new VFS(), null);
		NativeFileSystem fs = new NativeFileSystem(vfs.getVFS(), "/");
		vfs.mount("/", fs);

		Path cwd = Paths.get(".").toAbsolutePath().normalize();

		StringBuilder posixPath = new StringBuilder();
		if(cwd.getNameCount() == 0) {
			posixPath.append('/');
		}
		for(int i = 0; i < cwd.getNameCount(); i++) {
			posixPath.append('/').append(cwd.getName(i));
		}
		vfs.chdir(posixPath.toString());

		String ref = Files.readSymbolicLink(Paths.get("/lib/libmount.so.1")).toString();
		String act = vfs.readlink("/lib/libmount.so.1");
		assertEquals(ref, act);
	}

	@Test
	public void testSymlink004() throws PosixException, IOException {
		ProcessVFS vfs = new ProcessVFS(new VFS(), null);
		NativeFileSystem fs = new NativeFileSystem(vfs.getVFS(), "/");
		vfs.mount("/", fs);

		Path cwd = Paths.get(".").toAbsolutePath().normalize();

		StringBuilder posixPath = new StringBuilder();
		if(cwd.getNameCount() == 0) {
			posixPath.append('/');
		}
		for(int i = 0; i < cwd.getNameCount(); i++) {
			posixPath.append('/').append(cwd.getName(i));
		}
		vfs.chdir(posixPath.toString());

		String ref = Files.readSymbolicLink(Paths.get("/lib/ld-linux.so.2")).toString();
		String act = vfs.readlink("/lib/ld-linux.so.2");
		assertEquals(ref, act);
	}

	@Test
	public void testSymlink005() throws PosixException, IOException {
		ProcessVFS vfs = new ProcessVFS(new VFS(), null);
		NativeFileSystem fs = new NativeFileSystem(vfs.getVFS(), "/");
		vfs.mount("/", fs);

		Path cwd = Paths.get(".").toAbsolutePath().normalize();

		StringBuilder posixPath = new StringBuilder();
		if(cwd.getNameCount() == 0) {
			posixPath.append('/');
		}
		for(int i = 0; i < cwd.getNameCount(); i++) {
			posixPath.append('/').append(cwd.getName(i));
		}
		vfs.chdir(posixPath.toString());

		byte[] ref = Files.readAllBytes(Paths.get("/lib/ld-linux.so.2"));
		byte[] act = new byte[ref.length];
		Stream in = vfs.open("/lib/ld-linux.so.2", Fcntl.O_RDONLY, 0);
		assertEquals(act.length, in.read(act, 0, act.length));
		in.close();
		assertArrayEquals(ref, act);
	}

	@Ignore
	@Test
	public void testSymlink006() throws PosixException, IOException {
		ProcessVFS vfs = new ProcessVFS(new VFS(), null);
		NativeFileSystem fs = new NativeFileSystem(vfs.getVFS(), "/");
		vfs.mount("/", fs);

		Path cwd = Paths.get(".").toAbsolutePath().normalize();

		StringBuilder posixPath = new StringBuilder();
		if(cwd.getNameCount() == 0) {
			posixPath.append('/');
		}
		for(int i = 0; i < cwd.getNameCount(); i++) {
			posixPath.append('/').append(cwd.getName(i));
		}
		vfs.chdir(posixPath.toString());

		byte[] ref = Files.readAllBytes(Paths.get("/lib/power8/altivec/libc.so.6"));
		byte[] act = new byte[ref.length];
		Stream in = vfs.open("/lib/power8/altivec/libc.so.6", Fcntl.O_RDONLY, 0);
		assertEquals(act.length, in.read(act, 0, act.length));
		in.close();
		assertArrayEquals(ref, act);
	}
}
