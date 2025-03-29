package com.unknown.xml.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Deque;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.unknown.xml.XMLParser;
import com.unknown.xml.dom.Element;
import com.unknown.xml.dom.XMLReader;
import com.unknown.xml.sax.AttributesImpl;

public class XMLParserTest {
	private XMLParser parser;
	private Deque<DataElement> expected;
	private Deque<DataElement> actual;

	private static class DataElement {
		public final static int STARTELEMENT = 0;
		public final static int ENDELEMENT = 1;
		public final static int CHARACTERS = 2;
		public final static int PROCESSINGINSTRUCTION = 3;
		public final static int STARTDOCUMENT = 4;
		public final static int ENDDOCUMENT = 5;
		public final static int STARTPREFIXMAPPING = 6;
		public final static int ENDPREFIXMAPPING = 7;

		public String uri;
		public String localName;
		public String qName;
		public String data;
		public String target;
		public int type;
		public Attributes attributes;

		public DataElement(int type) {
			this.type = type;
		}

		public static DataElement startElement(String uri,
				String localName, String qName, Attributes attrs) {
			DataElement e = new DataElement(STARTELEMENT);
			e.uri = uri;
			e.localName = localName;
			e.qName = qName;
			e.attributes = attrs;
			return e;
		}

		public static DataElement endElement(String uri,
				String localName, String qName) {
			DataElement e = new DataElement(ENDELEMENT);
			e.uri = uri;
			e.localName = localName;
			e.qName = qName;
			return e;
		}

		public static DataElement characters(String s) {
			DataElement e = new DataElement(CHARACTERS);
			e.data = s;
			return e;
		}

		public static DataElement processingInstruction(String name,
				String data) {
			DataElement e = new DataElement(PROCESSINGINSTRUCTION);
			e.target = name;
			e.data = data;
			return e;
		}

		public static DataElement startPrefixMapping(String prefix,
				String uri) {
			DataElement e = new DataElement(STARTPREFIXMAPPING);
			e.target = prefix;
			e.uri = uri;
			return e;
		}

		public static DataElement endPrefixMapping(String prefix) {
			DataElement e = new DataElement(ENDPREFIXMAPPING);
			e.target = prefix;
			return e;
		}
	}

	private static class Attribute {
		public String uri;
		public String localName;
		public String qName;
		public String value;

		public Attribute(String uri, String localName, String qName,
				String value) {
			this.uri = uri;
			this.localName = localName;
			this.qName = qName;
			this.value = value;
		}
	}

	private class Handler extends DefaultHandler {
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			actual.add(DataElement.characters(new String(ch, start,
					length)));
		}

		@Override
		public void endDocument() throws SAXException {
			actual.add(new DataElement(DataElement.ENDDOCUMENT));
		}

		@Override
		public void endElement(String uri, String localName,
				String qName) throws SAXException {
			actual.add(DataElement
					.endElement(uri, localName, qName));
		}

		@Override
		public void endPrefixMapping(String prefix) throws SAXException {
			actual.add(DataElement.endPrefixMapping(prefix));
		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length)
				throws SAXException {
		}

		@Override
		public void processingInstruction(String target, String data)
				throws SAXException {
			actual.add(DataElement.processingInstruction(target,
					data));
		}

		@Override
		public void setDocumentLocator(Locator locator) {
		}

		@Override
		public void skippedEntity(String name) throws SAXException {
		}

		@Override
		public void startDocument() throws SAXException {
			actual.add(new DataElement(DataElement.STARTDOCUMENT));
		}

		@Override
		public void startElement(String uri, String localName,
				String qName, Attributes atts)
				throws SAXException {
			actual.add(DataElement.startElement(uri, localName,
					qName, atts));
		}

		@Override
		public void startPrefixMapping(String prefix, String uri)
				throws SAXException {
			actual.add(DataElement.startPrefixMapping(prefix, uri));
		}
	}

	@Before
	public void setup() {
		parser = new XMLParser();
		expected = new LinkedList<>();
		actual = new LinkedList<>();
		parser.setContentHandler(new Handler());
		expected.add(new DataElement(DataElement.STARTDOCUMENT));
	}

	private void startElement(String uri, String localName, String qName) {
		startElement(uri, localName, qName, new Attribute[] {});
	}

	private void startElement(String uri, String localName, String qName,
			Attribute[] attributes) {
		AttributesImpl atts = new AttributesImpl();
		for(Attribute a : attributes) {
			atts.add(a.uri, a.localName, a.qName, a.value);
		}
		expected.add(DataElement.startElement(uri, localName, qName,
				atts));
	}

	private void endElement(String uri, String localName, String qName) {
		expected.add(DataElement.endElement(uri, localName, qName));
	}

	private void characters(String s) {
		expected.add(DataElement.characters(s));
	}

	private void processingInstruction(String target, String data) {
		expected.add(DataElement.processingInstruction(target, data));
	}

	private void startPrefixMapping(String prefix, String uri) {
		expected.add(DataElement.startPrefixMapping(prefix, uri));
	}

	private void endPrefixMapping(String prefix) {
		expected.add(DataElement.endPrefixMapping(prefix));
	}

	private void run(String xml) throws ParseException {
		expected.add(new DataElement(DataElement.ENDDOCUMENT));
		parser.start();
		parser.process(xml.getBytes());
		parser.end();
		while(!expected.isEmpty()) {
			DataElement e = expected.removeFirst();
			DataElement a = actual.removeFirst();
			assertNotNull(a);
			assertEquals(e.type, a.type);
			if(e.type == DataElement.CHARACTERS) {
				assertEquals(e.data, a.data);
			} else if(e.type == DataElement.STARTELEMENT) {
				assertEquals(e.uri, a.uri);
				assertEquals(e.localName, a.localName);
				assertEquals(e.qName, a.qName);
				assertEquals(e.attributes, a.attributes);
			} else if(e.type == DataElement.ENDELEMENT) {
				assertEquals(e.uri, a.uri);
				assertEquals(e.localName, a.localName);
				assertEquals(e.qName, a.qName);
			} else if(e.type == DataElement.PROCESSINGINSTRUCTION) {
				assertEquals(e.target, a.target);
				assertEquals(e.data, a.data);
			} else if(e.type == DataElement.STARTPREFIXMAPPING) {
				assertEquals(e.target, a.target);
				assertEquals(e.uri, a.uri);
			} else if(e.type == DataElement.ENDPREFIXMAPPING) {
				assertEquals(e.target, a.target);
			}
		}
	}

	@Test
	public void simple1() throws ParseException {
		startElement("", "root", "root");
		endElement("", "root", "root");
		String xml = "<?xml version=\"1.0\"?><root/>";
		run(xml);
	}

	@Test
	public void simple2() throws ParseException {
		startElement("", "root", "root");
		characters("xyz");
		endElement("", "root", "root");
		String xml = "<?xml version=\"1.0\"?><root>xyz</root>";
		run(xml);
	}

	@Test
	public void simple3() throws ParseException {
		startElement("", "root", "root", new Attribute[] {
				new Attribute("", "a", "a", "b") });
		characters("xyz");
		endElement("", "root", "root");
		String xml = "<?xml version=\"1.0\"?><root a='b'>xyz</root>";
		run(xml);
	}

	@Test
	public void comment1() throws ParseException {
		startElement("", "root", "root", new Attribute[] {
				new Attribute("", "a", "a", "b"),
				new Attribute("", "attribute", "attribute",
						"value with 'quotes'") });
		characters("xz");
		endElement("", "root", "root");
		String xml = "<?xml version=\"1.0\"?><root a='b' attribute=\"value with 'quotes'\">x<!-- y -->z</root>";
		run(xml);
	}

	@Test
	public void ns1() throws ParseException {
		startElement("", "root", "root", new Attribute[] {
				new Attribute("", "a", "a", "b"),
				new Attribute("", "attribute", "attribute",
						"value with 'quotes'") });
		characters("xyz");
		startPrefixMapping("", "http://everyware");
		startElement("http://everyware", "node", "node");
		endElement("http://everyware", "node", "node");
		endPrefixMapping("");
		endElement("", "root", "root");
		String xml = "<?xml version=\"1.0\"?><root a='b' attribute=\"value with 'quotes'\">xyz<node xmlns=\"http://everyware\"/></root>";
		run(xml);
	}

	@Test
	public void ns2() throws ParseException {
		startElement("", "root", "root", new Attribute[] {
				new Attribute("", "a", "a", "b"),
				new Attribute("", "attribute", "attribute",
						"value with 'quotes'") });
		characters("xyz");
		startPrefixMapping("", "http://everyware");
		startPrefixMapping("ns", "http://everyware/ns");
		startElement("http://everyware", "node", "node");
		startElement("http://everyware/ns", "namespace", "ns:namespace");
		characters("the namespace!");
		endElement("http://everyware/ns", "namespace", "ns:namespace");
		endElement("http://everyware", "node", "node");
		endPrefixMapping("ns");
		endPrefixMapping("");
		endElement("", "root", "root");
		String xml = "<?xml version=\"1.0\"?><root a='b' attribute=\"value with 'quotes'\">xyz" +
				"<node xmlns=\"http://everyware\" xmlns:ns=\"http://everyware/ns\"><ns:namespace>the namespace!</ns:namespace></node>" +
				"</root>";
		run(xml);
	}

	@Test
	public void ns3() throws ParseException {
		startElement("", "root", "root", new Attribute[] {
				new Attribute("", "a", "a", "b"),
				new Attribute("", "attribute", "attribute",
						"value with 'quotes'") });
		characters("xyz");
		startPrefixMapping("", "http://everyware");
		startPrefixMapping("ns", "http://everyware/ns");
		startElement("http://everyware", "node", "node",
				new Attribute[] {
						new Attribute("http://everyware", "attr",
								"attr", "val") });
		startElement("http://everyware/ns",
				"namespace",
				"ns:namespace",
				new Attribute[] {
						new Attribute(
								"http://everyware",
								"normal",
								"normal",
								"regular"),
						new Attribute(
								"http://everyware/ns",
								"prefixed",
								"ns:prefixed",
								"namespaced") });
		characters("the namespace!");
		endElement("http://everyware/ns", "namespace", "ns:namespace");
		endElement("http://everyware", "node", "node");
		endPrefixMapping("ns");
		endPrefixMapping("");
		endElement("", "root", "root");
		String xml = "<?xml version=\"1.0\"?><root a='b' attribute=\"value with 'quotes'\">xyz" +
				"<node xmlns=\"http://everyware\" xmlns:ns=\"http://everyware/ns\" attr=\"val\">" +
				"<ns:namespace normal='regular' ns:prefixed='namespaced'>the namespace!</ns:namespace>" +
				"</node>" + "</root>";
		run(xml);
	}

	@Test
	public void pi1() throws ParseException {
		processingInstruction("pi",
				"this is a \"processing\" instruction");
		startElement("", "root", "root");
		endElement("", "root", "root");
		String xml = "<?xml version=\"1.0\"?><?pi this is a \"processing\" instruction?><root/>";
		run(xml);
	}

	@Test
	public void closingSlash() throws IOException {
		String xml = "<?xml version=\"1.0\"?><layer name=\"fg\">AD8APwA/AD8APwA/AD8APwA/AD8APwA/AD8APwA/AD8APwA" +
				"/AD8APwAgABgAAQAMACcAMQAYAAEADAAnADEAGAABAAwAJwAxABgAAQA9AD8AMgBPAE8ATwBPAE8ATwBPAE8" +
				"ATwBPAE8ATwBPAE8ATwBPAE8AJgA/AAIATwBPAE8ATwBPAE8ATwBPAE8ATwBPAE8ATwBPAE8ATwBPACgAPwA" +
				"DAE8ATwBPAE8ATwBPAE8ATwBPAE8ATwBPAE8ATwBPAE8ATwApAD8AAABPAE8ASQBMAEYATwBPAE8ATwBPAE8" +
				"ATwBPAE8ATwBPAE8ARAAMAAQATwBPAEoATQBHAE8ATwBPAE8ATwBPAE8ATwBPAE8ATwBPAE8ATwAzAE8ATwB" +
				"LAE4ASABPAE8ATwBPAE8ATwBPAE8ATwBPAE8ATwBPAE8AIQAwABkABQANACUAHABAAE8ATwBPAE8ATwBPAE8" +
				"ATwBPAE8ATwBPAD8APwA/AD8APwA/AD8AMgBPAE8ATwBPAE8ATwBPAE8ATwBPAE8ATwA/AD8APwA/AD8APwA" +
				"/ADoAOwBCADwAQwA8AEMAOwBCAEUADQAlABkAIAAdABgAAQAMACcAMQBBAE8ATwBPAE8ATwBPAE8ATwApAD8" +
				"APwA/AAAATwBPAE8ATwBPAE8ATwBPAE8ATwBPAEUAGQAFADAAIQA/AD8APwAEAE8ATwBPAE8ATwBPAE8ATwB" +
				"PAE8ATwAqAD8APwA/AD8APwA/AD8AIQAwAA0AJQAZAAUAHAANACUAGQAFADAAPgA/AD8APwA/AD8APwA/" +
				"</layer>";
		String refval = "AD8APwA/AD8APwA/AD8APwA/AD8APwA/AD8APwA/AD8APwA/AD8APwAgABgAAQAMACcAMQAYAAEADAAnADEAGA" +
				"ABAAwAJwAxABgAAQA9AD8AMgBPAE8ATwBPAE8ATwBPAE8ATwBPAE8ATwBPAE8ATwBPAE8AJgA/AAIATwBPAE" +
				"8ATwBPAE8ATwBPAE8ATwBPAE8ATwBPAE8ATwBPACgAPwADAE8ATwBPAE8ATwBPAE8ATwBPAE8ATwBPAE8ATw" +
				"BPAE8ATwApAD8AAABPAE8ASQBMAEYATwBPAE8ATwBPAE8ATwBPAE8ATwBPAE8ARAAMAAQATwBPAEoATQBHAE" +
				"8ATwBPAE8ATwBPAE8ATwBPAE8ATwBPAE8ATwAzAE8ATwBLAE4ASABPAE8ATwBPAE8ATwBPAE8ATwBPAE8ATw" +
				"BPAE8AIQAwABkABQANACUAHABAAE8ATwBPAE8ATwBPAE8ATwBPAE8ATwBPAD8APwA/AD8APwA/AD8AMgBPAE" +
				"8ATwBPAE8ATwBPAE8ATwBPAE8ATwA/AD8APwA/AD8APwA/ADoAOwBCADwAQwA8AEMAOwBCAEUADQAlABkAIA" +
				"AdABgAAQAMACcAMQBBAE8ATwBPAE8ATwBPAE8ATwApAD8APwA/AAAATwBPAE8ATwBPAE8ATwBPAE8ATwBPAE" +
				"UAGQAFADAAIQA/AD8APwAEAE8ATwBPAE8ATwBPAE8ATwBPAE8ATwAqAD8APwA/AD8APwA/AD8AIQAwAA0AJQ" +
				"AZAAUAHAANACUAGQAFADAAPgA/AD8APwA/AD8APwA/";
		Element root = XMLReader.read(new ByteArrayInputStream(xml.getBytes()));
		assertEquals(refval, root.value);
	}

	@Test
	public void bug1() throws ParseException {
		String xml = "<?xml version=\"1.0\"?><root>" +
				"<audio character=\"pit\" transcript=\"Hm? What's this?\" file=\"ch1/ground/021-whats-this.wav\"/>" +
				"<audio character=\"palutena\" transcript=\"An Intensity gate.\" file=\"ch1/ground/021-an-intensity-gate.wav\"/>" +
				"</root>";
		startElement("", "root", "root");
		startElement("", "audio", "audio", new Attribute[] {
				new Attribute("", "character", "character", "pit"),
				new Attribute("", "transcript", "transcript", "Hm? What's this?"),
				new Attribute("", "file", "file", "ch1/ground/021-whats-this.wav"),
		});
		endElement("", "audio", "audio");
		startElement("", "audio", "audio", new Attribute[] {
				new Attribute("", "character", "character", "palutena"),
				new Attribute("", "transcript", "transcript", "An Intensity gate."),
				new Attribute("", "file", "file", "ch1/ground/021-an-intensity-gate.wav"),
		});
		endElement("", "audio", "audio");
		endElement("", "root", "root");
		run(xml);
	}

	@Test
	public void bug2() throws ParseException {
		String xml = "<?xml version='1.0'?>\n" +
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
				"                </paper>\n";
		startElement("", "paper", "paper", new Attribute[] {
				new Attribute("", "doi", "doi", ""),
				new Attribute("", "id", "id", "072d8ad0-882b-45ff-8043-2bb9170965b9"),
				new Attribute("", "pubyear", "pubyear", "2007"),
				new Attribute("", "title", "title", "Dynamic spyware analysis"),
				new Attribute("", "url", "url",
						"https://www.usenix.org/legacy/events/usenix07/tech/full_papers/egele/egele.pdf"),
		});
		startElement("", "bibtex", "bibtex");
		characters("@inproceedings{Egele:2007:DSA:1364385.1364403,\n" +
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
				"}");
		endElement("", "bibtex", "bibtex");
		startElement("", "abstract", "abstract");
		characters("Spyware is a class of malicious code that is surreptitiously installed on victims' machines. Once active, it silently monitors the behavior of users, records their web surfing habits, and steals their passwords. Current anti-spyware tools operate in a way similar to traditional virus scanners. That is, they check unknown programs against signatures associated with known spyware instances. Unfortunately, these techniques cannot identify novel spyware, require frequent updates to signature databases, and are easy to evade by code obfuscation.\n" +
				"\n" +
				"In this paper, we present a novel dynamic analysis approach that precisely tracks the flow of sensitive information as it is processed by the web browser and any loaded browser helper objects. Using the results of our analysis, we can identify unknown components as spyware and provide comprehensive reports on their behavior. The techniques presented in this paper address limitations of our previouswork on spyware detection and significantly improve the quality and richness of our analysis. In particular, our approach allows a human analyst to observe the actual flows of sensitive data in the system. Based on this information, it is possible to precisely determine which sensitive data is accessed and where this data is sent to. To demonstrate the effectiveness of the detection and the comprehensiveness of the generated reports, we evaluated our system on a substantial body of spyware and benign samples.");
		endElement("", "abstract", "abstract");
		endElement("", "paper", "paper");
		run(xml);
	}
}
