package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class RlwinmTest {
	@Test
	public void test1() {
		byte[] binary = { 0x55, 0x29, 0x07, (byte) 0xb6 }; // rlwinm r9,r9,0,30,27
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(9, 0xfbad248c);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0xfbad2480, power.getState().getGPR(9));
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { 0x55, 0x29, 0x07, (byte) 0xb6 }; // rlwinm r9,r9,0,30,27
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	55 29 07 b6	rlwinm	r9,r9,0,30,27";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
