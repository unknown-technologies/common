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

public class ElfTest {
	private Power power;
	private PosixEnvironment posix;
	private ByteArrayOutputStream stdout;

	private static byte[] readTestFile(String name) throws IOException {
		try(InputStream in = ResourceLoader.loadResource(ElfTest.class, name);
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
		power = new Power();
		posix = power.getPosixEnvironment();
		posix.setStandardOut(stdout);
	}

	private String stdout() {
		byte[] buf = stdout.toByteArray();
		return new String(buf);
	}

	@Test
	public void run001() throws IOException {
		ElfLoader loader = new ElfLoader(power);
		loader.load(readTestFile("write001.elf"));
		assertEquals(0x10000104, power.getState().pc);
		power.run();
		assertEquals("Hello world\n", stdout());
	}

	@Test
	public void run002() throws IOException {
		ElfLoader loader = new ElfLoader(power);
		loader.load(readTestFile("write002.elf"));
		assertEquals(0x10000104, power.getState().pc);
		power.run();
		assertEquals("Hello world\n", stdout());
	}
}
