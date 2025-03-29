package com.unknown.util.test.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.unknown.util.io.Ini;

public class IniTest {
	private Ini ini;

	@Before
	public void setup() {
		ini = new Ini();
	}

	@Test
	public void testEmptyIni() {
		assertEquals(0, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(0, sections.size());
	}

	@Test
	public void testParseEmpty() throws IOException {
		ini = new Ini("");
		assertEquals(0, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(0, sections.size());
	}

	@Test
	public void testParseEmptyMultiline() throws IOException {
		ini = new Ini("\n\n\n");
		assertEquals(0, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(0, sections.size());
	}

	@Test
	public void testParseEmptyWhitespace() throws IOException {
		ini = new Ini("      ");
		assertEquals(0, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(0, sections.size());
	}

	@Test
	public void testParseEmptyMultilineWhitespace() throws IOException {
		ini = new Ini("   \n \t\t\n\t\t\n   \t   ");
		assertEquals(0, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(0, sections.size());
	}

	@Test
	public void testParseEmptyComments01() throws IOException {
		ini = new Ini("   \n \t\t\n\t\t\n   \t  # Noodles! ");
		assertEquals(0, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(0, sections.size());
	}

	@Test
	public void testParseEmptyComments02() throws IOException {
		ini = new Ini("# Noodles! \n\n# Banana\n");
		assertEquals(0, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(0, sections.size());
	}

	@Test
	public void testSyntaxErrorNoSection() {
		try {
			ini = new Ini("Noodles! \n\n# Banana\n");
			fail();
		} catch(IOException e) {
			assertEquals("Syntax error on line 1:1: unexpected token before section", e.getMessage());
		}
	}

	@Test
	public void testParseSectionName01() throws IOException {
		ini = new Ini("[test]");
		assertEquals(0, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(0, sections.size());
	}

	@Test
	public void testParseSectionName02() throws IOException {
		ini = new Ini("[test]\n");
		assertEquals(0, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(0, sections.size());
	}

	@Test
	public void testParseSectionName03() throws IOException {
		ini = new Ini("\n[test]\n");
		assertEquals(0, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(0, sections.size());
	}

	@Test
	public void testParseSectionName04() throws IOException {
		ini = new Ini("[test] # comment\n");
		assertEquals(0, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(0, sections.size());
	}

	@Test
	public void testSyntaxErrorUndelimitedSection() {
		try {
			ini = new Ini("[test");
			fail();
		} catch(IOException e) {
			assertEquals("Syntax error on line 1:5: unterminated token", e.getMessage());
		}
	}

	@Test
	public void testSyntaxErrorGarbageAfterSection() {
		try {
			ini = new Ini("[test] test");
			fail();
		} catch(IOException e) {
			assertEquals("Syntax error on line 1:8: unexpected token after section header", e.getMessage());
		}
	}

	@Test
	public void testParseKey01() throws IOException {
		ini = new Ini("[test]\nkey = value\n");
		assertEquals(1, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(1, sections.size());
		assertEquals("test", sections.get(0));
		List<String> keys = ini.getKeys("test");
		assertEquals(1, keys.size());
		assertEquals("key", keys.get(0));
		assertEquals("value", ini.get("test", "key"));
	}

	@Test
	public void testParseKey02() throws IOException {
		ini = new Ini("[test]\nkey=value\n");
		assertEquals(1, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(1, sections.size());
		assertEquals("test", sections.get(0));
		List<String> keys = ini.getKeys("test");
		assertEquals(1, keys.size());
		assertEquals("key", keys.get(0));
		assertEquals("value", ini.get("test", "key"));
	}

	@Test
	public void testParseKey03() throws IOException {
		ini = new Ini("[test]\n# this is a key\nkey=value\n");
		assertEquals(1, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(1, sections.size());
		assertEquals("test", sections.get(0));
		List<String> keys = ini.getKeys("test");
		assertEquals(1, keys.size());
		assertEquals("key", keys.get(0));
		assertEquals("value", ini.get("test", "key"));
	}

	@Test
	public void testParseMultikey01() throws IOException {
		ini = new Ini("[test]\nkey1=value 1\nkey2 = value 2");
		assertEquals(1, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(1, sections.size());
		assertEquals("test", sections.get(0));
		List<String> keys = ini.getKeys("test");
		assertEquals(2, keys.size());
		assertEquals("value 1", ini.get("test", "key1"));
		assertEquals("value 2", ini.get("test", "key2"));
	}

	@Test
	public void testParseMultikey02() throws IOException {
		ini = new Ini("[test]\nkey1=value 1\n\n# key 2 is important!\nkey2 = value 2");
		assertEquals(1, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(1, sections.size());
		assertEquals("test", sections.get(0));
		List<String> keys = ini.getKeys("test");
		assertEquals(2, keys.size());
		assertEquals("value 1", ini.get("test", "key1"));
		assertEquals("value 2", ini.get("test", "key2"));
	}

	@Test
	public void testParseUselessSection() throws IOException {
		ini = new Ini("[test]\nkey1=value 1\n[test]\nkey2 = value 2");
		assertEquals(1, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(1, sections.size());
		assertEquals("test", sections.get(0));
		List<String> keys = ini.getKeys("test");
		assertEquals(2, keys.size());
		assertEquals("value 1", ini.get("test", "key1"));
		assertEquals("value 2", ini.get("test", "key2"));
	}

	@Test
	public void testParseQuotedString01() throws IOException {
		ini = new Ini("[test]\nkey1 = \"value 1\"\n[test]\nkey2 = value 2");
		assertEquals(1, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(1, sections.size());
		assertEquals("test", sections.get(0));
		List<String> keys = ini.getKeys("test");
		assertEquals(2, keys.size());
		assertEquals("value 1", ini.get("test", "key1"));
		assertEquals("value 2", ini.get("test", "key2"));
	}

	@Test
	public void testParseQuotedString02() throws IOException {
		ini = new Ini("[test]\nkey1 = \"value 1\"      \n[test]\nkey2 = value 2");
		assertEquals(1, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(1, sections.size());
		assertEquals("test", sections.get(0));
		List<String> keys = ini.getKeys("test");
		assertEquals(2, keys.size());
		assertEquals("value 1", ini.get("test", "key1"));
		assertEquals("value 2", ini.get("test", "key2"));
	}

	@Test
	public void testParseQuotedString03() throws IOException {
		ini = new Ini("[test]\nkey1 = \"value 1\"   # noodle!   \n[test]\nkey2 = value 2");
		assertEquals(1, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(1, sections.size());
		assertEquals("test", sections.get(0));
		List<String> keys = ini.getKeys("test");
		assertEquals(2, keys.size());
		assertEquals("value 1", ini.get("test", "key1"));
		assertEquals("value 2", ini.get("test", "key2"));
	}

	@Test
	public void testParseTrimmedString01() throws IOException {
		ini = new Ini("[test]\nkey = value 1   ");
		assertEquals(1, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(1, sections.size());
		assertEquals("test", sections.get(0));
		List<String> keys = ini.getKeys("test");
		assertEquals(1, keys.size());
		assertEquals("key", keys.get(0));
		assertEquals("value 1", ini.get("test", "key"));
	}

	@Test
	public void testParseTrimmedString02() throws IOException {
		ini = new Ini("[test]\nkey = value 1   \n");
		assertEquals(1, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(1, sections.size());
		assertEquals("test", sections.get(0));
		List<String> keys = ini.getKeys("test");
		assertEquals(1, keys.size());
		assertEquals("key", keys.get(0));
		assertEquals("value 1", ini.get("test", "key"));
	}

	@Test
	public void testParseTrimmedString03() throws IOException {
		ini = new Ini("[test]\nkey = value 1 # noodle");
		assertEquals(1, ini.getSectionCount());
		List<String> sections = ini.getSections();
		assertEquals(1, sections.size());
		assertEquals("test", sections.get(0));
		List<String> keys = ini.getKeys("test");
		assertEquals(1, keys.size());
		assertEquals("key", keys.get(0));
		assertEquals("value 1 # noodle", ini.get("test", "key"));
	}

	@Test
	public void testToString01() {
		ini.set("sec", "key", "value");
		assertEquals("[sec]\nkey=value\n", ini.toString());
	}
}
