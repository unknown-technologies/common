package com.unknown.util.net;

import java.util.Arrays;

public class IpAddress {
	public static byte[] parseIPv4(String ip) {
		if(ip.length() < 7 || ip.length() > 15) {
			throw new IllegalArgumentException("not an IPv4 address");
		}
		String[] fields = ip.split("\\.");
		if(fields.length != 4) {
			throw new IllegalArgumentException("not an IPv4 address");
		}
		byte[] bytes = new byte[4];
		for(int i = 0; i < 4; i++) {
			try {
				int v = Integer.parseInt(fields[i]);
				if(v < 0 || v > 255) {
					throw new IllegalArgumentException("not an IPv4 address");
				}
				bytes[i] = (byte) v;
			} catch(NumberFormatException e) {
				throw new IllegalArgumentException("not an IPv4 address", e);
			}
		}
		return bytes;
	}

	public static String formatIPv4(byte[] ip) {
		if(ip.length != 4) {
			throw new IllegalArgumentException("not an IPv4 address");
		}
		return new StringBuilder().append(ip[0] & 0xFF).append('.')
				.append(ip[1] & 0xFF).append('.')
				.append(ip[2] & 0xFF).append('.')
				.append(ip[3] & 0xFF).toString();
	}

	public static byte[] parseIPv6(String ip) {
		String ipAddress = ip;
		if(ip.startsWith("[") && ip.endsWith("]")) {
			ipAddress = ip.substring(1, ip.length() - 1);
		}
		String addrPart = ipAddress.split("%")[0];
		if(addrPart.length() < 2) {
			throw new IllegalArgumentException("not an IPv6 address");
		}
		String[] fields = addrPart.split(":", -1);
		int shortPos = addrPart.indexOf("::");
		boolean shortened = shortPos != -1;
		boolean twice = shortened && addrPart.indexOf("::", shortPos + 2) != -1;
		if(twice || (!addrPart.endsWith("::") && addrPart.charAt(addrPart.length() - 1) == ':')) {
			throw new IllegalArgumentException("not an IPv6 address");
		}
		if(addrPart.equals("::")) {
			return new byte[16];
		}
		if(fields.length < 3 ||
				(fields.length > 8 && (!addrPart.endsWith("::") && !addrPart.startsWith("::"))) ||
				(fields.length > 9 && (addrPart.endsWith("::") || addrPart.startsWith("::")))) {
			throw new IllegalArgumentException("not an IPv6 address");
		}
		byte[] bytes = new byte[16];
		int b = 0;
		try {
			for(int i = 0; i < fields.length; i++) {
				if(fields[i].length() == 0) { // '::'
					b = 16 - (fields.length - i - 1) * 2;
					if(b < 0) {
						throw new IllegalArgumentException("not an IPv6 address");
					}
				} else {
					int field = Integer.parseInt(fields[i], 0x10);
					if(field < 0 || field > 0xFFFF) {
						throw new IllegalArgumentException("not an IPv6 address");
					}
					bytes[b++] = (byte) (field >> 8);
					bytes[b++] = (byte) field;
				}
			}
		} catch(NumberFormatException e) {
			throw new IllegalArgumentException("not an IPv6 address", e);
		}
		if(b != 16) {
			throw new IllegalArgumentException("not an IPv6 address");
		}
		return bytes;
	}

	public static String formatIPv6(byte[] bytes) {
		if(bytes.length != 16) {
			return null;
		}

		StringBuilder buf = new StringBuilder();
		int current = 0;
		int longest = 0;
		int longestPos = -1;
		// find longest match
		for(int i = 0; i < 8; i++) {
			int word = ((bytes[2 * i] & 0xFF) << 8) | (bytes[2 * i + 1] & 0xFF);
			if(word == 0) {
				current++;
			} else {
				if(current > 1 && current > longest) {
					longest = current;
					longestPos = i - current;
				}
				current = 0;
			}
		}
		if(current > 1 && current > longest) {
			longest = current;
			longestPos = 8 - current;
		}
		// trailing zero?
		if(longestPos == 0) {
			buf.append(':');
		}
		// shorten IP address
		for(int i = 0; i < 8; i++) {
			int word = ((bytes[2 * i] & 0xFF) << 8) | (bytes[2 * i + 1] & 0xFF);
			String n = Integer.toHexString(word);
			if(word == 0) {
				if(i >= longestPos && i < (longestPos + longest)) {
					buf.append(':');
					i += longest - 1;
					continue;
				} else {
					buf.append(n);
				}
			} else {
				buf.append(n);
			}
			buf.append(":");
		}
		if(longestPos + longest != 8) {
			buf.deleteCharAt(buf.length() - 1);
		}
		return buf.toString();
	}

	public static boolean isIPv4(String addr) {
		if(addr == null) {
			throw new NullPointerException();
		}
		try {
			parseIPv4(addr);
			return true;
		} catch(Throwable t) {
			return false;
		}
	}

	public static boolean isIPv6(String addr) {
		if(addr == null) {
			throw new NullPointerException();
		}
		try {
			parseIPv6(addr);
			return true;
		} catch(Throwable t) {
			return false;
		}
	}

	public static String normalize(String ip) {
		return normalize(ip, false);
	}

	public static String normalize(String ip, boolean brackets) {
		if(ip == null) {
			return null;
		}
		try {
			return normalizeAddress(ip, brackets);
		} catch(Throwable t) {
			return ip;
		}
	}

	public static String normalizeAddress(String ip) {
		return normalizeAddress(ip, false);
	}

	public static String normalizeAddress(String ip, boolean brackets) {
		if(ip == null) {
			throw new NullPointerException();
		}
		try {
			byte[] ipv4 = parseIPv4(ip);
			return formatIPv4(ipv4);
		} catch(Throwable t) {
		}
		try {
			byte[] ipv6 = parseIPv6(ip);
			if(brackets) {
				return "[" + formatIPv6(ipv6) + "]";
			} else {
				return formatIPv6(ipv6);
			}
		} catch(Throwable t) {
		}
		throw new IllegalArgumentException("not an IP address");
	}

	public static String normalizeIPv6(String ip) {
		return formatIPv6(parseIPv6(ip));
	}

	public static byte[] parseIP(String ip) {
		try {
			return parseIPv4(ip);
		} catch(Throwable t) {
			try {
				return parseIPv6(ip);
			} catch(Throwable u) {
				throw new IllegalArgumentException("not an IP address");
			}
		}
	}

	public static boolean equals(String ip1, String ip2) {
		try {
			byte[] a = IpAddress.parseIP(ip1);
			byte[] b = IpAddress.parseIP(ip2);
			return Arrays.equals(a, b);
		} catch(Throwable t) {
			return ip1.equals(ip2);
		}
	}
}
