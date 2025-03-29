package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class CmpiTest {
	@Test
	public void test1() {
		byte[] binary = { 0x2f, (byte) 0x80, 0x00, 0x00 }; // cmpwi, cr7,r0,0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(0, 0x0);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0x2, power.getState().cr);
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { 0x2f, (byte) 0x80, 0x00, 0x00 }; // cmpwi, cr7,r0,0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:\t2f 80 00 00\tcmpwi\tcr7,r0,0";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
