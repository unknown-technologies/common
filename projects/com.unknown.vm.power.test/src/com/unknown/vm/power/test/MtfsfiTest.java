package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class MtfsfiTest {
	@Test
	public void test1() {
		byte[] binary = { (byte) 0xff, (byte) 0x80, 0x31, 0x0c }; // mtfsfi 7,3
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setFPR(23, 1.0);
		power.getState().setFPR(31, 2.0);
		power.getState().pc = address;
		power.getState().fpscr = 0x82004000;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0x82004003, power.getState().fpscr);
	}

	@Test
	public void test2() {
		byte[] binary = { (byte) 0xfc, (byte) 0x80, (byte) 0xd1, 0x0c }; // mtfsfi 1,13
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setFPR(23, 1.0);
		power.getState().setFPR(31, 2.0);
		power.getState().pc = address;
		power.getState().fpscr = 0x00002001;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0x2D002001, power.getState().fpscr);
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { (byte) 0xff, (byte) 0x80, 0x31, 0x0c }; // mtfsfi 7,3
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	ff 80 31 0c	mtfsfi	7,3";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
