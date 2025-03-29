package com.unknown.xml;

public class XMLToken {
	public final XMLTokenType type;
	public String val;
	public final int pos;

	public XMLToken(XMLTokenType type, int pos) {
		this.type = type;
		this.val = null;
		this.pos = pos;
	}

	public XMLToken(XMLTokenType type, String val, int pos) {
		this.type = type;
		this.val = val;
		this.pos = pos;
	}

	public int getPosition() {
		return pos;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("XMLToken[" + type);
		if(type == XMLTokenType.text || type == XMLTokenType.copen || type == XMLTokenType.piopen ||
				type == XMLTokenType.ent) {
			b.append(':').append(val);
		}
		return b.append("]").toString();
	}
}
