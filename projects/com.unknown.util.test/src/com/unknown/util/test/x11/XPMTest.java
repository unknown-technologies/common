package com.unknown.util.test.x11;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.unknown.util.ResourceLoader;
import com.unknown.util.x11.XPM;

public class XPMTest {
	private static String load(String name) throws IOException {
		return new String(ResourceLoader.load(XPMTest.class, name), StandardCharsets.US_ASCII);
	}

	@Test
	public void parseSimple() throws IOException {
		XPM xpm = new XPM(load("Dtdata.l.pm"));
		assertEquals(25, xpm.getWidth());
		assertEquals(32, xpm.getHeight());
		assertEquals("data", xpm.getName());
	}

	@Test
	public void parseComplex() throws IOException {
		XPM xpm = new XPM(load("acroread.l.pm"));
		assertEquals(48, xpm.getWidth());
		assertEquals(48, xpm.getHeight());
		assertEquals("acroread_l_xpm", xpm.getName());
	}
}
