package com.unknown.platform.windows.registry;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.unknown.platform.windows.Windows;

public class RegistryValueInfo {
	private final String name;
	private final int type;
	private final byte[] data;

	public RegistryValueInfo(char[] name, int type, byte[] data) {
		this.name = Windows.lpwstr(name);
		this.type = type;
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public byte[] getData() {
		return data;
	}

	public String getSZ() {
		if(type != RegistryKey.REG_SZ) {
			throw new IllegalStateException("value is not of type REG_SZ");
		}

		return Windows.lpwstr(data);
	}

	public int getDWORD() {
		if(type != RegistryKey.REG_DWORD) {
			throw new IllegalStateException("value is not of type REG_DWORD");
		}

		return Byte.toUnsignedInt(data[0]) | (Byte.toUnsignedInt(data[1]) << 8) |
				(Byte.toUnsignedInt(data[2]) << 16) | (Byte.toUnsignedInt(data[3]) << 24);
	}

	private static String escape(String s) {
		StringBuilder buf = new StringBuilder(s.length());
		buf.append('"');
		for(char c : s.toCharArray()) {
			switch(c) {
			case '\r':
				buf.append("\\r");
				break;
			case '\n':
				buf.append("\\n");
				break;
			case 0:
				buf.append("\\0");
				break;
			case '\t':
				buf.append("\\t");
			case '\\':
				buf.append("\\\\");
				break;
			case '"':
				buf.append("\\\"");
				break;
			default:
				if(c < 0x20 || c > 0x7F) {
					buf.append(String.format("\\u%04X", c));
				} else {
					buf.append(c);
				}
			}
		}
		buf.append('"');
		return buf.toString();
	}

	private String decodeData() {
		switch(type) {
		case RegistryKey.REG_BINARY:
			return IntStream.range(0, data.length).map(i -> Byte.toUnsignedInt(data[i]))
					.mapToObj(i -> String.format("%02X", i)).collect(Collectors.joining(" "));
		case RegistryKey.REG_SZ:
			return escape(Windows.lpwstr(data));
		case RegistryKey.REG_NONE:
			return "(none)";
		case RegistryKey.REG_DWORD:
			return "0x" + Integer.toUnsignedString(Byte.toUnsignedInt(data[0]) |
					(Byte.toUnsignedInt(data[1]) << 8) | (Byte.toUnsignedInt(data[2]) << 16) |
					(Byte.toUnsignedInt(data[3]) << 24), 16);
		case RegistryKey.REG_DWORD_BIG_ENDIAN:
			return "0x" + Integer.toUnsignedString(Byte.toUnsignedInt(data[3]) |
					(Byte.toUnsignedInt(data[2]) << 8) | (Byte.toUnsignedInt(data[1]) << 16) |
					(Byte.toUnsignedInt(data[0]) << 24), 16);
		case RegistryKey.REG_QWORD:
			return "0x" + Long.toUnsignedString(Byte.toUnsignedLong(data[0]) |
					(Byte.toUnsignedLong(data[1]) << 8) | (Byte.toUnsignedLong(data[2]) << 16) |
					(Byte.toUnsignedLong(data[3]) << 24) | (Byte.toUnsignedLong(data[4]) << 32) |
					(Byte.toUnsignedLong(data[5]) << 40) | (Byte.toUnsignedLong(data[6]) << 48) |
					(Byte.toUnsignedLong(data[7]) << 56), 16);
		default:
			return "(" + RegistryKey.getType(type) + ")";
		}
	}

	@Override
	public String toString() {
		if(data != null) {
			return "RegistryValueInfo[name=" + escape(name) + ",type=" + RegistryKey.getType(type) +
					",data=" + decodeData() + "]";
		} else {
			return "RegistryValueInfo[name=" + escape(name) + ",type=" + RegistryKey.getType(type) + "]";
		}
	}
}
