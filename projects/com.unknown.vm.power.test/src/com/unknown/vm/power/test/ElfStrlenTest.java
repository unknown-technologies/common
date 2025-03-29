package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.unknown.util.ResourceLoader;
import com.unknown.vm.posix.PosixEnvironment;
import com.unknown.vm.power.ElfLoader;
import com.unknown.vm.power.Power;

public class ElfStrlenTest {
	private Power power;
	private PosixEnvironment posix;
	private ByteArrayOutputStream stdout;
	private ByteArrayOutputStream stderr;

	private static byte[] readTestFile(String name) throws IOException {
		try(InputStream in = ResourceLoader.loadResource(ElfStrlenTest.class, name);
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
	public void setup() {
		stdout = new ByteArrayOutputStream();
		stderr = new ByteArrayOutputStream();
		power = new Power();
		posix = power.getPosixEnvironment();
		posix.setStandardOut(stdout);
		posix.setStandardErr(stderr);
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
		String file = "strlen001.elf";
		ElfLoader loader = new ElfLoader(power);
		loader.setProgramName(file);
		loader.setArguments(file);
		loader.load(readTestFile(file));
		assertEquals(0x1000014c, power.getState().pc);
		assertEquals(0, power.run());
		assertEquals("Hello world\n", stdout());
		assertEquals("", stderr());
	}
}
