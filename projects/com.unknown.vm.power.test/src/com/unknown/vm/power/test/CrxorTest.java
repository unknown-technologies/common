package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class CrxorTest {
	@Test
	public void test1() {
		byte[] binary = { 0x4c, (byte) 0xc6, 0x31, (byte) 0x82 }; // crclr 4*cr1+eq
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().cr = 0b00000010000000000000000000000000;
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0b00000000000000000000000000000000, power.getState().cr);
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { 0x4c, (byte) 0xc6, 0x31, (byte) 0x82 }; // crclr 4*cr1+eq
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	4c c6 31 82	crclr	4*cr1+eq";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
