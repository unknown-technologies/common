package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.unknown.vm.power.Power;
import com.unknown.vm.power.isa.TrapException;

public class TwiTest {
	@Test
	public void testTweqi1() {
		byte[] binary = { 0x0c, (byte) 0x83, 0x00, 0x00 }; // tweqi r3,0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(3, 0x42);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
	}

	@Test(expected = TrapException.class)
	public void testTweqi2() {
		byte[] binary = { 0x0c, (byte) 0x83, 0x00, 0x00 }; // tweqi r3,0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(3, 0);
		power.getState().pc = address;
		power.run(1);
		fail();
	}

	@Test
	public void testDisasmTweqi() {
		byte[] binary = { 0x0c, (byte) 0x83, 0x00, 0x00 }; // tweqi r3,0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	0c 83 00 00	tweqi	r3,0";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
