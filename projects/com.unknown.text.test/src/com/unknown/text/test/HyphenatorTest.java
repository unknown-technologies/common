package com.unknown.text.test;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;

import org.junit.Test;

import com.unknown.text.hyph.Hyphenator;

public class HyphenatorTest {
	@Test
	public void test001() throws IOException {
		int[] associate = { 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0 };
		int[] associates = { 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0 };
		int[] declination = { 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0 };
		int[] obligatory = { 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 };
		int[] philanthropic = { 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0 };
		int[] present = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] presents = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] project = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] projects = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] reciprocity = { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 };
		int[] recognizance = { 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0 };
		int[] reformation = { 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0 };
		int[] retribution = { 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0 };
		int[] table = { 0, 0, 0, 1, 0, 0, 0 };

		Hyphenator hyp = new Hyphenator("en");
		assertArrayEquals(associate, hyp.getException("associate"));
		assertArrayEquals(associates, hyp.getException("associates"));
		assertArrayEquals(declination, hyp.getException("declination"));
		assertArrayEquals(obligatory, hyp.getException("obligatory"));
		assertArrayEquals(philanthropic, hyp.getException("philanthropic"));
		assertArrayEquals(present, hyp.getException("present"));
		assertArrayEquals(presents, hyp.getException("presents"));
		assertArrayEquals(project, hyp.getException("project"));
		assertArrayEquals(projects, hyp.getException("projects"));
		assertArrayEquals(reciprocity, hyp.getException("reciprocity"));
		assertArrayEquals(recognizance, hyp.getException("recognizance"));
		assertArrayEquals(reformation, hyp.getException("reformation"));
		assertArrayEquals(retribution, hyp.getException("retribution"));
		assertArrayEquals(table, hyp.getException("table"));
	}

	@Test
	public void test002() throws IOException {
		int[] _timo = { 0, 0, 0, 0, 5, 5 };
		int[] nfinites = { 0, 0, 0, 6, 3, 0, 0, 0, 0 };
		int[] urial_ = { 0, 0, 0, 4, 0, 0, 0 };
		int[] widesp = { 0, 0, 0, 0, 5, 0, 0 };

		Hyphenator hyp = new Hyphenator("en");
		assertArrayEquals(_timo, hyp.getPattern(".timo"));
		assertArrayEquals(nfinites, hyp.getPattern("nfinites"));
		assertArrayEquals(urial_, hyp.getPattern("urial."));
		assertArrayEquals(widesp, hyp.getPattern("widesp"));
	}

	@Test
	public void points001() throws IOException {
		int[] hyphenation = { 0, 0, 0, 3, 0, 0, 2, 5, 4, 2, 0, 0, 0, 0 };

		Hyphenator hyp = new Hyphenator("en");
		assertArrayEquals(hyphenation, hyp.getPoints("hyphenation"));
	}

	@Test
	public void points002() throws IOException {
		int[] supercalifragilisticexpialidocious = { 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 4, 0, 1, 2,
				0, 1, 0, 3, 2, 3, 0, 2, 1, 0, 1, 2, 0, 2, 0, 0, 0 };

		Hyphenator hyp = new Hyphenator("en");
		assertArrayEquals(supercalifragilisticexpialidocious,
				hyp.getPoints("supercalifragilisticexpialidocious"));
	}

	@Test
	public void hyphenate001() throws IOException {
		Hyphenator hyp = new Hyphenator("en");
		String[] ref = { "hy", "phen", "ation" };
		String[] act = hyp.hyphenate("hyphenation");
		assertArrayEquals(ref, act);
	}

	@Test
	public void hyphenate002() throws IOException {
		Hyphenator hyp = new Hyphenator("en");
		String[] ref = { "su", "per", "cal", "ifrag", "ilis", "tic", "ex", "pi", "ali", "do", "cious" };
		String[] act = hyp.hyphenate("supercalifragilisticexpialidocious");
		assertArrayEquals(ref, act);
	}

	@Test
	public void hyphenate003() throws IOException {
		Hyphenator hyp = new Hyphenator("en");
		String[] ref = { "project" };
		String[] act = hyp.hyphenate("project");
		assertArrayEquals(ref, act);
	}
}
