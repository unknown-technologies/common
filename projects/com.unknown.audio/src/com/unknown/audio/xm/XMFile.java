package com.unknown.audio.xm;

import java.io.IOException;
import java.io.InputStream;

import com.unknown.util.io.LEInputStream;
import com.unknown.util.io.WordInputStream;

public class XMFile {
	public final static String MAGIC = "Extended Module: ";

	private Pattern[] patterns;
	private Instrument[] instruments;
	private String title;
	private String trackerName;
	private byte[] patternOrderTable;
	private int channelCount;
	private int tempo;
	private int bpm;
	private int flags;
	private int songLength;
	private int restartPosition;

	public XMFile(WordInputStream in) throws IOException {
		read(in);
	}

	public XMFile(InputStream in) throws IOException {
		this(new LEInputStream(in));
	}

	public XMFile(String title, String trackerName, int channelCount,
			int songLength,
			int restartPosition, int flags, int tempo, int bpm,
			Pattern[] patterns, byte[] patternOrderTable) {
		this.title = title;
		this.trackerName = trackerName;
		this.channelCount = channelCount;
		this.songLength = songLength;
		this.restartPosition = restartPosition;
		this.flags = flags;
		this.tempo = tempo;
		this.bpm = bpm;
		this.patterns = patterns;
		this.patternOrderTable = patternOrderTable;
	}

	public void read(WordInputStream in) throws IOException {
		String magic = new String(in.read(17));
		if(!magic.equals(MAGIC)) {
			throw new IllegalArgumentException("not an XM file");
		}
		title = new String(in.read(20)).trim();
		int sep = in.read();
		if(sep != 0x1A) {
			throw new IllegalArgumentException("not an XM file");
		}
		trackerName = new String(in.read(20)).trim();
		int version = in.read16bit();
		if(version != 0x0104) {
			throw new IllegalArgumentException(
					"invalid XM file format version");
		}
		int headerSize = in.read32bit();
		songLength = in.read16bit();
		restartPosition = in.read16bit();
		channelCount = in.read16bit();
		int patternCount = in.read16bit();
		int instrumentCount = in.read16bit();
		flags = in.read16bit();
		tempo = in.read16bit();
		bpm = in.read16bit();
		patternOrderTable = in.read(256);
		if(headerSize != 276) {
			in.read(headerSize - 276); // padding
		}
		patterns = new Pattern[patternCount];
		for(int i = 0; i < patterns.length; i++) {
			patterns[i] = new Pattern(in, channelCount);
		}
		instruments = new Instrument[instrumentCount];
	}

	public String getTitle() {
		return title;
	}

	public String getTrackerName() {
		return trackerName;
	}

	public int getChannelCount() {
		return channelCount;
	}

	public int getSongLength() {
		return songLength;
	}

	public int getRestartPosition() {
		return restartPosition;
	}

	public int getTempo() {
		return tempo;
	}

	public int getBPM() {
		return bpm;
	}

	public int getFlags() {
		return flags;
	}

	public int getOrderPatternID(int index) {
		return patternOrderTable[index] & 0xFF;
	}

	public Pattern[] getPatterns() {
		return patterns;
	}

	public Pattern getPattern(int index) {
		return patterns[index];
	}

	public Pattern getOrder(int index) {
		return getPattern(getOrderPatternID(index));
	}

	public Instrument[] getInstruments() {
		return instruments;
	}

	public Instrument getInstrument(int index) {
		return instruments[index];
	}

	@Override
	public String toString() {
		return new StringBuffer("XMFile[title='").append(title)
				.append("',").append(channelCount)
				.append("ch,").append(songLength)
				.append("orders]").toString();
	}
}
