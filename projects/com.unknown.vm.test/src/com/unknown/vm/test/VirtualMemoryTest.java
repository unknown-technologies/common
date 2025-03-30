package com.unknown.vm.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.unknown.vm.memory.ByteMemory;
import com.unknown.vm.memory.Memory;
import com.unknown.vm.memory.MemoryPage;
import com.unknown.vm.memory.VirtualMemory;

public class VirtualMemoryTest {
	private VirtualMemory vm;
	private MemoryPage page;
	private Memory mem;
	private byte[] bytes;

	@Before
	public void setup() {
		vm = new VirtualMemory();
		bytes = new byte[32];
		mem = new ByteMemory(bytes);
		page = new MemoryPage(mem, 0, bytes.length);
		vm.add(page);
		vm.setBrk(32);
	}

	@Test
	public void compute001() {
		int val = 0xF0000021;
		long address = 0x00000000;
		vm.setI32(address, val);
		assertEquals(0xF0000021, vm.getI32(address));
	}

	@Test
	public void brk001() {
		long start = vm.brk();
		assertEquals(0x20, start);
		long brk = vm.brk(start + 0x10);
		assertEquals(0x30, brk);
		brk = vm.brk(brk + 0x10);
		assertEquals(0x40, brk);
		brk = vm.brk(brk + 0x10);
		assertEquals(0x50, brk);

		long address = start;
		for(int i = 0; i < 0x30; i++) {
			vm.setI8(address, (byte) i);
			address++;
		}

		address = start;
		for(int i = 0; i < 0x30; i++) {
			assertEquals((byte) i, vm.getI8(address));
			address++;
		}
	}

	@Test
	public void vm001() {
		vm.set32bit();
		Memory m = new ByteMemory(4096);
		m.setI32(0x42, 0xDEADBEEF);
		MemoryPage p = new MemoryPage(m, 0x80000000L, 4096);
		vm.add(p);
		assertEquals(0xDEADBEEF, vm.getI32(0x80000042));
	}

	@Test
	public void vm002() {
		vm.set32bit();
		Memory m = new ByteMemory(8192);
		MemoryPage p = new MemoryPage(m, 0x80000000L, 8192);
		vm.add(p);
		vm.setI32(0x80000042, 0xDEADBEEF);
		assertEquals(0xDEADBEEF, m.getI32(0x42));
	}

	@Test
	public void vmsplit001() {
		vm.set32bit();
		Memory m1 = new ByteMemory(65536);
		Memory m2 = new ByteMemory(4096);
		MemoryPage p1 = new MemoryPage(m1, 0x80000000L, 65536);
		MemoryPage p2 = new MemoryPage(m2, 0x80001000L, 4096);
		vm.add(p1);
		vm.add(p2);
		vm.setI32(0x80000042, 0xDEADBEEF);
		vm.setI32(0x80001042, 0xC0DEBABE);
		vm.setI32(0x80002042, 0xCAFEBABE);
		assertEquals(0xDEADBEEF, m1.getI32(0x42));
		assertEquals(0xC0DEBABE, m2.getI32(0x42));
		assertEquals(0xCAFEBABE, m1.getI32(0x2042));
	}
}
