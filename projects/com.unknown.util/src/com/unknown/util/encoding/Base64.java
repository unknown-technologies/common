package com.unknown.util.encoding;

public class Base64 {
	// @formatter:off
	public final static String ALPHABET =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "abcdefghijklmnopqrstuvwxyz"
			+ "0123456789"
			+ "+/";
	// @formatter:on
	public final static int INVERSE[];

	static {
		INVERSE = new int[128];
		for(int i = 0; i < INVERSE.length; i++)
			INVERSE[i] = -1;
		for(int i = 0; i < ALPHABET.length(); i++)
			INVERSE[ALPHABET.charAt(i)] = i;
	}

	private static int inv(char c) {
		if(c == '=')
			return 0;
		if(c >= INVERSE.length || INVERSE[c] == -1)
			throw new IllegalArgumentException("invalid char: " + c);
		return INVERSE[c];
	}

	private static char get(int c) {
		return ALPHABET.charAt(c);
	}

	public static byte[] decode(String data) {
		String in = data.trim();
		if(in.length() == 0)
			return new byte[0];
		if(in.length() % 4 != 0)
			throw new IllegalArgumentException();
		int size = (int) (in.length() * 3L / 4L);
		if(in.charAt(in.length() - 1) == '=')
			size--;
		if(in.charAt(in.length() - 2) == '=')
			size--;
		byte[] out = new byte[size];
		for(int i = 0, o = 0; i < in.length(); i += 4, o += 3) {
			int a = inv(in.charAt(i));
			int b = inv(in.charAt(i + 1));
			int c = inv(in.charAt(i + 2));
			int d = inv(in.charAt(i + 3));
			int value = (a << 18) | (b << 12) | (c << 6) | d;
			out[o] = (byte) (value >> 16);
			if(in.charAt(i + 2) != '=')
				out[o + 1] = (byte) (value >> 8);
			if(in.charAt(i + 3) != '=')
				out[o + 2] = (byte) value;
		}
		return out;
	}

	public static String encode(byte[] in) {
		StringBuilder result = new StringBuilder(in.length * 4 / 3);
		for(int i = 0; i < in.length; i += 3) {
			byte a = in[i];
			byte b = i + 1 < in.length ? in[i + 1] : 0;
			byte c = i + 2 < in.length ? in[i + 2] : 0;
			int value = ((a & 0xFF) << 16) | ((b & 0xFF) << 8) | (c & 0xFF);
			result.append(get((value >> 18) & 0x3F));
			result.append(get((value >> 12) & 0x3F));
			result.append(i + 1 < in.length ? get((value >> 6) & 0x3F) : '=');
			result.append(i + 2 < in.length ? get(value & 0x3F) : '=');
		}
		return result.toString();
	}
}
