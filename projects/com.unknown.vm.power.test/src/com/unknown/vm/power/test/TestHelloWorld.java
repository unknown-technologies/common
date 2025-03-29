package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class TestHelloWorld {
	@Test
	public void test1() {
		byte[] binary = { 0x7c, 0x08, 0x02, (byte) 0xa6, // mflr r0
				(byte) 0x94, 0x21, (byte) 0xff, (byte) 0xf0, // stwu r1,-16(r1)
				0x3c, 0x60, 0x00, 0x00, // lis r3,0
				0x38, 0x63, 0x00, 0x00, // addi r3,r3,0
				(byte) 0x90, 0x01, 0x00, 0x14, // stw r0,20(r1)
				0x48, 0x00, 0x00, 0x01, // bl 14
				(byte) 0x80, 0x01, 0x00, 0x14, // lwz r0,20(r1)
				0x38, 0x60, 0x00, 0x00, // li r3,0
				0x38, 0x21, 0x00, 0x10, // addi r1,r1,16
				0x7c, 0x08, 0x03, (byte) 0xa6, // mtlr r0
				0x4e, (byte) 0x80, 0x00, 0x20 // blr
		};
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().pc = address;
		power.getState().lr = 0xDEADBEEFL;
		power.getState().setGPR(3, 0xC0DE);
		long oldSp = power.getState().getGPR(1);
		power.run(6);
		assertEquals(0x80000014L, power.getState().pc);
		assertEquals(0x80000018L, power.getState().lr);
		assertEquals(0xDEADBEEFL, power.getState().getGPR(0));
		assertEquals(0, power.getState().getGPR(3));
		long sp = power.getState().getGPR(1);
		assertEquals((int) oldSp, power.getState().getMemory().getI32(sp));
		assertEquals(oldSp - 16, sp);
	}

	@Test
	public void testDisasm1() {
		byte[] binary = { 0x7c, 0x08, 0x02, (byte) 0xa6, // mflr r0
				(byte) 0x94, 0x21, (byte) 0xff, (byte) 0xf0, // stwu r1,-16(r1)
				0x3c, 0x60, 0x00, 0x00, // lis r3,0
				0x38, 0x63, 0x00, 0x00, // addi r3,r3,0
				(byte) 0x90, 0x01, 0x00, 0x14, // stw r0,20(r1)
				0x48, 0x00, 0x00, 0x01, // bl 14
				(byte) 0x80, 0x01, 0x00, 0x14, // lwz r0,20(r1)
				0x38, 0x60, 0x00, 0x00, // li r3,0
				0x38, 0x21, 0x00, 0x10, // addi r1,r1,16
				0x7c, 0x08, 0x03, (byte) 0xa6, // mtlr r0
				0x4e, (byte) 0x80, 0x00, 0x20 // blr
		};
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		String asm = power.disassemble(address, address + binary.length);
		// @formatter:off
		String ref = "0000000080000000:	7c 08 02 a6	mflr	r0\n" +
			     "0000000080000004:	94 21 ff f0	stwu	r1,-16(r1)\n" +
			     "0000000080000008:	3c 60 00 00	lis	r3,0\n" +
			     "000000008000000c:	38 63 00 00	addi	r3,r3,0\n" +
			     "0000000080000010:	90 01 00 14	stw	r0,20(r1)\n" +
			     "0000000080000014:	48 00 00 01	bl	80000014\n" +
			     "0000000080000018:	80 01 00 14	lwz	r0,20(r1)\n" +
			     "000000008000001c:	38 60 00 00	li	r3,0\n" +
			     "0000000080000020:	38 21 00 10	addi	r1,r1,16\n" +
			     "0000000080000024:	7c 08 03 a6	mtlr	r0\n" +
			     "0000000080000028:	4e 80 00 20	blr";
		// @formatter:on
		assertEquals(ref, asm);
	}
}
