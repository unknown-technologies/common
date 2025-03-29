package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class FcmpuTest {
	@Test
	public void test1() {
		byte[] binary = { (byte) 0xff, (byte) 0x97, (byte) 0xf8, 0x00 }; // fcmpu cr7,f23,f31
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setFPR(23, 1.0);
		power.getState().setFPR(31, 2.0);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0x8, power.getState().cr);
	}

	@Test
	public void test2() {
		byte[] binary = { (byte) 0xff, (byte) 0x97, (byte) 0xf8, 0x00 }; // fcmpu cr7,f23,f31
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setFPR(23, 2.0);
		power.getState().setFPR(31, 1.0);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0x4, power.getState().cr);
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { (byte) 0xff, (byte) 0x97, (byte) 0xf8, 0x00 }; // fcmpu cr7,f23,f31
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	ff 97 f8 00	fcmpu	cr7,f23,f31";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
