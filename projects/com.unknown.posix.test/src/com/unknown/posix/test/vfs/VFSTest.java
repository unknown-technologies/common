package com.unknown.posix.test.vfs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.unknown.posix.api.Errno;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.vfs.ProcessVFS;
import com.unknown.posix.vfs.Tmpfs;
import com.unknown.posix.vfs.VFS;
import com.unknown.posix.vfs.VFSDirectory;
import com.unknown.posix.vfs.VFSEntry;

public class VFSTest {
	private ProcessVFS vfs;

	@Before
	public void setup() {
		vfs = new ProcessVFS(new VFS(), null);
	}

	@Test
	public void normalize001() {
		assertEquals("/", VFS.normalize("/"));
	}

	@Test
	public void normalize002() {
		assertEquals("/test", VFS.normalize("/test"));
	}

	@Test
	public void normalize003() {
		assertEquals("test", VFS.normalize("./test"));
	}

	@Test
	public void normalize004() {
		assertEquals("test", VFS.normalize("../test"));
	}

	@Test
	public void normalize005() {
		assertEquals("test", VFS.normalize("xyz/../test"));
	}

	@Test
	public void normalize006() {
		assertEquals("xyz/test", VFS.normalize("xyz/./test"));
	}

	@Test
	public void normalize007() {
		assertEquals("/xyz/test", VFS.normalize("/xyz/./test"));
	}

	@Test
	public void normalize008() {
		assertEquals("/xyz/test", VFS.normalize("/xyz//test"));
	}

	@Test
	public void normalize009() {
		assertEquals("xyz/test", VFS.normalize("xyz////test"));
	}

	@Test
	public void normalize010() {
		assertEquals("xyz/test/", VFS.normalize("xyz////test/"));
	}

	@Test
	public void normalize011() {
		assertEquals("/xyz/test/", VFS.normalize("//xyz////test////"));
	}

	@Test
	public void normalize012() {
		assertEquals("/xyz/test/", VFS.normalize("/xyz/test/."));
	}

	@Test
	public void normalize013() {
		assertEquals("/xyz/", VFS.normalize("/xyz/test/.."));
	}

	@Test
	public void dirname001() {
		assertEquals("/", VFS.dirname("/test"));
	}

	@Test
	public void dirname002() {
		assertEquals(".", VFS.dirname("test"));
	}

	@Test
	public void dirname003() {
		assertEquals("/tmp", VFS.dirname("/tmp/test.c"));
	}

	@Test
	public void dirname004() {
		assertEquals("/tmp/dir", VFS.dirname("/tmp/dir/test.c"));
	}

	@Test
	public void dirname005() {
		assertEquals("tmp/dir", VFS.dirname("tmp/dir/test.c"));
	}

	@Test
	public void basename001() {
		assertEquals("test", VFS.basename("/test"));
	}

	@Test
	public void basename002() {
		assertEquals("test", VFS.basename("test"));
	}

	@Test
	public void basename003() {
		assertEquals("test.c", VFS.basename("/tmp/test.c"));
	}

	@Test
	public void basename004() {
		assertEquals("test.c", VFS.basename("/tmp/dir/test.c"));
	}

	@Test
	public void basename005() {
		assertEquals("test.c", VFS.basename("tmp/dir/test.c"));
	}

	@Test
	public void testRoot001() throws PosixException {
		VFSEntry root = vfs.get("/");
		assertNotNull(root);
		assertEquals(root.getEntryPath(), "");
		assertTrue(root instanceof VFSDirectory);
	}

	@Test
	public void testMkdir001() throws PosixException {
		VFSDirectory root = vfs.get("/");
		VFSDirectory test = root.mkdir("test", 0, 0, 0755);
		assertNotNull(test);
		assertEquals("test", test.getName());
		assertEquals(0, test.getUID());
		assertEquals(0, test.getGID());
		assertEquals(0755, test.getPermissions());
		assertEquals(1, root.readdir().size());
		assertEquals(test, root.readdir().get(0));
		VFSDirectory dir = vfs.get("/test");
		assertNotNull(dir);
		assertSame(test, dir);
	}

	@Test
	public void testEnoent001() {
		try {
			vfs.get("/file");
			fail();
		} catch(PosixException e) {
			assertEquals(Errno.ENOENT, e.getErrno());
		}
	}

	@Test
	public void testEnotdir001() throws PosixException {
		VFSDirectory root = vfs.get("/");
		VFSDirectory test = root.mkdir("test", 0, 0, 0755);
		test.mkfile("file", 0, 0, 0755);
		try {
			vfs.get("/test/file/xyz");
			fail();
		} catch(PosixException e) {
			assertEquals(Errno.ENOTDIR, e.getErrno());
		}
	}

	@Test
	public void testMount001() throws PosixException {
		Tmpfs tmpfs = new Tmpfs(vfs.getVFS());
		vfs.mkdir("/tmp", 0, 0, 0755);
		vfs.mount("/tmp", tmpfs);
		VFSDirectory mnt = vfs.get("/tmp");
		assertNotNull(mnt);
		assertEquals("tmp", mnt.getName());
	}

	@Test
	public void testRealpath001() throws PosixException {
		vfs.mkdir("/tmp", 0, 0, 0755);
		vfs.mkdir("/tmp/dir", 0, 0, 0755);
		vfs.mkfile("/tmp/dir/file", 0, 0, 0755);
		vfs.symlink("/tmp/link", 0, 0, 0755, "/tmp/dir");

		assertEquals("/tmp/dir", vfs.readlink("/tmp/link"));

		// vfs.chdir("/");
		assertEquals("/tmp", vfs.realpath("/tmp"));
		assertEquals("/tmp", vfs.realpath("tmp"));
		assertEquals("/tmp/dir", vfs.realpath("tmp/dir"));
		assertEquals("/tmp/dir", vfs.realpath("/tmp/dir"));
		assertEquals("/tmp/dir/file", vfs.realpath("/tmp/dir/file"));
		assertEquals("/tmp/dir/file", vfs.realpath("/tmp/link/file"));
	}
}
