package com.unknown.util;

import java.util.regex.Pattern;

import com.unknown.util.io.Endianess;

public class UUID {
	private static final Pattern format = Pattern
			.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

	public static String randomUUID() {
		return java.util.UUID.randomUUID().toString();
	}

	public static byte[] randomUUIDBytes() {
		java.util.UUID uuid = java.util.UUID.randomUUID();
		long hi = uuid.getMostSignificantBits();
		long lo = uuid.getLeastSignificantBits();
		byte[] result = new byte[16];
		Endianess.set64bitBE(result, 0, hi);
		Endianess.set64bitBE(result, 8, lo);
		return result;
	}

	public static boolean isUUID(String s) {
		if(s.length() != 36) {
			return false;
		}
		return format.matcher(s).matches();
	}
}
