package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class FmaddTest {
	@Test
	public void test1() {
		byte[] binary = { (byte) 0xff, (byte) 0xfe, (byte) 0xff, (byte) 0xfa }; // fmadd f31,f30,f31,f31
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setFPR(30, 0xbfeffffffff24190L);
		power.getState().setFPR(31, 0x3feffffffff24190L);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0x3ddb7cdffff431afL, power.getState().getFPR(31));
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { (byte) 0xff, (byte) 0xfe, (byte) 0xff, (byte) 0xfa }; // fmadd f31,f30,f31,f31
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	ff fe ff fa	fmadd	f31,f30,f31,f31";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
