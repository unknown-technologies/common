package com.unknown.net.artnet;

import java.io.IOException;
import java.util.Arrays;

import com.unknown.util.io.Endianess;

public class ArtDMXPacket extends ArtNetPacket {
	private int sequence; // 0 disables this feature
	private int universe;
	private int physical;
	private byte[] parameters;

	public ArtDMXPacket(int universe, byte[] parameters) {
		this(universe, 0, parameters);
	}

	public ArtDMXPacket(int universe, int physical, byte[] parameters) {
		super(Opcode.OpDmx);

		if(parameters.length > 512 || (parameters.length & 1) == 1) {
			throw new IllegalArgumentException("invalid parameters");
		}

		if(physical < 0 || physical > 255) {
			throw new IllegalArgumentException("invalid physical port");
		}

		if(universe < 0 || universe > 0x7FFF) {
			throw new IllegalArgumentException("invalid universe");
		}

		this.universe = universe;
		this.physical = physical;
		this.parameters = parameters;
	}

	ArtDMXPacket(byte[] data, int offset, int length) throws IOException {
		super(Opcode.OpDmx);

		if(length < 6) {
			throw new IOException("not a valid ArtDMX packet");
		}

		sequence = Byte.toUnsignedInt(data[offset]);
		physical = Byte.toUnsignedInt(data[offset + 1]);
		universe = Short.toUnsignedInt(Endianess.get16bitLE(data, offset + 2));
		int len = Short.toUnsignedInt(Endianess.get16bitBE(data, offset + 4));
		parameters = Arrays.copyOfRange(data, offset + 6, offset + 6 + len);
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getUniverse() {
		return universe;
	}

	public int getPhysical() {
		return physical;
	}

	public byte[] getParameters() {
		return parameters;
	}

	@Override
	protected int getPayloadSize() {
		return parameters.length + 6;
	}

	@Override
	protected void fill(byte[] data, int offset) {
		data[offset + 0] = (byte) sequence;
		data[offset + 1] = (byte) physical;
		data[offset + 2] = (byte) universe;
		data[offset + 3] = (byte) (universe >> 8);
		data[offset + 4] = (byte) (parameters.length >> 8);
		data[offset + 5] = (byte) parameters.length;
		System.arraycopy(parameters, 0, data, offset + 6, parameters.length);
	}
}
