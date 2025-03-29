package com.unknown.audio.xm;

import java.io.IOException;
import java.io.InputStream;

public class Note {
	public final static int KEY_OFF = 97;
	public final static int NOTE_C0 = 0x01;
	public final static int NOTE_CS0 = 0x02;
	public final static int NOTE_D0 = 0x03;
	public final static int NOTE_DS0 = 0x04;
	public final static int NOTE_E0 = 0x05;
	public final static int NOTE_F0 = 0x06;
	public final static int NOTE_FS0 = 0x07;
	public final static int NOTE_G0 = 0x08;
	public final static int NOTE_GS0 = 0x09;
	public final static int NOTE_A0 = 0x0A;
	public final static int NOTE_AS0 = 0x0B;
	public final static int NOTE_H0 = 0x0C;
	public final static int NOTE_C1 = 0x0D;
	public final static int NOTE_CS1 = 0x0E;
	public final static int NOTE_D1 = 0x0F;
	public final static int NOTE_DS1 = 0x10;
	public final static int NOTE_E1 = 0x11;
	public final static int NOTE_F1 = 0x12;
	public final static int NOTE_FS1 = 0x13;
	public final static int NOTE_G1 = 0x14;
	public final static int NOTE_GS1 = 0x15;
	public final static int NOTE_A1 = 0x16;
	public final static int NOTE_AS1 = 0x17;
	public final static int NOTE_H1 = 0x18;
	public final static int NOTE_C2 = 0x19;
	public final static int NOTE_CS2 = 0x1A;
	public final static int NOTE_D2 = 0x1B;
	public final static int NOTE_DS2 = 0x1C;
	public final static int NOTE_E2 = 0x1D;
	public final static int NOTE_F2 = 0x1E;
	public final static int NOTE_FS2 = 0x1F;
	public final static int NOTE_G2 = 0x20;
	public final static int NOTE_GS2 = 0x21;
	public final static int NOTE_A2 = 0x22;
	public final static int NOTE_AS2 = 0x23;
	public final static int NOTE_H2 = 0x24;
	public final static int NOTE_C3 = 0x25;
	public final static int NOTE_CS3 = 0x26;
	public final static int NOTE_D3 = 0x27;
	public final static int NOTE_DS3 = 0x28;
	public final static int NOTE_E3 = 0x29;
	public final static int NOTE_F3 = 0x2A;
	public final static int NOTE_FS3 = 0x2B;
	public final static int NOTE_G3 = 0x2C;
	public final static int NOTE_GS3 = 0x2D;
	public final static int NOTE_A3 = 0x2E;
	public final static int NOTE_AS3 = 0x2F;
	public final static int NOTE_H3 = 0x30;
	public final static int NOTE_C4 = 0x31;
	public final static int NOTE_CS4 = 0x32;
	public final static int NOTE_D4 = 0x33;
	public final static int NOTE_DS4 = 0x34;
	public final static int NOTE_E4 = 0x35;
	public final static int NOTE_F4 = 0x36;
	public final static int NOTE_FS4 = 0x37;
	public final static int NOTE_G4 = 0x38;
	public final static int NOTE_GS4 = 0x39;
	public final static int NOTE_A4 = 0x3A;
	public final static int NOTE_AS4 = 0x3B;
	public final static int NOTE_H4 = 0x3C;
	public final static int NOTE_C5 = 0x3D;
	public final static int NOTE_CS5 = 0x3E;
	public final static int NOTE_D5 = 0x3F;
	public final static int NOTE_DS5 = 0x40;
	public final static int NOTE_E5 = 0x41;
	public final static int NOTE_F5 = 0x42;
	public final static int NOTE_FS5 = 0x43;
	public final static int NOTE_G5 = 0x44;
	public final static int NOTE_GS5 = 0x45;
	public final static int NOTE_A5 = 0x46;
	public final static int NOTE_AS5 = 0x47;
	public final static int NOTE_H5 = 0x48;
	public final static int NOTE_C6 = 0x49;
	public final static int NOTE_CS6 = 0x4A;
	public final static int NOTE_D6 = 0x4B;
	public final static int NOTE_DS6 = 0x4C;
	public final static int NOTE_E6 = 0x4D;
	public final static int NOTE_F6 = 0x4E;
	public final static int NOTE_FS6 = 0x4F;
	public final static int NOTE_G6 = 0x50;
	public final static int NOTE_GS6 = 0x51;
	public final static int NOTE_A6 = 0x52;
	public final static int NOTE_AS6 = 0x53;
	public final static int NOTE_H6 = 0x54;
	public final static int NOTE_C7 = 0x55;
	public final static int NOTE_CS7 = 0x56;
	public final static int NOTE_D7 = 0x57;
	public final static int NOTE_DS7 = 0x58;
	public final static int NOTE_E7 = 0x59;
	public final static int NOTE_F7 = 0x5A;
	public final static int NOTE_FS7 = 0x5B;
	public final static int NOTE_G7 = 0x5C;
	public final static int NOTE_GS7 = 0x5D;
	public final static int NOTE_A7 = 0x5E;
	public final static int NOTE_AS7 = 0x5F;
	public final static int NOTE_H7 = 0x60;

	public final static String[] names = { "C", "C#", "D", "D#", "E", "F",
			"F#", "G", "G#", "A", "A#", "H" };

	public static String getName(int i) {
		int n = i - 1; // zero-offset
		int octave = n / 12;
		int note = n % 12;
		return names[note] + (names[note].length() == 1 ? "-" : "") + octave;
	}

	private int note;
	private int instrument;
	private int volume;
	private int effectType;
	private int effectParameter;

	public Note(InputStream in) throws IOException {
		read(in);
	}

	public Note() {
		this(0, 0, 0, 0, 0);
	}

	public Note(int note, int instrument, int volume, int effectType,
			int effectParameter) {
		this.note = note;
		this.instrument = instrument;
		this.volume = volume;
		this.effectType = effectType;
		this.effectParameter = effectParameter;
	}

	private void read(InputStream in) throws IOException {
		note = 0;
		instrument = 0;
		volume = 0;
		effectType = 0;
		effectParameter = 0;
		int tmp = in.read();
		if((tmp & 0x80) != 0) { // compressed
			if((tmp & 0x01) != 0) {
				note = in.read();
			}
			if((tmp & 0x02) != 0) {
				instrument = in.read();
			}
			if((tmp & 0x04) != 0) {
				volume = in.read();
			}
			if((tmp & 0x08) != 0) {
				effectType = in.read();
			}
			if((tmp & 0x10) != 0) {
				effectParameter = in.read();
			}
		} else {
			note = tmp;
			instrument = in.read();
			volume = in.read();
			effectType = in.read();
			effectParameter = in.read();
		}
	}

	public boolean isKeyOff() {
		return note == KEY_OFF;
	}

	public boolean isEmpty() {
		return note == 0;
	}

	public int getNote() {
		return note;
	}

	public int getInstrument() {
		return instrument;
	}

	public int getVolume() {
		return volume;
	}

	public int getEffectType() {
		return effectType;
	}

	public int getEffectParameter() {
		return effectParameter;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("Note[")
				.append(getName(note)).append(",")
				.append(instrument).append(",");
		if(volume != 0) {
			buf.append(volume);
		}
		buf.append(",");
		if(effectType != 0) {
			buf.append(effectType).append(":")
					.append(effectParameter);
		}
		return buf.append("]").toString();
	}
}
