package com.unknown.audio.test.midi;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.unknown.audio.midi.smf.VariableInt;

public class VariableIntTest {
	private static int read(byte[] bytes) throws IOException {
		try(InputStream in = new ByteArrayInputStream(bytes)) {
			return VariableInt.read(in);
		}
	}

	private static byte[] write(int value) throws IOException {
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			VariableInt.write(value, out);
			return out.toByteArray();
		}
	}

	@Test
	public void testRead() throws IOException {
		assertEquals(0, read(new byte[] { 0 }));
		assertEquals(0x40, read(new byte[] { 0x40 }));
		assertEquals(0x7F, read(new byte[] { 0x7F }));
		assertEquals(0x80, read(new byte[] { (byte) 0x81, 0x00 }));
		assertEquals(0x2000, read(new byte[] { (byte) 0xC0, 0x00 }));
		assertEquals(0x3FFF, read(new byte[] { (byte) 0xFF, 0x7F }));
		assertEquals(0x4000, read(new byte[] { (byte) 0x81, (byte) 0x80, 0x00 }));
		assertEquals(0x100000, read(new byte[] { (byte) 0xC0, (byte) 0x80, 0x00 }));
		assertEquals(0x1FFFFF, read(new byte[] { (byte) 0xFF, (byte) 0xFF, 0x7F }));
		assertEquals(0x200000, read(new byte[] { (byte) 0x81, (byte) 0x80, (byte) 0x80, 0x00 }));
		assertEquals(0x8000000, read(new byte[] { (byte) 0xC0, (byte) 0x80, (byte) 0x80, 0x00 }));
		assertEquals(0xFFFFFFF, read(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F }));
	}

	@Test
	public void testWrite() throws IOException {
		assertArrayEquals(new byte[] { 0 }, write(0));
		assertArrayEquals(new byte[] { 0x40 }, write(0x40));
		assertArrayEquals(new byte[] { 0x7F }, write(0x7F));
		assertArrayEquals(new byte[] { (byte) 0x81, 0x00 }, write(0x80));
		assertArrayEquals(new byte[] { (byte) 0xC0, 0x00 }, write(0x2000));
		assertArrayEquals(new byte[] { (byte) 0xFF, 0x7F }, write(0x3FFF));
		assertArrayEquals(new byte[] { (byte) 0x81, (byte) 0x80, 0x00 }, write(0x4000));
		assertArrayEquals(new byte[] { (byte) 0xC0, (byte) 0x80, 0x00 }, write(0x100000));
		assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF, 0x7F }, write(0x1FFFFF));
		assertArrayEquals(new byte[] { (byte) 0x81, (byte) 0x80, (byte) 0x80, 0x00 }, write(0x200000));
		assertArrayEquals(new byte[] { (byte) 0xC0, (byte) 0x80, (byte) 0x80, 0x00 }, write(0x8000000));
		assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F }, write(0xFFFFFFF));
	}

	@Test
	public void testLength() {
		assertEquals(1, VariableInt.length(0));
		assertEquals(1, VariableInt.length(0x40));
		assertEquals(1, VariableInt.length(0x7F));
		assertEquals(2, VariableInt.length(0x80));
		assertEquals(2, VariableInt.length(0x2000));
		assertEquals(2, VariableInt.length(0x3FFF));
		assertEquals(3, VariableInt.length(0x4000));
		assertEquals(3, VariableInt.length(0x100000));
		assertEquals(3, VariableInt.length(0x1FFFFF));
		assertEquals(4, VariableInt.length(0x200000));
		assertEquals(4, VariableInt.length(0x8000000));
		assertEquals(4, VariableInt.length(0xFFFFFFF));
	}
}
