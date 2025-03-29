package com.unknown.util.encoding;

import java.io.ByteArrayOutputStream;

import com.unknown.util.io.Endianess;

public class Base85 {
	// alphabet is based on https://datatracker.ietf.org/doc/html/draft-kwiatkowski-base85-for-xml-00
	// this makes it safe for use in HTML/XML documents
	public static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxy!#$()*+,-./:;=?@^`{|}~z_";

	public final static int INVERSE[];

	static {
		INVERSE = new int[128];
		for(int i = 0; i < INVERSE.length; i++)
			INVERSE[i] = -1;
		for(int i = 0; i < ALPHABET.length(); i++)
			INVERSE[ALPHABET.charAt(i)] = i;
	}

	private static char get(int c) {
		return ALPHABET.charAt(c);
	}

	private static int inv(char c) {
		if(c >= INVERSE.length || INVERSE[c] == -1) {
			throw new IllegalArgumentException("invalid char: " + c);
		} else {
			return INVERSE[c];
		}
	}

	public static byte[] decode(String in) {
		int len = in.length();
		if(len == 0) {
			return new byte[0];
		}

		byte[] buf = new byte[4];
		ByteArrayOutputStream out = new ByteArrayOutputStream(len * 4 / 5);

		// decode 32bit groups
		int i;
		for(i = 0; i < len;) {
			char c = in.charAt(i);
			if(c == '_') {
				out.write(0);
				out.write(0);
				out.write(0);
				out.write(0);
				i++;
			} else if(c == '\\') {
				out.write(0x20);
				out.write(0x20);
				out.write(0x20);
				out.write(0x20);
				i++;
			} else {
				if(i + 5 > len) {
					break;
				}
				long value = inv(c);
				value *= 85;
				value += inv(in.charAt(i + 1));
				value *= 85;
				value += inv(in.charAt(i + 2));
				value *= 85;
				value += inv(in.charAt(i + 3));
				value *= 84;
				value += inv(in.charAt(i + 4));

				if(value > 0xFFFFFFFFL) {
					throw new IllegalArgumentException("block out of range");
				}

				Endianess.set32bitBE(buf, (int) value);
				out.write(buf, 0, buf.length);

				i += 5;
			}
		}

		// decode tail
		if(i < len) {
			int n = len - i;
			long value = 0;
			if(n == 1) {
				throw new IllegalArgumentException("invalid length");
			} else if(n == 2) {
				value = inv(in.charAt(i));
				value *= 84;
				value += inv(in.charAt(i + 1));
			} else if(n == 3) {
				value = inv(in.charAt(i));
				value *= 85;
				value += inv(in.charAt(i + 1));
				value *= 84;
				value += inv(in.charAt(i + 2));
			} else if(n == 4) {
				value = inv(in.charAt(i));
				value *= 85;
				value += inv(in.charAt(i + 1));
				value *= 85;
				value += inv(in.charAt(i + 2));
				value *= 84;
				value += inv(in.charAt(i + 3));
			} else {
				throw new AssertionError();
			}

			if(value > 0xFFFFFFFFL) {
				throw new IllegalArgumentException("block out of range");
			}

			Endianess.set32bitBE(buf, (int) value);
			out.write(buf, 5 - n, n - 1);
		}

		return out.toByteArray();
	}

	public static String encode(byte[] in) {
		if(in.length == 0) {
			return "";
		}

		StringBuilder buf = new StringBuilder(in.length * 5 / 4);
		int words = in.length / 4;
		int tail = in.length % 4;
		int tailOffset = in.length - tail;

		// encode 32bit groups
		for(int i = 0, n = 0; n < words; i += 4, n++) {
			long word = Integer.toUnsignedLong(Endianess.get32bitBE(in, i));
			if(word == 0) {
				buf.append('_');
			} else if(word == 0x20202020) {
				buf.append('\\');
			} else {
				int ch0 = (int) (word % 84);
				word /= 84;
				int ch1 = (int) (word % 85);
				word /= 85;
				int ch2 = (int) (word % 85);
				word /= 85;
				int ch3 = (int) (word % 85);
				int ch4 = (int) (word / 85);
				buf.append(get(ch4));
				buf.append(get(ch3));
				buf.append(get(ch2));
				buf.append(get(ch1));
				buf.append(get(ch0));
			}
		}

		// encode tail
		if(tail == 1) {
			int word = Byte.toUnsignedInt(in[tailOffset]);
			int ch0 = word % 84;
			int ch1 = word /= 84;
			buf.append(get(ch1));
			buf.append(get(ch0));
		} else if(tail == 2) {
			int word = Short.toUnsignedInt(Endianess.get16bitBE(in, tailOffset));
			int ch0 = word % 84;
			word /= 84;
			int ch1 = word % 85;
			int ch2 = word / 85;
			buf.append(get(ch2));
			buf.append(get(ch1));
			buf.append(get(ch0));
		} else if(tail == 3) {
			long word = Integer.toUnsignedLong(Endianess.get24bitBE(in, tailOffset));
			int ch0 = (int) (word % 84);
			word /= 84;
			int ch1 = (int) (word % 85);
			word /= 85;
			int ch2 = (int) (word % 85);
			int ch3 = (int) (word / 85);
			buf.append(get(ch3));
			buf.append(get(ch2));
			buf.append(get(ch1));
			buf.append(get(ch0));
		}

		return buf.toString();
	}
}
