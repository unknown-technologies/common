package com.unknown.posix.test.vfs;

import static org.junit.Assert.assertArrayEquals;
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
import com.unknown.posix.vfs.Tmpfs;
import com.unknown.posix.vfs.VFS;
import com.unknown.posix.vfs.VFSDirectory;
import com.unknown.posix.vfs.VFSFile;

public class TmpfsTest {
	private ProcessVFS vfs;
	private Tmpfs tmpfs;

	@Before
	public void setup() throws PosixException {
		VFS rawvfs = new VFS();
		vfs = new ProcessVFS(rawvfs, null);
		tmpfs = new Tmpfs(rawvfs);
		vfs.mkdir("/tmp", 0, 0, 0755);
		vfs.mount("/tmp", tmpfs);
	}

	@Test
	public void test001() throws PosixException {
		VFSDirectory dir = vfs.get("/tmp");
		dir.mkfile("test", 0, 0, 0644);

		VFSFile file = vfs.get("/tmp/test");
		assertNotNull(file);
		assertEquals(0, file.size());
		assertEquals(0, file.getUID());
		assertEquals(0, file.getGID());
		assertEquals(0644, file.getPermissions());
	}

	@Test
	public void test002() throws PosixException {
		VFSDirectory dir = vfs.get("/tmp");
		dir.mkfile("test", 0, 0, 0644);

		VFSFile file = vfs.get("/tmp/test");
		Stream stream = file.open(Fcntl.O_WRONLY);
		assertEquals(0, stream.lseek(0, Stream.SEEK_SET));
		assertEquals(0, stream.lseek(0, Stream.SEEK_END));
		byte[] data = "Hello world!".getBytes();
		PosixPointer ptr = new BytePosixPointer(data);
		assertEquals(data.length, stream.write(ptr, data.length));
		assertEquals(data.length, stream.lseek(0, Stream.SEEK_END));
		assertEquals(data.length, stream.lseek(0, Stream.SEEK_CUR));
		assertEquals(0, stream.close());

		stream = file.open(Fcntl.O_RDONLY);
		assertEquals(data.length, file.size());
		assertEquals(data.length, stream.lseek(0, Stream.SEEK_END));
		assertEquals(0, stream.lseek(0, Stream.SEEK_SET));
		assertEquals(0, stream.lseek(0, Stream.SEEK_CUR));

		byte[] read = new byte[data.length];
		ptr = new BytePosixPointer(read);
		assertEquals(data.length, stream.read(ptr, data.length));
		assertArrayEquals(data, read);
		assertEquals(data.length, stream.lseek(0, Stream.SEEK_CUR));
	}
}
