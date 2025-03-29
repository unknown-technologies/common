package com.unknown.audio.midi.smf;

import java.io.EOFException;
import java.io.IOException;

import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public abstract class Event {
	private long time;

	protected Event(long time) {
		this.time = time;
	}

	public final long getTime() {
		return time;
	}

	public final void setTime(long time) {
		this.time = time;
	}

	public final int getDelta(Event event) {
		return getDelta(event.getTime());
	}

	public final int getDelta(long last) {
		if(last > getTime()) {
			throw new IllegalArgumentException("delta must not be negative");
		}

		return (int) (getTime() - last);
	}

	public final int size(long last) {
		return VariableInt.length(getDelta(last)) + getSize();
	}

	protected abstract int getSize();

	protected abstract void writeData(WordOutputStream out) throws IOException;

	public void write(long last, WordOutputStream out) throws IOException {
		VariableInt.write(getDelta(last), out);
		writeData(out);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Event> T read(long last, byte lastStatus, WordInputStream in) throws IOException {
		int deltaTime;
		try {
			deltaTime = VariableInt.read(in);
		} catch(EOFException e) {
			return null;
		}
		long time = last + deltaTime;

		byte status = in.read8bit();
		if(status == (byte) 0xF0) {
			// sysex
			int length = VariableInt.read(in);
			byte[] data = new byte[length + 1];
			in.read(data, 1, length);
			data[0] = (byte) 0xF0;
			return (T) new SYSEXEvent(time, data);
		} else if(status == (byte) 0xF7) {
			// escaped
			throw new UnsupportedOperationException("escaped event not implemented");
		} else if(status == (byte) 0xFF) {
			// meta
			return (T) MetaEvent.readEvent(time, in);
		} else if((status & 0xF0) >= 0x80 && (status & 0xF0) <= 0xEF) {
			// MIDI event
			return (T) new MIDIEvent(time, status, in);
		} else {
			// running status
			int len = MIDIEvent.getSize(lastStatus);
			byte data1 = status;
			byte data2 = 0;

			if(len == 3) {
				data2 = in.read8bit();
			}
			return (T) new MIDIEvent(time, lastStatus, data1, data2);
		}
	}
}
