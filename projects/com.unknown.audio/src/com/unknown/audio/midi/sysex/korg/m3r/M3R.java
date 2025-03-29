package com.unknown.audio.midi.sysex.korg.m3r;

public class M3R {
	public static final int MODE_REQUEST = 0x12;
	public static final int DRUMSOUND_NAME_DUMP_REQUEST = 0x1F;
	public static final int MULTISOUND_NAME_DUMP_REQUEST = 0x16;
	public static final int PROGRAM_PARAMETER_DUMP_REQUEST = 0x10;
	public static final int ALL_PROGRAM_PARAMETER_DUMP_REQUEST = 0x1C;
	public static final int COMBINATION_PARAMETER_DUMP_REQUEST = 0x19;
	public static final int ALL_COMBINATION_PARAMETER_DUMP_REQUEST = 0x1D;
	public static final int GLOBAL_DATA_DUMP_REQUEST = 0x0E;
	public static final int DRUMS_DATA_DUMP_REQUEST = 0x0D;
	public static final int ALL_DATA_DUMP_REQUEST = 0x0F;
	public static final int PROGRAM_WRITE_REQUEST = 0x11;
	public static final int COMBINATION_WRITE_REQUEST = 0x1A;
	public static final int PROGRAM_PARAMETER_DUMP = 0x40;
	public static final int ALL_PROGRAM_PARAMETER_DUMP = 0x4C;
	public static final int COMBINATION_PARAMETER_DUMP = 0x49;
	public static final int ALL_COMBINATION_PARAMETER_DUMP = 0x4D;
	public static final int GLOBAL_DATA_DUMP = 0x51;
	public static final int DRUMS_DATA_DUMP = 0x52;
	public static final int ALL_DATA_DUMP = 0x50;
	public static final int MODE_CHANGE = 0x4E;
	public static final int PARAMETER_CHANGE = 0x41;
}
