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
	private final InterfaceId interfaceId;
	private final short[] license;
	private final byte[] unknown;

	private final long timestamp = System.currentTimeMillis();

	public LaserInfo(InetAddress address, byte[] packet) {
		this.address = address;

		bootloader = Endianess.get32bitLE(packet, 32) ^ 0xD49A3433;
		firmware = Endianess.get32bitLE(packet, 36) ^ 0xD49A3433;
		int[] id = new int[3];
		for(int i = 0; i < id.length; i++) {
			id[i] = Endianess.get32bitLE(packet, 40 + i * 4) ^ 0xD49A3433;
		}
		interfaceId = new InterfaceId(id);
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

	public InetAddress getAddress() {
		return address;
	}

	public int geteBootloader() {
		return bootloader;
	}

	public int getFirmware() {
		return firmware;
	}

	public int getHardwareId() {
		return hardwareId;
	}

	public InterfaceId getInterfaceId() {
		return interfaceId;
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
				",firmware=" + getFirmwareString() + ",hwid=" + getHardwareId() + ",interface=" +
				interfaceId.toString() + "(" + interfaceId.getDecoded() + "),licenses=[" +
				getLicenseString() + "]]";
	}
}
