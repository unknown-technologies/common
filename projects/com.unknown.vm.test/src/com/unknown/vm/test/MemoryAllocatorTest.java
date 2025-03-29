package com.unknown.vm.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.unknown.vm.memory.MemoryAllocator;

public class MemoryAllocatorTest {
	private MemoryAllocator allocator;

	@Before
	public void setup() {
		allocator = new MemoryAllocator(0xffffffffffffffffL);
		assertEquals("Block[0x0000000000000000, 0xffffffffffffffff, free=true]", allocator.dump());
	}

	@Test
	public void testAllocAt001() {
		allocator.allocat(0, 4096);
		assertEquals(4096, allocator.getUsedMemory());
		assertEquals("Block[0x0000000000000000, 0x0000000000001000, free=false]\n" +
				"Block[0x0000000000001000, 0xffffffffffffefff, free=true]", allocator.dump());
	}

	@Test
	public void testAllocAt002() {
		allocator.allocat(0x00000000f6ff6000L, 4096);
		assertEquals(4096, allocator.getUsedMemory());
		assertEquals("Block[0x0000000000000000, 0x00000000f6ff6000, free=true]\n" +
				"Block[0x00000000f6ff6000, 0x0000000000001000, free=false]\n" +
				"Block[0x00000000f6ff7000, 0xffffffff09008fff, free=true]", allocator.dump());
	}

	@Test
	public void testAllocAt003() {
		allocator.allocat(0x00000000f6ff6000L, 4096);
		assertEquals(4096, allocator.getUsedMemory());
		assertEquals("Block[0x0000000000000000, 0x00000000f6ff6000, free=true]\n" +
				"Block[0x00000000f6ff6000, 0x0000000000001000, free=false]\n" +
				"Block[0x00000000f6ff7000, 0xffffffff09008fff, free=true]", allocator.dump());
		allocator.allocat(0x0000000000000000L, 4096);
		assertEquals(8192, allocator.getUsedMemory());
		assertEquals("Block[0x0000000000000000, 0x0000000000001000, free=false]\n" +
				"Block[0x0000000000001000, 0x00000000f6ff5000, free=true]\n" +
				"Block[0x00000000f6ff6000, 0x0000000000001000, free=false]\n" +
				"Block[0x00000000f6ff7000, 0xffffffff09008fff, free=true]", allocator.dump());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAllocAtError001() {
		allocator.allocat(0, 16);
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAllocAtError002() {
		allocator.allocat(16, 4096);
		fail();
	}

	@Test
	public void testAlloc001() {
		long p = allocator.alloc(4096);
		assertEquals(0, p);
		assertEquals("Block[0x0000000000000000, 0x0000000000001000, free=false]\n" +
				"Block[0x0000000000001000, 0xffffffffffffefff, free=true]", allocator.dump());
	}

	@Test
	public void testAlloc002() {
		long p1 = allocator.alloc(4096);
		long p2 = allocator.alloc(4096);
		assertEquals(0, p1);
		assertEquals(4096, p2);
		assertEquals("Block[0x0000000000000000, 0x0000000000001000, free=false]\n" +
				"Block[0x0000000000001000, 0x0000000000001000, free=false]\n" +
				"Block[0x0000000000002000, 0xffffffffffffdfff, free=true]", allocator.dump());
	}

	@Test
	public void testAllocFree001() {
		long p1 = allocator.alloc(4096);
		long p2 = allocator.alloc(4096);
		allocator.free(p1);
		long p3 = allocator.alloc(4096);
		assertEquals(0, p1);
		assertEquals(4096, p2);
		assertEquals(0, p3);
		assertEquals("Block[0x0000000000000000, 0x0000000000001000, free=false]\n" +
				"Block[0x0000000000001000, 0x0000000000001000, free=false]\n" +
				"Block[0x0000000000002000, 0xffffffffffffdfff, free=true]", allocator.dump());
	}

	@Test
	public void testAllocFree002() {
		long p1 = allocator.alloc(4096);
		long p2 = allocator.alloc(4096);
		allocator.free(p1);
		assertEquals(0, p1);
		assertEquals(4096, p2);
		assertEquals("Block[0x0000000000000000, 0x0000000000001000, free=true]\n" +
				"Block[0x0000000000001000, 0x0000000000001000, free=false]\n" +
				"Block[0x0000000000002000, 0xffffffffffffdfff, free=true]", allocator.dump());
	}

	@Test
	public void testAllocFree003() {
		long p1 = allocator.alloc(4096);
		long p2 = allocator.alloc(4096);
		allocator.free(p1);
		long p3 = allocator.alloc(8192);
		assertEquals(0, p1);
		assertEquals(4096, p2);
		assertEquals(8192, p3);
		assertEquals("Block[0x0000000000000000, 0x0000000000001000, free=true]\n" +
				"Block[0x0000000000001000, 0x0000000000001000, free=false]\n" +
				"Block[0x0000000000002000, 0x0000000000002000, free=false]\n" +
				"Block[0x0000000000004000, 0xffffffffffffbfff, free=true]", allocator.dump());
	}

	@Test
	public void testAllocFreeLoop001() {
		long size = 16 * 4096;
		for(int i = 0; i < 10; i++) {
			long ptr = allocator.alloc(size);
			assertEquals(0, ptr);
			allocator.free(ptr, size);
			assertEquals(0, allocator.getUsedMemory());
		}
	}
}
