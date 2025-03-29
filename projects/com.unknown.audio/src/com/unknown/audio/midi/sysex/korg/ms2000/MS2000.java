package com.unknown.audio.midi.sysex.korg.ms2000;

public class MS2000 {
	public static final int CURRENT_PROGRAM_DATA_DUMP_REQUEST = 0x10;
	public static final int PROGRAM_DATA_DUMP_REQUEST = 0x1C;
	public static final int GLOBAL_DATA_DUMP_REQUEST = 0x0E;
	public static final int ALL_DATA_DUMP_REQUEST = 0x0F;
	public static final int MODE_REQUEST = 0x12;
	public static final int CURRENT_PROGRAM_DATA_DUMP = 0x40;
	public static final int PROGRAM_DATA_DUMP = 0x4C;
	public static final int GLOBAL_DATA_DUMP = 0x51;
	public static final int ALL_DATA_DUMP = 0x50;
	public static final int PROGRAM_WRITE_REQUEST = 0x11;
	public static final int PARAMETER_CHANGE = 0x41;
	public static final int MODE_CHANGE = 0x4E;

	public static final int BANK_A = 0;
	public static final int BANK_B = 1 * 16;
	public static final int BANK_C = 2 * 16;
	public static final int BANK_D = 3 * 16;
	public static final int BANK_E = 4 * 16;
	public static final int BANK_F = 5 * 16;
	public static final int BANK_G = 6 * 16;
	public static final int BANK_H = 7 * 16;
}
