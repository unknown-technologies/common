package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class AddisTest {
	@Test
	public void test1() {
		byte[] binary = { 0x3f, (byte) 0xde, 0x00, 0x21 }; // addis r30,r30,1
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(30, 0x42);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0x210042, power.getState().getGPR(30));
	}

	@Test
	public void test2() {
		byte[] binary = { 0x3c, 0x60, 0x10, 0x00 }; // lis r3,4096
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(3, 0x42);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0x10000000, power.getState().getGPR(3));
	}

	@Test
	public void test3() {
		byte[] binary = { 0x3d, 0x22, 0x00, 0x00 }; // addis r9,r2,0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(2, 0x42);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0x42, power.getState().getGPR(9));
	}

	@Test
	public void test4() {
		byte[] binary = { 0x3d, 0x22, 0x00, 0x00 }; // addis r9,r2,0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		long r1 = power.getState().getGPR(1);
		power.getState().setGPR(2, r1);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(r1, power.getState().getGPR(9));
	}

	@Test
	public void test5() {
		byte[] binary = { 0x3d, 0x22, 0x00, 0x00 }; // addis r9,r2,0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		long ptr = power.getState().getGPR(1);
		power.getState().setGPR(2, ptr);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(ptr, power.getState().getGPR(9));
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { 0x3f, (byte) 0xde, 0x00, 0x01 }; // addis r30,r30,1
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:\t3f de 00 01\taddis\tr30,r30,1";
		// @formatter:on
		assertEquals(ref, asm);
	}

	@Test
	public void testDisasm2() {
		byte[] binary = { 0x3c, 0x60, 0x10, 0x00 }; // lis r3,4096
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:\t3c 60 10 00\tlis\tr3,4096";
		// @formatter:on
		assertEquals(ref, asm);
	}

	@Test
	public void testDisasm3() {
		byte[] binary = { 0x3d, 0x22, 0x00, 0x00 }; // addis r9,r2,0
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	3d 22 00 00	addis	r9,r2,0";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
