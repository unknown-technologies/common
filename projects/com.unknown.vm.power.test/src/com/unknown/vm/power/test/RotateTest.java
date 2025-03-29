package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.isa.Rotate;

public class RotateTest {
	@Test
	public void testMaskForward001() {
		long mask = Rotate.mask(32, 32);
		assertEquals(0x80000000L, mask);
	}

	@Test
	public void testMaskForward002() {
		long mask = Rotate.mask(32, 63);
		assertEquals(0xffffffffL, mask);
	}

	@Test
	public void testMaskReverse001() {
		long mask = Rotate.mask(64, 31);
		assertEquals(0xffffffff00000000L, mask);
	}
}
