package com.unknown.audio.xm;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MidiConverter {
	public final static int OFFSET = -1;
	public final static int RESOLUTION = 16;
	public final static int TICKS_PER_ROW = 16;

	public static Sequence toMidi(XMFile xm)
			throws InvalidMidiDataException {
		return toMidi(xm, 0, TICKS_PER_ROW);
	}

	public static Sequence toMidi(XMFile xm, int offset, int ticksPerRow)
			throws InvalidMidiDataException {
		Sequence seq = new Sequence(Sequence.PPQ, RESOLUTION);
		Track trk = seq.createTrack();
		int channels = xm.getChannelCount();
		long time = 0;
		int[] onkeys = new int[channels];
		int[] programs = new int[channels];
		for(int i = 0; i < channels; i++) {
			onkeys[i] = -1;
			programs[i] = -1;
		}
		byte[] title = xm.getTitle().getBytes();
		MetaMessage titleMessage = new MetaMessage(0x03, title,
				title.length);
		trk.add(new MidiEvent(titleMessage, 0));
		for(int order = 0; order < xm.getSongLength(); order++) {
			Pattern pattern = xm.getOrder(order);
			byte[] orderMarkerText = new String("Order " + order)
					.getBytes();
			MetaMessage orderMarker = new MetaMessage(0x06,
					orderMarkerText, orderMarkerText.length);
			trk.add(new MidiEvent(orderMarker, time));
			for(int row = 0; row < pattern.getRows(); row++) {
				for(int channel = 0; channel < channels; channel++) {
					Note note = pattern.get(row, channel);
					int key = note.getNote() + offset + OFFSET;
					if(note.isKeyOff()) {
						if(onkeys[channel] != -1) {
							ShortMessage msg = new ShortMessage(ShortMessage.NOTE_OFF,
									channel, onkeys[channel], 0x40);
							trk.add(new MidiEvent(msg, time));
						}
						onkeys[channel] = -1;
					} else if(!note.isEmpty()) {
						if(onkeys[channel] != -1) { // stop old note
							ShortMessage msg = new ShortMessage(ShortMessage.NOTE_OFF,
									channel, onkeys[channel], 0x40);
							trk.add(new MidiEvent(msg, time));
						}
						int instrument = note
								.getInstrument();
						if(programs[channel] != instrument && instrument != 0) {
							ShortMessage msg = new ShortMessage(ShortMessage.PROGRAM_CHANGE,
									channel, instrument - 1, 0);
							trk.add(new MidiEvent(msg, time));
							programs[channel] = instrument - 1;
						}
						onkeys[channel] = key;
						int volume = 0x40;
						int v = note.getVolume();
						if(v >= 0x10 && v <= 0x50) {
							volume = (v - 0x10) << 1;
						}
						ShortMessage msg = new ShortMessage(ShortMessage.NOTE_ON, channel, key,
								volume);
						trk.add(new MidiEvent(msg, time));
					}
				}
				time += ticksPerRow;
			}
		}
		for(int channel = 0; channel < channels; channel++) {
			if(onkeys[channel] != -1) {
				ShortMessage msg = new ShortMessage(ShortMessage.NOTE_OFF, channel, onkeys[channel],
						0x40);
				trk.add(new MidiEvent(msg, time));
			}
			onkeys[channel] = -1;
		}
		return seq;
	}

	public static void writeMidi(XMFile xm, OutputStream out)
			throws IOException, InvalidMidiDataException {
		writeMidi(xm, out, 0, TICKS_PER_ROW);
	}

	public static void writeMidi(XMFile xm, OutputStream out, int offset,
			int ticksPerRow) throws IOException,
			InvalidMidiDataException {
		Sequence seq = toMidi(xm, offset, ticksPerRow);
		int[] types = MidiSystem.getMidiFileTypes(seq);
		if(types.length == 0) {
			throw new IOException("no valid midi file format");
		}
		Arrays.sort(types); // will use type 0 or 1
		int type = types[0]; // use first type
		MidiSystem.write(seq, type, out);
	}
}
