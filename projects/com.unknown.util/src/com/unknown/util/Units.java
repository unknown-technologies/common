package com.unknown.util;

public class Units {
	public final static long K = 1024;
	public final static long M = 1024 * 1024;
	public final static long G = 1024 * 1024 * 1024;
	public final static long T = 1024 * 1024 * 1024 * 1024;
	public final static long P = 1024 * 1024 * 1024 * 1024 * 1024;

	public static String size(String s) {
		return size(Long.parseLong(s));
	}

	public static String size(long size) {
		if(size < K) {
			return Long.toString(size) + " B";
		} else if(size < M) {
			return Long.toString(size / K) + " KiB";
		} else if(size < G) {
			return Long.toString(size / M) + " MiB";
		} else if(size < T) {
			return Long.toString(size / G) + " GiB";
		} else if(size < P) {
			return Long.toString(size / T) + " TiB";
		} else {
			return Long.toString(size / P) + " PiB";
		}
	}

	private static double round(double x, int comma) {
		long tmp = Math.round(x * Math.pow(10, comma));
		return tmp / Math.pow(10, comma);
	}

	public static String size(long size, int comma) {
		if(size < K) {
			return Long.toString(size) + " B";
		} else if(size < M) {
			return round(size / (double) K, comma) + " KiB";
		} else if(size < G) {
			return round(size / (double) M, comma) + " MiB";
		} else if(size < T) {
			return round(size / (double) G, comma) + " GiB";
		} else if(size < P) {
			return round(size / (double) T, comma) + " TiB";
		} else {
			return round(size / (double) P, comma) + " PiB";
		}
	}
}
