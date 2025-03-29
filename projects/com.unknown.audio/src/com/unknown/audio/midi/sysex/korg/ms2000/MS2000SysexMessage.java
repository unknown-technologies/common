package com.unknown.audio.midi.sysex.korg.ms2000;

import javax.sound.midi.InvalidMidiDataException;

import com.unknown.audio.midi.sysex.korg.KORG;
import com.unknown.audio.midi.sysex.korg.KORGSysexMessage;

public abstract class MS2000SysexMessage extends KORGSysexMessage {
	protected MS2000SysexMessage() {
		super(KORG.MS2000_ID);
	}

	protected MS2000SysexMessage(byte[] data) {
		super(KORG.MS2000_ID, data);
	}

	@SuppressWarnings("unchecked")
	public static <T extends MS2000SysexMessage> T parseSysex(byte[] data) throws InvalidMidiDataException {
		if(!is(KORG.MS2000_ID, data)) {
			throw new InvalidMidiDataException("not a KORG MS2000 sysex message");
		}

		int function = Byte.toUnsignedInt(data[4]);
		switch(function) {
		case MS2000.CURRENT_PROGRAM_DATA_DUMP_REQUEST:
		case MS2000.PROGRAM_DATA_DUMP_REQUEST:
		case MS2000.GLOBAL_DATA_DUMP_REQUEST:
		case MS2000.ALL_DATA_DUMP_REQUEST:
		case MS2000.MODE_REQUEST:
			throw new InvalidMidiDataException(String.format("Unknown function %02X", function));
		case MS2000.CURRENT_PROGRAM_DATA_DUMP:
			return (T) new MS2000CurrentProgramDataDump(data);
		case MS2000.PROGRAM_DATA_DUMP:
			return (T) new MS2000ProgramDataDump(data);
		case MS2000.GLOBAL_DATA_DUMP:
		case MS2000.ALL_DATA_DUMP:
		case MS2000.PROGRAM_WRITE_REQUEST:
		case MS2000.PARAMETER_CHANGE:
		case MS2000.MODE_CHANGE:
		default:
			throw new InvalidMidiDataException(String.format("Unknown function %02X", function));
		}
	}
}
