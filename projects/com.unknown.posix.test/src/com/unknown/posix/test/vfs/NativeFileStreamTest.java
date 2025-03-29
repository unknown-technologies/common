package com.unknown.posix.test.vfs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.file.Paths;

import org.junit.Test;

import com.unknown.posix.api.Errno;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.io.Fcntl;
import com.unknown.posix.vfs.NativeFileStream;

public class NativeFileStreamTest {
	@Test
	public void testOpen1() throws Exception {
		NativeFileStream stream = new NativeFileStream(Paths.get("/proc/cpuinfo"), Fcntl.O_RDONLY);
		assertEquals(0, stream.close());
	}

	@Test
	public void testNonExistent1() throws Exception {
		try {
			NativeFileStream stream = new NativeFileStream(Paths.get("/nonexistent"), Fcntl.O_RDONLY);
			fail();
			stream.close();
		} catch(PosixException e) {
			assertEquals(Errno.ENOENT, e.getErrno());
		}
	}
}
