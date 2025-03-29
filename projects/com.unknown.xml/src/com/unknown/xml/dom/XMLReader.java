package com.unknown.xml.dom;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import com.unknown.xml.XMLParser;

public class XMLReader {
	public static Element read(InputStream in) throws IOException {
		try {
			Collector collector = new Collector();
			XMLParser parser = new XMLParser(collector);
			byte[] buf = new byte[256];
			int n;
			parser.start();
			while((n = in.read(buf)) != -1) {
				parser.process(buf, 0, n);
			}
			parser.end();
			return collector.getRoot();
		} catch(ParseException e) {
			throw new IOException("Failed to parse input: " + e.getMessage(), e);
		}
	}

	public static Element read(String s) throws ParseException {
		Collector collector = new Collector();
		XMLParser parser = new XMLParser(collector);
		parser.start();
		parser.process(s);
		parser.end();
		return collector.getRoot();
	}

	public static Element read(String s, boolean trim) throws ParseException {
		Collector collector = new Collector();
		XMLParser parser = new XMLParser(collector);
		parser.setTrim(trim);
		parser.start();
		parser.process(s);
		parser.end();
		return collector.getRoot();
	}
}
