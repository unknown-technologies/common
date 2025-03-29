package com.unknown.xml.dom;

public abstract class Node {
	public final static int TEXT = 0;
	public final static int TAG = 1;
	public final static int PROCESSING_INSTRUCTION = 2;
	public final static int ATTRIBUTE = 3;

	public final int type;

	public Node(int type) {
		this.type = type;
	}

	public static String escape(String s) {
		StringBuilder out = new StringBuilder(s.length());
		for(char c : s.toCharArray()) {
			switch(c) {
			case '<':
				out.append("&lt;");
				break;
			case '>':
				out.append("&gt;");
				break;
			case '&':
				out.append("&amp;");
				break;
			case '"':
				out.append("&quot;");
				break;
			default:
				out.append(c);
				break;
			}
		}
		return out.toString();
	}
}
