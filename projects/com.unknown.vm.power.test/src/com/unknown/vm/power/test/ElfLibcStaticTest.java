package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.unknown.posix.api.PosixException;
import com.unknown.posix.vfs.Tmpfs;
import com.unknown.posix.vfs.TmpfsFile;
import com.unknown.util.ResourceLoader;
import com.unknown.vm.posix.PosixEnvironment;
import com.unknown.vm.power.ElfLoader;
import com.unknown.vm.power.Power;

public class ElfLibcStaticTest {
	private Power power;
	private PosixEnvironment posix;
	private ByteArrayOutputStream stdout;
	private ByteArrayOutputStream stderr;

	private static byte[] readTestFile(String name) throws IOException {
		try(InputStream in = ResourceLoader.loadResource(ElfLibcStaticTest.class, name);
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
	public void setup() throws PosixException {
		stdout = new ByteArrayOutputStream();
		stderr = new ByteArrayOutputStream();
		power = new Power();
		posix = power.getPosixEnvironment();
		posix.setStandardOut(stdout);
		posix.setStandardErr(stderr);
		posix.getPosix().mkdir("/tmp", 0755);
		posix.getPosix().mkdir("/proc", 0755);
		posix.getPosix().mkdir("/proc/1", 0755);
		posix.getPosix().symlink("/proc/1", "/proc/self");
		Tmpfs tmpfs = new Tmpfs(posix.getVFS().getVFS());
		posix.mount("/tmp", tmpfs);
		TmpfsFile program = (TmpfsFile) posix.getVFS().mkfile("/tmp/program", 0, 0, 0755);
		program.setContent("The program".getBytes());
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
	public void run001() throws IOException {
		String file = "libcstatic001.elf";
		ElfLoader loader = new ElfLoader(power);
		loader.setProgramName(file);
		loader.setArguments(file);
		loader.load(readTestFile(file));
		assertEquals(0x10000550, power.getState().pc);
		assertEquals(0, power.run());
		assertEquals("Hello world\n", stdout());
		assertEquals("", stderr());
	}

	@Test
	public void run002() throws IOException {
		String file = "libcstatic002.elf";
		ElfLoader loader = new ElfLoader(power);
		loader.setProgramName(file);
		loader.setArguments(file);
		loader.load(readTestFile(file));
		assertEquals(0x10000558, power.getState().pc);
		assertEquals(0, power.run());
		assertEquals("r2 = 0x100a44d0\n", stdout());
		assertEquals("", stderr());
	}
}
