package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class AddiTest {
	@Test
	public void test1() {
		byte[] binary = { 0x38, 0x60, 0x00, 0x00 }; // li r3, 0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(3, 0x42);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0, power.getState().getGPR(3));
	}

	@Test
	public void test2() {
		byte[] binary = { 0x38, 0x21, 0x00, 0x10 }; // addi r1, r1, 16
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(1, 0x32);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0x42, power.getState().getGPR(1));
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { 0x38, 0x60, 0x00, 0x00 }; // li r3, 0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:\t38 60 00 00\tli\tr3,0";
		// @formatter:on
		assertEquals(ref, asm);
	}

	@Test
	public void testDisasm2() {
		byte[] binary = { 0x38, 0x21, 0x00, 0x10 }; // addi r1, r1, 16
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:\t38 21 00 10\taddi\tr1,r1,16";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
