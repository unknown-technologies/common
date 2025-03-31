package com.unknown.net.artnet;

import java.io.IOException;

import com.unknown.util.io.Endianess;

public abstract class ArtNetPacket {
	private static final byte[] HEADER = { 'A', 'r', 't', '-', 'N', 'e', 't', 0 };

	private final int opcode;

	protected ArtNetPacket(int opcode) {
		this.opcode = opcode;
	}

	public int getOpcode() {
		return opcode;
	}

	protected abstract int getPayloadSize();

	protected abstract void fill(byte[] data, int offset);

	public static ArtNetPacket read(byte[] data) throws IOException {
		return read(data, 0, data.length);
	}

	public static ArtNetPacket read(byte[] data, int offset, int length) throws IOException {
		if(length < 12) {
			throw new IOException("not a valid ArtNet packet");
		}

		for(int i = 0; i < HEADER.length; i++) {
			if(data[offset + i] != HEADER[i]) {
				throw new IOException("not a valid ArtNet packet");
			}
		}

		if(data[10] != 0 || data[11] != 14) {
			throw new IOException("unknown ArtNet protocol version");
		}

		int opcode = Short.toUnsignedInt(Endianess.get16bitLE(data, offset + 8));
		switch(opcode) {
		case Opcode.OpDmx:
			return new ArtDMXPacket(data, offset + 12, length - 12);
		default:
			return null;
		}
	}

	public byte[] write() {
		byte[] data = new byte[getPayloadSize() + 12];
		System.arraycopy(HEADER, 0, data, 0, HEADER.length);
		data[8] = (byte) opcode;
		data[9] = (byte) (opcode >> 8);
		data[10] = 0; // ProtVerHigh
		data[11] = 14; // ProtVerLow
		fill(data, 12);
		return data;
	}
}
