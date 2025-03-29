package com.unknown.xml.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import com.unknown.xml.XMLParser;
import com.unknown.xml.dom.Attribute;
import com.unknown.xml.dom.Collector;
import com.unknown.xml.dom.Element;
import com.unknown.xml.dom.Text;

public class CollectorTest {
	private XMLParser parser;
	private Collector collector;

	@Before
	public void setup() {
		parser = new XMLParser();
		collector = new Collector();
		parser.setContentHandler(collector);
	}

	private Element run(String xml) throws ParseException {
		parser.start();
		parser.process(xml.getBytes());
		parser.end();
		return collector.getRoot();
	}

	@Test
	public void test1() throws ParseException {
		Element root = run("<?xml version='1.0'?><root/>");
		assertEquals("root", root.name);
		assertEquals(0, root.getAttributes().length);
		assertEquals(0, root.getChildren().length);
	}

	@Test
	public void test2() throws ParseException {
		Element root = run("<?xml version='1.0'?><root><node/></root>");
		assertEquals("root", root.name);
		assertEquals(0, root.getAttributes().length);
		Element[] children = root.getChildren();
		assertEquals(1, children.length);
		assertEquals("node", children[0].name);
		assertEquals(0, children[0].getAttributes().length);
		assertEquals(0, children[0].getChildren().length);
	}

	@Test
	public void test3() throws ParseException {
		Element root = run("<?xml version='1.0'?><root attr='val'><node/></root>");
		assertEquals("root", root.name);
		Attribute[] attributes = root.getAttributes();
		assertEquals(1, attributes.length);
		assertEquals("attr", attributes[0].name);
		assertEquals("val", attributes[0].value);
		Element[] children = root.getChildren();
		assertEquals(1, children.length);
		assertEquals("node", children[0].name);
		assertEquals(0, children[0].getAttributes().length);
		assertEquals(0, children[0].getChildren().length);
	}

	@Test
	public void test4() throws ParseException {
		Element root = run("<?xml version='1.0'?><root><node attr='val'/></root>");
		assertEquals("root", root.name);
		assertEquals(0, root.getAttributes().length);
		Element[] children = root.getChildren();
		assertEquals(1, children.length);
		assertEquals("node", children[0].name);
		Attribute[] attributes = children[0].getAttributes();
		assertEquals(1, attributes.length);
		assertEquals("attr", attributes[0].name);
		assertEquals("val", attributes[0].value);
		assertEquals(0, children[0].getChildren().length);
	}

	@Test
	public void text1() throws ParseException {
		Element root = run("<?xml version='1.0'?><root>text</root>");
		assertEquals("root", root.name);
		assertEquals(0, root.getAttributes().length);
		assertEquals(0, root.getChildren().length);
		assertEquals("text", root.value);
	}

	@Test
	public void text2() throws ParseException {
		Element root = run("<?xml version='1.0'?><root>text with <i>elements</i> somewhere</root>");
		assertEquals("root", root.name);
		assertEquals(null, root.value);
		Element[] children = root.getChildren();
		assertEquals(0, root.getAttributes().length);
		assertEquals(3, children.length);
		assertTrue(children[0] instanceof Text);
		assertFalse(children[1] instanceof Text);
		assertTrue(children[2] instanceof Text);
		assertEquals("text with ", children[0].value);
		assertEquals("i", children[1].name);
		assertEquals("elements", children[1].value);
		assertEquals(" somewhere", children[2].value);
	}

	@Test
	public void text3() throws ParseException {
		Element root = run("<?xml version='1.0'?><root>text with &quot;quotes&quot;</root>");
		assertEquals("root", root.name);
		assertEquals(0, root.getAttributes().length);
		assertEquals(0, root.getChildren().length);
		assertEquals("text with \"quotes\"", root.value);
	}

	@Test
	public void attribute1() throws ParseException {
		Element root = run("<?xml version='1.0'?><root attr=\"text with &quot;quotes&quot;\"/>");
		assertEquals("root", root.name);
		assertEquals(1, root.getAttributes().length);
		assertEquals(0, root.getChildren().length);
		assertNull(root.value);
		assertEquals("text with \"quotes\"", root.getAttribute("attr"));
	}

	@Test
	public void testEverything1() throws ParseException {
		Element root = run("<?xml version='1.0'?>\n" +
				"                <paper doi=\"\" id=\"072d8ad0-882b-45ff-8043-2bb9170965b9\" pubyear=\"2007\" title=\"Dynamic spyware analysis\" url=\"https://www.usenix.org/legacy/events/usenix07/tech/full_papers/egele/egele.pdf\">\n" +
				"                        <bibtex>@inproceedings{Egele:2007:DSA:1364385.1364403,\n" +
				" author = {Egele, Manuel and Kruegel, Christopher and Kirda, Engin and Yin, Heng and Song, Dawn},\n" +
				" title = {Dynamic Spyware Analysis},\n" +
				" booktitle = {2007 USENIX Annual Technical Conference on Proceedings of the USENIX Annual Technical Conference},\n" +
				" series = {ATC'07},\n" +
				" year = {2007},\n" +
				" isbn = {999-8888-77-6},\n" +
				" location = {Santa Clara, CA},\n" +
				" pages = {18:1--18:14},\n" +
				" articleno = {18},\n" +
				" numpages = {14},\n" +
				" url = {http://dl.acm.org/citation.cfm?id=1364385.1364403},\n" +
				" acmid = {1364403},\n" +
				" publisher = {USENIX Association},\n" +
				" address = {Berkeley, CA, USA},\n" +
				"}</bibtex>\n" +
				"                        <abstract>Spyware is a class of malicious code that is surreptitiously installed on victims' machines. Once active, it silently monitors the behavior of users, records their web surfing habits, and steals their passwords. Current anti-spyware tools operate in a way similar to traditional virus scanners. That is, they check unknown programs against signatures associated with known spyware instances. Unfortunately, these techniques cannot identify novel spyware, require frequent updates to signature databases, and are easy to evade by code obfuscation.\n" +
				"\n" +
				"In this paper, we present a novel dynamic analysis approach that precisely tracks the flow of sensitive information as it is processed by the web browser and any loaded browser helper objects. Using the results of our analysis, we can identify unknown components as spyware and provide comprehensive reports on their behavior. The techniques presented in this paper address limitations of our previouswork on spyware detection and significantly improve the quality and richness of our analysis. In particular, our approach allows a human analyst to observe the actual flows of sensitive data in the system. Based on this information, it is possible to precisely determine which sensitive data is accessed and where this data is sent to. To demonstrate the effectiveness of the detection and the comprehensiveness of the generated reports, we evaluated our system on a substantial body of spyware and benign samples.</abstract>\n" +
				"                </paper>\n");
		assertEquals("paper", root.name);
		assertEquals(5, root.getAttributes().length);
		assertEquals(2, root.getChildren().length);
	}
}
