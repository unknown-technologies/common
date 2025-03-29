package com.unknown.vm.memory;

import com.unknown.posix.api.PosixPointer;
import com.unknown.vm.Code;
import com.unknown.vm.exceptions.SegmentationViolation;

public class MemoryPage {
	public final Memory memory;
	public final long base;
	public final long size;
	public final long end;
	private final long offset;

	public boolean r;
	public boolean w;
	public boolean x;

	public Code code;
	public String name;
	public long fileOffset;

	private final long id = nextID();
	private static long seq = 0;

	private final static long nextID() {
		return seq++;
	}

	public MemoryPage(Memory memory, long base, long size) {
		this.memory = memory;
		this.base = base;
		this.size = size;
		this.end = base + size;
		offset = 0;
		r = true;
		w = true;
		x = true;
		name = null;
		fileOffset = 0;
	}

	public MemoryPage(Memory memory, long base, long size, String name) {
		this.memory = memory;
		this.base = base;
		this.size = size;
		this.end = base + size;
		this.name = name;
		offset = 0;
		r = true;
		w = true;
		x = true;
		fileOffset = 0;
	}

	public MemoryPage(Memory memory, long base, long size, String name, long fileOffset) {
		this.memory = memory;
		this.base = base;
		this.size = size;
		this.end = base + size;
		this.name = name;
		offset = 0;
		r = true;
		w = true;
		x = true;
		this.fileOffset = fileOffset;
	}

	protected MemoryPage(MemoryPage page) {
		this.memory = page.memory;
		this.base = page.base;
		this.size = page.size;
		this.end = page.end;
		this.offset = page.offset;
		this.r = page.r;
		this.w = page.w;
		this.x = page.x;
		this.code = page.code;
		this.name = page.name;
		this.fileOffset = page.fileOffset;
	}

	public MemoryPage(MemoryPage page, long address, long size) {
		this.memory = page.memory;
		this.base = address;
		this.size = size;
		this.end = base + size;
		this.r = page.r;
		this.w = page.w;
		this.x = page.x;
		this.code = page.code;
		this.offset = page.offset + address - page.base;
		this.name = page.name;
		this.fileOffset = page.fileOffset + (address - page.base);
	}

	public boolean contains(long address) {
		return Long.compareUnsigned(address, base) >= 0 && Long.compareUnsigned(address, end) < 0;
	}

	public Memory getMemory() {
		return memory;
	}

	public long getOffset(long addr) {
		return addr - base + offset;
	}

	public long getBase() {
		return base;
	}

	public long getEnd() {
		return end;
	}

	public PosixPointer getPosixPointer(long address) {
		return memory.getPosixPointer(getOffset(address));
	}

	public byte getI8(long addr) {
		if(!r) {
			throw new SegmentationViolation(addr);
		}
		try {
			return memory.getI8(getOffset(addr));
		} catch(SegmentationViolation e) {
			throw new SegmentationViolation(addr);
		}
	}

	public short getI16(long addr) {
		if(!r) {
			throw new SegmentationViolation(addr);
		}
		try {
			return memory.getI16(getOffset(addr));
		} catch(SegmentationViolation e) {
			throw new SegmentationViolation(addr);
		}
	}

	public int getI32(long addr) {
		if(!r) {
			throw new SegmentationViolation(addr);
		}
		try {
			return memory.getI32(getOffset(addr));
		} catch(SegmentationViolation e) {
			throw new SegmentationViolation(addr);
		}
	}

	public long getI64(long addr) {
		if(!r) {
			throw new SegmentationViolation(addr);
		}
		try {
			return memory.getI64(getOffset(addr));
		} catch(SegmentationViolation e) {
			throw new SegmentationViolation(addr);
		}
	}

	public void setI8(long addr, byte val) {
		if(!w) {
			throw new SegmentationViolation(addr);
		}
		try {
			memory.setI8(getOffset(addr), val);
		} catch(SegmentationViolation e) {
			throw new SegmentationViolation(addr);
		}
	}

	public void setI16(long addr, short val) {
		if(!w) {
			throw new SegmentationViolation(addr);
		}
		try {
			memory.setI16(getOffset(addr), val);
		} catch(SegmentationViolation e) {
			throw new SegmentationViolation(addr);
		}
	}

	public void setI32(long addr, int val) {
		if(!w) {
			throw new SegmentationViolation(addr);
		}
		try {
			memory.setI32(getOffset(addr), val);
		} catch(SegmentationViolation e) {
			throw new SegmentationViolation(addr);
		}
	}

	public void setI64(long addr, long val) {
		if(!w) {
			throw new SegmentationViolation(addr);
		}
		try {
			memory.setI64(getOffset(addr), val);
		} catch(SegmentationViolation e) {
			throw new SegmentationViolation(addr);
		}
	}

	public byte[] get(long addr, long len) {
		if(!w) {
			throw new SegmentationViolation(addr);
		}
		try {
			return memory.get(getOffset(addr), len);
		} catch(SegmentationViolation e) {
			throw new SegmentationViolation(addr);
		}
	}

	public boolean isExecutable() {
		return code != null;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof MemoryPage)) {
			return false;
		}
		MemoryPage p = (MemoryPage) o;
		return p.id == id;
	}

	@Override
	public int hashCode() {
		return (int) id;
	}

	@Override
	public String toString() {
		return String.format("%016x-%016x %c%c%cp %08x %s",
				base, end, r ? 'r' : '-', w ? 'w' : '-', code != null ? 'x' : '-', fileOffset,
				name != null ? name : "");
	}
}
