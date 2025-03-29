package com.unknown.posix.test.elf;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.unknown.posix.elf.Elf;
import com.unknown.posix.elf.ProgramHeader;
import com.unknown.posix.elf.Section;
import com.unknown.posix.elf.Symbol;
import com.unknown.util.ResourceLoader;

public class ElfParserTest {
	private Elf elf;

	private static byte[] readTestFile() throws IOException {
		try(InputStream in = ResourceLoader.loadResource(ElfParserTest.class, "metroid2.elf");
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
	public void setup() throws IOException {
		elf = new Elf(readTestFile());
	}

	@Test
	public void testFileHeader() {
		assertEquals(Elf.ELFCLASS32, elf.ei_class);
		assertEquals(Elf.ELFDATA2MSB, elf.ei_data);
		assertEquals(1, elf.ei_version);
		assertEquals(Elf.OSABI_SYSV, elf.ei_osabi);
		assertEquals(0, elf.ei_abiversion);
		assertEquals(Elf.ET_EXEC, elf.e_type);
		assertEquals(Elf.EM_PPC, elf.e_machine);
		assertEquals(1, elf.e_version);
		assertEquals(0x80003100, elf.e_entry);
		assertEquals(52, elf.e_phoff);
		assertEquals(471648, elf.e_shoff);
		assertEquals(0x80000000, elf.e_flags);
		assertEquals(32, elf.e_phentsize);
		assertEquals(8, elf.e_phnum);
		assertEquals(40, elf.e_shentsize);
		assertEquals(15, elf.e_shnum);
		assertEquals(12, elf.e_shstrndx);
	}

	private void testProgramHeader(int n, int p_type, long p_offset, long p_vaddr, long p_paddr, long p_filesz,
			long p_memsz, int p_flags, long p_align) {
		ProgramHeader ph = elf.getProgramHeader(n);
		assertEquals(p_type, ph.p_type);
		assertEquals(p_offset, ph.p_offset);
		assertEquals(p_vaddr, ph.p_vaddr);
		assertEquals(p_paddr, ph.p_paddr);
		assertEquals(p_filesz, ph.p_filesz);
		assertEquals(p_memsz, ph.p_memsz);
		assertEquals(p_flags, ph.p_flags);
		assertEquals(p_align, ph.p_align);
	}

	@Test
	public void testProgramHeaders() {
		int LOAD = Elf.PT_LOAD;
		int R = Elf.PF_R;
		int RW = Elf.PF_R | Elf.PF_W;
		int RWE = Elf.PF_R | Elf.PF_W | Elf.PF_X;
		int E = Elf.PF_X;

		// @formatter:off
		testProgramHeader(0, LOAD, 0x000140, 0x80003100, 0x00000000, 0x003c0, 0x003c0, RWE, 0x20);
		testProgramHeader(1, LOAD, 0x000500, 0x800034c0, 0x00000000, 0x47714, 0x47714, R|E, 0x20);
		testProgramHeader(2, LOAD, 0x047c20, 0x8004abe0, 0x00000000, 0x00fd0, 0x00fd0, R,   0x20);
		testProgramHeader(3, LOAD, 0x048c00, 0x8004bbc0, 0x00000000, 0x164b8, 0x164b8, RW,  0x20);
		testProgramHeader(4, LOAD, 0x05f0c0, 0x80062080, 0x00000000, 0x00000, 0x0f308, RW,  0x20);
		testProgramHeader(5, LOAD, 0x05f0c0, 0x800713a0, 0x00000000, 0x00478, 0x00478, RW,  0x20);
		testProgramHeader(6, LOAD, 0x05f540, 0x80071820, 0x00000000, 0x00000, 0x003b4, RW,  0x20);
		testProgramHeader(7, LOAD, 0x05f540, 0x80071be0, 0x00000000, 0x00ea8, 0x00ea8, RW,  0x20);
		// @formatter:on
	}

	private void testSectionHeader(int i, String name, int type, int addr, int off, int size, int es, int flg,
			int lk, int inf, int al) {
		Section sh = elf.getSection(i);
		assertEquals(name, sh.getName());
		assertEquals(type, sh.getType());
		assertEquals(addr, sh.getAddress());
		assertEquals(off, sh.getOffset());
		assertEquals(size, sh.sh_size);
		assertEquals(es, sh.sh_entsize);
		assertEquals(flg, sh.sh_flags);
		assertEquals(lk, sh.getLinkNum());
		assertEquals(inf, sh.sh_info);
		assertEquals(al, sh.sh_addralign);
	}

	@Test
	public void testSectionHeaders() {
		int NULL = Elf.SHT_NULL;
		int PROGBITS = Elf.SHT_PROGBITS;
		int NOBITS = Elf.SHT_NOBITS;
		int STRTAB = Elf.SHT_STRTAB;
		int SYMTAB = Elf.SHT_SYMTAB;
		int WAX = Elf.SHF_WRITE | Elf.SHF_ALLOC | Elf.SHF_EXECINSTR;
		int WA = Elf.SHF_WRITE | Elf.SHF_ALLOC;
		int AX = Elf.SHF_ALLOC | Elf.SHF_EXECINSTR;
		int A = Elf.SHF_ALLOC;

		// @formatter:off
		testSectionHeader( 0, "",          NULL,     0x00000000, 0x000000, 0x000000, 0x00,   0, 0,    0,  0);
		testSectionHeader( 1, ".init",     PROGBITS, 0x80003100, 0x000140, 0x0003c0, 0x00, WAX, 0,    0,  4);
		testSectionHeader( 2, ".text",     PROGBITS, 0x800034c0, 0x000500, 0x047714, 0x00,  AX, 0,    0, 16);
		testSectionHeader( 3, ".ctors",    PROGBITS, 0x8004abe0, 0x047c20, 0x000008, 0x00,   A, 0,    0,  1);
		testSectionHeader( 4, ".dtors",    PROGBITS, 0x8004ac00, 0x047c40, 0x000008, 0x00,   A, 0,    0,  1);
		testSectionHeader( 5, ".rodata",   PROGBITS, 0x8004ac20, 0x047c60, 0x000f90, 0x00,   A, 0,    0,  8);
		testSectionHeader( 6, ".data",     PROGBITS, 0x8004bbc0, 0x048c00, 0x0164b8, 0x00,  WA, 0,    0, 32);
		testSectionHeader( 7, ".bss",      NOBITS,   0x80062080, 0x05f0c0, 0x00f308, 0x00,  WA, 0,    0, 32);
		testSectionHeader( 8, ".sdata",    PROGBITS, 0x800713a0, 0x05f0c0, 0x000478, 0x00,  WA, 0,    0,  8);
		testSectionHeader( 9, ".sbss",     NOBITS,   0x80071820, 0x05f540, 0x0003b4, 0x00,  WA, 0,    0,  8);
		testSectionHeader(10, ".sdata2",   PROGBITS, 0x80071be0, 0x05f540, 0x000ea8, 0x00,  WA, 0,    0,  8);
		testSectionHeader(11, ".sbss2",    NOBITS,   0x80072a88, 0x0603e8, 0x000000, 0x00,   A, 0,    0,  1);
		testSectionHeader(12, ".shstrtab", STRTAB,   0x00000000, 0x0731fa, 0x000064, 0x00,   0, 0,    0,  1);
		testSectionHeader(13, ".symtab",   SYMTAB,   0x00000000, 0x0603e8, 0x00b0f0, 0x10,   0,14, 1519,  4);
		testSectionHeader(14, ".strtab",   STRTAB,   0x00000000, 0x06b4d8, 0x007d22, 0x00,   0, 0,    0,  1);
		// @formatter:on

	}

	@Test
	public void testSectionHeaderStringTable() {
		String[] ref = { "", ".init", ".text", ".ctors", ".dtors", ".rodata", ".data", ".bss", ".sdata",
				".sbss", ".sdata2", ".sbss2", ".shstrtab", ".symtab", ".strtab" };
		String[] act = elf.stringTable.getStrings().toArray(new String[0]);
		assertArrayEquals(ref, act);
	}

	@Test
	public void testSymbolTable() {
		Symbol GXGetScissor = elf.getSymbol("GXGetScissor");
		assertEquals(0x80048324, GXGetScissor.getValue());
		assertEquals(72, GXGetScissor.getSize());
		assertEquals(Symbol.FUNC, GXGetScissor.getType());
		assertEquals(Symbol.GLOBAL, GXGetScissor.getBind());
		assertEquals(Symbol.DEFAULT, GXGetScissor.getVisibility());
		assertEquals(2, GXGetScissor.getSectionIndex());
		assertEquals(2808, GXGetScissor.getIndex());

		Symbol main = elf.getSymbol("main");
		assertEquals(0x800034c0, main.getValue());
		assertEquals(728, main.getSize());
		assertEquals(Symbol.FUNC, main.getType());
		assertEquals(Symbol.GLOBAL, main.getBind());
		assertEquals(Symbol.DEFAULT, main.getVisibility());
		assertEquals(2, main.getSectionIndex());
		assertEquals(1543, main.getIndex());
	}
}
