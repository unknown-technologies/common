package com.unknown.vm.exceptions;

import com.unknown.vm.memory.Memory;
import com.unknown.vm.memory.MemoryPage;

public class SegmentationViolation extends ArrayIndexOutOfBoundsException {
	private static final long serialVersionUID = -2498444194134824375L;

	private final Memory mem;
	private final MemoryPage page;
	private final long offset;

	public SegmentationViolation(long offset) {
		super((int) offset);
		this.mem = null;
		this.page = null;
		this.offset = offset;
	}

	public SegmentationViolation(Memory mem, long offset) {
		super((int) offset);
		this.mem = mem;
		this.page = null;
		this.offset = offset;
	}

	public SegmentationViolation(MemoryPage page, long offset) {
		super((int) offset);
		this.page = page;
		this.mem = null;
		this.offset = offset;
	}

	public Memory getMemory() {
		return mem;
	}

	public MemoryPage getMemoryPage() {
		return page;
	}

	public long getOffset() {
		return offset;
	}

	@Override
	public String toString() {
		if(page != null) {
			return String.format(
					"Invalid memory access at 0x%016X (page base: 0x%016X, page end: 0x%019X)",
					offset, page.getBase(), page.getEnd());
		} else if(mem != null) {
			return String.format("Invalid memory access at 0x%016X", offset);
		} else {
			return String.format("Invalid memory access at 0x%016X", offset);
		}
	}
}
