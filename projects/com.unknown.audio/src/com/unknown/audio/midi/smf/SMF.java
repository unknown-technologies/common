package com.unknown.audio.midi.smf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SMF {
	private MThd header;
	private List<MTrk> tracks;

	public SMF() {
		header = new MThd();
		tracks = new ArrayList<>();
	}

	public SMF(InputStream in) throws IOException {
		header = Chunk.read(in);
		tracks = new ArrayList<>();
		for(int i = 0; i < header.getTracks(); i++) {
			MTrk track = Chunk.read(in);
			tracks.add(track);
		}
	}

	public MThd getHeader() {
		return header;
	}

	public void addTrack(MTrk track) {
		tracks.add(track);
		header.setTracks((short) tracks.size());
	}

	public void removeTrack(MTrk track) {
		tracks.remove(track);
		header.setTracks((short) tracks.size());
	}

	public MTrk getTrack(int id) {
		return tracks.get(id);
	}

	public List<MTrk> getTracks() {
		return Collections.unmodifiableList(tracks);
	}

	public void write(OutputStream out) throws IOException {
		header.write(out);
		for(MTrk track : tracks) {
			track.write(out);
		}
	}
}
