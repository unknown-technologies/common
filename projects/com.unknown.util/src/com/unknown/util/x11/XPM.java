package com.unknown.util.x11;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class XPM {
	private final BufferedImage img;
	private final String name;
	private final int xhot;
	private final int yhot;

	public XPM(String source) throws IOException {
		String src = source.trim();
		if(!src.startsWith("/* XPM */")) {
			throw new IOException("not an XPM3 file");
		}

		// tokenize into C tokens
		Scanner s = new Scanner(src);
		s.expect("static");
		s.expect("char");
		s.expect("*");

		Token nametoken = s.scan();
		if(nametoken.string) {
			throw new IOException("not an XPM3 file");
		}
		name = nametoken.value;

		s.expect("[");
		s.expect("]");
		s.expect("=");
		s.expect("{");

		// read image info
		Token info = s.scan();
		if(!info.string) {
			throw new IOException("not an XPM3 file");
		}

		StringTokenizer st = new StringTokenizer(info.value, " \r\n\t");
		int width = Integer.parseInt(st.nextToken());
		int height = Integer.parseInt(st.nextToken());
		int ncolors = Integer.parseInt(st.nextToken());
		int cpp = Integer.parseInt(st.nextToken());
		int x_hot = 0;
		int y_hot = 0;
		if(st.hasMoreTokens()) {
			x_hot = Integer.parseInt(st.nextToken());
			y_hot = Integer.parseInt(st.nextToken());
		}

		xhot = x_hot;
		yhot = y_hot;

		s.expect(",");

		// read palette
		Map<String, Color> palette = new HashMap<>();
		for(int i = 0; i < ncolors; i++) {
			Token line = s.scan();
			if(!line.string) {
				throw new IOException("not an XPM3 file");
			}
			s.expect(",");

			String chars = line.value.substring(0, cpp);
			try {
				Color color = parseColor(line.value.substring(cpp).trim());
				palette.put(chars, color);
			} catch(Throwable t) {
				System.out.println("Error at entry " + i + ": \"" + line.value + "\"");
			}
		}

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		DataBufferInt pixels = (DataBufferInt) img.getRaster().getDataBuffer();
		int[] data = pixels.getData();

		// read image
		for(int y = 0; y < height; y++) {
			Token line = s.scan();
			if(!line.string) {
				throw new IOException("not an XPM3 file");
			}

			String chars = line.value;

			for(int x = 0; x < width; x++) {
				int px = x * cpp;
				String idx = chars.substring(px, px + cpp);
				Color color = palette.get(idx);
				if(color == null) {
					throw new IOException(
							"invalid pixel at (" + x + "," + y + "): \"" + idx + "\"");
				}
				data[y * width + x] = color.getRGB();
			}
			if(y + 1 < height) {
				s.expect(",");
			}
		}

		s.expect("}");
		s.expect(";");
	}

	private static Color parseColor(String s) throws IOException {
		StringTokenizer st = new StringTokenizer(s, " \r\n\t");

		String first = st.nextToken();
		if(first.equals("c")) {
			return parseColorName(st.nextToken());
		} else if(first.equals("s")) {
			// String name = st.nextToken();
			st.nextToken();
			first = st.nextToken();
			if(first.equals("m")) {
				// skip
				st.nextToken();
				first = st.nextToken();
			}
			if(first.equals("c")) {
				return parseColorName(st.nextToken());
			} else {
				throw new IOException("invalid palette entry");
			}
		} else {
			throw new IOException("invalid palette entry: \"" + s + "\"");
		}
	}

	private static Color parseColorName(String name) throws IOException {
		if(name.startsWith("#")) {
			int r = Integer.parseInt(name.substring(1, 3), 16);
			int g = Integer.parseInt(name.substring(3, 5), 16);
			int b = Integer.parseInt(name.substring(5, 7), 16);
			int a = 255;
			return new Color(r, g, b, a);
		} else if(name.equals("None")) {
			return new Color(0, 0, 0, 0);
		} else {
			Color color = X11Colors.lookupColor(name);
			if(color == null) {
				throw new IOException("invalid color");
			} else {
				return color;
			}
		}
	}

	private static class Token {
		public final String value;
		public final boolean string;

		public Token(String value) {
			this(value, false);
		}

		public Token(String value, boolean string) {
			this.value = value;
			this.string = string;
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
			case '"': {
				// parse entire string
				StringBuilder buf = new StringBuilder();
				c = read();
				while(c != -1) {
					switch(c) {
					case '\\':
						c = read();
						switch(c) {
						case 'r':
							buf.append('\r');
							break;
						case 'n':
							buf.append('\n');
							break;
						case 't':
							buf.append('\t');
							break;
						case '"':
							buf.append('"');
							break;
						case '\'':
							buf.append('\'');
							break;
						default:
							throw new IOException("not an XPM3 file");
						}
						break;
					case '"':
						return new Token(buf.toString(), true);
					default:
						buf.append((char) c);
					}

					c = read();
				}
			}
			}

			if(isIdentStart(c)) {
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
			if(token.value == null) {
				throw new IOException("not an XPM3 file");
			}
			if(token.string || !token.value.equals(data)) {
				throw new IOException("not an XPM3 file");
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

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "XPM[name=" + name + ",width=" + img.getWidth() + ",height=" + img.getHeight() + ",x_hot=" +
				xhot + ",y_hot=" + yhot + "]";
	}
}
