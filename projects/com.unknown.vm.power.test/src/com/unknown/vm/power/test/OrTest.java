package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class OrTest {
	@Test
	public void test1() {
		byte[] binary = { 0x7c, 0x23, 0x0b, 0x78 }; // mr r3,r1
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(1, 0xC0DE);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0xC0DE, power.getState().getGPR(3));
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { 0x7c, 0x23, 0x0b, 0x78 }; // mr r3,r1
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	7c 23 0b 78	mr	r3,r1";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
