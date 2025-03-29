package com.unknown.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class PitchbendRanger {
	private final double scale;

	public PitchbendRanger(int inRange, int outRange) {
		scale = (double) inRange / (double) outRange;
	}

	public Sequence process(Sequence seq) throws InvalidMidiDataException {
		Track[] tracks = seq.getTracks();
		int clipped = 0;
		for(Track track : tracks) {
			for(int i = 0; i < track.size(); i++) {
				MidiEvent evt = track.get(i);
				if(evt.getMessage() instanceof ShortMessage) {
					ShortMessage msg = (ShortMessage) evt.getMessage();
					int cmd = msg.getCommand();
					if(cmd == ShortMessage.PITCH_BEND) {
						int channel = msg.getChannel();
						int lo = msg.getData1();
						int hi = msg.getData2();
						int bend = lo | (hi << 7) - 8192;
						int outbend = (int) Math.round(bend * scale) + 8192;
						if(outbend < 0) {
							outbend = 0;
							clipped++;
						} else if(outbend >= 16384) {
							outbend = 16383;
							clipped++;
						}
						int loout = outbend & 0x7F;
						int hiout = (outbend >> 7) & 0x7F;
						msg.setMessage(cmd, channel, loout, hiout);
					}
				}
			}
		}
		if(clipped > 0) {
			System.out.println(clipped + " bend events got clipped");
		}
		return seq;
	}

	public static void main(String[] args) throws IOException, InvalidMidiDataException {
		int inrange = Integer.parseInt(args[0]);
		int outrange = Integer.parseInt(args[1]);
		String infilename = args[2];
		String outfilename = args[3];

		Sequence in = MidiSystem.getSequence(new File(infilename));

		PitchbendRanger ranger = new PitchbendRanger(inrange, outrange);
		Sequence out = ranger.process(in);

		int[] types = MidiSystem.getMidiFileTypes(out);
		if(types.length == 0) {
			throw new IOException("No acceptable MIDI file type found");
		}
		int type = types[0];
		MidiSystem.write(out, type, new File(outfilename));
	}
}
