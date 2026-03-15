package com.unknown.platform.windows.registry;

import java.util.HashMap;
import java.util.Map;

import com.unknown.platform.Platform;
import com.unknown.platform.windows.WinError;
import com.unknown.platform.windows.Windows;

public class RegistryKey implements AutoCloseable {
	public static final RegistryKey HKEY_CLASSES_ROOT = new RegistryKey(0x80000000);
	public static final RegistryKey HKEY_CURRENT_USER = new RegistryKey(0x80000001);
	public static final RegistryKey HKEY_LOCAL_MACHINE = new RegistryKey(0x80000002);
	public static final RegistryKey HKEY_USERS = new RegistryKey(0x80000003);
	public static final RegistryKey HKEY_PERFORMANCE_DATA = new RegistryKey(0x80000004);
	public static final RegistryKey HKEY_PERFORMANCE_TEXT = new RegistryKey(0x80000050);
	public static final RegistryKey HKEY_PERFORMANCE_NLSTEXT = new RegistryKey(0x80000060);
	public static final RegistryKey HKEY_CURRENT_CONFIG = new RegistryKey(0x80000005);
	public static final RegistryKey HKEY_DYN_DATA = new RegistryKey(0x80000006);

	public static final int KEY_ALL_ACCESS = 0xF003F;
	public static final int KEY_CREATE_LINK = 0x0020;
	public static final int KEY_CREATE_SUB_KEY = 0x0004;
	public static final int KEY_ENUMERATE_SUB_KEYS = 0x0008;
	public static final int KEY_EXECUTE = 0x20019;
	public static final int KEY_NOTIFY = 0x0010;
	public static final int KEY_QUERY_VALUE = 0x0001;
	public static final int KEY_READ = 0x20019;
	public static final int KEY_SET_VALUE = 0x0002;
	public static final int KEY_WOW64_32KEY = 0x0200;
	public static final int KEY_WOW64_64KEY = 0x0100;
	public static final int KEY_WRITE = 0x20006;

	public static final int REG_NONE = 0;
	public static final int REG_SZ = 1;
	public static final int REG_EXPAND_SZ = 2;
	public static final int REG_BINARY = 3;
	public static final int REG_DWORD = 4;
	public static final int REG_DWORD_LITTLE_ENDIAN = 4;
	public static final int REG_DWORD_BIG_ENDIAN = 5;
	public static final int REG_LINK = 6;
	public static final int REG_MULTI_SZ = 7;
	public static final int REG_RESOURCE_LIST = 8;
	public static final int REG_FULL_RESOURCE_DESCRIPTOR = 9;
	public static final int REG_RESOURCE_REQUIREMENTS_LIST = 10;
	public static final int REG_QWORD = 11;
	public static final int REG_QWORD_LITTLE_ENDIAN = 11;

	private static final Map<Integer, String> REG_TYPES;

	private long handle;

	private boolean closed = false;
	private final boolean closable;

	public RegistryKey(RegistryKey key, String subkey, int permissions) throws WinError {
		closable = true;
		handle = open(key.handle, Windows.lpwstr(subkey), permissions);
	}

	private RegistryKey(long handle) {
		this.handle = handle;
		closable = false;
	}

	private native static long open(long handle, char[] subkey, int permissions) throws WinError;

	private native static void close(long handle) throws WinError;

	private native static byte[] query(long handle, char[] name, int type, int size) throws WinError;

	private native static RegistryKeyInfo queryInfoKey(long handle) throws WinError;

	private native static char[] enumKey(long handle, int index, int namelen) throws WinError;

	private native static RegistryValueInfo enumValue(long handle, int index, boolean data, int namelen,
			int datalen)
			throws WinError;

	@Override
	public void close() throws WinError {
		if(!closed) {
			close(handle);
			closed = true;
		}
	}

	public RegistryKeyInfo info() throws WinError {
		return queryInfoKey(handle);
	}

	public byte[] query(String name, int type) throws WinError {
		return query(handle, Windows.lpwstr(name), type, 0);
	}

	public String querySZ(String name) throws WinError {
		byte[] data = query(name, REG_SZ);
		return Windows.lpwstr(data);
	}

	public int queryDWORD(String name) throws WinError {
		byte[] data = query(name, REG_DWORD);
		return Byte.toUnsignedInt(data[0]) | (Byte.toUnsignedInt(data[1]) << 8) |
				(Byte.toUnsignedInt(data[2]) << 16) | (Byte.toUnsignedInt(data[3]) << 24);
	}

	public byte[] queryBinary(String name) throws WinError {
		return query(name, REG_BINARY);
	}

	public String[] enumerateKeys() throws WinError {
		RegistryKeyInfo info = info();
		String[] names = new String[info.getSubkeys()];
		for(int i = 0; i < info.getSubkeys(); i++) {
			char[] name = enumKey(handle, i, info.getMaxSubkeyLength());
			names[i] = Windows.lpwstr(name);
		}
		return names;
	}

	public RegistryValueInfo[] enumerateValues() throws WinError {
		return enumerateValues(true);
	}

	public RegistryValueInfo[] enumerateValues(boolean getData) throws WinError {
		RegistryKeyInfo info = info();
		RegistryValueInfo[] values = new RegistryValueInfo[info.getValues()];
		for(int i = 0; i < info.getValues(); i++) {
			values[i] = enumValue(handle, i, getData, info.getMaxValueNameLength(),
					info.getMaxValueLength());
		}
		return values;
	}

	static {
		Platform.loadNativeLibrary();

		REG_TYPES = new HashMap<>();
		REG_TYPES.put(REG_NONE, "REG_NONE");
		REG_TYPES.put(REG_SZ, "REG_SZ");
		REG_TYPES.put(REG_EXPAND_SZ, "REG_EXPAND_SZ");
		REG_TYPES.put(REG_BINARY, "REG_BINARY");
		REG_TYPES.put(REG_DWORD, "REG_DWORD");
		// REG_TYPES.put(REG_DWORD_LITTLE_ENDIAN, "REG_DWORD_LITTLE_ENDIAN");
		REG_TYPES.put(REG_DWORD_BIG_ENDIAN, "REG_DWORD_BIG_ENDIAN");
		REG_TYPES.put(REG_LINK, "REG_LINK");
		REG_TYPES.put(REG_MULTI_SZ, "REG_MULTI_SZ");
		REG_TYPES.put(REG_RESOURCE_LIST, "REG_RESOURCE_LIST");
		REG_TYPES.put(REG_FULL_RESOURCE_DESCRIPTOR, "REG_FULL_RESOURCE_DESCRIPTOR");
		REG_TYPES.put(REG_RESOURCE_REQUIREMENTS_LIST, "REG_RESOURCE_REQUIREMENTS_LIST");
		REG_TYPES.put(REG_QWORD, "REG_QWORD");
		// REG_TYPES.put(REG_QWORD_LITTLE_ENDIAN, "REG_QWORD_LITTLE_ENDIAN");
	}

	public static String getType(int type) {
		String name = REG_TYPES.get(type);
		if(name != null) {
			return name;
		} else {
			return Integer.toString(type);
		}
	}

	@Override
	public String toString() {
		return "RegistryKey[0x" + Long.toUnsignedString(handle, 16) + "]";
	}
}
