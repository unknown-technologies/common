package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class SubfeTest {
	private static void test_subfe(int x, int y, long xerIn, long rt, long xer) {
		byte[] binary = { 0x7c, (byte) 0xc4, 0x29, 0x10 }; // subfe r6,r4,r5
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(4, x);
		power.getState().setGPR(5, y);
		power.getState().xer = xerIn;
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(rt, (int) power.getState().getGPR(6));
		assertEquals(xer, power.getState().xer);
	}

	@Test
	public void test001() {
		test_subfe(0x00000000, 0x00000000, 0x00000000, 0xFFFFFFFF, 0x00000000);
	}

	@Test
	public void test002() {
		test_subfe(0x00000000, 0x00000000, 0x20000000, 0x00000000, 0x20000000);
	}

	public void test003() {
		test_subfe(0x00000D0C, 0x00000000, 0x20000000, 0xFFFFF2F4, 0x00000000);
	}

	@Test
	public void test004() {
		test_subfe(0x00000D0C, 0x00000D0C, 0x20000000, 0x00000000, 0x20000000);
	}

	@Test
	public void test005() {
		test_subfe(0x00000000, 0x00000D0C, 0x20000000, 0x00000D0C, 0x20000000);
	}

	@Test
	public void test006() {
		test_subfe(0x00000D0C, 0x00000000, 0x00000000, 0xFFFFF2F3, 0x00000000);
	}

	@Test
	public void test007() {
		test_subfe(0x00000D0C, 0x00000D0C, 0x00000000, 0xFFFFFFFF, 0x00000000);
	}

	@Test
	public void test008() {
		test_subfe(0x00000000, 0x00000D0C, 0x00000000, 0x00000D0B, 0x20000000);
	}

	@Test
	public void test009() {
		test_subfe(0xFFFFFFFF, 0x00000000, 0x00000000, 0x00000000, 0x00000000);
	}

	@Test
	public void test010() {
		test_subfe(0xFFFFFFFF, 0x00000D0C, 0x00000000, 0x00000D0C, 0x00000000);
	}

	@Test
	public void test011() {
		test_subfe(0xFFFFFFFF, 0x80000000, 0x00000000, 0x80000000, 0x00000000);
	}

	@Test
	public void test012() {
		test_subfe(0xFFFFFFFF, 0xFFFFFFFF, 0x00000000, 0xFFFFFFFF, 0x00000000);
	}

	@Test
	public void test014() {
		test_subfe(0xFFFFFFFF, 0x00000000, 0x20000000, 0x00000001, 0x00000000);
	}

	@Test
	public void test015() {
		test_subfe(0xFFFFFFFF, 0x00000D0C, 0x20000000, 0x00000D0D, 0x00000000);
	}

	@Test
	public void test016() {
		test_subfe(0xFFFFFFFF, 0x80000000, 0x20000000, 0x80000001, 0x00000000);
	}

	@Test
	public void test017() {
		test_subfe(0xFFFFFFFF, 0xFFFFFFFF, 0x20000000, 0x00000000, 0x20000000);
	}

	@Test
	public void test018() {
		test_subfe(0x80000000, 0x00000000, 0x00000000, 0x7FFFFFFF, 0x00000000);
	}

	@Test
	public void test019() {
		test_subfe(0x80000000, 0x00000D0C, 0x00000000, 0x80000D0B, 0x00000000);
	}

	@Test
	public void test020() {
		test_subfe(0x80000000, 0x80000000, 0x00000000, 0xFFFFFFFF, 0x00000000);
	}

	@Test
	public void test021() {
		test_subfe(0x80000000, 0xFFFFFFFF, 0x00000000, 0x7FFFFFFE, 0x20000000);
	}

	@Test
	public void test022() {
		test_subfe(0x80000000, 0x00000000, 0x20000000, 0x80000000, 0x00000000);
	}

	@Test
	public void test023() {
		test_subfe(0x80000000, 0x00000D0C, 0x20000000, 0x80000D0C, 0x00000000);
	}

	@Test
	public void test024() {
		test_subfe(0x80000000, 0x80000000, 0x20000000, 0x00000000, 0x20000000);
	}

	@Test
	public void test025() {
		test_subfe(0x80000000, 0xFFFFFFFF, 0x20000000, 0x7FFFFFFF, 0x20000000);
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { 0x7c, (byte) 0xc4, 0x29, 0x10 }; // subfe r6,r4,r5
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	7c c4 29 10	subfe	r6,r4,r5";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
