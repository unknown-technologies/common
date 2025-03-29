package com.unknown.audio.midi.sysex.korg.ms2000;

import java.nio.charset.StandardCharsets;

import com.unknown.util.io.Endianess;

public class Program {
	public static final int VOICE_MODE_SINGLE = 0;
	public static final int VOICE_MODE_SPLIT = 1;
	public static final int VOICE_MODE_LAYER = 2;
	public static final int VOICE_MODE_VOCODER = 3;

	private String name;
	private int timbreVoice;
	private int voiceMode;
	private int scaleKey;
	private int scaleType;
	private int splitPoint;

	// delay fx
	private boolean delaySync;
	private int delayTimeBase;
	private int delayTime;
	private int delayDepth;
	private int delayType;

	// mod fx
	private int modLfoSpeed;
	private int modDepth;
	private int modType;

	// eq
	private int eqHiFreq;
	private int eqHiGain;
	private int eqLowFreq;
	private int eqLowGain;

	// arpeggio
	private int arpTempo;
	private boolean arpActive;
	private boolean arpLatch;
	private int arpTarget;
	private boolean arpKeySync;
	private int arpType;
	private int arpRange;
	private int arpGateTime;
	private int arpResolution;
	private int arpSwing;

	private final Timbre timbre1 = new Timbre();
	private final Timbre timbre2 = new Timbre();
	private final Vocoder vocoder = new Vocoder();

	public Program(byte[] data) {
		this(data, 0);
	}

	public Program(byte[] data, int offset) {
		int namelen = 12;
		for(int i = 0; i < 12; i++) {
			if(data[offset + i] == 0) {
				namelen = i;
				break;
			}
		}

		name = new String(data, offset, namelen, StandardCharsets.US_ASCII);
		timbreVoice = (data[offset + 16] >> 6) & 0x03;
		voiceMode = (data[offset + 16] >> 4) & 0x03;
		scaleKey = (data[offset + 17] >> 4) & 0x0F;
		scaleType = data[offset + 17] & 0x0F;
		splitPoint = data[offset + 18];

		// DELAY FX
		delaySync = (data[offset + 19] & 0x80) != 0;
		delayTimeBase = data[offset + 19] & 0x0F;
		delayTime = data[offset + 20];
		delayDepth = data[offset + 21];
		delayType = data[offset + 22];

		// MOD FX
		modLfoSpeed = data[offset + 23];
		modDepth = data[offset + 24];
		modType = data[offset + 25];

		// EQ
		eqHiFreq = data[offset + 26];
		eqHiGain = data[offset + 27] - 64;
		eqLowFreq = data[offset + 28];
		eqLowGain = data[offset + 29] - 64;

		// ARPEGGIO
		arpTempo = Endianess.get16bitBEu(data, 30);
		arpActive = (data[offset + 32] & 0x80) != 0;
		arpLatch = (data[offset + 32] & 0x40) != 0;
		arpTarget = (data[offset + 32] >> 4) & 0x03;
		arpKeySync = (data[offset + 32] & 0x01) != 0;
		arpType = data[offset + 33] & 0x0F;
		arpRange = (data[offset + 33] >> 4) & 0x0F;
		arpGateTime = data[offset + 34];
		arpResolution = data[offset + 35];
		arpSwing = data[offset + 36];

		// TODO: decide between timbre and vocoder
		if(voiceMode == VOICE_MODE_VOCODER) {
			vocoder.read(data, offset + 38);
		} else {
			timbre1.read(data, offset + 38);
			timbre2.read(data, offset + 146);
		}
	}

	public void write(byte[] data, int offset) {
		for(int i = 0; i < 12; i++) {
			char ch = i < name.length() ? name.charAt(i) : ' ';
			if(ch > 0x7F || ch < 0x20) {
				ch = ' ';
			}
			data[offset + i] = (byte) ch;
		}

		data[offset + 16] = (byte) (((timbreVoice & 0x03) << 6) | ((voiceMode & 0x03) << 4));
		data[offset + 17] = (byte) (((scaleKey & 0x0F) << 4) | (scaleType & 0x0F));
		data[offset + 18] = (byte) (splitPoint & 0x7F);
		data[offset + 19] = (byte) ((delaySync ? 0x80 : 0) | (delayTimeBase & 0x0F));
		data[offset + 20] = (byte) (delayTime & 0x7F);
		data[offset + 21] = (byte) (delayDepth & 0x7F);
		data[offset + 22] = (byte) (delayType & 0x03);
		data[offset + 23] = (byte) (modLfoSpeed & 0x7F);
		data[offset + 24] = (byte) (modDepth & 0x7F);
		data[offset + 25] = (byte) (modType & 0x03);
		data[offset + 26] = (byte) (eqHiFreq & 0x1F);
		data[offset + 27] = (byte) ((eqHiGain + 64) & 0x7F);
		data[offset + 28] = (byte) (eqLowFreq & 0x1F);
		data[offset + 29] = (byte) ((eqLowGain + 64) & 0x7F);
		data[offset + 30] = (byte) (arpTempo >> 8);
		data[offset + 31] = (byte) arpTempo;
		data[offset + 32] = (byte) ((arpActive ? 0x80 : 0) | (arpLatch ? 0x40 : 0) | ((arpTarget & 0x03) << 4) |
				(arpKeySync ? 1 : 0));
		data[offset + 33] = (byte) (((arpRange & 0x0F) << 4) | ((arpType) & 0x0F));
		data[offset + 34] = (byte) (arpGateTime & 0x7F);
		data[offset + 35] = (byte) (arpResolution & 0x07);
		data[offset + 36] = (byte) (arpSwing & 0x7F);

		// TODO: decide between timbre and vocoder
		if(voiceMode == VOICE_MODE_VOCODER) {
			vocoder.write(data, offset + 38);
		} else {
			timbre1.write(data, offset + 38);
			timbre2.write(data, offset + 146);
		}
	}

	public byte[] getData() {
		byte[] data = new byte[254];
		write(data, 0);
		return data;
	}

	public String getName() {
		if(!name.startsWith(" ")) {
			return name.trim();
		} else {
			return name;
		}
	}

	public int getVoiceMode() {
		return voiceMode;
	}

	public Timbre getTimbre1() {
		return timbre1;
	}

	public Timbre getTimbre2() {
		return timbre2;
	}

	public void swapTimbres() {
		Timbre tmp = timbre1.clone();
		timbre2.copy(timbre1);
		timbre1.copy(tmp);
		if(arpTarget == 1) {
			arpTarget = 2;
		} else if(arpTarget == 2) {
			arpTarget = 1;
		}
	}

	@Override
	public String toString() {
		return "Program[name='" + name + "',timbre1=" + timbre1 + ",timbre2=" + timbre2 + "]";
	}
}
