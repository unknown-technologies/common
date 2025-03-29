package com.unknown.audio.midi.smf;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public class MIDIEvent extends Event {
	/* MIDI messages */
	public static final byte NOTE_OFF = (byte) 0x80;
	public static final byte NOTE_ON = (byte) 0x90;
	public static final byte POLY_PRESSURE = (byte) 0xA0;
	public static final byte CTRL_CHANGE = (byte) 0xB0;
	public static final byte PROG_CHANGE = (byte) 0xC0;
	public static final byte CHAN_PRESSURE = (byte) 0xD0;
	public static final byte PITCH_BEND = (byte) 0xE0;

	/* CC names */
	public static final byte CC_MODULATION = 1;
	public static final byte CC_BREATH = 2;
	public static final byte CC_FOOT = 4;
	public static final byte CC_PORTAMENTO = 5;
	public static final byte CC_VOLUME = 7;
	public static final byte CC_BALANCE = 8;
	public static final byte CC_PAN = 10;
	public static final byte CC_EXPRESSION = 11;
	public static final byte CC_DAMPER = 64;
	public static final byte CC_PORT_EN = 65;
	public static final byte CC_SOSTENUTO = 66;
	public static final byte CC_SOFT = 67;
	public static final byte CC_LEGATO = 68;

	public static final byte CC_ALL_SND_OFF = 120;
	public static final byte CC_RESET_CTRL = 121;
	public static final byte CC_LOCAL = 122;
	public static final byte CC_ALL_NOTE_OFF = 123;
	public static final byte CC_OMNI_OFF = 124;
	public static final byte CC_OMNI_ON = 125;
	public static final byte CC_MONO_MODE = 126;
	public static final byte CC_POLY_MODE = 127;

	private byte status;
	private byte data1;
	private byte data2;

	public MIDIEvent(long time) {
		super(time);
		status = 0;
		data1 = 0;
		data2 = 0;
	}

	public MIDIEvent(long time, byte status) {
		super(time);
		this.status = status;
		data1 = 0;
		data2 = 0;
	}

	public MIDIEvent(long time, byte status, byte data1) {
		super(time);
		this.status = status;
		this.data1 = data1;
		data2 = 0;
	}

	public MIDIEvent(long time, byte status, byte data1, byte data2) {
		super(time);
		this.status = status;
		this.data1 = data1;
		this.data2 = data2;
	}

	public MIDIEvent(long time, byte status, WordInputStream in) throws IOException {
		super(time);
		this.status = status;
		int size = getSize() - 1;
		if(size == 1) {
			data1 = in.read8bit();
		} else {
			data1 = in.read8bit();
			data2 = in.read8bit();
		}
	}

	public byte getStatus() {
		return status;
	}

	public byte getCommand() {
		return (byte) (status & 0xF0);
	}

	public byte getChannel() {
		return (byte) (status & 0x0F);
	}

	public byte getData1() {
		return data1;
	}

	public byte getData2() {
		return data2;
	}

	public int getBend() {
		if(getCommand() != PITCH_BEND) {
			throw new IllegalStateException(
					String.format("not a pitch bend: 0x%02X", Byte.toUnsignedInt(getCommand())));
		}

		return getBend(data1, data2);
	}

	protected static int getSize(byte status) {
		switch((byte) (status & 0xF0)) {
		case NOTE_OFF:
		case NOTE_ON:
		case POLY_PRESSURE:
		case CTRL_CHANGE:
		case PITCH_BEND:
			return 3;
		case PROG_CHANGE:
		case CHAN_PRESSURE:
			return 2;
		default:
			throw new IllegalStateException(
					String.format("invalid status byte: 0x%02X", Byte.toUnsignedInt(status)));
		}
	}

	@Override
	protected int getSize() {
		return getSize(getCommand());
	}

	@Override
	protected void writeData(WordOutputStream out) throws IOException {
		int size = getSize();
		switch(size) {
		case 2:
			out.write8bit(status);
			out.write8bit(data1);
			break;
		case 3:
			out.write8bit(status);
			out.write8bit(data1);
			out.write8bit(data2);
			break;
		}
	}

	public static int getBend(int lsb, int msb) {
		int value = (lsb & 0x7F) | ((msb & 0x7F) << 7);
		return value - 8192;
	}

	// interoperability with javax.sound.midi
	public ShortMessage toShortMessage() throws InvalidMidiDataException {
		return new ShortMessage(Byte.toUnsignedInt(status), Byte.toUnsignedInt(data1),
				Byte.toUnsignedInt(data2));
	}

	@Override
	public String toString() {
		if(getSize() == 2) {
			return String.format("MIDIMessage[%02X %02X]", Byte.toUnsignedInt(status),
					Byte.toUnsignedInt(data1));
		} else {
			return String.format("MIDIMessage[%02X %02X %02X]", Byte.toUnsignedInt(status),
					Byte.toUnsignedInt(data1), Byte.toUnsignedInt(data2));
		}
	}
}
