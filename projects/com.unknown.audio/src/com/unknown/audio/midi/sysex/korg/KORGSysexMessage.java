package com.unknown.audio.midi.sysex.korg;

import javax.sound.midi.SysexMessage;

public abstract class KORGSysexMessage extends SysexMessage {
	public static final byte END_OF_EXCLUSIVE = (byte) 0xF7;

	protected final byte devid;

	protected int channel;
	protected int function;

	protected KORGSysexMessage(byte devid) {
		this.devid = devid;
	}

	protected KORGSysexMessage(byte devid, byte[] data) {
		super(data);
		this.devid = devid;
	}

	protected int getChannel() {
		return channel;
	}

	protected void setChannel(int channel) {
		this.channel = channel;
	}

	protected int getFunction() {
		return function;
	}

	protected void setFunction(int function) {
		this.function = function;
	}

	protected int fill(byte[] msg) {
		msg[0] = (byte) SYSTEM_EXCLUSIVE;
		msg[1] = KORG.KORG_ID;
		msg[2] = (byte) (0x30 | getChannel());
		msg[3] = devid;
		msg[4] = (byte) getFunction();
		msg[msg.length - 1] = END_OF_EXCLUSIVE;
		return 5;
	}

	protected int parse(byte[] msg) {
		if(msg[0] != (byte) SYSTEM_EXCLUSIVE || msg[1] != KORG.KORG_ID || msg[3] != devid) {
			throw new IllegalArgumentException();
		}
		setChannel(msg[2] & 0x0F);
		setFunction(msg[4] & 0xFF);
		return 5;
	}

	protected int getSize(int len) {
		return len + 6;
	}

	public static boolean is(byte devid, byte[] data) {
		return data[0] == (byte) SYSTEM_EXCLUSIVE && data[1] == KORG.KORG_ID && data[3] == devid;
	}
}
