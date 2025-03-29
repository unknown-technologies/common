package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class FnegTest {
	@Test
	public void test1() {
		byte[] binary = { (byte) 0xfc, 0x00, 0x00, 0x50 }; // fneg f0,f0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setFPR(0, 0x4018000000000000L);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0xc018000000000000L, power.getState().getFPR(0));
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { (byte) 0xfc, 0x00, 0x00, 0x50 }; // fneg f0,f0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	fc 00 00 50	fneg	f0,f0";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
