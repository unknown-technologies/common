package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.io.Stat;
import com.unknown.posix.vfs.Tmpfs;
import com.unknown.posix.vfs.TmpfsDirectory;
import com.unknown.posix.vfs.TmpfsFile;
import com.unknown.util.ResourceLoader;
import com.unknown.vm.posix.PosixEnvironment;
import com.unknown.vm.power.ElfLoader;
import com.unknown.vm.power.Power;

public class ElfStatTest {
	private Power power;
	private PosixEnvironment posix;
	private ByteArrayOutputStream stdout;
	private ByteArrayOutputStream stderr;
	private TmpfsFile test_c;

	// @formatter:off
	public static final String EXPECTED =
			"stat for 0\n" +
			"st_dev:          0x000000001009ee70\n" +
			"st_ino:          0x0000000000000000\n" +
			"st_mode:         0x0000000000002190\n" +
			"st_nlink:        0x0000000000000000\n" +
			"st_uid:          0x0000000000000000\n" +
			"st_gid:          0x0000000000000000\n" +
			"st_rdev:         0x000000001009ee70\n" +
			"st_size:         0x0000000000000000\n" +
			"st_blksize:      0x0000000000000000\n" +
			"st_blocks:       0x0000000000000000\n" +
			"st_atim.tv_sec:  0x0000000000000000\n" +
			"st_atim.tv_nsec: 0x0000000000000000\n" +
			"st_mtim.tv_sec:  0x0000000000000000\n" +
			"st_mtim.tv_nsec: 0x0000000000000000\n" +
			"st_ctim.tv_sec:  0x0000000000000000\n" +
			"st_ctim.tv_nsec: 0x0000000000000000\n" +
			"stat for 1\n" +
			"st_dev:          0x000000001009ee70\n" +
			"st_ino:          0x0000000000000000\n" +
			"st_mode:         0x0000000000002190\n" +
			"st_nlink:        0x0000000000000000\n" +
			"st_uid:          0x0000000000000000\n" +
			"st_gid:          0x0000000000000000\n" +
			"st_rdev:         0x000000001009ee70\n" +
			"st_size:         0x0000000000000000\n" +
			"st_blksize:      0x0000000000000000\n" +
			"st_blocks:       0x0000000000000000\n" +
			"st_atim.tv_sec:  0x0000000000000000\n" +
			"st_atim.tv_nsec: 0x0000000000000000\n" +
			"st_mtim.tv_sec:  0x0000000000000000\n" +
			"st_mtim.tv_nsec: 0x0000000000000000\n" +
			"st_ctim.tv_sec:  0x0000000000000000\n" +
			"st_ctim.tv_nsec: 0x0000000000000000\n" +
			"stat for 2\n" +
			"st_dev:          0x000000001009ee70\n" +
			"st_ino:          0x0000000000000000\n" +
			"st_mode:         0x0000000000002190\n" +
			"st_nlink:        0x0000000000000000\n" +
			"st_uid:          0x0000000000000000\n" +
			"st_gid:          0x0000000000000000\n" +
			"st_rdev:         0x000000001009ee70\n" +
			"st_size:         0x0000000000000000\n" +
			"st_blksize:      0x0000000000000000\n" +
			"st_blocks:       0x0000000000000000\n" +
			"st_atim.tv_sec:  0x0000000000000000\n" +
			"st_atim.tv_nsec: 0x0000000000000000\n" +
			"st_mtim.tv_sec:  0x0000000000000000\n" +
			"st_mtim.tv_nsec: 0x0000000000000000\n" +
			"st_ctim.tv_sec:  0x0000000000000000\n" +
			"st_ctim.tv_nsec: 0x0000000000000000\n";
	public static final String EXPECTED_TEST =
			"stat for /tmp/test.c\n" +
			"st_dev:          0x000000001009ee70\n" +
			"st_ino:          0x%016x\n" +
			"st_mode:         0x0000000000008284\n" +
			"st_nlink:        0x0000000000000001\n" +
			"st_uid:          0x0000000000000000\n" +
			"st_gid:          0x0000000000000000\n" +
			"st_rdev:         0x000000001009ee70\n" +
			"st_size:         0x000000000000004c\n" +
			"st_blksize:      0x0000000000001000\n" +
			"st_blocks:       0x0000000000000001\n" +
			"st_atim.tv_sec:  0x0000000059c44b5a\n" +
			"st_atim.tv_nsec: 0x0000000000000000\n" +
			"st_mtim.tv_sec:  0x0000000059c44b5a\n" +
			"st_mtim.tv_nsec: 0x0000000000000000\n" +
			"st_ctim.tv_sec:  0x0000000059c44b5a\n" +
			"st_ctim.tv_nsec: 0x0000000000000000\n";
	// @formatter:on

	private static byte[] readTestFile(String name) throws IOException {
		try(InputStream in = ResourceLoader.loadResource(ElfStatTest.class, name);
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			byte[] buf = new byte[256];
			int n;
			while((n = in.read(buf)) != -1) {
				out.write(buf, 0, n);
			}
			return out.toByteArray();
		}
	}

	@Before
	public void setup() throws PosixException, IOException {
		stdout = new ByteArrayOutputStream();
		stderr = new ByteArrayOutputStream();
		power = new Power();
		posix = power.getPosixEnvironment();
		posix.setStandardOutTTY(stdout);
		posix.setStandardErrTTY(stderr);

		Tmpfs tmpfs = new Tmpfs(posix.getVFS().getVFS());
		posix.getPosix().mkdir("/tmp", 0755);
		posix.getPosix().mkdir("/proc", 0755);
		posix.getPosix().mkdir("/proc/1", 0755);
		posix.getPosix().symlink("/proc/1", "/proc/self");
		posix.mount("/tmp", tmpfs);
		TmpfsDirectory dir = tmpfs.getRoot();
		test_c = (TmpfsFile) dir.mkfile("test.c", 0, 0, 644);
		test_c.setContent(readTestFile("files/test.c"));
		Date time = new Date(1506036570_000L);
		test_c.atime(time);
		test_c.mtime(time);
		test_c.ctime(time);
		Stat stat = new Stat();
		test_c.stat(stat);
		TmpfsFile exe = (TmpfsFile) dir.mkfile("program", 0, 0, 644);
		exe.setContent("The file".getBytes());
		posix.getPosix().symlink("/tmp/program", "/proc/1/exe");
	}

	private String stdout() {
		byte[] buf = stdout.toByteArray();
		return new String(buf);
	}

	private String stderr() {
		byte[] buf = stderr.toByteArray();
		return new String(buf);
	}

	@Test
	public void stat001() throws IOException {
		posix.setStandardInTTY(new ByteArrayInputStream(new byte[0]));
		ElfLoader loader = new ElfLoader(power);
		loader.setProgramName("stat");
		loader.setArguments("stat");
		loader.load(readTestFile("stat.elf"));
		assertEquals(0, power.run());
		assertEquals(EXPECTED, stdout());
		assertEquals("", stderr());
	}

	@Test
	public void stat002() throws IOException {
		ElfLoader loader = new ElfLoader(power);
		int inode = test_c.getInode();
		loader.setProgramName("stat");
		loader.setArguments("stat", "/tmp/test.c");
		loader.load(readTestFile("stat.elf"));
		assertEquals(0, power.run());
		assertEquals(EXPECTED_TEST.formatted(inode), stdout());
		assertEquals("", stderr());
	}
}
