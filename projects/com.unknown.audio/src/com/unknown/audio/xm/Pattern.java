package com.unknown.audio.xm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.unknown.util.io.LEInputStream;
import com.unknown.util.io.WordInputStream;

public class Pattern {
	private Note[][] notes;

	public Pattern(WordInputStream in, int channelCount) throws IOException {
		read(in, channelCount);
	}

	public Pattern(InputStream in, int channelCount) throws IOException {
		this(new LEInputStream(in), channelCount);
	}

	private void read(WordInputStream in, int channelCount)
			throws IOException {
		int headerLength = in.read32bit();
		int packingType = in.read();
		if(packingType != 0) {
			throw new IllegalArgumentException(
					"unknown packing type");
		}
		int rows = in.read16bit();
		int patterndataSize = in.read16bit();
		if(headerLength != 9) {
			in.read(headerLength - 9);
		}
		notes = new Note[rows][channelCount];
		if(patterndataSize == 0) {
			for(int row = 0; row < rows; row++) {
				for(int channel = 0; channel < channelCount; channel++) {
					notes[row][channel] = new Note();
				}
			}
		} else {
			byte[] data = in.read(patterndataSize);
			WordInputStream pin = new LEInputStream(
					new ByteArrayInputStream(data));
			for(int row = 0; row < rows; row++) {
				for(int channel = 0; channel < channelCount; channel++) {
					notes[row][channel] = new Note(pin);
				}
			}
			pin.close();
		}
	}

	public int getRows() {
		return notes.length;
	}

	public Note get(int row, int channel) {
		return notes[row][channel];
	}
}
