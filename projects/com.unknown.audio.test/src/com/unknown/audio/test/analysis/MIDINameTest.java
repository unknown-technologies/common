package com.unknown.audio.test.analysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.audio.analysis.MIDINames;

public class MIDINameTest {
	@Test
	public void testCm2Name() {
		String name = MIDINames.getNoteName(0);
		assertEquals("C-2", name);
	}

	@Test
	public void testC0Name() {
		String name = MIDINames.getNoteName(24);
		assertEquals("C0", name);
	}

	@Test
	public void testC_0Name() {
		String name = MIDINames.getNoteName(25);
		assertEquals("C#0", name);
	}

	@Test
	public void testC_m2Name() {
		String name = MIDINames.getNoteName(1);
		assertEquals("C#-2", name);
	}

	@Test
	public void testA_m1Name() {
		String name = MIDINames.getNoteName(22);
		assertEquals("A#-1", name);
	}

	@Test
	public void testCm2Number() {
		int key = MIDINames.getNoteNumber("c-2");
		assertEquals(0, key);
	}

	@Test
	public void testC0Number() {
		int key = MIDINames.getNoteNumber("c0");
		assertEquals(24, key);
	}

	@Test
	public void testC_0Number() {
		int key = MIDINames.getNoteNumber("c#0");
		assertEquals(25, key);
	}

	@Test
	public void testC_m2Number() {
		int key = MIDINames.getNoteNumber("c#-2");
		assertEquals(1, key);
	}

	@Test
	public void testCm1Number() {
		int key = MIDINames.getNoteNumber("C-1");
		assertEquals(12, key);
	}

	@Test
	public void testC_m1Number() {
		int key = MIDINames.getNoteNumber("C#-1");
		assertEquals(13, key);
	}

	@Test
	public void testDm1Number() {
		int key = MIDINames.getNoteNumber("D-1");
		assertEquals(14, key);
	}

	@Test
	public void testD_m1Number() {
		int key = MIDINames.getNoteNumber("D#-1");
		assertEquals(15, key);
	}

	@Test
	public void testEm1Number() {
		int key = MIDINames.getNoteNumber("E-1");
		assertEquals(16, key);
	}

	@Test
	public void testFm1Number() {
		int key = MIDINames.getNoteNumber("F-1");
		assertEquals(17, key);
	}

	@Test
	public void testF_m1Number() {
		int key = MIDINames.getNoteNumber("F#-1");
		assertEquals(18, key);
	}

	@Test
	public void testGm1Number() {
		int key = MIDINames.getNoteNumber("G-1");
		assertEquals(19, key);
	}

	@Test
	public void testG_m1Number() {
		int key = MIDINames.getNoteNumber("G#-1");
		assertEquals(20, key);
	}

	@Test
	public void testAm1Number() {
		int key = MIDINames.getNoteNumber("A-1");
		assertEquals(21, key);
	}

	@Test
	public void testA_m1Number() {
		int key = MIDINames.getNoteNumber("A#-1");
		assertEquals(22, key);
	}

	@Test
	public void testBm1Number() {
		int key = MIDINames.getNoteNumber("B-1");
		assertEquals(23, key);
	}
}
