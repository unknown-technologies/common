package com.unknown.net.test.shownet;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

import com.unknown.net.shownet.InterfaceId;

public class InterfaceIdTest {
	@Test
	public void testToString() {
		InterfaceId id = new InterfaceId(0x001D0046, 0x34335113, 0x34303437);
		assertEquals("D4873475:E0A96520:E0AA0004", id.toString());
		assertEquals(0x001D0046, id.get(0));
		assertEquals(0x34335113, id.get(1));
		assertEquals(0x34303437, id.get(2));
	}

	@Test
	public void testParse() throws ParseException {
		InterfaceId id = InterfaceId.parse("D4873475:E0A96520:E0AA0004");
		assertEquals("D4873475:E0A96520:E0AA0004", id.toString());
		assertEquals(0x001D0046, id.get(0));
		assertEquals(0x34335113, id.get(1));
		assertEquals(0x34303437, id.get(2));
	}

	@Test
	public void testParseCtor() {
		InterfaceId id = new InterfaceId("D4873475:E0A96520:E0AA0004");
		assertEquals("D4873475:E0A96520:E0AA0004", id.toString());
		assertEquals(0x001D0046, id.get(0));
		assertEquals(0x34335113, id.get(1));
		assertEquals(0x34303437, id.get(2));
	}
}
