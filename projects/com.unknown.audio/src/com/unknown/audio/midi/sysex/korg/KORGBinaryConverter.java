package com.unknown.audio.midi.sysex.korg;

public class KORGBinaryConverter {
	public static byte[] fromMIDI(byte[] data, int offset, int length) {
		int blocks = length / 8;
		int remainder = length - blocks * 8 - 1;
		byte[] out = new byte[blocks * 7 + remainder];
		for(int i = 0; i < blocks; i++) {
			int bits = Integer.reverse(data[offset + i * 8]) >> 24;
			out[i * 7 + 0] = (byte) (((bits << 0) & 0x80) | data[offset + i * 8 + 1]);
			out[i * 7 + 1] = (byte) (((bits << 1) & 0x80) | data[offset + i * 8 + 2]);
			out[i * 7 + 2] = (byte) (((bits << 2) & 0x80) | data[offset + i * 8 + 3]);
			out[i * 7 + 3] = (byte) (((bits << 3) & 0x80) | data[offset + i * 8 + 4]);
			out[i * 7 + 4] = (byte) (((bits << 4) & 0x80) | data[offset + i * 8 + 5]);
			out[i * 7 + 5] = (byte) (((bits << 5) & 0x80) | data[offset + i * 8 + 6]);
			out[i * 7 + 6] = (byte) (((bits << 6) & 0x80) | data[offset + i * 8 + 7]);
		}
		if(remainder > 0) {
			int bits = Integer.reverse(data[offset + blocks * 8]) >> 24;
			for(int i = 0; i < remainder; i++) {
				out[blocks * 7 + i] = (byte) (((bits << i) & 0x80) | data[offset + blocks * 8 + i + 1]);
			}
		}
		return out;
	}

	public static byte[] toMIDI(byte[] data, int offset, int length) {
		int blocks = length / 7;
		int remainder = length - blocks * 7;
		int add = 0;
		if(remainder > 0) {
			add = 1;
		}
		byte[] out = new byte[blocks * 8 + remainder + add];
		toMIDI(out, 0, data, offset, length);
		return out;
	}

	public static void toMIDI(byte[] out, int outoff, byte[] data, int offset, int length) {
		int blocks = length / 7;
		int remainder = length - blocks * 7;
		for(int i = 0; i < blocks; i++) {
			int bits = ((data[offset + i * 7 + 0] >>> 0) & 0x80) |
					((data[offset + i * 7 + 1] >>> 1) & 0x40) |
					((data[offset + i * 7 + 2] >>> 2) & 0x20) |
					((data[offset + i * 7 + 3] >>> 3) & 0x10) |
					((data[offset + i * 7 + 4] >>> 4) & 0x08) |
					((data[offset + i * 7 + 5] >>> 5) & 0x04) |
					((data[offset + i * 7 + 6] >>> 6) & 0x02);
			bits = Integer.reverse(bits) >> 24;
			out[outoff + i * 8 + 0] = (byte) (bits & 0x7F);
			out[outoff + i * 8 + 1] = (byte) (data[offset + i * 7 + 0] & 0x7F);
			out[outoff + i * 8 + 2] = (byte) (data[offset + i * 7 + 1] & 0x7F);
			out[outoff + i * 8 + 3] = (byte) (data[offset + i * 7 + 2] & 0x7F);
			out[outoff + i * 8 + 4] = (byte) (data[offset + i * 7 + 3] & 0x7F);
			out[outoff + i * 8 + 5] = (byte) (data[offset + i * 7 + 4] & 0x7F);
			out[outoff + i * 8 + 6] = (byte) (data[offset + i * 7 + 5] & 0x7F);
			out[outoff + i * 8 + 7] = (byte) (data[offset + i * 7 + 6] & 0x7F);
		}
		if(remainder > 0) {
			int bits = 0;
			for(int i = 0; i < remainder; i++) {
				bits |= (data[offset + blocks * 7 + i] & 0x80) >>> i;
			}
			bits = Integer.reverse(bits) >> 24;
			out[outoff + blocks * 8] = (byte) (bits & 0x7F);
			for(int i = 0; i < remainder; i++) {
				out[outoff + blocks * 8 + (i + 1)] = (byte) (data[offset + blocks * 7 + i] & 0x7F);
			}
		}
	}

	public static void print(String prefix, byte[] data) {
		print(prefix, data, 0, data.length, 0);
	}

	public static void print(String prefix, byte[] data, int offset, int length, int space) {
		System.out.printf("%s:", prefix);
		for(int i = 0; i < space; i++) {
			System.out.print(" ");
		}
		for(int i = 0; i < length; i++) {
			System.out.printf(" %02X", data[offset + i] & 0xFF);
		}
		System.out.println();
	}
}
