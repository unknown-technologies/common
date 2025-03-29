package com.unknown.posix.test.vfs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.unknown.posix.api.BytePosixPointer;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.PosixPointer;
import com.unknown.posix.api.io.Fcntl;
import com.unknown.posix.api.io.Stream;
import com.unknown.posix.vfs.ProcessVFS;
import com.unknown.posix.vfs.VFS;
import com.unknown.posix.vfs.VFSDirectory;
import com.unknown.posix.vfs.VFSEntry;
import com.unknown.posix.vfs.VFSFile;

public class SymlinkTest {
	private ProcessVFS vfs;

	@Before
	public void setup() throws PosixException {
		VFS rawvfs = new VFS();
		vfs = new ProcessVFS(rawvfs, null);
		vfs.mkdir("/lib", 0, 0, 0755);
		vfs.mkdir("/usr", 0, 0, 0755);
		vfs.mkdir("/usr/powerpc-linux-gnu", 0, 0, 0755);
		vfs.mkdir("/usr/powerpc-linux-gnu/lib", 0, 0, 0755);
		vfs.mkdir("/home", 0, 0, 0755);
		vfs.mkdir("/home/user", 0, 0, 0755);
		vfs.chdir("/home/user");

		VFSDirectory dir = vfs.get("/usr/powerpc-linux-gnu/lib");
		dir.mkfile("ld-2.29.so", 0, 0, 0755);
		VFSFile file = vfs.get("/usr/powerpc-linux-gnu/lib/ld-2.29.so");
		Stream stream = file.open(Fcntl.O_WRONLY);
		PosixPointer ptr = new BytePosixPointer("dynamic linker".getBytes());
		stream.write(ptr, (int) ptr.size());
		stream.close();

		vfs.symlink("/usr/powerpc-linux-gnu/lib/ld.so.1", 0, 0, 0755, "ld-2.29.so");
		vfs.symlink("/lib/ld.so.1", 0, 0, 0755, "/usr/powerpc-linux-gnu/lib/ld.so.1");
	}

	@Test
	public void testReadlink() throws PosixException {
		assertEquals("/usr/powerpc-linux-gnu/lib/ld.so.1", vfs.readlink("/lib/ld.so.1"));
		assertEquals("ld-2.29.so", vfs.readlink("/usr/powerpc-linux-gnu/lib/ld.so.1"));
	}

	@Test
	public void testGetLevel0() throws PosixException {
		VFSEntry file = vfs.get("/usr/powerpc-linux-gnu/lib/ld.so.1");
		assertNotNull(file);
		assertEquals("ld-2.29.so", file.getName());
	}

	@Test
	public void testGetLevel1() throws PosixException {
		VFSEntry file = vfs.get("/lib/ld.so.1");
		assertNotNull(file);
		assertEquals("ld-2.29.so", file.getName());
	}

	@Test
	public void testGetLevel0Nofollow() throws PosixException {
		VFSEntry file = vfs.get("/usr/powerpc-linux-gnu/lib/ld.so.1", false);
		assertNotNull(file);
		assertEquals("ld.so.1", file.getName());
	}

	@Test
	public void testGetLevel1Nofollow() throws PosixException {
		VFSEntry file = vfs.get("/lib/ld.so.1", false);
		assertNotNull(file);
		assertEquals("ld.so.1", file.getName());
	}
}
