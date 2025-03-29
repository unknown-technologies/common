package com.unknown.audio.midi.sysex.korg.ms2000;

import com.unknown.audio.midi.sysex.korg.KORGBinaryConverter;

public class MS2000CurrentProgramDataDump extends MS2000SysexMessage {
	private Program program;

	public MS2000CurrentProgramDataDump(byte[] data) {
		super(data);
		int off = parse(data);
		byte[] rawdata = KORGBinaryConverter.fromMIDI(data, off, 291);
		program = new Program(rawdata, 0);
	}

	public MS2000CurrentProgramDataDump(int channel, Program programs) {
		this.program = programs;
		setChannel(channel);
		setFunction(MS2000.CURRENT_PROGRAM_DATA_DUMP);
	}

	public Program getProgram() {
		return program;
	}

	@Override
	public int getLength() {
		return getSize(291);
	}

	@Override
	public byte[] getMessage() {
		byte[] message = new byte[getLength()];
		int off = fill(message);
		byte[] msg = new byte[254];
		program.write(msg, 0);
		KORGBinaryConverter.toMIDI(message, off, msg, 0, msg.length);
		return message;
	}

	@Override
	public String toString() {
		return "CurrentProgramDataDump[" + program + "]";
	}
}
