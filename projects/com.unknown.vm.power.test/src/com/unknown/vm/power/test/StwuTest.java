package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class StwuTest {
	@Test
	public void test1() {
		byte[] binary = { (byte) 0x94, 0x21, (byte) 0xff, (byte) 0xf0 }; // stwu r1,-16(r1)
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().pc = address;
		long oldSp = power.getState().getGPR(1);
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		long sp = power.getState().getGPR(1);
		assertEquals((int) oldSp, power.getState().getMemory().getI32(sp));
		assertEquals(oldSp - 16, sp);
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { (byte) 0x94, 0x21, (byte) 0xff, (byte) 0xf0 }; // stwu r1,-16(r1)
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:\t94 21 ff f0\tstwu\tr1,-16(r1)";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
