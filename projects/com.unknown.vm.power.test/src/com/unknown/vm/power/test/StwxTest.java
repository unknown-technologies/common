package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.memory.ByteMemory;
import com.unknown.vm.memory.Memory;
import com.unknown.vm.memory.MemoryPage;
import com.unknown.vm.power.Power;

public class StwxTest {
	@Test
	public void test1() {
		byte[] binary = { 0x7d, 0x48, (byte) 0xc9, 0x2e }; // stwx r10,r8,r25
		long address = 0x80000000L;
		Power power = new Power();
		Memory memory = new ByteMemory(4096);
		MemoryPage page = new MemoryPage(memory, power.getMemory().pageStart(0x000000008018e578L), 4096);
		power.getMemory().add(page);
		power.loadCode(address, binary);
		power.getState().setGPR(8, 0x000000000018e578L);
		power.getState().setGPR(10, 0xffffffff80190a78L);
		power.getState().setGPR(25, 0xffffffff80000000L);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		int value = memory.getI32B(0x578);
		assertEquals(0x80190a78, value);
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { 0x7d, 0x48, (byte) 0xc9, 0x2e }; // stwx r10,r8,r25
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	7d 48 c9 2e	stwx	r10,r8,r25";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
