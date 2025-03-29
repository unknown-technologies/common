package com.unknown.text.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.unknown.text.TextWrap;
import com.unknown.text.hyph.Hyphenator;

public class TextWrapTest {
	private static void checkLengths(String[] strings, int maxlen) {
		for(String s : strings) {
			assertTrue(s.length() + " is longer than " + maxlen, s.length() <= maxlen);
		}
	}

	@Test
	public void test001() {
		String text = "Hello world, this is a very long text that has to be wrapped automatically.";
		String[] expected = { "Hello world, this is",
				"a very long text",
				"that has to be",
				"wrapped",
				"automatically." };
		String[] lines = TextWrap.wrap(text, 20);
		checkLengths(lines, 20);
		assertArrayEquals(expected, lines);
	}

	@Test
	public void test002() {
		String text = "Hello world, this is a very long\ntext that has to be wrapped automatically.";
		String[] expected = { "Hello world, this is",
				"a very long text",
				"that has to be",
				"wrapped",
				"automatically." };
		String[] lines = TextWrap.wrap(text, 20);
		checkLengths(lines, 20);
		assertArrayEquals(expected, lines);
	}

	@Test
	public void test003() {
		String text = "Hello world, this is a very long\ntext\nthat has to be wrapped automatically.";
		String[] expected = { "Hello world, this is",
				"a very long text",
				"that has to be",
				"wrapped",
				"automatically." };
		String[] lines = TextWrap.wrap(text, 20);
		checkLengths(lines, 20);
		assertArrayEquals(expected, lines);
	}

	@Test
	public void test004() {
		String text = "Hello world, this is a very long text that has to be\nwrapped automatically.";
		String[] expected = { "Hello world, this is",
				"a very long text",
				"that has to be",
				"wrapped",
				"automatically." };
		String[] lines = TextWrap.wrap(text, 20);
		checkLengths(lines, 20);
		assertArrayEquals(expected, lines);
	}

	@Test
	public void test005() {
		String text = "Hello world, this is a very long text that has\n\nto be wrapped automatically.";
		String[] expected = { "Hello world, this is",
				"a very long text",
				"that has",
				"to be wrapped",
				"automatically." };
		String[] lines = TextWrap.wrap(text, 20);
		checkLengths(lines, 20);
		assertArrayEquals(expected, lines);
	}

	@Test
	public void test006() throws IOException {
		String text = "Hello world, this is a very long text that has to be wrapped automatically.";
		String[] expected = { "Hello world, this is",
				"a very long text",
				"that has to be",
				"wrapped automatical-",
				"ly." };
		String[] lines = TextWrap.wrap(text, 20, new Hyphenator("en"));
		checkLengths(lines, 20);
		assertArrayEquals(expected, lines);
	}

	@Test
	public void test007() throws IOException {
		String text = "Usually text has to be re-formatted in order to fit into very small computer screens. However, this is not always possible, therefore it is necessary to implement specialized wrapping algorithms.";
		String[] lines = TextWrap.wrap(text, 20, new Hyphenator("en"));
		String[] expected = {
				"Usually text has to",
				"be re-formatted in",
				"order to fit into",
				"very small computer",
				"screens. However,",
				"this is not always",
				"possible, therefore",
				"it is necessary to",
				"implement special-",
				"ized wrapping algo-",
				"rithms."
		};
		checkLengths(lines, 20);
		assertArrayEquals(expected, lines);
	}

	@Test
	public void test008() throws IOException {
		String text = "     ssh (SSH client) is a program for logging into a remote machine and for\n" +
				"     executing commands on a remote machine.  It is intended to provide secure\n" +
				"     encrypted communications between two untrusted hosts over an insecure\n" +
				"     network.  X11 connections, arbitrary TCP ports and UNIX-domain sockets\n" +
				"     can also be forwarded over the secure channel.\n";
		String[] lines = TextWrap.wrap(text, 80, new Hyphenator("en"));
		String[] expected = { "ssh (SSH client) is a program for logging into a remote machine and for execut-",
				"ing commands on a remote machine. It is intended to provide secure encrypted",
				"communications between two untrusted hosts over an insecure network. X11 connec-",
				"tions, arbitrary TCP ports and UNIX-domain sockets can also be forwarded over",
				"the secure channel." };
		checkLengths(lines, 80);
		assertArrayEquals(expected, lines);
	}
}
