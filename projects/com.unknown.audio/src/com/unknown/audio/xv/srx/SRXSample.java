package com.unknown.audio.xv.srx;

public class SRXSample {
	public static final byte LOOP_MODE_FORWARD = 0;
	public static final byte LOOP_MODE_PING_PONG = 1;
	public static final byte LOOP_MODE_NONE = 2;
	public static final byte LOOP_MODE_REVERSE = 6;

	private SRXROM rom;
	private int offset;

	public SRXSample(SRXROM rom) {
		this.rom = rom;
	}

	public byte getVolume() {
		return rom.getByte(offset);
	}

	public byte getLoopMode() {
		return rom.getByte(offset + 1);
	}

	public byte getRootKey() {
		return rom.getByte(offset + 2);
	}

	public short getFineTune() {
		byte b1 = rom.getByte(offset + 6);
		byte b2 = rom.getByte(offset + 7);
		return (short) ((b1 << 8 | Byte.toUnsignedInt(b2)) - 0x400);
	}

	public short getLoopFineTune() {
		byte b1 = rom.getByte(offset + 8);
		byte b2 = rom.getByte(offset + 9);
		return (short) ((b1 << 8 | Byte.toUnsignedInt(b2)) - 0x400);
	}

	public int getSampleRate() {
		byte b1 = rom.getByte(offset + 0x0A);
		byte b2 = rom.getByte(offset + 0x0B);
		return (Byte.toUnsignedInt(b1) << 8 | Byte.toUnsignedInt(b2)) * 100;
	}

	public int getSampleStart() {
		byte b1 = rom.getByte(offset + 0x0C);
		byte b2 = rom.getByte(offset + 0x0D);
		byte b3 = rom.getByte(offset + 0x0E);
		byte b4 = rom.getByte(offset + 0x0F);
		return Byte.toUnsignedInt(b1) << 24 | Byte.toUnsignedInt(b2) << 16 | Byte.toUnsignedInt(b3) << 8 |
				Byte.toUnsignedInt(b4);
	}

	public int getSampleLoop() {
		byte b1 = rom.getByte(offset + 0x10);
		byte b2 = rom.getByte(offset + 0x11);
		byte b3 = rom.getByte(offset + 0x12);
		byte b4 = rom.getByte(offset + 0x13);
		return Byte.toUnsignedInt(b1) << 24 | Byte.toUnsignedInt(b2) << 16 | Byte.toUnsignedInt(b3) << 8 |
				Byte.toUnsignedInt(b4);
	}

	public int getSampleEnd() {
		byte b1 = rom.getByte(offset + 0x14);
		byte b2 = rom.getByte(offset + 0x15);
		byte b3 = rom.getByte(offset + 0x16);
		byte b4 = rom.getByte(offset + 0x17);
		return Byte.toUnsignedInt(b1) << 24 | Byte.toUnsignedInt(b2) << 16 | Byte.toUnsignedInt(b3) << 8 |
				Byte.toUnsignedInt(b4);
	}
}
