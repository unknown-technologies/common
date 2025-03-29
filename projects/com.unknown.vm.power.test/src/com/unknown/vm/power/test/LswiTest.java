package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class LswiTest {
	@Test
	public void test1() {
		byte[] binary = { 0x7c, (byte) 0xb8, 0x64, (byte) 0xaa };
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().pc = address;
		long sp = power.getState().getGPR(1);
		sp -= 24;
		long ptr = sp;
		power.getMemory().setI32(ptr, 0x12345678);
		ptr += 4;
		power.getMemory().setI32(ptr, 0xDEADBEEF);
		ptr += 4;
		power.getMemory().setI32(ptr, 0xC0FEBABE);
		power.getState().setGPR(24, sp);
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0, power.getState().cr);
		assertEquals(0x12345678, (int) power.getState().getGPR(5));
		assertEquals(0xDEADBEEF, (int) power.getState().getGPR(6));
		assertEquals(0xC0FEBABE, (int) power.getState().getGPR(7));
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { 0x7c, (byte) 0xb8, 0x64, (byte) 0xaa };
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	7c b8 64 aa	lswi	r5,r24,12";
		// @formatter:on
		assertEquals(ref, asm);
	}

	private static long shift(byte val, int i) {
		return Byte.toUnsignedLong(val) << (56 - i);
	}

	private static long mask(int i) {
		return shift((byte) 0xFF, i);
	}

	private static byte get(long val, int i) {
		return (byte) (val >> (56 - i));
	}

	@Test
	public void testShift001() {
		assertEquals(0xFF000000L, shift((byte) 0xFF, 32));
		assertEquals(0x00FF0000L, shift((byte) 0xFF, 40));
		assertEquals(0x000000FFL, shift((byte) 0xFF, 56));
	}

	@Test
	public void testShift002() {
		assertEquals(0xFF000000L, mask(32));
		assertEquals(0x00FF0000L, mask(40));
		assertEquals(0x000000FFL, mask(56));
	}

	@Test
	public void testShift003() {
		assertEquals(0x12, get(0x12345678, 32));
		assertEquals(0x34, get(0x12345678, 40));
		assertEquals(0x78, get(0x12345678, 56));
	}
}
