package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class FdivTest {
	@Test
	public void test1() {
		byte[] binary = { (byte) 0xfc, 0x0c, 0x00, 0x24 }; // fdiv f0,f12,f0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setFPR(0, 0xfff0000000000000L);
		power.getState().setFPR(12, 0xfff0000000000000L);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0x7ff8000000000000L, power.getState().getFPR(0)); // qemu
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { (byte) 0xfc, 0x0c, 0x00, 0x24 }; // fdiv f0,f12,f0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	fc 0c 00 24	fdiv	f0,f12,f0";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
