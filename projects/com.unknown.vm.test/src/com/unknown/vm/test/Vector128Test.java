package com.unknown.vm.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.unknown.vm.register.Vector128;

public class Vector128Test {
	private Vector128 vec;

	@Before
	public void setup() {
		vec = new Vector128();
	}

	@Test
	public void test001() {
		vec.setI64(0, 0xC0DEBABEDEADBEEFL);
		vec.setI64(1, 0xDEADCAFEBEEFC0FEL);
		assertEquals("0xc0debabedeadbeefdeadcafebeefc0fe", vec.toString());
	}

	@Test
	public void test002() {
		vec.setI64(0, 0xC0DEBABEDEADBEEFL);
		vec.setI64(1, 0xDEADCAFEBEEFC0FEL);
		assertEquals(0xC0DEBABE, vec.getI32(0));
		assertEquals(0xDEADBEEF, vec.getI32(1));
		assertEquals(0xDEADCAFE, vec.getI32(2));
		assertEquals(0xBEEFC0FE, vec.getI32(3));
	}

	@Test
	public void test003() {
		vec.setI32(0, 0xC0DE0001);
		vec.setI32(1, 0xC0DE0002);
		vec.setI32(2, 0xC0DE0003);
		vec.setI32(3, 0xC0DE0004);
		assertEquals("0xc0de0001c0de0002c0de0003c0de0004", vec.toString());
	}

	@Test
	public void test004() {
		vec.setI32(0, 0xC0DE0001);
		vec.setI32(1, 0xC0DE0002);
		vec.setI32(2, 0xC0DE0003);
		vec.setI32(3, 0xC0DE0004);
		assertEquals(0xC0DE0001C0DE0002L, vec.getI64(0));
		assertEquals(0xC0DE0003C0DE0004L, vec.getI64(1));
	}
}
