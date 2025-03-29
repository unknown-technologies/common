package com.unknown.util.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.unknown.util.net.IpAddress;

public class IpAddressTest {
	private static class IPv6Address {
		public final String str;
		public final byte[] bytes;

		public IPv6Address(String str, byte[] bytes) {
			this.str = str;
			this.bytes = bytes;
		}

		@Override
		public String toString() {
			return str;
		}
	}

	public static final IPv6Address[] TESTCASES = {
			new IPv6Address("::", new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }),
			new IPv6Address("1:2::5", new byte[] { 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 }),
			new IPv6Address("2001:0DB8:AC10:FE01:0000:0000:0000:0000", new byte[] { 0x20, 0x01, 0x0d,
					(byte) 0xb8, (byte) 0xac, 0x10, (byte) 0xfe, 0x01, 0, 0, 0, 0, 0, 0, 0, 0 }),
			new IPv6Address("2001:0DB8:AC10:FE01::", new byte[] { 0x20, 0x01, 0x0d, (byte) 0xb8,
					(byte) 0xac, 0x10, (byte) 0xfe, 0x01, 0, 0, 0, 0, 0, 0, 0, 0 }),
			new IPv6Address("2a02:810a:8840:21d4:e816:2884:18e8:97da", new byte[] { 0x2a, 0x02,
					(byte) 0x81, 0x0a, (byte) 0x88, 0x40, 0x21, (byte) 0xd4, (byte) 0xe8, 0x16,
					0x28, (byte) 0x84, 0x18, (byte) 0xe8, (byte) 0x97, (byte) 0xda }),
			new IPv6Address("1::", new byte[] { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }),
			new IPv6Address("1:2:3:4:5:6:7::",
					new byte[] { 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 0 }),
			new IPv6Address("1::8", new byte[] { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8 }),
			new IPv6Address("1:2:3:4:5:6::8", new byte[] { 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 0, 0, 8 }),
			new IPv6Address("1::7:8", new byte[] { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 8 }),
			new IPv6Address("1:2:3:4:5::7:8", new byte[] { 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 0, 0, 7, 0, 8 }),
			new IPv6Address("1:2:3:4:5::8", new byte[] { 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 0, 0, 0, 0, 8 }),
			new IPv6Address("1::6:7:8", new byte[] { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 7, 0, 8 }),
			new IPv6Address("1:2:3:4::6:7:8", new byte[] { 0, 1, 0, 2, 0, 3, 0, 4, 0, 0, 0, 6, 0, 7, 0, 8 }),
			new IPv6Address("1:2:3:4::8", new byte[] { 0, 1, 0, 2, 0, 3, 0, 4, 0, 0, 0, 0, 0, 0, 0, 8 }),
			new IPv6Address("1::5:6:7:8", new byte[] { 0, 1, 0, 0, 0, 0, 0, 0, 0, 5, 0, 6, 0, 7, 0, 8 }),
			new IPv6Address("1:2:3::5:6:7:8", new byte[] { 0, 1, 0, 2, 0, 3, 0, 0, 0, 5, 0, 6, 0, 7, 0, 8 }),
			new IPv6Address("1:2:3::8", new byte[] { 0, 1, 0, 2, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8 }),
			new IPv6Address("1::4:5:6:7:8", new byte[] { 0, 1, 0, 0, 0, 0, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8 }),
			new IPv6Address("1:2::4:5:6:7:8", new byte[] { 0, 1, 0, 2, 0, 0, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8 }),
			new IPv6Address("1:2::8", new byte[] { 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8 }),
			new IPv6Address("1::3:4:5:6:7:8", new byte[] { 0, 1, 0, 0, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8 }),
			new IPv6Address("1::3:4:5:6:7:8", new byte[] { 0, 1, 0, 0, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8 }),
			new IPv6Address("1::8", new byte[] { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8 }),
			new IPv6Address("::2:3:4:5:6:7:8",
					new byte[] { 0, 0, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8 }),
			new IPv6Address("::8", new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8 }),
	};

	private static final String[] INVALID_IPV6 = {
			"1:2::5::",
			"1:2:3:4:5:6:7:8:",
			"1:2:3:4:5:6:7:8::",
			"1:2:3:4:5:6:7:",
			"2001:0DB8:AC10:FE01:0000:0000:0000:0000:0000"
	};

	@Test
	public void parseIPv6() {
		for(IPv6Address addr : TESTCASES) {
			byte[] actual = null;
			try {
				actual = IpAddress.parseIPv6(addr.str);
			} catch(IllegalArgumentException e) {
				throw new AssertionError("error: " + addr.str, e);
			}
			assertArrayEquals(addr.str, addr.bytes, actual);
		}
	}

	@Test
	public void invalidIPv6() {
		for(String addr : INVALID_IPV6) {
			assertFalse(addr, IpAddress.isIPv6(addr));
		}
	}

	@Test
	public void parseIPv4Ok() {
		byte[] expected = { (byte) 192, (byte) 168, 0, 1 };
		byte[] actual = IpAddress.parseIPv4("192.168.0.1");
		assertArrayEquals(expected, actual);
	}

	@Test
	public void parseIPv4Long() {
		byte[] expected = { (byte) 192, (byte) 168, 0, 1 };
		byte[] actual = IpAddress.parseIPv4("192.168.000.001");
		assertArrayEquals(expected, actual);
	}

	@Test
	public void parseIPv4Short() {
		byte[] expected = { 0, 0, 0, 0 };
		byte[] actual = IpAddress.parseIPv4("0.0.0.0");
		assertArrayEquals(expected, actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseIPv4TooLong() {
		IpAddress.parseIPv4("192.168.000.0001");
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseIPv4TooShort() {
		IpAddress.parseIPv4("1.1.1.");
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseIPv4Negative() {
		IpAddress.parseIPv4("192.168.0.-1");
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseIPv4TooLarge() {
		IpAddress.parseIPv4("192.168.0.256");
		fail();
	}

	@Test
	public void formatIPv4Ok() {
		String expected = "192.168.0.1";
		String actual = IpAddress.formatIPv4(new byte[] { (byte) 192,
				(byte) 168, 0, 1 });
		assertEquals(expected, actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void formatIPv4TooLong() {
		IpAddress.formatIPv4(new byte[] { (byte) 192, (byte) 168, 0, 1,
				2 });
		fail();
	}

	@Test
	public void parseIPv6Long() {
		byte[] expected = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 1 };
		byte[] actual = IpAddress.parseIPv6("0:0:0:0:0:0:0:1");
		assertArrayEquals(expected, actual);
	}

	@Test
	public void parseIPv6Short1() {
		byte[] expected = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 1 };
		byte[] actual = IpAddress.parseIPv6("::1");
		assertArrayEquals(expected, actual);
	}

	@Test
	public void parseIPv6Short2() {
		byte[] expected = { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 2 };
		byte[] actual = IpAddress.parseIPv6("1::2");
		assertArrayEquals(expected, actual);
	}

	@Test
	public void parseIPv6Short3() {
		byte[] expected = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				-1, -1 };
		byte[] actual = IpAddress.parseIPv6("::ffff");
		assertArrayEquals(expected, actual);
	}

	@Test
	public void parseIPv6Short4() {
		byte[] expected = { 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				3, 4 };
		byte[] actual = IpAddress.parseIPv6("0102::0304");
		assertArrayEquals(expected, actual);
	}

	@Test
	public void parseIPv6Short5() {
		byte[] expected = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 1 };
		byte[] actual = IpAddress.parseIPv6("0:0:0::0:0:0:1");
		assertArrayEquals(expected, actual);
	}

	@Test
	public void parseIPv6Short6() {
		byte[] expected = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0 };
		byte[] actual = IpAddress.parseIPv6("::");
		assertArrayEquals(expected, actual);
	}

	@Test
	public void parseIPv6Bracket() {
		byte[] expected = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 1 };
		byte[] actual = IpAddress.parseIPv6("[::1]");
		assertArrayEquals(expected, actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseIPv6TooShort() {
		IpAddress.parseIPv6("0:0:0:0:0:0:1");
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseIPv6SyntaxError1() {
		IpAddress.parseIPv6(":1");
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseIPv6SyntaxError2() {
		IpAddress.parseIPv6("1:");
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseIPv6SyntaxError3() {
		IpAddress.parseIPv6("::-1");
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseIPv6SyntaxError4() {
		IpAddress.parseIPv6("::1ffff");
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseIPv6SyntaxError5() {
		IpAddress.parseIPv6("[[::1ffff]");
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseIPv6SyntaxError6() {
		IpAddress.parseIPv6("[::1ffff");
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseIPv6SyntaxError7() {
		IpAddress.parseIPv6("::1ffff]");
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void parseIPv6SyntaxError8() {
		IpAddress.parseIPv6("::g");
		fail();
	}

	@Test
	public void normalize1() {
		assertEquals("192.168.0.1", IpAddress.normalize("192.168.0.1"));
	}

	@Test
	public void normalize2() {
		assertEquals("192.168.0.1",
				IpAddress.normalize("192.168.000.001"));
	}

	@Test
	public void normalize3() {
		assertEquals("192.168.0.1", IpAddress.normalize("192.168.00.1"));
	}

	@Test
	public void normalize4() {
		assertEquals("::1", IpAddress.normalize("0::1"));
	}

	@Test
	public void normalize5() {
		assertEquals("::", IpAddress.normalize("0:0:0:0:0:0:0:0"));
	}

	@Test
	public void normalize6() {
		assertEquals("::1", IpAddress.normalize("[0:0:0:0:0:0:0:1]"));
	}

	@Test
	public void normalize7() {
		assertEquals("::1", IpAddress.normalize("[::1]"));
	}

	@Test
	public void normalize8() {
		assertEquals("[::1]", IpAddress.normalize("[::1]", true));
	}

	@Test
	public void normalize9() {
		assertEquals("[::1]", IpAddress.normalize("::1", true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void normalizeFail1() {
		IpAddress.normalizeAddress("localhost", true);
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void normalizeFail2() {
		IpAddress.normalizeAddress("192.168.0.256", true);
		fail();
	}

	@Test(expected = IllegalArgumentException.class)
	public void normalizeFail3() {
		IpAddress.normalizeAddress("192.168.0.0/24", true);
		fail();
	}

	@Test(expected = NullPointerException.class)
	public void normalizeNull1() {
		IpAddress.normalizeAddress(null);
		fail();
	}

	@Test(expected = NullPointerException.class)
	public void normalizeNull2() {
		IpAddress.normalizeAddress(null, true);
		fail();
	}

	@Test
	public void normalizeNull3() {
		assertNull(IpAddress.normalize(null));
	}

	@Test
	public void normalizeNull4() {
		assertNull(IpAddress.normalize(null, true));
	}

	@Test
	public void normalizeFail1Alt() {
		assertEquals("localhost",
				IpAddress.normalize("localhost", true));
	}

	@Test
	public void normalizeFail2Alt() {
		assertEquals("192.168.0.256",
				IpAddress.normalize("192.168.0.256", true));
	}

	@Test
	public void normalizeFail3Alt() {
		assertEquals("192.168.0.0/24",
				IpAddress.normalize("192.168.0.0/24", true));
	}

	@Test
	public void equals1() {
		assertTrue(IpAddress.equals("192.168.0.1", "192.168.0.1"));
	}

	@Test
	public void equals2() {
		assertTrue(IpAddress.equals("192.168.0.1", "192.168.000.001"));
	}

	@Test
	public void equals3() {
		assertFalse(IpAddress.equals("192.168.0.1", "192.169.0.1"));
	}

	@Test
	public void equals4() {
		assertTrue(IpAddress.equals("::1", "::1"));
	}

	@Test
	public void equals6() {
		assertFalse(IpAddress.equals("::1", "::2"));
	}

	@Test
	public void equals7() {
		assertFalse(IpAddress.equals("::1", "localhost"));
	}

	@Test
	public void equals8() {
		assertTrue(IpAddress.equals("localhost", "localhost"));
	}

	@Test
	public void equals9() {
		assertTrue(IpAddress.equals("::ffff", "::FFFF"));
	}
}
