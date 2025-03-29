package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class SrawiTest {
	@Test
	public void test1() {
		byte[] binary = { 0x7d, 0x08, 0x0e, 0x70 }; // srawi r8,r8,1
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().xer = 0;
		power.getState().setGPR(8, 0xffffffffdffffdeaL);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0xeffffef5, (int) power.getState().getGPR(8));
		assertEquals(0, power.getState().xer);
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { 0x7d, 0x08, 0x0e, 0x70 }; // srawi r8,r8,1
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	7d 08 0e 70	srawi	r8,r8,1";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
