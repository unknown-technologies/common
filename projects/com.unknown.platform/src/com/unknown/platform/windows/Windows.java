package com.unknown.platform.windows;

import java.nio.charset.StandardCharsets;

public class Windows {
	public static final long WINDOWS_TICK = 10000000L;
	public static final long SEC_TO_UNIX_EPOCH = 11644473600L;

	public static long windowsTickToUnixSeconds(long windowsTicks) {
		return Long.divideUnsigned(windowsTicks, WINDOWS_TICK) - SEC_TO_UNIX_EPOCH;
	}

	public static char[] lpwstr(String s) {
		char[] chars = s.toCharArray();
		char[] result = new char[chars.length + 1];
		System.arraycopy(chars, 0, result, 0, chars.length);
		result[chars.length] = 0;
		return result;
	}

	public static String lpwstr(char[] data) {
		if(data.length == 0) {
			return "";
		} else if(data[data.length - 1] == 0) {
			return new String(data, 0, data.length - 1);
		} else {
			return new String(data);
		}
	}

	public static String lpwstr(byte[] data) {
		if(data.length == 0 || data.length == 1) {
			return "";
		} else if(data[data.length - 1] == 0 && data[data.length - 2] == 0) {
			return new String(data, 0, data.length - 2, StandardCharsets.UTF_16LE);
		} else {
			return new String(data, StandardCharsets.UTF_16LE);
		}
	}

	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}
}
