package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.Power;

public class LiTest {
	@Test
	public void test1() {
		byte[] binary = { 0x3c, 0x60, 0x10, 0x00, // lis r3,4096
				0x38, 0x63, 0x06, 0x50 // addi r3,r3,1616
		};
		long address = 0x80000000L;
		Power power = new Power();
		power.loadCode(address, binary);
		power.getState().pc = address;
		power.run(2);
		assertEquals(0x80000008L, power.getState().pc);
		assertEquals(0x10000650, (int) power.getState().getGPR(3));
	}
}
