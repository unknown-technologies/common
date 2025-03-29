package com.unknown.xml.sax;

public class Attribute {
	public String uri;
	public String localName;
	public String qName;
	public String value;

	public Attribute(String uri, String localName, String qName, String value) {
		this.uri = uri;
		this.localName = localName;
		this.qName = qName;
		this.value = value;
	}

	@Override
	public String toString() {
		return "Attribute[" + qName + ";uri=" + uri + ";value=" + value + "]";
	}
}
