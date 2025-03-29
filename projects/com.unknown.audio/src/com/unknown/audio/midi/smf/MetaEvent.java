package com.unknown.audio.midi.smf;

import java.io.IOException;
import java.util.logging.Logger;

import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;
import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;

public abstract class MetaEvent extends Event {
	private static final Logger log = Trace.create(MetaEvent.class);

	private byte type;

	public MetaEvent(long time, byte type) {
		super(time);
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	protected abstract int getDataSize();

	protected abstract void writeContent(WordOutputStream out) throws IOException;

	@Override
	protected final int getSize() {
		return getDataSize() + 2;
	}

	@Override
	protected final void writeData(WordOutputStream out) throws IOException {
		out.write8bit((byte) 0xFF);
		out.write8bit(type);
		writeContent(out);
	}

	@SuppressWarnings("unchecked")
	public static <T extends MetaEvent> T readEvent(long time, WordInputStream in) throws IOException {
		byte type = in.read8bit();
		switch(type) {
		case TextEvent.TYPE:
			return (T) new TextEvent(time, in);
		case CopyrightEvent.TYPE:
			return (T) new CopyrightEvent(time, in);
		case SequenceNameEvent.TYPE:
			return (T) new SequenceNameEvent(time, in);
		case InstrumentEvent.TYPE:
			return (T) new InstrumentEvent(time, in);
		case LyricEvent.TYPE:
			return (T) new LyricEvent(time, in);
		case MarkerEvent.TYPE:
			return (T) new MarkerEvent(time, in);
		case CuePointEvent.TYPE:
			return (T) new CuePointEvent(time, in);
		case ProgramNameEvent.TYPE:
			return (T) new ProgramNameEvent(time, in);
		case DeviceNameEvent.TYPE:
			return (T) new DeviceNameEvent(time, in);
		case EndOfTrackEvent.TYPE:
			return (T) new EndOfTrackEvent(time, in);
		case TempoEvent.TYPE:
			return (T) new TempoEvent(time, in);
		case SMPTEOffsetEvent.TYPE:
			return (T) new SMPTEOffsetEvent(time, in);
		case TimeSignatureEvent.TYPE:
			return (T) new TimeSignatureEvent(time, in);
		case KeySignatureEvent.TYPE:
			return (T) new KeySignatureEvent(time, in);
		case ProprietaryEvent.TYPE:
			return (T) new ProprietaryEvent(time, in);
		default:
			log.log(Levels.WARNING,
					String.format("Unknown meta event type %02X", Byte.toUnsignedInt(type)));
			return (T) new UnknownMetaEvent(time, type, in);
		}
	}
}
