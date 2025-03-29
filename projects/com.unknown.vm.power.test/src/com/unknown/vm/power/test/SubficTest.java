package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class SubficTest {
	@Test
	public void test1() {
		byte[] binary = { 0x21, (byte) 0x9f, 0x00, 0x10 }; // subfic r12,r31,16
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(31, 0);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0x20000000L, power.getState().xer);
		assertEquals(0x10, (int) power.getState().getGPR(12));
	}

	@Test
	public void test2() {
		byte[] binary = { 0x21, (byte) 0x9f, 0x00, 0x10 }; // subfic r12,r31,16
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(31, 32);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0, power.getState().xer);
		assertEquals(0xFFFFFFF0, (int) power.getState().getGPR(12));
	}

	private static void test_subfic(int x, int rt, long xer) {
		byte[] binary = { 0x21, (byte) 0x9f, 0x00, 0x10 }; // subfic r12,r31,16
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(31, x);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(xer, power.getState().xer);
		assertEquals(rt, (int) power.getState().getGPR(12));
	}

	@Test
	public void tests() {
		test_subfic(0x00000000, 0x00000010, 0x20000000);
		test_subfic(0x00000020, 0xFFFFFFF0, 0x00000000);
		test_subfic(0x00000010, 0x00000000, 0x20000000);
		test_subfic(0x0000000F, 0x00000001, 0x20000000);
		test_subfic(0x00000011, 0xFFFFFFFF, 0x00000000);
		test_subfic(0xFFFFFFF0, 0x00000020, 0x00000000);
		test_subfic(0xFFFFFFF1, 0x0000001F, 0x00000000);
		test_subfic(0xFFFFFFEF, 0x00000021, 0x00000000);
		test_subfic(0x7FFFFFFF, 0x80000011, 0x00000000);
		test_subfic(0x80000000, 0x80000010, 0x00000000);
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { 0x21, (byte) 0x9f, 0x00, 0x20 }; // subfic r12,r31,32
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	21 9f 00 20	subfic	r12,r31,32";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
