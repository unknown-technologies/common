package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.unknown.posix.api.PosixException;
import com.unknown.posix.vfs.TmpfsFile;
import com.unknown.util.ResourceLoader;
import com.unknown.vm.posix.PosixEnvironment;
import com.unknown.vm.power.ElfLoader;
import com.unknown.vm.power.Power;

public class ElfLdTest {
	private Power power;
	private PosixEnvironment posix;
	private ByteArrayOutputStream stdout;
	private ByteArrayOutputStream stderr;

	// @formatter:off
	public static final String HELPMSG =
			"Usage: ld.so [OPTION]... EXECUTABLE-FILE [ARGS-FOR-PROGRAM...]\n" +
			"You have invoked `ld.so', the helper program for shared library executables.\n" +
			"This program usually lives in the file `/lib/ld.so', and special directives\n" +
			"in executable files using ELF shared libraries tell the system's program\n" +
			"loader to load the helper program from this file.  This helper program loads\n" +
			"the shared libraries needed by the program executable, prepares the program\n" +
			"to run, and runs it.  You may invoke this helper program directly from the\n" +
			"command line to load and run an ELF executable file; this is like executing\n" +
			"that file itself, but always uses this helper program from the file you\n" +
			"specified, instead of the helper program file specified in the executable\n" +
			"file you run.  This is mostly of use for maintainers to test new versions\n" +
			"of this helper program; chances are you did not intend to run this program.\n" +
			"\n" +
			"  --list                list all dependencies and how they are resolved\n" +
			"  --verify              verify that given object really is a dynamically linked\n" +
			"			object we can handle\n" +
			"  --inhibit-cache       Do not use //etc/ld.so.cache\n" +
			"  --library-path PATH   use given PATH instead of content of the environment\n" +
			"			variable LD_LIBRARY_PATH\n" +
			"  --inhibit-rpath LIST  ignore RUNPATH and RPATH information in object names\n" +
			"			in LIST\n" +
			"  --audit LIST          use objects named in LIST as auditors\n";

	public static final String NOTFOUNDMSG =
			"test: error while loading shared libraries: test: cannot open shared object file: No such file or directory\n";

	public static final String LD_SHOW_AUXV =
			"AT_DCACHEBSIZE:  0x20\n" +
			"AT_ICACHEBSIZE:  0x20\n" +
			"AT_UCACHEBSIZE:  0x0\n" +
			"AT_PHDR:         0x40000034\n" +
			"AT_PHENT:        32\n" +
			"AT_PHNUM:        7\n" +
			"AT_PAGESZ:       4096\n" +
			"AT_BASE:         0x0\n" +
			"AT_FLAGS:        0x0\n" +
			"AT_ENTRY:        0x40019a34\n" +
			"AT_UID:          1000\n" +
			"AT_EUID:         1000\n" +
			"AT_GID:          1000\n" +
			"AT_EGID:         1000\n" +
			"AT_PLATFORM:     power8\n" +
			"AT_HWCAP:        vsx fpu altivec ppc32\n" +
			"AT_SECURE:       0\n" +
			"AT_BASE_PLATFORM:power8\n" +
			"AT_RANDOM:       0xf6ffefa0\n" +
			"AT_HWCAP2:       arch_2_07\n" +
			"AT_EXECFN:       ld-2.23.so\n";

	public static final String STATICALLY_LINKED = "\tstatically linked\n";
	// @formatter:on

	private static byte[] readTestFile(String name) throws IOException {
		try(InputStream in = ResourceLoader.loadResource(ElfLdTest.class, name);
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
		ElfLoader loader = new ElfLoader(power);
		loader.load(readTestFile("ld-2.23.so"));
		assertEquals(0x40019a34, power.getState().pc);
		assertEquals(127, power.run());
		assertEquals("", stdout());
		assertEquals(HELPMSG, stderr());
	}

	@Test
	public void run002() throws IOException {
		ElfLoader loader = new ElfLoader(power);
		loader.setProgramName("ld-2.23.so");
		loader.setArguments("ld-2.23.so", "test");
		loader.load(readTestFile("ld-2.23.so"));
		assertEquals(0x40019a34, power.getState().pc);
		assertEquals(127, power.run());
		assertEquals("", stdout());
		assertEquals(NOTFOUNDMSG, stderr());
	}

	@Test
	public void run003() throws IOException {
		Map<String, String> env = new HashMap<>();
		env.put("LD_SHOW_AUXV", "1");
		ElfLoader loader = new ElfLoader(power);
		loader.setProgramName("ld-2.23.so");
		loader.setArguments("ld-2.23.so");
		loader.setEnvironment(env);
		loader.load(readTestFile("ld-2.23.so"));
		assertEquals(0x40019a34, power.getState().pc);
		assertEquals(127, power.run());
		assertEquals(LD_SHOW_AUXV, stdout());
		assertEquals(HELPMSG, stderr());
	}

	@Test
	public void run004() throws IOException, PosixException {
		posix.getVFS().mkfile("/args001.elf", 0, 0, 0755);
		((TmpfsFile) posix.getVFS().get("/args001.elf")).setContent(readTestFile("args001.elf"));
		ElfLoader loader = new ElfLoader(power);
		loader.setProgramName("ld-2.23.so");
		loader.setArguments("ld-2.23.so", "--list", "/args001.elf");
		loader.load(readTestFile("ld-2.23.so"));
		assertEquals(0x40019a34, power.getState().pc);
		assertEquals(0, power.run());
		assertEquals(STATICALLY_LINKED, stdout());
		assertEquals("", stderr());
	}
}
