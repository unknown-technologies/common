package com.unknown.xml;

import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.LinkedList;

public class XMLScanner {
	private State state;
	private StringBuffer buf;
	private Deque<XMLToken> tokens;

	private int pos = 0;

	private static enum State {
		INIT, LT, QM, LTEXCL, LTEXCLDASH, LTEXCLSQ, LTEXCLSQC, LTEXCLSQCD, LTEXCLSQCDA, LTEXCLSQCDAT, LTEXCLSQCDATA, COMMENT, DASH, DASHDASH, SLASH, SQ, SQSQ, ENT, TEXT, TEXTSQ, TEXTDQ, TEXTSQENT, TEXTDQENT, CDATA;
	}

	public XMLScanner() {
		state = State.INIT;
		buf = new StringBuffer();
		tokens = new LinkedList<>();
	}

	public void process(byte[] data, int off, int len) {
		String s = new String(data, off, len, StandardCharsets.UTF_8);
		for(char c : s.toCharArray()) {
			scan(c);
		}
	}

	public void process(byte[] data) {
		String s = new String(data, StandardCharsets.UTF_8);
		for(char c : s.toCharArray()) {
			scan(c);
		}
	}

	public void process(char[] data, int off, int len) {
		for(int i = 0; i < len; i++) {
			scan(data[i + off]);
		}
	}

	public void process(char[] data) {
		for(char c : data) {
			scan(c);
		}
	}

	public void process(String data) {
		for(int i = 0; i < data.length(); i++) {
			scan(data.charAt(i));
		}
	}

	public boolean available() {
		return !tokens.isEmpty();
	}

	public XMLToken scan() {
		if(tokens.isEmpty()) {
			return null;
		}
		return tokens.removeFirst();
	}

	private void push(XMLToken t) {
		tokens.add(t);
	}

	private void scan(char c) {
		switch(state) {
		case INIT:
			if(c != '<') {
				XMLToken t = new XMLToken(XMLTokenType.error, pos);
				t.val = "<";
				push(t);
			} else {
				state = State.LT;
			}
			break;
		case LT:
			if(c == '?') {
				state = State.TEXT;
				if(buf.length() > 0) {
					XMLToken t = new XMLToken(XMLTokenType.text, pos);
					t.val = buf.toString();
					buf = new StringBuffer();
					push(t);
				}
				push(new XMLToken(XMLTokenType.piopen, pos));
			} else if(c == '!') {
				state = State.LTEXCL;
			} else if(c == '/') {
				state = State.TEXT;
				if(buf.length() > 0) {
					XMLToken t = new XMLToken(XMLTokenType.text, pos);
					t.val = buf.toString();
					buf = new StringBuffer();
					push(t);
				}
				push(new XMLToken(XMLTokenType.tsopen, pos));
			} else {
				state = State.TEXT;
				if(buf.length() > 0) {
					XMLToken t = new XMLToken(XMLTokenType.text, pos);
					t.val = buf.toString();
					buf = new StringBuffer();
					push(t);
				}
				push(new XMLToken(XMLTokenType.topen, pos));
				buf.append(c);
			}
			break;
		case LTEXCL:
			if(c == '-') {
				state = State.LTEXCLDASH;
			} else if(c == '[') {
				state = State.LTEXCLSQ;
			} else {
				XMLToken t = new XMLToken(XMLTokenType.error, pos);
				t.val = "<!" + c;
				push(t);
			}
			break;
		case LTEXCLSQ:
			if(c == 'C') {
				state = State.LTEXCLSQC;
			} else {
				XMLToken t = new XMLToken(XMLTokenType.error, pos);
				t.val = "<![" + c;
				push(t);
			}
			break;
		case LTEXCLSQC:
			if(c == 'D') {
				state = State.LTEXCLSQCD;
			} else {
				XMLToken t = new XMLToken(XMLTokenType.error, pos);
				t.val = "<![C" + c;
				push(t);
			}
			break;
		case LTEXCLSQCD:
			if(c == 'A') {
				state = State.LTEXCLSQCDA;
			} else {
				XMLToken t = new XMLToken(XMLTokenType.error, pos);
				t.val = "<![CD" + c;
				push(t);
			}
			break;
		case LTEXCLSQCDA:
			if(c == 'T') {
				state = State.LTEXCLSQCDAT;
			} else {
				XMLToken t = new XMLToken(XMLTokenType.error, pos);
				t.val = "<![CDA" + c;
				push(t);
			}
			break;
		case LTEXCLSQCDAT:
			if(c == 'A') {
				state = State.LTEXCLSQCDATA;
			} else {
				XMLToken t = new XMLToken(XMLTokenType.error, pos);
				t.val = "<![CDAT" + c;
				push(t);
			}
			break;
		case LTEXCLSQCDATA:
			if(c == '[') {
				state = State.CDATA;
			} else {
				XMLToken t = new XMLToken(XMLTokenType.error, pos);
				t.val = "<![CDATA" + c;
				push(t);
			}
			break;
		case LTEXCLDASH:
			if(c == '-') {
				state = State.COMMENT;
			} else {
				XMLToken t = new XMLToken(XMLTokenType.error, pos);
				t.val = "<!-" + c;
				push(t);
			}
			break;
		case COMMENT:
			if(c == '-') {
				state = State.DASH;
			}
			break;
		case DASH:
			if(c == '-') {
				state = State.DASHDASH;
			} else {
				state = State.DASH;
			}
			break;
		case DASHDASH:
			if(c == '>') {
				state = State.TEXT;
			} else if(c != '-') {
				state = State.COMMENT;
			}
			break;
		case QM:
			if(c == '>') {
				state = State.TEXT;
				if(buf.length() > 0) {
					XMLToken t = new XMLToken(XMLTokenType.text, pos);
					t.val = buf.toString();
					buf = new StringBuffer();
					push(t);
				}
				push(new XMLToken(XMLTokenType.piclose, pos));
			} else if(c == '?') {
				buf.append('?');
			} else {
				state = State.TEXT;
				buf.append('?');
				switch(c) {
				case '<':
					state = State.LT;
					break;
				case '?':
					state = State.QM;
					break;
				case '&':
					state = State.ENT;
					if(buf.length() > 0) {
						XMLToken t = new XMLToken(XMLTokenType.text, pos);
						t.val = buf.toString();
						buf = new StringBuffer();
						push(t);
					}
					break;
				case '\t':
				case '\r':
				case '\n':
				case ' ':
					if(buf.length() > 0) {
						XMLToken t = new XMLToken(XMLTokenType.text, pos);
						t.val = buf.toString();
						buf = new StringBuffer();
						push(t);
					}
					push(new XMLToken(XMLTokenType.space, Character.toString(c), pos));
					break;
				case '"':
					if(buf.length() > 0) {
						XMLToken t = new XMLToken(XMLTokenType.text, pos);
						t.val = buf.toString();
						buf = new StringBuffer();
						push(t);
					}
					push(new XMLToken(XMLTokenType.quote, pos));
					state = State.TEXT;
					break;
				case '\'':
					if(buf.length() > 0) {
						XMLToken t = new XMLToken(XMLTokenType.text, pos);
						t.val = buf.toString();
						buf = new StringBuffer();
						push(t);
					}
					push(new XMLToken(XMLTokenType.squote, pos));
					state = State.TEXT;
					break;
				case '=':
					if(buf.length() > 0) {
						XMLToken t = new XMLToken(XMLTokenType.text, pos);
						t.val = buf.toString();
						buf = new StringBuffer();
						push(t);
					}
					push(new XMLToken(XMLTokenType.equal, pos));
					break;
				case '/':
					state = State.SLASH;
					break;
				default:
					buf.append(c);
				}
			}
			break;
		case SLASH:
			if(c == '>') {
				state = State.TEXT;
				if(buf.length() > 0) {
					XMLToken t = new XMLToken(XMLTokenType.text, pos);
					t.val = buf.toString();
					buf = new StringBuffer();
					push(t);
				}
				push(new XMLToken(XMLTokenType.tcloses, pos));
				break;
			} else if(c == '/') {
				buf.append(c);
				break;
			} else if(c == '<') {
				buf.append('/');
				state = State.LT;
				break;
			} else {
				state = State.TEXT;
				buf.append('/');
				// fall through
			}
		case TEXT:
			switch(c) {
			case '<':
				state = State.LT;
				break;
			case '?':
				state = State.QM;
				break;
			case '&':
				state = State.ENT;
				if(buf.length() > 0) {
					XMLToken t = new XMLToken(XMLTokenType.text, pos);
					t.val = buf.toString();
					buf = new StringBuffer();
					push(t);
				}
				break;
			case '\t':
			case '\r':
			case '\n':
			case ' ':
				if(buf.length() > 0) {
					XMLToken t = new XMLToken(XMLTokenType.text, pos);
					t.val = buf.toString();
					buf = new StringBuffer();
					push(t);
				}
				push(new XMLToken(XMLTokenType.space, Character.toString(c), pos));
				break;
			case '"':
				if(buf.length() > 0) {
					XMLToken t = new XMLToken(XMLTokenType.text, pos);
					t.val = buf.toString();
					buf = new StringBuffer();
					push(t);
				}
				push(new XMLToken(XMLTokenType.quote, pos));
				state = State.TEXT;
				break;
			case '\'':
				if(buf.length() > 0) {
					XMLToken t = new XMLToken(XMLTokenType.text, pos);
					t.val = buf.toString();
					buf = new StringBuffer();
					push(t);
				}
				push(new XMLToken(XMLTokenType.squote, pos));
				state = State.TEXT;
				break;
			case '=':
				if(buf.length() > 0) {
					XMLToken t = new XMLToken(XMLTokenType.text, pos);
					t.val = buf.toString();
					buf = new StringBuffer();
					push(t);
				}
				push(new XMLToken(XMLTokenType.equal, pos));
				break;
			case '/':
				state = State.SLASH;
				break;
			case '>':
				if(buf.length() > 0) {
					XMLToken t = new XMLToken(XMLTokenType.text, pos);
					t.val = buf.toString();
					buf = new StringBuffer();
					push(t);
				}
				push(new XMLToken(XMLTokenType.tclose, pos));
				break;
			default:
				buf.append(c);
			}
			break;
		case TEXTDQ:
			switch(c) {
			case '"':
				if(buf.length() > 0) {
					XMLToken t = new XMLToken(XMLTokenType.text, pos);
					t.val = buf.toString();
					buf = new StringBuffer();
					push(t);
				}
				push(new XMLToken(XMLTokenType.quote, pos));
				state = State.TEXT;
				break;
			case '&':
				state = State.TEXTDQENT;
				if(buf.length() > 0) {
					XMLToken t = new XMLToken(XMLTokenType.text, pos);
					t.val = buf.toString();
					buf = new StringBuffer();
					push(t);
				}
				break;
			default:
				buf.append(c);
			}
			break;
		case TEXTSQ:
			switch(c) {
			case '\'':
				if(buf.length() > 0) {
					XMLToken t = new XMLToken(XMLTokenType.text, pos);
					t.val = buf.toString();
					buf = new StringBuffer();
					push(t);
				}
				push(new XMLToken(XMLTokenType.squote, pos));
				state = State.TEXT;
				break;
			case '&':
				state = State.TEXTSQENT;
				if(buf.length() > 0) {
					XMLToken t = new XMLToken(XMLTokenType.text, pos);
					t.val = buf.toString();
					buf = new StringBuffer();
					push(t);
				}
				break;
			default:
				buf.append(c);
			}
			break;
		case CDATA:
			if(c == ']') {
				state = State.SQ;
			} else {
				state = State.CDATA;
				buf.append(c);
			}
			break;
		case SQ:
			if(c == ']') {
				state = State.SQSQ;
			} else {
				state = State.CDATA;
				buf.append(']');
				buf.append(c);
			}
			break;
		case SQSQ:
			if(c == '>') {
				state = State.TEXT;
				if(buf.length() > 0) {
					XMLToken t = new XMLToken(XMLTokenType.text, pos);
					t.val = buf.toString();
					buf = new StringBuffer();
					push(t);
				}
			} else if(c == ']') {
				buf.append(']');
			} else {
				buf.append("]]");
				buf.append(c);
			}
			break;
		case ENT:
			if(c == ';') {
				state = State.TEXT;
				XMLToken t = new XMLToken(XMLTokenType.ent, pos);
				t.val = buf.toString();
				buf = new StringBuffer();
				push(t);
			} else {
				buf.append(c);
			}
			break;
		case TEXTSQENT:
			if(c == ';') {
				state = State.TEXTSQ;
				XMLToken t = new XMLToken(XMLTokenType.ent, pos);
				t.val = buf.toString();
				buf = new StringBuffer();
				push(t);
			} else {
				buf.append(c);
			}
			break;
		case TEXTDQENT:
			if(c == ';') {
				state = State.TEXTDQ;
				XMLToken t = new XMLToken(XMLTokenType.ent, pos);
				t.val = buf.toString();
				buf = new StringBuffer();
				push(t);
			} else {
				buf.append(c);
			}
			break;
		}
		pos++;
	}
}
