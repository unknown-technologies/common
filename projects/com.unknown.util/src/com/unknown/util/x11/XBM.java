package com.unknown.util.x11;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

public class XBM {
	private static final String ERROR = "not an XBM file";

	private final BufferedImage img;
	private final String name;
	private final int xhot;
	private final int yhot;
	private final byte[] words;

	public XBM(String source) throws IOException {
		String src = source.trim();

		// tokenize into C tokens
		Scanner s = new Scanner(src);

		// read image info
		int width = -1;
		int height = -1;
		int x_hot = 0;
		int y_hot = 0;

		Token token = s.scan();
		while(token != null) {
			if(token.str == null) {
				throw new IOException(ERROR);
			}

			if(token.str.equals("static")) {
				break;
			}

			if(!token.str.equals("#define")) {
				throw new IOException(ERROR);
			}

			Token var = s.scan();
			if(var.str == null) {
				throw new IOException(ERROR);
			}

			Token val = s.scan();
			if(val.str != null) {
				throw new IOException(ERROR);
			}

			if(var.str.endsWith("_width")) {
				width = val.ival;
			} else if(var.str.endsWith("_height")) {
				height = val.ival;
			} else if(var.str.endsWith("_xhot")) {
				x_hot = var.ival;
			} else if(var.str.endsWith("_yhot")) {
				y_hot = var.ival;
			} else {
				throw new IOException(ERROR);
			}

			token = s.scan();
		}

		if(width == -1 || height == -1) {
			throw new IOException(ERROR);
		}

		xhot = x_hot;
		yhot = y_hot;

		s.expect("char");

		Token nametoken = s.scan();
		if(nametoken.str == null) {
			throw new IOException(ERROR);
		}

		if(nametoken.str.endsWith("_bits")) {
			name = nametoken.str.substring(0, nametoken.str.length() - 5);
		} else {
			name = nametoken.str;
		}

		s.expect("[");
		s.expect("]");
		s.expect("=");
		s.expect("{");

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		DataBufferInt pixels = (DataBufferInt) img.getRaster().getDataBuffer();
		int[] data = pixels.getData();

		int row = width / 8;
		if((width & 0x07) != 0) {
			row++;
		}
		int size = row * height;

		// read image
		words = new byte[size];
		for(int i = 0; i < words.length; i++) {
			Token word = s.scan();
			if(word.str != null) {
				throw new IOException(ERROR);
			}

			words[i] = (byte) word.ival;

			if(i + 1 < words.length) {
				s.expect(",");
			}
		}

		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				int word = words[y * row + x / 8];
				boolean bit = (word & (1 << (x & 0x07))) != 0;
				data[y * width + x] = bit ? 0xFF000000 : 0x00000000;
			}
		}

		s.expect("}");
		s.expect(";");
	}

	private static class Token {
		public final String str;
		public final int ival;

		public Token(String value) {
			str = value;
			ival = 0;
		}

		public Token(int value) {
			str = null;
			ival = value;
		}
	}

	private static class Scanner {
		private final String src;
		private int index;

		public Scanner(String s) {
			src = s;
		}

		private int read() {
			if(index < src.length()) {
				return src.charAt(index++);
			} else {
				return -1;
			}
		}

		private void unread() {
			if(index > 0) {
				index--;
			}
		}

		private static boolean isIdentStart(int c) {
			if(c >= 'A' && c <= 'Z') {
				return true;
			}
			if(c >= 'a' && c <= 'z') {
				return true;
			}
			if(c == '_') {
				return true;
			}
			return false;
		}

		private static boolean isIdent(int c) {
			return isIdentStart(c) || (c >= '0' && c <= '9');
		}

		private static boolean isWhitespace(char c) {
			switch(c) {
			case ' ':
			case '\r':
			case '\n':
			case '\t':
				return true;
			default:
				return false;
			}
		}

		private static boolean isHex(char c) {
			if(isDigit(c)) {
				return true;
			} else if(c >= 'A' && c <= 'F') {
				return true;
			} else {
				return c >= 'a' && c <= 'f';
			}
		}

		private static boolean isDigit(char c) {
			return c >= '0' && c <= '9';
		}

		public Token scan() throws IOException {
			int c = read();

			// skip whitespace
			while(isWhitespace((char) c)) {
				c = read();
			}

			// handle comments
			while(c == '/') {
				c = read();
				if(c == '/') {
					// line comment
					while(c != -1 && c != '\r' && c != '\n') {
						c = read();
					}
				} else if(c == '*') {
					// block comment
					c = read();
					while(c != -1) {
						if(c == '*') {
							c = read();
							if(c == '/') {
								c = read();
								break;
							}
						} else {
							c = read();
						}
					}
				} else {
					unread();
				}

				// skip whitespace
				while(isWhitespace((char) c)) {
					c = read();
				}
			}

			switch(c) {
			case '*':
				return new Token("*");
			case '[':
				return new Token("[");
			case ']':
				return new Token("]");
			case '{':
				return new Token("{");
			case '}':
				return new Token("}");
			case '=':
				return new Token("=");
			case ';':
				return new Token(";");
			case ',':
				return new Token(",");
			case '0': {
				int val = 0;
				c = read();
				if(c == 'x' || c == 'X') {
					c = read();
					while(isHex((char) c)) {
						val <<= 4;
						if(c >= '0' && c <= '9') {
							val |= c - '0';
						} else if(c >= 'A' && c <= 'F') {
							val |= c - 'A' + 10;
						} else if(c >= 'a' && c <= 'f') {
							val |= c - 'a' + 10;
						} else {
							throw new AssertionError("unreachable");
						}
						c = read();
					}
					if(isIdent((char) c)) {
						throw new IOException(ERROR);
					}
					unread();
				} else {
					while(isDigit((char) c)) {
						val *= 10;
						val += c - '0';
						c = read();
					}
					if(isIdent((char) c)) {
						throw new IOException(ERROR);
					}
					unread();
				}
				return new Token(val);
			}
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9': {
				int val = c - '0';
				c = read();
				while(isDigit((char) c)) {
					val *= 10;
					val += c - '0';
					c = read();
				}
				if(isIdent((char) c)) {
					throw new IOException(ERROR);
				}
				unread();
				return new Token(val);
			}
			}

			if(c == '#') {
				StringBuilder buf = new StringBuilder();
				buf.append((char) c);
				c = read();
				while(isIdent(c)) {
					buf.append((char) c);
					c = read();
				}
				unread();
				return new Token(buf.toString());
			} else if(isIdentStart(c)) {
				StringBuilder buf = new StringBuilder();
				buf.append((char) c);
				c = read();
				while(isIdent(c)) {
					buf.append((char) c);
					c = read();
				}
				unread();
				return new Token(buf.toString());
			}

			return new Token(null);
		}

		public void expect(String data) throws IOException {
			Token token = scan();
			if(token.str == null) {
				throw new IOException(ERROR);
			}
			if(!token.str.equals(data)) {
				throw new IOException(ERROR);
			}
		}
	}

	public BufferedImage getImage() {
		return img;
	}

	public int getWidth() {
		return img.getWidth();
	}

	public int getHeight() {
		return img.getHeight();
	}

	public int getXHot() {
		return xhot;
	}

	public int getYHot() {
		return yhot;
	}

	private int getRowSize() {
		int width = getWidth();
		int row = width / 8;
		if((width & 0x07) != 0) {
			row++;
		}
		return row;
	}

	public boolean getPixel(int x, int y) {
		int row = getRowSize();
		int word = words[y * row + x / 8];
		return (word & (1 << (x & 0x07))) != 0;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "XBM[name=" + name + ",width=" + img.getWidth() + ",height=" + img.getHeight() + ",x_hot=" +
				xhot + ",y_hot=" + yhot + "]";
	}
}
