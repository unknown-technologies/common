package com.unknown.net.shownet;

import java.util.Arrays;

import com.unknown.util.io.Endianess;

public class InterfaceId {
	private final int[] id = new int[3];

	public InterfaceId(int[] id) {
		if(id.length != this.id.length) {
			throw new IllegalArgumentException("Invalid interface ID");
		}
		System.arraycopy(id, 0, this.id, 0, id.length);
	}

	public int[] getInterfaceId() {
		return Arrays.copyOf(id, id.length);
	}

	public int get(int index) {
		return id[index];
	}

	public String getDecoded() {
		byte[] bytes = new byte[4];
		char[] chars = new char[8];
		for(int i = 0; i < 2; i++) {
			Endianess.set32bitLE(bytes, 0, id[i + 1]);
			for(int j = 0; j < 4; j++) {
				int b = Byte.toUnsignedInt(bytes[j]);
				if(b < 0x20 || b >= 0x7F) {
					b = '.';
				}
				chars[i * 4 + j] = (char) b;
			}
		}
		return String.format("%08X:%02X:%s", id[0], id[1] & 0xFF, new String(chars, 1, chars.length - 1));
	}

	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof InterfaceId)) {
			return false;
		}

		InterfaceId i = (InterfaceId) o;
		return Arrays.equals(id, i.id);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(id);
	}

	@Override
	public String toString() {
		return String.format("%08X:%08X:%08X", id[0] ^ 0xD49A3433, id[1] ^ 0xD49A3433, id[2] ^ 0xD49A3433);
	}
}
