package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class StfdTest {
	@Test
	public void test1() {
		byte[] binary = { (byte) 0xd8, 0x3f, 0x00, 0x00 }; // stfd f1,0(r31)
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		long sp = power.getState().getGPR(1);
		power.getState().setGPR(31, sp);
		power.getState().pc = address;
		power.getState().setFPR(1, 0x400921fb54524550L);
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0, power.getState().cr);
		assertEquals(0x400921fb54524550L, power.getMemory().getI64(sp));
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { (byte) 0xd8, 0x3f, 0x00, 0x00 }; // stfd f1,0(r31)
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	d8 3f 00 00	stfd	f1,0(r31)";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
