package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class CmplTest {
	@Test
	public void test1() {
		byte[] binary = { 0x7f, (byte) 0x89, (byte) 0xf0, 0x40 }; // cmplw cr7,r9,r30
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(9, 0x0);
		power.getState().setGPR(30, 0x1);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0x8, power.getState().cr);
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { 0x7f, (byte) 0x89, (byte) 0xf0, 0x40 }; // cmplw cr7,r9,r30
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:\t7f 89 f0 40\tcmplw\tcr7,r9,r30";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
