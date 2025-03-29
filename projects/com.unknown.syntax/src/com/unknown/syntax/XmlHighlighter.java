package com.unknown.syntax;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.StringTokenizer;

public class XmlHighlighter extends Highlighter {
	private enum State {
		NORMAL, TAG, CDATA, COMMENT
	}

	private enum AttributeState {
		NAME, VALUE, STRING, ALTSTRING, SPACE
	}

	private State state = State.NORMAL;
	private StringBuffer result = new StringBuffer();
	private StringBuffer tmp = new StringBuffer();

	public XmlHighlighter() {
		super("\r\n\t ", null);
	}

	private static int findWhiteSpace(String s) {
		StringTokenizer st = new StringTokenizer(s, "\r\n\t ");
		String t = st.nextToken();
		if(s.equals(t)) {
			return -1;
		} else if(s.startsWith(t)) {
			if(t.length() == 0) {
				return -1;
			} else {
				return t.length();
			}
		} else {
			return -1;
		}
	}

	private String formatTag(String tag) {
		if(tag.length() == 0) {
			return new String();
		}
		if(tag.startsWith("!--") && tag.endsWith("--")) { // comment
			return new StringBuffer("<span class=\"")
					.append(CSS_PREFIX)
					.append("comment\">")
					.append(htmlspecialchars("<" + tag
							+ ">"))
					.append("</span>").toString();
		} else {
			int end = findWhiteSpace(tag);
			if(end == -1) {
				return new StringBuffer("<span class=\"")
						.append(CSS_PREFIX)
						.append("keyword\">")
						.append(htmlspecialchars("<"
								+ tag + ">"))
						.append("</span>").toString();
			} else {
				String tagName = tag.substring(0, end);
				String attributes = tag.substring(end,
						tag.length());
				return new StringBuffer("<span class=\"")
						.append(CSS_PREFIX)
						.append("keyword\">")
						.append(htmlspecialchars("<"
								+ tagName))
						.append("</span>")
						.append(formatAttributes(attributes))
						.append("<span class=\"")
						.append(CSS_PREFIX)
						.append("keyword\">&gt;</span>")
						.toString();
			}
		}
	}

	private String formatAttributes(String tag) {
		StringBuffer buf = new StringBuffer();
		int i = 0;
		char ch;
		int length = tag.length();
		AttributeState attrState = AttributeState.SPACE;
		while(i < length) {
			ch = tag.charAt(i++);
			switch(attrState) {
			case SPACE:
				switch(ch) {
				case '\r':
				case '\n':
				case '\t':
				case ' ':
					buf.append(ch);
					break;
				default:
					buf.append("<span class=\"");
					buf.append(CSS_PREFIX);
					buf.append("class\">");
					buf.append(ch);
					attrState = AttributeState.NAME;
				}
				break;
			case NAME:
				if(ch == '=') {
					buf.append("</span><span class=\"");
					buf.append(CSS_PREFIX);
					buf.append("operator\">");
					buf.append(ch);
					buf.append("</span><span class=\"");
					buf.append(CSS_PREFIX);
					buf.append("string\">");
					attrState = AttributeState.VALUE;
				} else if(isDelimiter(ch)) {
					buf.append("</span>");
					attrState = AttributeState.SPACE;
				} else {
					buf.append(htmlspecialchars(ch));
				}
				break;
			case VALUE:
				buf.append(ch);
				if(ch == '"') {
					attrState = AttributeState.STRING;
				} else if(ch == '\'') {
					attrState = AttributeState.ALTSTRING;
				} else if(isDelimiter(ch)) {
					attrState = AttributeState.SPACE;
					buf.append("</span>");
				}
				break;
			case STRING:
				buf.append(ch);
				if(ch == '"') {
					buf.append("</span>");
					attrState = AttributeState.SPACE;
				}
				break;
			case ALTSTRING:
				buf.append(ch);
				if(ch == '\'') {
					buf.append("</span>");
					attrState = AttributeState.SPACE;
				}
				break;
			}
		}
		if(attrState != AttributeState.SPACE) {
			buf.append("</span>");
		}
		return buf.toString();
	}

	@Override
	public void format(Reader in, Writer out) throws IOException {
		StringBuffer data = new StringBuffer();
		char[] buf = new char[256];
		int n;
		while((n = in.read(buf)) != -1) {
			data.append(buf, 0, n);
		}
		out.write(formatLine(data.toString()));
	}

	@Override
	public String formatLine(String line) {
		int i = 0;
		char ch;
		int length = line.length();
		while(i < length) {
			ch = line.charAt(i++);
			switch(state) {
			case NORMAL:
				if(ch == '<') {
					result.append(htmlspecialchars(tmp
							.toString()));
					tmp = new StringBuffer();
					state = State.TAG;
				} else {
					tmp.append(ch);
				}
				break;
			case TAG:
				switch(ch) {
				case '>':
					state = State.NORMAL;
					String s = tmp.toString();
					tmp = new StringBuffer();
					result.append(formatTag(s));
					break;
				case '[':
					tmp.append(ch);
					if(tmp.toString().equals("[CDATA[")) {
						state = State.CDATA;
					}
					break;
				case '-':
					tmp.append(ch);
					if(tmp.toString().startsWith("!--")) {
						state = State.COMMENT;
					}
					break;
				default:
					tmp.append(ch);
				}
				break;
			case CDATA:
				tmp.append(ch);
				if(ch == '>') {
					if(tmp.toString().endsWith("]]>")) {
						result.append("<span class=\"");
						result.append(CSS_PREFIX);
						result.append("string\">");
						result.append(htmlspecialchars(tmp
								.toString()));
						result.append("</span>");
						tmp = new StringBuffer();
						state = State.NORMAL;
					}
				}
				break;
			case COMMENT:
				tmp.append(ch);
				if(ch == '>') {
					if(tmp.toString().endsWith("-->")) {
						result.append("<span class=\"");
						result.append(CSS_PREFIX);
						result.append("comment\">&lt;");
						result.append(htmlspecialchars(tmp
								.toString()));
						result.append("</span>");
						tmp = new StringBuffer();
						state = State.NORMAL;
					}
				}
			}
		}
		if((state == State.NORMAL) && (tmp.length() != 0)) {
			result.append(htmlspecialchars(tmp.toString()));
			tmp = new StringBuffer();
		}
		String r = result.append("\n").toString();
		result = new StringBuffer();
		return r;
	}
}
