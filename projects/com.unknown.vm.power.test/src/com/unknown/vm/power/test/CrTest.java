package com.unknown.vm.power.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.vm.power.isa.Cr;

public class CrTest {
	private Cr craccess;
	private int cr;

	private void create(int bf) {
		craccess = new Cr(bf);
	}

	private int get() {
		return craccess.get(cr);
	}

	private void set(int x) {
		cr = craccess.set(cr, x);
	}

	@Test
	public void test1() {
		cr = 0b10010000000000000000000000000000;
		int cr0 = 0b1001;
		create(0);
		assertEquals(cr0, get());
	}

	@Test
	public void test2() {
		cr = 0b00001001000000000000000000000000;
		int cr1 = 0b1001;
		create(1);
		assertEquals(cr1, get());
	}

	@Test
	public void test3() {
		cr = 0b10010000000000000000000000000000;
		int cr0 = 0b1100;
		create(0);
		set(cr0);
		assertEquals(0b11000000000000000000000000000000, cr);
	}

	@Test
	public void test4() {
		cr = 0b10010000000000000000000000000000;
		int cr1 = 0b1100;
		create(1);
		set(cr1);
		assertEquals(0b10011100000000000000000000000000, cr);
	}
}
