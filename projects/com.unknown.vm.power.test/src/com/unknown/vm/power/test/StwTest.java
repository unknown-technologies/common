package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.exceptions.SegmentationViolation;
import com.unknown.vm.power.Power;

public class StwTest {
	@Test
	public void test1() {
		byte[] binary = { (byte) 0x90, 0x01, 0x00, 0x00 }; // stw r0,0(r1)
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(0, 0xC0DEBABE);
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		long sp = power.getState().getGPR(1);
		assertEquals(0xC0DEBABE, power.getState().getMemory().getI32(sp));
	}

	@Test(expected = SegmentationViolation.class)
	public void test2() {
		byte[] binary = { (byte) 0x90, 0x01, 0x00, 0x14 }; // stw r0,20(r1)
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(0, 0xC0DEBABE);
		power.getState().pc = address;
		power.run(1);
	}

	@Test
	public void test3() {
		byte[] binary = { (byte) 0x94, 0x21, (byte) 0xff, (byte) 0xf0, // stwu r1,-16(r1)
				(byte) 0x90, 0x01, 0x00, 0x14 // stw r0,20(r1)
		};
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(0, 0xC0DEBABE);
		power.getState().pc = address;
		power.run(2);
		assertEquals(0x80000008L, power.getState().pc);
		long sp = power.getState().getGPR(1);
		assertEquals(0xC0DEBABE, power.getState().getMemory().getI32(sp + 20));
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { (byte) 0x90, 0x01, 0x00, 0x14 }; // stw r0,20(r1)
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:\t90 01 00 14\tstw\tr0,20(r1)";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
