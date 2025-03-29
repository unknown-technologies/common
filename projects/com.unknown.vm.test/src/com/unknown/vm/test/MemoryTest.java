package com.unknown.vm.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.exceptions.SegmentationViolation;
import com.unknown.vm.memory.ByteMemory;
import com.unknown.vm.memory.Memory;

public class MemoryTest {
	@Test
	public void test1() {
		Memory mem = new ByteMemory(16);
		mem.setI8(0, (byte) 0xAB);
		mem.setI8(1, (byte) 0xCD);
		assertEquals((short) 0xABCD, mem.getI16B(0));
		assertEquals((short) 0xCDAB, mem.getI16L(0));
	}

	@Test(expected = SegmentationViolation.class)
	public void testSegV1() {
		Memory mem = new ByteMemory(16);
		mem.setI8(16, (byte) 0);
	}

	@Test(expected = SegmentationViolation.class)
	public void testSegV2() {
		Memory mem = new ByteMemory(16);
		mem.setI16B(16, (short) 0);
	}
}
