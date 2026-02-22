package com.unknown.net.shownet;

import com.unknown.util.io.Endianess;

public class Crypt {
	private static final int[] KEY = { 0x92F5B6E1, 0x43DA778C, 0x2B5E7C49, 0xB7341A22 };

	public static int getKey(int index) {
		return KEY[index];
	}

	public static void crypt(byte[] data, int start, int length) {
		crypt(data, KEY, start, length);
	}

	public static void decrypt(byte[] data, int start, int length) {
		decrypt(data, KEY, start, length);
	}

	public static void crypt(byte[] data, int[] key, int start, int length) {
		int rounded = length + 7;
		int iterations = rounded >>> 3;
		for(int i = 0; i < iterations; i++) {
			int k = 0;
			int data0 = Endianess.get32bitLE(data, start + (2 * i) * 4);
			int data1 = Endianess.get32bitLE(data, start + (2 * i + 1) * 4);
			for(int j = 0; j < 32; j++) {
				data0 += (k + key[k & 3]) ^ (data1 + ((data1 >>> 5) ^ (data1 << 4)));
				k += 0x9E3779B9;
				data1 += (k + key[(k >>> 11) & 3]) ^ (data0 + ((data0 >>> 5) ^ (data0 << 4)));
			}
			Endianess.set32bitLE(data, start + (2 * i) * 4, data0);
			Endianess.set32bitLE(data, start + (2 * i + 1) * 4, data1);
		}
	}

	public static void decrypt(byte[] data, int[] key, int start, int length) {
		int rounded = length + 7;
		int iterations = rounded >>> 3;
		for(int i = 0; i < iterations; i++) {
			int k = 0xC6EF3720;
			int data0 = Endianess.get32bitLE(data, start + (2 * i) * 4);
			int data1 = Endianess.get32bitLE(data, start + (2 * i + 1) * 4);
			for(int j = 0; j < 32; j++) {
				data1 -= (k + key[(k >>> 11) & 3]) ^ (data0 + ((data0 >>> 5) ^ (data0 << 4)));
				k -= 0x9E3779B9;
				data0 -= (k + key[k & 3]) ^ (data1 + ((data1 >>> 5) ^ (data1 << 4)));
			}
			Endianess.set32bitLE(data, start + (2 * i) * 4, data0);
			Endianess.set32bitLE(data, start + (2 * i + 1) * 4, data1);
		}
	}
}
