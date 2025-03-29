package com.unknown.audio.midi.sysex.korg.ms2000;

import com.unknown.audio.midi.sysex.korg.KORGBinaryConverter;

public class MS2000ProgramDataDump extends MS2000SysexMessage {
	private Program[] programs = new Program[128];

	public MS2000ProgramDataDump(byte[] data) {
		super(data);
		int off = parse(data);
		byte[] rawdata = KORGBinaryConverter.fromMIDI(data, off, 37157);
		for(int i = 0; i < 128; i++) {
			programs[i] = new Program(rawdata, i * 254);
		}
	}

	public MS2000ProgramDataDump(int channel, Program[] programs) {
		this.programs = programs;
		setChannel(channel);
		setFunction(MS2000.PROGRAM_DATA_DUMP);
	}

	public Program getProgram(int id) {
		return programs[id];
	}

	@Override
	public int getLength() {
		return getSize(37157);
	}

	@Override
	public byte[] getMessage() {
		byte[] message = new byte[getLength()];
		int off = fill(message);
		byte[] msg = new byte[254 * 128];
		for(int i = 0; i < 128; i++) {
			programs[i].write(msg, i * 254);
		}
		KORGBinaryConverter.toMIDI(message, off, msg, 0, msg.length);
		return message;
	}

	@Override
	public String toString() {
		return "ProgramDataDump";
	}
}
