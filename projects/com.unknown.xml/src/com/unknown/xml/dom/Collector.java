package com.unknown.xml.dom;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class Collector implements ContentHandler {
	private Element root;
	private Element element;
	private Stack<Element> elements;

	public Collector() {
		elements = new Stack<>();
	}

	public Element get() {
		return elements.get(0);
	}

	public Element getRoot() {
		return root;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		element.addChild(new Text(new String(ch, start, length)));
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		element = elements.pop();
		element.compress();
		if(!elements.isEmpty()) {
			elements.peek().addChild(element);
			element = elements.peek();
		}
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	@Override
	public void setDocumentLocator(Locator locator) {
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
	}

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		element = new Element(uri, localName, qName);
		int cnt = atts.getLength();
		for(int i = 0; i < cnt; i++) {
			String auri = atts.getURI(i);
			String aname = atts.getLocalName(i);
			String aqname = atts.getQName(i);
			String avalue = atts.getValue(i);
			element.addAttribute(new Attribute(auri, aname, aqname,
					avalue));
		}
		elements.push(element);
		if(root == null) {
			root = element;
		}
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}
}
