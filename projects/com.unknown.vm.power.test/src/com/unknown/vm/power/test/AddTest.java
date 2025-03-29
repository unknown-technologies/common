package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.exceptions.SegmentationViolation;
import com.unknown.vm.power.Power;

public class AddTest {
	@Test
	public void test1() {
		byte[] binary = { 0x7f, (byte) 0x9c, 0x1a, 0x14, // add r28, r28, r3
				0x7e, (byte) 0xc6, (byte) 0xb2, 0x14 // add r22, r6, r22
		};
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(6, 0x0E);
		power.getState().setGPR(22, 0x13);
		power.getState().setGPR(28, 0x21);
		power.getState().setGPR(3, 0x21);
		power.getState().pc = address;
		power.run(2);
		assertEquals(0x80000008L, power.getState().pc);
		assertEquals(0x21, power.getState().getGPR(22));
		assertEquals(0x42, power.getState().getGPR(28));
	}

	@Test(expected = SegmentationViolation.class)
	public void test2() {
		byte[] binary = { 0x7f, (byte) 0x9c, 0x1a, 0x14, // add r28, r28, r3
				0x7e, (byte) 0xc6, (byte) 0xb2, 0x14 // add r22, r6, r22
		};
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().pc = address;
		power.run(3);
	}

	@Test
	public void test3() {
		byte[] binary = { 0x7f, (byte) 0xbd, (byte) 0xfa, 0x15 }; // add. r29,r29,r31
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().setGPR(29, 0xffffffffffffffc0L);
		power.getState().setGPR(31, 0x0000000000000040L);
		power.getState().cr = 0x24024402;
		power.getState().pc = address;
		power.run(1);
		assertEquals(0x80000004L, power.getState().pc);
		assertEquals(0, power.getState().getGPR(29));
		assertEquals(0x24024402, power.getState().cr);
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { 0x7f, (byte) 0x9c, 0x1a, 0x14, // add r28, r28, r3
				0x7e, (byte) 0xc6, (byte) 0xb2, 0x14 // add r22, r6, r22
		};
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:\t7f 9c 1a 14\tadd\tr28,r28,r3\n" +
			     "0000000080000004:\t7e c6 b2 14\tadd\tr22,r6,r22";
		// @formatter:on
		assertEquals(ref, asm);
	}

	@Test
	public void testDisasm2() {
		byte[] binary = { 0x7f, (byte) 0x9c, 0x1a, 0x14, // add r28, r28, r3
				0x7e, (byte) 0xc6, (byte) 0xb2, 0x14 // add r22, r6, r22
		};
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().pc = address;
		power.run(2);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:\t7f 9c 1a 14\tadd\tr28,r28,r3\n" +
			     "0000000080000004:\t7e c6 b2 14\tadd\tr22,r6,r22";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
