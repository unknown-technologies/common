package com.unknown.audio.midi.sysex.korg.ms2000;

public class SEQParameter {
	public static final int DEST_COUNT = 31;

	public static final boolean MOTION_SMOOTH = false;
	public static final boolean MOTION_STEP = true;

	private static final String[] KNOBS = {
			/* 00 */ "None",
			/* 01 */ "Pitch",
			/* 02 */ "StepLength",
			/* 03 */ "Portamento",
			/* 04 */ "OSC1CTRL1",
			/* 05 */ "OSC1CTRL2",
			/* 06 */ "OSC2Semi",
			/* 07 */ "OSC2Tune",
			/* 08 */ "OSC1Level",
			/* 09 */ "OSC2Level",
			/* 10 */ "NoiseLevel",
			/* 11 */ "CutOff",
			/* 12 */ "Resonance",
			/* 13 */ "EG1 Int",
			/* 14 */ "KBD Track",
			/* 15 */ "AmpLevel",
			/* 16 */ "Panpot",
			/* 17 */ "EG1Attack",
			/* 18 */ "EG1Decay",
			/* 19 */ "EG1Sustain",
			/* 20 */ "EG1Release",
			/* 21 */ "EG2Attack",
			/* 22 */ "EG2Decay",
			/* 23 */ "EG2Sustain",
			/* 24 */ "EG2Release",
			/* 25 */ "LFO1Freq",
			/* 26 */ "LFO2Freq",
			/* 27 */ "Patch1Int",
			/* 28 */ "Patch2Int",
			/* 29 */ "Patch3Int",
			/* 30 */ "Patch4Int"
	};

	private int knob;
	private boolean motionType;
	private final int[] value = new int[16];

	public void read(byte[] data, int offset) {
		knob = data[offset];
		motionType = (data[offset + 1] & 0x01) != 0;
		for(int i = 0; i < 16; i++) {
			value[i] = data[offset + i + 2] - 64;
		}
	}

	public void write(byte[] data, int offset) {
		data[offset] = (byte) knob;
		data[offset + 1] = (byte) (motionType ? 1 : 0);
		for(int i = 0; i < 16; i++) {
			data[offset + i + 2] = (byte) (value[i] + 64);
		}
	}

	public int getKnob() {
		return knob;
	}

	public void setKnob(int knob) {
		if(knob < 0 || knob >= DEST_COUNT) {
			throw new IllegalArgumentException("invalid knob");
		}
		this.knob = knob;
	}

	public String getKnobName() {
		return getKnobName(knob);
	}

	public static String getKnobName(int knob) {
		return KNOBS[knob];
	}

	public boolean getMotionType() {
		return motionType;
	}

	public void setMotionType(boolean motionType) {
		this.motionType = motionType;
	}

	public int getValue(int id) {
		return value[id];
	}

	public void setValue(int id, int value) {
		if(value < -63 || value > 63) {
			throw new IllegalArgumentException("invalid step value");
		}
		this.value[id] = value;
	}

	public void copy(SEQParameter seq) {
		knob = seq.knob;
		motionType = seq.motionType;
		for(int i = 0; i < 16; i++) {
			value[i] = seq.value[i];
		}
	}

	@Override
	public String toString() {
		StringBuilder values = new StringBuilder();
		for(int i = 0; i < value.length; i++) {
			if(i != 0) {
				values.append(',');
			}
			values.append(value[i]);
		}
		return "SEQ[knob=" + getKnobName() + "," + (getMotionType() == MOTION_SMOOTH ? "smooth" : "step") +
				",values=[" + values + "]]";
	}
}
