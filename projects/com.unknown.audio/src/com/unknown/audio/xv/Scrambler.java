package com.unknown.audio.xv;

public abstract class Scrambler {
	public abstract int scrambleAddress(int addr);

	public abstract short scrambleData(short word);

	public abstract short descrambleData(short word);
}
