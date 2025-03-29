package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class MfsprTest {
	@Test
	public void test1() {
		byte[] binary = { 0x7c, 0x08, 0x02, (byte) 0xa6 }; // mflr r0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().lr = 0x8000028cL;
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0x8000028cL, power.getState().getGPR(0));
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { 0x7c, 0x08, 0x02, (byte) 0xa6 }; // mflr r0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:\t7c 08 02 a6\tmflr\tr0";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
