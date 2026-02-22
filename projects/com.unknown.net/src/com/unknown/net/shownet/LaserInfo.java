package com.unknown.net.shownet;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.unknown.util.io.Endianess;

public class LaserInfo {
	private final InetAddress address;
	private final int bootloader;
	private final int firmware;
	private final int hardwareId;
	private final int[] interfaceId;
	private final short[] license;
	private final byte[] unknown;

	private final long timestamp = System.currentTimeMillis();

	public LaserInfo(InetAddress address, byte[] packet) {
		this.address = address;

		bootloader = Endianess.get32bitLE(packet, 32) ^ 0xD49A3433;
		firmware = Endianess.get32bitLE(packet, 36) ^ 0xD49A3433;
		interfaceId = new int[3];
		for(int i = 0; i < interfaceId.length; i++) {
			interfaceId[i] = Endianess.get32bitLE(packet, 40 + i * 4) ^ 0xD49A3433;
		}
		hardwareId = Endianess.get32bitLE(packet, 52);
		license = new short[8];
		for(int i = 0; i < license.length; i++) {
			license[i] = Endianess.get16bitLE(packet, 56 + i * 2);
		}
		unknown = new byte[32];
		for(int i = 0; i < unknown.length; i++) {
			unknown[i] = packet[72 + i];
		}
	}

	public int geteBootloader() {
		return bootloader;
	}

	public int getFirmware() {
		return firmware;
	}

	public int getHardwareID() {
		return hardwareId;
	}

	public int[] getInterfaceID() {
		return Arrays.copyOf(interfaceId, interfaceId.length);
	}

	public short[] getLicense() {
		return Arrays.copyOf(license, license.length);
	}

	public String getBootloaderString() {
		return Integer.toUnsignedString(bootloader);
	}

	public String getFirmwareString() {
		return Integer.toUnsignedString(firmware);
	}

	public String getInterfaceIDString() {
		return String.format("%08X:%08X:%08X", interfaceId[0] ^ 0xD49A3433, interfaceId[1] ^ 0xD49A3433,
				interfaceId[2] ^ 0xD49A3433);
	}

	public String getDecodedInterfaceIDString() {
		byte[] bytes = new byte[4];
		char[] chars = new char[8];
		for(int i = 0; i < 2; i++) {
			Endianess.set32bitLE(bytes, 0, interfaceId[i + 1]);
			for(int j = 0; j < 4; j++) {
				int b = Byte.toUnsignedInt(bytes[j]);
				if(b < 0x20 || b >= 0x7F) {
					b = '.';
				}
				chars[i * 4 + j] = (char) b;
			}
		}
		return String.format("%08X:%02X:%s", interfaceId[0], interfaceId[1] & 0xFF,
				new String(chars, 1, chars.length - 1));
	}

	public String getLicenseString() {
		List<String> tokens = new ArrayList<>();
		for(short id : license) {
			switch(Short.toUnsignedInt(id)) {
			case 0xF3CC:
				tokens.add("/Open");
				break;
			case 0xA:
				tokens.add("/Phoenix");
				break;
			case 0x16BD:
				tokens.add("/HE-Laserscan");
				break;
			case 0x3D69:
				tokens.add("/Pango");
				break;
			case 0x1730:
				tokens.add("/Showeditor");
				break;
			case 0x70B8:
				tokens.add("/Showcontroller");
				break;
			case 0x4008:
				tokens.add("/Dummy1");
				break;
			case 0x76E8:
				tokens.add("/Dummy2");
				break;
			case 0x2B43:
				tokens.add("/Dummy3");
				break;
			case 0x5DB0:
				tokens.add("/Dummy4");
				break;
			case 0x134A:
				tokens.add("/Dummy5");
				break;
			case 0x5E7A:
				tokens.add("/Dummy6");
				break;
			}
		}
		return tokens.stream().collect(Collectors.joining(" "));
	}

	public long getDiscoveryTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "Laser[addr=" + address.getHostAddress() + ",bootloader=" + getBootloaderString() +
				",firmware=" + getFirmwareString() + ",hwid=" + getHardwareID() + ",interface=" +
				getInterfaceIDString() + "(" + getDecodedInterfaceIDString() + "),licenses=[" +
				getLicenseString() + "]]";
	}
}
