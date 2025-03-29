package com.unknown.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.unknown.util.IdentityHashSet;

public class IdentityHashSetTest {
	private IdentityHashSet<AllEqual> set;

	private static class AllEqual {
		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object o) {
			return o != null && o instanceof AllEqual;
		}
	}

	@Before
	public void setup() {
		set = new IdentityHashSet<>();
	}

	@Test
	public void test1() {
		AllEqual a = new AllEqual();
		AllEqual b = new AllEqual();
		set.add(a);
		set.add(b);
		assertEquals(2, set.size());
	}

	@Test
	public void test2() {
		AllEqual a = new AllEqual();
		AllEqual b = new AllEqual();
		set.add(a);
		set.add(b);
		set.add(b);
		assertEquals(2, set.size());
	}

	@Test
	public void test3() {
		AllEqual a = new AllEqual();
		AllEqual b = new AllEqual();
		AllEqual c = new AllEqual();
		set.add(a);
		set.add(b);
		assertTrue(set.contains(a));
		assertTrue(set.contains(b));
		assertFalse(set.contains(c));
	}

	@Test
	public void test4() {
		AllEqual a = new AllEqual();
		AllEqual b = new AllEqual();
		set.add(a);
		set.add(b);
		set.remove(a);
		assertEquals(1, set.size());
		assertFalse(set.contains(a));
		assertTrue(set.contains(b));
	}

	@Test
	public void test5() {
		AllEqual a = new AllEqual();
		AllEqual b = new AllEqual();
		set.add(a);
		set.add(b);
		set.remove(a);
		set.remove(b);
		assertEquals(0, set.size());
		assertFalse(set.contains(a));
		assertFalse(set.contains(b));
	}
}
