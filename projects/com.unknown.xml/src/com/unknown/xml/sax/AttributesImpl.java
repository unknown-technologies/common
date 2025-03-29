package com.unknown.xml.sax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;

public class AttributesImpl implements Attributes {
	private List<Attribute> attributes = new ArrayList<>();
	private Map<String, Integer> indicesQName = new HashMap<>();
	private Map<String, Integer> indicesURI = new HashMap<>();

	public void add(String uri, String localName, String qName, String value) {
		indicesQName.put(qName, attributes.size());
		indicesURI.put(uri + "@" + localName, attributes.size());
		attributes.add(new Attribute(uri, localName, qName, value));
	}

	@Override
	public int getIndex(String qName) {
		if(indicesQName.containsKey(qName)) {
			return indicesQName.get(qName);
		}
		return -1;
	}

	@Override
	public int getIndex(String uri, String localName) {
		String key = uri + "@" + localName;
		if(indicesURI.containsKey(key)) {
			return indicesURI.get(key);
		}
		return -1;
	}

	@Override
	public int getLength() {
		return attributes.size();
	}

	@Override
	public String getLocalName(int index) {
		if(index < 0 || index > attributes.size()) {
			return null;
		}
		return attributes.get(index).localName;
	}

	@Override
	public String getQName(int index) {
		if(index < 0 || index > attributes.size()) {
			return null;
		}
		return attributes.get(index).qName;
	}

	@Override
	public String getType(int index) {
		if(index < 0 || index > attributes.size()) {
			return null;
		}
		return "CDATA";
	}

	@Override
	public String getType(String qName) {
		int idx = getIndex(qName);
		if(idx == -1) {
			return null;
		} else {
			return getType(idx);
		}
	}

	@Override
	public String getType(String uri, String localName) {
		int idx = getIndex(uri, localName);
		if(idx == -1) {
			return null;
		} else {
			return getType(idx);
		}
	}

	@Override
	public String getURI(int index) {
		if(index < 0 || index > attributes.size()) {
			return null;
		}
		return attributes.get(index).uri;
	}

	@Override
	public String getValue(int index) {
		if(index < 0 || index > attributes.size()) {
			return null;
		}
		return attributes.get(index).value;
	}

	@Override
	public String getValue(String qName) {
		int idx = getIndex(qName);
		if(idx == -1) {
			return null;
		} else {
			return getValue(idx);
		}
	}

	@Override
	public String getValue(String uri, String localName) {
		int idx = getIndex(uri, localName);
		if(idx == -1) {
			return null;
		} else {
			return getValue(idx);
		}
	}

	@Override
	public int hashCode() {
		// TODO: find better function
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Attributes)) {
			return false;
		}
		Attributes a = (Attributes) o;
		int len = a.getLength();
		if(len != getLength()) {
			return false;
		}
		for(int i = 0; i < len; i++) {
			if(!a.getValue(i).equals(getValue(i))) {
				return false;
			}
			if(!a.getLocalName(i).equals(getLocalName(i))) {
				return false;
			}
			if(!a.getQName(i).equals(getQName(i))) {
				return false;
			}
			if(!a.getType(i).equals(getType(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder("Attributes[");
		boolean first = true;
		for(int i = 0; i < attributes.size(); i++) {
			if(first) {
				first = false;
			} else {
				buf.append(',');
			}
			buf.append(attributes.get(i));
		}
		return buf.append("]").toString();
	}
}
