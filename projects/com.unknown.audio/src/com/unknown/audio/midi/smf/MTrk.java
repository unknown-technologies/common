package com.unknown.audio.midi.smf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public class MTrk extends Chunk {
	public static final int ID = 0x4D54726B; // "MTrk"

	private List<Event> events;

	public MTrk() {
		super(ID);

		events = new ArrayList<>();
	}

	public void addEvent(Event event) {
		events.add(event);
	}

	public void removeEvent(Event event) {
		events.remove(event);
	}

	public List<Event> getEvents() {
		return Collections.unmodifiableList(events);
	}

	public boolean hasNonMetaEvents() {
		for(Event event : events) {
			if(event instanceof MIDIEvent) {
				return true;
			}
		}

		return false;
	}

	public String getTrackName() {
		for(Event event : events) {
			if(event instanceof SequenceNameEvent) {
				return ((SequenceNameEvent) event).getName();
			}
		}

		return null;
	}

	public String getInstrumentName() {
		for(Event event : events) {
			if(event instanceof InstrumentEvent) {
				return ((InstrumentEvent) event).getName();
			}
		}

		return null;
	}

	public String getDeviceInstrument() {
		for(Event event : events) {
			if(event instanceof DeviceNameEvent) {
				return ((DeviceNameEvent) event).getName();
			}
		}

		return null;
	}

	public int getMicroTempo() {
		for(Event event : events) {
			if(event.getTime() > 0) {
				break;
			} else if(event instanceof TempoEvent) {
				return ((TempoEvent) event).getMicroTempo();
			}
		}

		return TempoEvent.getMicroTempo(120.0);
	}

	public void sort() {
		Collections.sort(events, (a, b) -> Long.compareUnsigned(a.getTime(), b.getTime()));
	}

	@Override
	protected int size() {
		int size = 0;
		long time = 0;
		for(Event event : events) {
			size += event.size(time);
			time = event.getTime();
		}
		return size;
	}

	@Override
	protected void write(WordOutputStream out) throws IOException {
		sort();

		long time = 0;
		for(Event event : events) {
			long pos = out.tell();
			int sz = event.size(time);
			event.write(time, out);
			long pos2 = out.tell();
			long diff = pos2 - pos;
			if(diff != sz) {
				throw new IOException("invalid size of event " + event + ": expected " + sz + ", was " +
						diff);
			}
			time = event.getTime();
		}
	}

	@Override
	protected void read(WordInputStream in, int size) throws IOException {
		long last = 0;
		byte lastStatus = 0;
		while(true) {
			Event evt = Event.read(last, lastStatus, in);
			if(evt == null) {
				break;
			} else {
				if(evt instanceof MIDIEvent) {
					lastStatus = ((MIDIEvent) evt).getStatus();
				}
				events.add(evt);
				if(evt instanceof EndOfTrackEvent) {
					break;
				}
			}
			last = evt.getTime();
		}
	}
}
