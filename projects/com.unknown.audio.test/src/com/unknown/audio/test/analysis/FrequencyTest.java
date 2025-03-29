package com.unknown.audio.test.analysis;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.audio.analysis.Frequency;

public class FrequencyTest {
	@Test
	public void test1() {
		assertEquals(440, Frequency.MIDInoteToFreq(69), 1e-12);
	}

	@Test
	public void test2() {
		assertEquals(69, Frequency.freqToMIDInote(440), 1e-12);
	}

	@Test
	public void test3() {
		assertEquals(261.63, Frequency.MIDInoteToFreq(60), 1e-2);
	}

	@Test
	public void test4() {
		assertEquals(60, Frequency.freqToMIDInote(261.63), 1e-2);
	}
}
