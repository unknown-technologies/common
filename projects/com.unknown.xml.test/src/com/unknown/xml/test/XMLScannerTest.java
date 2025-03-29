package com.unknown.xml.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Deque;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import com.unknown.xml.XMLScanner;
import com.unknown.xml.XMLToken;
import com.unknown.xml.XMLTokenType;

public class XMLScannerTest {
	private XMLScanner scanner;
	private Deque<XMLToken> expected;

	@Before
	public void setup() {
		scanner = new XMLScanner();
		expected = new LinkedList<>();
	}

	private void token(XMLTokenType type) {
		expected.add(new XMLToken(type, 0));
	}

	private void token(XMLTokenType type, String val) {
		XMLToken t = new XMLToken(type, 0);
		t.val = val;
		expected.add(t);
	}

	private void run(String xml) {
		scanner.process(xml.getBytes());
		while(!expected.isEmpty()) {
			XMLToken e = expected.removeFirst();
			XMLToken a = scanner.scan();
			assertNotNull(a);
			assertEquals(e.type, a.type);
			assertEquals(e.val, a.val);
		}
		assertNull(scanner.scan());
	}

	@Test
	public void simple1() {
		token(XMLTokenType.piopen);
		token(XMLTokenType.text, "xml");
		token(XMLTokenType.space, " ");
		token(XMLTokenType.text, "version");
		token(XMLTokenType.equal);
		token(XMLTokenType.quote);
		token(XMLTokenType.text, "1.0");
		token(XMLTokenType.quote);
		token(XMLTokenType.piclose);
		token(XMLTokenType.topen);
		token(XMLTokenType.text, "xml");
		token(XMLTokenType.tcloses);
		String xml = "<?xml version=\"1.0\"?><xml/>";
		run(xml);
	}

	@Test
	public void simple2() {
		token(XMLTokenType.piopen);
		token(XMLTokenType.text, "xml");
		token(XMLTokenType.space, " ");
		token(XMLTokenType.text, "version");
		token(XMLTokenType.equal);
		token(XMLTokenType.quote);
		token(XMLTokenType.text, "1.0");
		token(XMLTokenType.quote);
		token(XMLTokenType.piclose);
		token(XMLTokenType.topen);
		token(XMLTokenType.text, "xml");
		token(XMLTokenType.tclose);
		token(XMLTokenType.text, "xyz");
		token(XMLTokenType.tsopen);
		token(XMLTokenType.text, "xml");
		token(XMLTokenType.tclose);
		String xml = "<?xml version=\"1.0\"?><xml>xyz</xml>";
		run(xml);
	}

	@Test
	public void simple3() {
		token(XMLTokenType.piopen);
		token(XMLTokenType.text, "xml");
		token(XMLTokenType.space, " ");
		token(XMLTokenType.text, "version");
		token(XMLTokenType.equal);
		token(XMLTokenType.quote);
		token(XMLTokenType.text, "1.0");
		token(XMLTokenType.quote);
		token(XMLTokenType.piclose);
		token(XMLTokenType.topen);
		token(XMLTokenType.text, "xml");
		token(XMLTokenType.space, " ");
		token(XMLTokenType.text, "a");
		token(XMLTokenType.equal);
		token(XMLTokenType.squote);
		token(XMLTokenType.text, "b");
		token(XMLTokenType.squote);
		token(XMLTokenType.tclose);
		token(XMLTokenType.text, "xyz");
		token(XMLTokenType.tsopen);
		token(XMLTokenType.text, "xml");
		token(XMLTokenType.tclose);
		String xml = "<?xml version=\"1.0\"?><xml a='b'>xyz</xml>";
		run(xml);
	}

	@Test
	public void comment1() {
		token(XMLTokenType.piopen);
		token(XMLTokenType.text, "xml");
		token(XMLTokenType.space, " ");
		token(XMLTokenType.text, "version");
		token(XMLTokenType.equal);
		token(XMLTokenType.quote);
		token(XMLTokenType.text, "1.0");
		token(XMLTokenType.quote);
		token(XMLTokenType.piclose);
		token(XMLTokenType.topen);
		token(XMLTokenType.text, "xml");
		token(XMLTokenType.space, " ");
		token(XMLTokenType.text, "a");
		token(XMLTokenType.equal);
		token(XMLTokenType.squote);
		token(XMLTokenType.text, "b");
		token(XMLTokenType.squote);
		token(XMLTokenType.tclose);
		token(XMLTokenType.text, "xz");
		token(XMLTokenType.tsopen);
		token(XMLTokenType.text, "xml");
		token(XMLTokenType.tclose);
		String xml = "<?xml version=\"1.0\"?><xml a='b'>x<!-- y -->z</xml>";
		run(xml);
	}

	@Test
	public void cdata1() {
		token(XMLTokenType.piopen);
		token(XMLTokenType.text, "xml");
		token(XMLTokenType.space, " ");
		token(XMLTokenType.text, "version");
		token(XMLTokenType.equal);
		token(XMLTokenType.quote);
		token(XMLTokenType.text, "1.0");
		token(XMLTokenType.quote);
		token(XMLTokenType.piclose);
		token(XMLTokenType.topen);
		token(XMLTokenType.text, "xml");
		token(XMLTokenType.tclose);
		token(XMLTokenType.text, "xyz");
		token(XMLTokenType.tsopen);
		token(XMLTokenType.text, "xml");
		token(XMLTokenType.tclose);
		String xml = "<?xml version=\"1.0\"?><xml><![CDATA[xyz]]></xml>";
		run(xml);
	}
}
