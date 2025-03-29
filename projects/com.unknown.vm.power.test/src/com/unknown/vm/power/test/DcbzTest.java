package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.memory.ByteMemory;
import com.unknown.vm.memory.Memory;
import com.unknown.vm.memory.MemoryPage;
import com.unknown.vm.power.Power;

public class DcbzTest {
	@Test
	public void test1() {
		byte[] binary = { 0x7c, 0, 0x37, (byte) 0xec }; // dcbz 0,r6
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().pc = address;
		power.getState().setGPR(6, 0x10000020L);
		Memory mem = new ByteMemory(64);
		MemoryPage page = new MemoryPage(mem, 0x10000000L, 64);
		power.getMemory().add(page);
		for(int i = 0; i < 64; i++) {
			power.getMemory().setI8(0x10000000L + i, (byte) i);
		}
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		for(int i = 0; i < 32; i++) {
			byte b = power.getMemory().getI8(0x10000000L + i);
			assertEquals((byte) i, b);
		}
		for(int i = 32; i < 64; i++) {
			byte b = power.getMemory().getI8(0x10000000L + i);
			assertEquals(0, b);
		}
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { 0x7c, 0, 0x37, (byte) 0xec }; // dcbz 0,r6
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	7c 00 37 ec	dcbz	0,r6";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
