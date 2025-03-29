package com.unknown.util.json;

import static com.unknown.util.json.Token.Type.ADD;
import static com.unknown.util.json.Token.Type.COLON;
import static com.unknown.util.json.Token.Type.COMMA;
import static com.unknown.util.json.Token.Type.FALSE;
import static com.unknown.util.json.Token.Type.LBRAC;
import static com.unknown.util.json.Token.Type.LBRACE;
import static com.unknown.util.json.Token.Type.NONE;
import static com.unknown.util.json.Token.Type.NULL;
import static com.unknown.util.json.Token.Type.RBRAC;
import static com.unknown.util.json.Token.Type.RBRACE;
import static com.unknown.util.json.Token.Type.SUB;
import static com.unknown.util.json.Token.Type.TRUE;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

public class Scanner {
	private final Reader in;
	private int last;
	private int unread;

	private int pos;

	public Scanner(Reader in) {
		this.in = in;
		last = -1;
		unread = -1;
		pos = 0;
	}

	public int getPosition() {
		return pos;
	}

	private int read() throws ParseException {
		try {
			pos++;
			if(unread != -1) {
				int result = unread;
				unread = -1;
				return result;
			} else {
				last = in.read();
				return last;
			}
		} catch(IOException e) {
			throw new ParseException("failed to read source", pos);
		}
	}

	private void unread() {
		pos--;
		unread = last;
	}

	private static boolean isWhitespace(int c) {
		switch(c) {
		case '\t':
		case '\f':
		case ' ':
			return true;
		default:
			return false;
		}
	}

	private static boolean isEOL(int c) {
		return c == '\r' || c == '\n';
	}

	private static boolean isIdent(int c) {
		return c == '_' || c == '.' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || isNumber(c);
	}

	private static boolean isIdentFirst(int c) {
		return c == '_' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private static boolean isNumber(int c) {
		return c >= '0' && c <= '9';
	}

	private static boolean isHex(int c) {
		return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
	}

	private static boolean isOctal(int c) {
		return(c >= '0' && c <= '7');
	}

	public Token scan() throws ParseException {
		int c = read();

		// skip comments and whitespace
		while(isWhitespace(c)) {
			c = read();
		}

		if(isEOL(c)) {
			while(isEOL(c)) {
				c = read();
			}
			unread();
			return scan();
		}

		if(isIdentFirst(c)) {
			StringBuilder buf = new StringBuilder();
			while(isIdent(c)) {
				buf.append((char) c);
				c = read();
			}

			unread();
			String s = buf.toString();
			switch(s) {
			case "true":
				return new Token(TRUE);
			case "false":
				return new Token(FALSE);
			case "null":
				return new Token(NULL);
			default:
				throw new ParseException("unexpected keyword " + s, pos);
			}
		}

		if(c == '0') {
			long num = 0;
			c = read();
			if(c == 'x' || c == 'X') {
				// hex
				c = read();
				while(isHex(c)) {
					num <<= 4;
					if(c >= '0' && c <= '9') {
						num |= c - '0';
					} else if(c >= 'a' && c <= 'f') {
						num |= c - 'a' + 0x0A;
					} else if(c >= 'A' && c <= 'F') {
						num |= c - 'A' + 0x0A;
					}

					c = read();
				}
				if(c == '.') {
					// this is a float
					c = read();
					long frac = 0;
					int n = 1;
					while(isHex(c)) {
						frac <<= 4;
						if(c >= '0' && c <= '9') {
							frac |= c - '0';
						} else if(c >= 'a' && c <= 'f') {
							frac |= c - 'a' + 0x0A;
						} else if(c >= 'A' && c <= 'F') {
							frac |= c - 'A' + 0x0A;
						}

						c = read();
						n <<= 4;
					}

					unread();
					double value = num + (frac / (double) n);
					return new Token(value);
				}
			} else {
				while(isOctal(c)) {
					num <<= 3;
					num |= c - '0';
					c = read();
				}
				if(c == '.') {
					// this is a float
					c = read();
					long frac = 0;
					int n = 1;
					while(isHex(c)) {
						frac <<= 3;
						frac |= c - '0';

						c = read();
						n <<= 4;
					}

					unread();
					double value = num + (frac / (double) n);
					return new Token(value);
				}
			}
			unread();
			return new Token(num);
		} else if(isNumber(c)) {
			long num = 0;
			while(isNumber(c)) {
				num *= 10;
				num += c - '0';
				c = read();
			}
			if(c == '.') {
				// this is a float
				c = read();
				long frac = 0;
				int n = 1;
				while(isNumber(c)) {
					frac *= 10;
					frac += c - '0';

					c = read();
					n *= 10;
				}

				unread();
				double value = num + (frac / (double) n);
				return new Token(value);
			}
			unread();
			return new Token(num);
		}

		switch(c) {
		case ':':
			return new Token(COLON);
		case ',':
			return new Token(COMMA);
		case '"': {
			StringBuilder buf = new StringBuilder();
			c = read();
			while(c != '"') {
				if(c == '\\') {
					c = read();
					switch(c) {
					case '"':
						c = '"';
						break;
					case '\\':
						c = '\\';
						break;
					case '/':
						c = '/';
						break;
					case 'b':
						c = '\b';
						break;
					case 'f':
						c = '\f';
						break;
					case 'n':
						c = '\n';
						break;
					case 'r':
						c = '\r';
						break;
					case 't':
						c = '\t';
						break;
					default:
						throw new ParseException("unrecognized escape character " + (char) c,
								pos);
					}
				}
				if(c == -1) {
					throw new ParseException("unexpected EOF", pos);
				}
				buf.append((char) c);
				c = read();
			}
			return new Token(buf.toString());
		}
		case '+':
			return new Token(ADD);
		case '-':
			return new Token(SUB);
		case '[':
			return new Token(LBRAC);
		case ']':
			return new Token(RBRAC);
		case '{':
			return new Token(LBRACE);
		case '}':
			return new Token(RBRACE);
		}

		return new Token(NONE);
	}
}
