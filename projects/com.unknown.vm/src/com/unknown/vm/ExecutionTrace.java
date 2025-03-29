package com.unknown.vm;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import com.unknown.posix.elf.Symbol;
import com.unknown.util.io.BEOutputStream;
import com.unknown.util.io.WordOutputStream;
import com.unknown.vm.memory.ByteMemory;
import com.unknown.vm.memory.MemoryPage;

public abstract class ExecutionTrace implements Closeable {
	protected static final byte TYPE_SYMBOLS = 0;
	protected static final byte TYPE_MMAP = 1;
	protected static final byte TYPE_MMAP2 = 2;
	protected static final byte TYPE_MUNMAP = 3;
	protected static final byte TYPE_MPROTECT = 4;
	protected static final byte TYPE_BRK = 5;
	protected static final byte TYPE_DUMP = 6;
	protected static final byte TYPE_MEM32R8 = 7;
	protected static final byte TYPE_MEM32R16 = 8;
	protected static final byte TYPE_MEM32R32 = 9;
	protected static final byte TYPE_MEM32R64 = 10;
	protected static final byte TYPE_MEM32W8 = 11;
	protected static final byte TYPE_MEM32W16 = 12;
	protected static final byte TYPE_MEM32W32 = 13;
	protected static final byte TYPE_MEM32W64 = 14;
	protected static final byte TYPE_MEM32R8F = 15;
	protected static final byte TYPE_MEM32R16F = 16;
	protected static final byte TYPE_MEM32R32F = 17;
	protected static final byte TYPE_MEM32R64F = 18;
	protected static final byte TYPE_MEM32W8F = 19;
	protected static final byte TYPE_MEM32W16F = 20;
	protected static final byte TYPE_MEM32W32F = 21;
	protected static final byte TYPE_MEM32W64F = 22;
	protected static final byte TYPE_MEM64R8 = 23;
	protected static final byte TYPE_MEM64R16 = 24;
	protected static final byte TYPE_MEM64R32 = 25;
	protected static final byte TYPE_MEM64R64 = 26;
	protected static final byte TYPE_MEM64W8 = 27;
	protected static final byte TYPE_MEM64W16 = 28;
	protected static final byte TYPE_MEM64W32 = 29;
	protected static final byte TYPE_MEM64W64 = 30;
	protected static final byte TYPE_MEM64R8F = 31;
	protected static final byte TYPE_MEM64R16F = 32;
	protected static final byte TYPE_MEM64R32F = 33;
	protected static final byte TYPE_MEM64R64F = 34;
	protected static final byte TYPE_MEM64W8F = 35;
	protected static final byte TYPE_MEM64W16F = 36;
	protected static final byte TYPE_MEM64W32F = 37;
	protected static final byte TYPE_MEM64W64F = 38;

	protected static final byte MAX_TYPE = TYPE_MEM64W64F;

	protected WordOutputStream out;

	protected ExecutionTrace(File file, short id) throws IOException {
		out = new BEOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

		out.write32bit(0x58545243);
		out.write16bit(id);
	}

	public abstract void insn(ArchitecturalState state, Instruction insn);

	@Override
	public void close() throws IOException {
		try {
			out.close();
		} finally {
			out = null;
		}
	}

	protected final void writeString(String s) throws IOException {
		if(s == null) {
			out.write16bit((short) -1);
		} else if(s.length() == 0) {
			out.write16bit((short) 0);
		} else {
			byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
			out.write16bit((short) bytes.length);
			out.write(bytes);
		}
	}

	// memory access with 32bit address space
	public void mem32Read8(int addr, byte value) throws IOException {
		out.write8bit(TYPE_MEM32R8);
		out.write32bit(addr);
		out.write8bit(value);
	}

	public void mem32Read16(int addr, short value) throws IOException {
		out.write8bit(TYPE_MEM32R16);
		out.write32bit(addr);
		out.write16bit(value);
	}

	public void mem32Read32(int addr, int value) throws IOException {
		out.write8bit(TYPE_MEM32R32);
		out.write32bit(addr);
		out.write32bit(value);
	}

	public void mem32Read64(int addr, long value) throws IOException {
		out.write8bit(TYPE_MEM32R64);
		out.write32bit(addr);
		out.write64bit(value);
	}

	public void mem32Read8Fail(int addr) throws IOException {
		out.write8bit(TYPE_MEM32R8F);
		out.write32bit(addr);
	}

	public void mem32Read16Fail(int addr) throws IOException {
		out.write8bit(TYPE_MEM32R16F);
		out.write32bit(addr);
	}

	public void mem32Read32Fail(int addr) throws IOException {
		out.write8bit(TYPE_MEM32R32F);
		out.write32bit(addr);
	}

	public void mem32Read64Fail(int addr) throws IOException {
		out.write8bit(TYPE_MEM32R64F);
		out.write32bit(addr);
	}

	public void mem32Write8(int addr, byte value) throws IOException {
		out.write8bit(TYPE_MEM32W8);
		out.write32bit(addr);
		out.write8bit(value);
	}

	public void mem32Write16(int addr, short value) throws IOException {
		out.write8bit(TYPE_MEM32W16);
		out.write32bit(addr);
		out.write16bit(value);
	}

	public void mem32Write32(int addr, int value) throws IOException {
		out.write8bit(TYPE_MEM32W32);
		out.write32bit(addr);
		out.write32bit(value);
	}

	public void mem32Write64(int addr, long value) throws IOException {
		out.write8bit(TYPE_MEM32W64);
		out.write32bit(addr);
		out.write64bit(value);
	}

	public void mem32Write8Fail(int addr, byte value) throws IOException {
		out.write8bit(TYPE_MEM32W8F);
		out.write32bit(addr);
		out.write8bit(value);
	}

	public void mem32Write16Fail(int addr, short value) throws IOException {
		out.write8bit(TYPE_MEM32W16F);
		out.write32bit(addr);
		out.write16bit(value);
	}

	public void mem32Write32Fail(int addr, int value) throws IOException {
		out.write8bit(TYPE_MEM32W32F);
		out.write32bit(addr);
		out.write32bit(value);
	}

	public void mem32Write64Fail(int addr, long value) throws IOException {
		out.write8bit(TYPE_MEM32W64F);
		out.write32bit(addr);
		out.write64bit(value);
	}

	// memory access with 64bit address space
	public void mem64Read8(long addr, byte value) throws IOException {
		out.write8bit(TYPE_MEM64R8);
		out.write64bit(addr);
		out.write8bit(value);
	}

	public void mem64Read16(long addr, short value) throws IOException {
		out.write8bit(TYPE_MEM64R16);
		out.write64bit(addr);
		out.write16bit(value);
	}

	public void mem64Read32(long addr, int value) throws IOException {
		out.write8bit(TYPE_MEM64R32);
		out.write64bit(addr);
		out.write32bit(value);
	}

	public void mem64Read64(long addr, long value) throws IOException {
		out.write8bit(TYPE_MEM64R64);
		out.write64bit(addr);
		out.write64bit(value);
	}

	public void mem64Read8Fail(long addr) throws IOException {
		out.write8bit(TYPE_MEM64R8F);
		out.write64bit(addr);
	}

	public void mem64Read16Fail(long addr) throws IOException {
		out.write8bit(TYPE_MEM64R16F);
		out.write64bit(addr);
	}

	public void mem64Read32Fail(long addr) throws IOException {
		out.write8bit(TYPE_MEM64R32F);
		out.write64bit(addr);
	}

	public void mem64Read64Fail(long addr) throws IOException {
		out.write8bit(TYPE_MEM64R64F);
		out.write64bit(addr);
	}

	public void mem64Write8(long addr, byte value) throws IOException {
		out.write8bit(TYPE_MEM64W8);
		out.write64bit(addr);
		out.write8bit(value);
	}

	public void mem64Write16(long addr, short value) throws IOException {
		out.write8bit(TYPE_MEM64W16);
		out.write64bit(addr);
		out.write16bit(value);
	}

	public void mem64Write32(long addr, int value) throws IOException {
		out.write8bit(TYPE_MEM64W32);
		out.write64bit(addr);
		out.write32bit(value);
	}

	public void mem64Write64(long addr, long value) throws IOException {
		out.write8bit(TYPE_MEM64W64);
		out.write64bit(addr);
		out.write64bit(value);
	}

	public void mem64Write8Fail(long addr, byte value) throws IOException {
		out.write8bit(TYPE_MEM64W8F);
		out.write64bit(addr);
		out.write8bit(value);
	}

	public void mem64Write16Fail(long addr, short value) throws IOException {
		out.write8bit(TYPE_MEM64W16F);
		out.write64bit(addr);
		out.write16bit(value);
	}

	public void mem64Write32Fail(long addr, int value) throws IOException {
		out.write8bit(TYPE_MEM64W32F);
		out.write64bit(addr);
		out.write32bit(value);
	}

	public void mem64Write64Fail(long addr, long value) throws IOException {
		out.write8bit(TYPE_MEM64W64F);
		out.write64bit(addr);
		out.write64bit(value);
	}

	public void symtab32(Collection<Symbol> symbols, long loadBias, long address, long size, String filename)
			throws IOException {
		out.write8bit(TYPE_SYMBOLS);
		out.write32bit((int) loadBias);
		out.write32bit((int) address);
		out.write32bit((int) size);
		writeString(filename);
		out.write32bit(symbols.size());
		for(Symbol symbol : symbols) {
			out.write32bit((int) (symbol.getValue() + loadBias));
			out.write32bit((int) symbol.getSize());
			out.write16bit(symbol.getSectionIndex());
			out.write(symbol.getBind());
			out.write(symbol.getType());
			out.write(symbol.getVisibility());
			writeString(symbol.getName());
		}
	}

	public void symtab64(Collection<Symbol> symbols, long loadBias, long address, long size, String filename)
			throws IOException {
		out.write8bit(TYPE_SYMBOLS);
		out.write64bit(loadBias);
		out.write64bit(address);
		out.write64bit(size);
		writeString(filename);
		out.write32bit(symbols.size());
		for(Symbol symbol : symbols) {
			out.write64bit(symbol.getValue() + loadBias);
			out.write64bit(symbol.getSize());
			out.write16bit(symbol.getSectionIndex());
			out.write(symbol.getBind());
			out.write(symbol.getType());
			out.write(symbol.getVisibility());
			writeString(symbol.getName());
		}
	}

	public void dump(long addr, byte[] data) throws IOException {
		if(data == null || data.length == 0) {
			return;
		}

		out.write8bit(TYPE_DUMP);
		out.write64bit(addr);
		out.write32bit(data.length);
		out.write(data);
	}

	public void mmap(long start, long end, String name) throws IOException {
		out.write8bit(TYPE_MMAP);
		out.write64bit(start);
		out.write64bit(end);
		writeString(name);
	}

	public void mmap(long addr, long len, int prot, int flags, int fildes, long offset, long result,
			String name, byte[] data) throws IOException {
		out.write8bit(TYPE_MMAP2);
		out.write64bit(addr);
		out.write64bit(len);
		out.write32bit(prot);
		out.write32bit(flags);
		out.write32bit(fildes);
		out.write64bit(offset);
		out.write64bit(result);
		writeString(name);

		if(data != null && data.length != 0) {
			out.write32bit(data.length);
			out.write(data);
		} else {
			out.write32bit(0);
		}
	}

	public void mmap(MemoryPage page) throws IOException {
		out.write8bit(TYPE_MMAP);
		out.write64bit(page.base);
		out.write64bit(page.end);
		writeString(page.name);

		if(page.memory != null && page.memory instanceof ByteMemory) {
			ByteMemory mem = (ByteMemory) page.memory;
			byte[] data = mem.getBytes();
			dump(page.base, data);
		}
	}

	public void brk(long brk, long result) throws IOException {
		out.write8bit(TYPE_BRK);
		out.write64bit(brk);
		out.write64bit(result);
	}

	public void munmap(long addr, long len, int result) throws IOException {
		out.write8bit(TYPE_MUNMAP);
		out.write64bit(addr);
		out.write64bit(len);
		out.write32bit(result);
	}

	public void mprotect(long addr, long size, int prot, int result) throws IOException {
		out.write8bit(TYPE_MPROTECT);
		out.write64bit(addr);
		out.write64bit(size);
		out.write32bit(prot);
		out.write32bit(result);
	}
}
