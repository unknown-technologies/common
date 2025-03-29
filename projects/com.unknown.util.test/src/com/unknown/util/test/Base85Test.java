package com.unknown.util.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.util.encoding.Base85;

public class Base85Test {
	@Test
	public void testZero() {
		assertEquals("_", Base85.encode(new byte[] { 0, 0, 0, 0 }));
	}

	@Test
	public void testSpaces() {
		assertEquals("\\", Base85.encode(new byte[] { ' ', ' ', ' ', ' ' }));
	}

	@Test
	public void testData() {
		assertEquals("z0_yz_2FF", Base85.encode(new byte[] { (byte) 0xFF, 0x3E, 0x79, 0x5F, 0x00, 0x00, 0x00,
				0x00, 0x3C, (byte) 0xC3 }));
		assertEquals("z00zz", Base85.encode(new byte[] { (byte) 0xFF, 0x35, 0x5A, 0x1B }));
		assertEquals("_L@33",
				Base85.encode(new byte[] { 0x00, 0x00, 0x00, 0x00, (byte) 0xCA, (byte) 0xC1, 0x73 }));
		assertEquals("zL@33", Base85.encode(new byte[] { -1, -1, -1, -1 }));
		assertEquals("00", Base85.encode(new byte[] { 0 }));
		assertEquals("000", Base85.encode(new byte[] { 0, 0 }));
		assertEquals("0000", Base85.encode(new byte[] { 0, 0, 0 }));
	}

	@Test
	public void testDecode() {
		assertArrayEquals(new byte[] { (byte) 0xFF, 0x3E, 0x79, 0x5F, 0x00, 0x00, 0x00,
				0x00, 0x3C, (byte) 0xC3 }, Base85.decode("z0_yz_2FF"));
		assertArrayEquals(new byte[] { (byte) 0xFF, 0x35, 0x5A, 0x1B }, Base85.decode("z00zz"));
		assertArrayEquals(new byte[] { 0x00, 0x00, 0x00, 0x00, (byte) 0xCA, (byte) 0xC1, 0x73 },
				Base85.decode("_L@33"));
		assertArrayEquals(new byte[] { -1, -1, -1, -1 },
				Base85.decode("zL@33"));
		assertArrayEquals(new byte[] { 0 }, Base85.decode("00"));
		assertArrayEquals(new byte[] { 0, 0 }, Base85.decode("000"));
		assertArrayEquals(new byte[] { 0, 0, 0 }, Base85.decode("0000"));
	}
}
