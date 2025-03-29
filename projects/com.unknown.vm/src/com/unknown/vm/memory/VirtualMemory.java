package com.unknown.vm.memory;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.unknown.posix.api.Errno;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.PosixPointer;
import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;
import com.unknown.vm.ExecutionTrace;
import com.unknown.vm.exceptions.SegmentationViolation;

public class VirtualMemory {
	private static final Logger log = Trace.create(VirtualMemory.class);

	private static final boolean DEBUG = false;

	private static final long POINTER_BASE = 0x0000000080000000L;
	private static final long POINTER_END = 0x00000000F8000000L;

	public static final long PAGE_SIZE = 4096;
	public static final long PAGE_MASK = ~(PAGE_SIZE - 1);

	private NavigableMap<Long, MemoryPage> pages;

	private MemoryAllocator allocator;

	private long mask;
	private boolean is64bit;

	private long brk;
	private long reportedBrk;

	private MemoryPage cache;
	private MemoryPage cache2;
	private long cacheHits;
	private long cacheMisses;

	private ExecutionTrace trace;

	public VirtualMemory() {
		pages = new TreeMap<>(Long::compareUnsigned);
		// TODO: implement 64bit version
		allocator = new MemoryAllocator(POINTER_BASE, POINTER_END - POINTER_BASE);
		brk = 0;
		reportedBrk = brk;
		cache = null;
		cache2 = null;
		cacheHits = 0;
		cacheMisses = 0;
		set32bit();
	}

	public void setTrace(ExecutionTrace trace) {
		this.trace = trace;
	}

	public void set32bit() {
		mask = 0x00000000FFFFFFFFL;
		is64bit = false;
	}

	public void set64bit() {
		mask = 0xFFFFFFFFFFFFFFFFL;
		is64bit = true;
	}

	public long addr(long addr) {
		return addr & mask;
	}

	public void setBrk(long brk) {
		this.brk = brk;
		this.reportedBrk = brk;
	}

	public long brk() {
		return reportedBrk;
	}

	public Collection<MemoryPage> getPages() {
		return Collections.unmodifiableCollection(pages.values());
	}

	public long brk(long addr) {
		if(Long.compareUnsigned(addr, brk) > 0 && Long.compareUnsigned(addr, POINTER_BASE) <= 0) {
			long sz = addr - brk;
			Memory mem = new ByteMemory(sz);
			MemoryPage page = new MemoryPage(mem, brk, sz, "[heap]");
			add(page);
			brk = addr;
			reportedBrk = brk;
			return brk;
		} else {
			reportedBrk = addr;
			return reportedBrk;
		}
	}

	public long pageStart(long addr) {
		return addr(addr) & PAGE_MASK;
	}

	public long roundToPageSize(long size) {
		long base = size & VirtualMemory.PAGE_MASK;
		if(base != size) {
			return base + VirtualMemory.PAGE_SIZE;
		} else {
			return base;
		}
	}

	public void add(MemoryPage page) {
		boolean ok = Long.compareUnsigned(page.end, POINTER_BASE) <= 0 ||
				Long.compareUnsigned(page.end, POINTER_END) > 0;
		if(!ok) {
			allocator.allocat(page.base, page.size);
		}
		try {
			MemoryPage oldPage = get(page.base);
			if(page.contains(oldPage.base) && page.contains(oldPage.end - 1)) {
				pages.remove(oldPage.base);
			} else {
				if(DEBUG) {
					System.out.printf(
							"Splitting old page: 0x%016X-0x%016X, new page is 0x%016X-0x%016X\n",
							oldPage.base, oldPage.end, page.base, page.end);
				}
				long size1 = page.base - oldPage.base;
				long size2 = oldPage.end - page.end;
				if(DEBUG) {
					System.out.printf("size1 = 0x%016X, size2 = 0x%016X\n", size1, size2);
				}
				if(size1 > 0) {
					MemoryPage p = new MemoryPage(oldPage, oldPage.base, size1);
					p.name = oldPage.name;
					pages.put(oldPage.base, p);
					cache = null;
					cache2 = null;
					if(DEBUG) {
						System.out.printf("Added new page: 0x%016X[0x%016X;0x%016X]\n",
								oldPage.base,
								pages.get(oldPage.base).base,
								pages.get(oldPage.base).end);
					}
				}
				if(size2 > 0) {
					MemoryPage p = new MemoryPage(oldPage, page.end, size2);
					p.name = oldPage.name;
					pages.put(page.end, p);
					cache = null;
					cache2 = null;
					if(DEBUG) {
						System.out.printf("Added new page: 0x%016X[0x%016X;0x%016X]\n",
								page.end,
								pages.get(page.end).base, pages.get(page.end).end);
					}
				}
			}
		} catch(SegmentationViolation e) {
		}
		pages.put(page.base, page);
		cache = null;
		cache2 = null;
		if(page.base != pageStart(page.base)) {
			if(DEBUG) {
				System.out.printf("bad page start: 0x%016X, should be 0x%016X\n", page.base,
						pageStart(page.base));
			}
			long base = pageStart(page.base);
			long size = page.base - base;
			try {
				get(base);
			} catch(SegmentationViolation e) {
				Memory buf = new ByteMemory(size);
				MemoryPage bufpage = new MemoryPage(buf, base, size);
				bufpage.name = page.name;
				pages.put(base, bufpage);
				cache = null;
				cache2 = null;
			}
		}
		if(DEBUG) {
			printLayout();
		}
	}

	public void remove(long addr, long length) throws PosixException {
		cache = null;
		cache2 = null;
		long address = addr(addr);
		if((address & ~PAGE_MASK) != 0) {
			throw new PosixException(Errno.EINVAL);
		}
		try {
			for(long p = address; Long.compareUnsigned(p, address + length) < 0;) {
				MemoryPage page = get(p);
				if(p != page.base) {
					// TODO: split
					throw new AssertionError("split not yet implemented");
				}
				pages.remove(page.base);
				allocator.free(page.base, page.size);
				p = page.end;
			}
		} catch(SegmentationViolation e) {
			// swallow
		}
	}

	public MemoryPage allocate(long size) {
		long base = allocator.alloc(size);
		if(base == 0) {
			return null;
		} else {
			Memory mem = new ByteMemory(size);
			MemoryPage page = new MemoryPage(mem, base, size);
			add(page);
			return page;
		}
	}

	public MemoryPage allocate(Memory memory, long size, String name) {
		long base = allocator.alloc(size);
		if(base == 0) {
			return null;
		} else {
			MemoryPage page = new MemoryPage(memory, base, size, name);
			add(page);
			return page;
		}
	}

	public MemoryPage allocate(Memory memory, long size, String name, long offset) {
		long base = allocator.alloc(size);
		if(base == 0) {
			return null;
		} else {
			MemoryPage page = new MemoryPage(memory, base, size, name, offset);
			add(page);
			return page;
		}
	}

	public void free(long address) {
		MemoryPage page = pages.remove(address);
		allocator.free(address, page.size);
	}

	public void printAccessError(long addr, MemoryPage page) {
		if(page != null) {
			System.err.printf("Tried to access 0x%016X, nearest page is P[0x%016X;0x%016X]\n", addr,
					page.base, page.end);
		} else {
			System.err.printf("Tried to access 0x%016X\n", addr);
		}
		printLayout(System.err);
	}

	public MemoryPage get(long address) {
		long addr = addr(address);
		// TODO: fix this!
		if(cache != null && cache.contains(addr)) {
			cacheHits++;
			return cache;
		} else if(cache2 != null && cache2.contains(addr)) {
			cacheHits++;
			// swap cache entries
			MemoryPage page = cache2;
			cache2 = cache;
			cache = page;
			return page;
		} else {
			cacheMisses++;
		}
		Map.Entry<Long, MemoryPage> entry = pages.floorEntry(addr);
		if(entry == null) {
			throw new SegmentationViolation(addr);
		}
		MemoryPage page = entry.getValue();
		if(page.contains(addr)) {
			if(cache != null) {
				cache2 = page;
			} else {
				cache = page;
			}
			return page;
		} else {
			throw new SegmentationViolation(addr);
		}
	}

	public PosixPointer getPosixPointer(long address) {
		long addr = addr(address);
		return new PosixVirtualMemoryPointer(this, addr);
	}

	public boolean contains(long address) {
		long addr = addr(address);
		Map.Entry<Long, MemoryPage> entry = pages.floorEntry(addr);
		if(entry == null) {
			return false;
		}
		MemoryPage page = entry.getValue();
		return page.contains(addr);
	}

	public byte getI8(long address) {
		long ptr = addr(address);
		try {
			MemoryPage page = get(ptr);
			byte val = page.getI8(ptr);
			logMemoryRead(address, 1, val);
			return val;
		} catch(Throwable t) {
			logMemoryRead(address, 1);
			throw t;
		}
	}

	public short getI16(long address) {
		long ptr = addr(address);
		try {
			MemoryPage page = get(ptr);
			short val = page.getI16(ptr);
			logMemoryRead(address, 2, val);
			return val;
		} catch(Throwable t) {
			logMemoryRead(address, 2);
			throw t;
		}
	}

	public int getI32(long address) {
		long ptr = addr(address);
		try {
			MemoryPage page = get(ptr);
			int v = page.getI32(ptr);
			logMemoryRead(address, 4, v);
			return v;
		} catch(Throwable t) {
			logMemoryRead(address, 4);
			throw t;
		}
	}

	public long getI64(long address) {
		long ptr = addr(address);
		try {
			MemoryPage page = get(ptr);
			long v = page.getI64(ptr);
			logMemoryRead(address, 8, v);
			return v;
		} catch(Throwable t) {
			logMemoryRead(address, 8);
			throw t;
		}
	}

	public void setI8(long address, byte val) {
		logMemoryWrite(address, 1, val);
		long ptr = addr(address);
		MemoryPage page = get(ptr);
		page.setI8(ptr, val);
	}

	public void setI16(long address, short val) {
		logMemoryWrite(address, 2, val);
		long ptr = addr(address);
		MemoryPage page = get(ptr);
		page.setI16(ptr, val);
	}

	public void setI32(long address, int val) {
		logMemoryWrite(address, 4, val);
		long ptr = addr(address);
		MemoryPage page = get(ptr);
		page.setI32(ptr, val);
	}

	public void setI64(long address, long val) {
		logMemoryWrite(address, 8, val);
		long ptr = addr(address);
		MemoryPage page = get(ptr);
		page.setI64(ptr, val);
	}

	public void mprotect(long address, long len, boolean r, boolean w, boolean x) throws PosixException {
		if(addr(address) != pageStart(addr(address))) {
			throw new PosixException(Errno.EINVAL);
		}
		long remaining = addr(len);
		long p = addr(address);
		while(remaining > 0) {
			MemoryPage page = get(p);
			if(page.base == addr(p) && Long.compareUnsigned(page.size, remaining) <= 0) {
				// whole "page"
				page.r = r;
				page.w = w;
				page.x = x;
				p = page.end;
				remaining -= page.size;
			} else if(page.base == addr(p) && Long.compareUnsigned(page.size, remaining) > 0) {
				// split, modify first part
				MemoryPage p1 = new MemoryPage(page, page.base, remaining);
				MemoryPage p2 = new MemoryPage(page, page.base + remaining, page.size - remaining);
				p1.r = r;
				p1.w = w;
				p1.x = x;
				pages.remove(page.base);
				pages.put(p1.base, p1);
				pages.put(p2.base, p2);
				cache = null;
				cache2 = null;
				checkConsistency();
				return;
			} else {
				// split, modify second part
				assert Long.compareUnsigned(page.base, p) < 0;
				long off = p - page.base;
				MemoryPage p1 = new MemoryPage(page, page.base, off);
				MemoryPage p2 = new MemoryPage(page, page.base + off, page.size - off);
				p2.r = r;
				p2.w = w;
				p2.x = x;
				pages.remove(page.base);
				pages.put(p1.base, p1);
				pages.put(p2.base, p2);
				cache = null;
				cache2 = null;
				p = page.end;
				remaining -= page.size;
			}
		}
		checkConsistency();
	}

	public void dump(long p, int size) {
		long ptr = addr(p);
		System.out.printf("memory at 0x%016x:\n", p);
		long ptr2 = ptr;
		boolean nl = true;
		for(int i = 0; i < size; i++) {
			nl = true;
			if(i % 16 == 0) {
				System.out.printf("%016x:", ptr);
			}
			byte u8 = getI8(ptr);
			ptr++;
			System.out.printf(" %02x", Byte.toUnsignedInt(u8));
			if(i % 16 == 15) {
				System.out.print("   ");
				for(int j = 0; j < 16; j++) {
					u8 = getI8(ptr2);
					ptr2++;
					char ch = (char) (u8 & 0xff);
					if(ch < 32 || ch > 127) {
						ch = '.';
					}
					System.out.printf("%c", ch);
				}
				System.out.println();
				nl = false;
			}
		}
		if(nl) {
			System.out.println();
		}
	}

	private void logMemoryRead(long address, int size) {
		if(trace != null) {
			try {
				if(is64bit) {
					switch(size) {
					case 1:
						trace.mem64Read8Fail(address);
						break;
					case 2:
						trace.mem64Read16Fail(address);
						break;
					case 4:
						trace.mem64Read32Fail(address);
						break;
					case 8:
						trace.mem64Read64Fail(address);
						break;
					}
				} else {
					int addr = (int) address;
					switch(size) {
					case 1:
						trace.mem32Read8Fail(addr);
						break;
					case 2:
						trace.mem32Read16Fail(addr);
						break;
					case 4:
						trace.mem32Read32Fail(addr);
						break;
					case 8:
						trace.mem32Read64Fail(addr);
						break;
					}
				}
			} catch(IOException e) {
				log.log(Levels.ERROR, "Failed to write memory access to log file: " + e.getMessage(),
						e);
				trace = null;
			}
		}
	}

	private void logMemoryRead(long address, int size, long value) {
		if(trace != null) {
			try {
				if(is64bit) {
					switch(size) {
					case 1:
						trace.mem64Read8(address, (byte) value);
						break;
					case 2:
						trace.mem64Read16(address, (short) value);
						break;
					case 4:
						trace.mem64Read32(address, (int) value);
						break;
					case 8:
						trace.mem64Read64(address, value);
						break;
					}
				} else {
					int addr = (int) address;
					switch(size) {
					case 1:
						trace.mem32Read8(addr, (byte) value);
						break;
					case 2:
						trace.mem32Read16(addr, (short) value);
						break;
					case 4:
						trace.mem32Read32(addr, (int) value);
						break;
					case 8:
						trace.mem32Read64(addr, value);
						break;
					}
				}
			} catch(IOException e) {
				log.log(Levels.ERROR, "Failed to write memory access to log file: " + e.getMessage(),
						e);
				trace = null;
			}
		}
	}

	private void logMemoryWrite(long address, int size, long value) {
		if(trace != null) {
			try {
				if(is64bit) {
					switch(size) {
					case 1:
						trace.mem64Write8(address, (byte) value);
						break;
					case 2:
						trace.mem64Write16(address, (short) value);
						break;
					case 4:
						trace.mem64Write32(address, (int) value);
						break;
					case 8:
						trace.mem64Write64(address, value);
						break;
					}
				} else {
					int addr = (int) address;
					switch(size) {
					case 1:
						trace.mem32Write8(addr, (byte) value);
						break;
					case 2:
						trace.mem32Write16(addr, (short) value);
						break;
					case 4:
						trace.mem32Write32(addr, (int) value);
						break;
					case 8:
						trace.mem32Write64(addr, value);
						break;
					}
				}
			} catch(IOException e) {
				log.log(Levels.ERROR, "Failed to write memory access to log file: " + e.getMessage(),
						e);
				trace = null;
			}
		}
	}

	public void printLayout() {
		printLayout(System.out);
	}

	public void printLayout(PrintStream out) {
		out.println("Memory map:");
		pages.entrySet().stream()
				.map((x) -> x.getValue().toString())
				.forEachOrdered(out::println);
	}

	public void printMaps(PrintStream out) {
		pages.entrySet().stream().map((x) -> x.getValue().toString()).forEachOrdered(out::println);
	}

	public void printStats(PrintStream out) {
		out.printf("Cache: %d hits, %d misses (%5.3f%% hits)\n", cacheHits, cacheMisses,
				((double) cacheHits / (double) (cacheHits + cacheMisses)) * 100);
	}

	public void checkConsistency() {
		if(pages.size() < 2) {
			return;
		}
		for(long addr : pages.keySet()) {
			MemoryPage page = pages.get(addr);
			Entry<Long, MemoryPage> before = addr > 0 ? pages.floorEntry(addr - 1) : null;
			Entry<Long, MemoryPage> after = pages.ceilingEntry(addr + 1);
			if(before != null) {
				// check overlap
				MemoryPage p = before.getValue();
				if(p.getEnd() > addr) {
					printLayout();
					throw new RuntimeException("violation at " + p + " vs " + page);
				}
			}
			if(after != null) {
				// check overlap
				MemoryPage p = after.getValue();
				if(p.getBase() < page.getEnd()) {
					printLayout();
					throw new RuntimeException("violation at " + p + " vs " + page);
				}
			}
		}
	}
}
