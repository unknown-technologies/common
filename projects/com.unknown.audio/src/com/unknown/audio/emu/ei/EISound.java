package com.unknown.audio.emu.ei;

import com.unknown.audio.analysis.Frequency;
import com.unknown.util.io.Endianess;

/*
 * Format:
 *
 * Simple:
 * u8 header[16];
 * u8 sample[...];
 *
 * the header consists of:
 * offset 7: u16 sample_length + 8
 *
 * Multi-Sample:
 * offset 0x10: sample_count
 *
 * u8 unknown[16];
 * u8 sample_count;
 * u8 unknown[15];
 */
public class EISound {
	private static final int DEFAULT_TUNING[] = { 0xa8c0, 0xa8ef, 0xa91b, 0xa944, 0x896c, 0x8991, 0x89b4, 0x89d5,
			0x69f4, 0x6a11, 0x6a2d, 0x6a47, 0x4a60, 0x4a77, 0x4a8d, 0x4aa2, 0x2ab6, 0x2ac8, 0x2ada, 0x2aea,
			0x2afa, 0xb09, 0xb17, 0xb24, 0x1b30 };

	private final byte[] data = new byte[16 * 3584];

	private final int midiOffset;

	private EISample[] samples;

	public EISound(byte[] data, int offset, boolean upper) {
		System.arraycopy(data, offset, this.data, 0, this.data.length);

		midiOffset = upper ? 48 : 24;

		parse();
	}

	private void parse() {
		int mode = Byte.toUnsignedInt(data[0x00]);
		boolean multisample = (mode & 0x10) != 0;

		if(multisample) {
			int count = Byte.toUnsignedInt(data[0x10]);
			int zonesize = Byte.toUnsignedInt(data[0x20]);
			samples = new EISample[count];
			for(int i = 0; i < count; i++) {
				samples[i] = parseSample(i + 1, i * zonesize, zonesize);
			}
		} else {
			samples = new EISample[1];
			samples[0] = parseSample(0, 0, 25);
		}
	}

	private EISample parseSample(int id, int lowKey, int size) {
		int offset = id * 0x10;
		int sampleStart = Short.toUnsignedInt(Endianess.get16bitLE(data, offset + 0x0004)) - 0x2000;
		int sampleStartLen = Short.toUnsignedInt(Endianess.get16bitLE(data, offset + 0x0006));
		int sampleLoop = Short.toUnsignedInt(Endianess.get16bitLE(data, offset + 0x0008)) - 0x2000;
		int sampleLoopLen = Short.toUnsignedInt(Endianess.get16bitLE(data, offset + 0x000A));
		int sampleDecay = Short.toUnsignedInt(Endianess.get16bitLE(data, offset + 0x000C)) - 0x2000;
		int sampleDecayLen = Short.toUnsignedInt(Endianess.get16bitLE(data, offset + 0x000E));
		int sampleEnd = sampleDecay + sampleDecayLen;

		int cutoff = 7 - (Byte.toUnsignedInt(data[offset + 0x0001]) >> 5);

		int tuningptr = Short.toUnsignedInt(Endianess.get16bitLE(data, offset + 0x0002)) - 0x2000;

		double rootkey = 0;
		for(int i = 0; i < size; i++) {
			int key = lowKey + i;
			int timer;
			if(id == 0) {
				timer = DEFAULT_TUNING[key];
			} else {
				timer = Short.toUnsignedInt(Endianess.get16bitLE(data, tuningptr + 2 * key));
			}
			timer &= 0x3FF;

			double freq = 11.55e6 / (0x1000 - (0xC00 | timer));
			double playback = freq / 27778.0;

			double ref = Frequency.MIDInoteToFreq(key + midiOffset);
			double act = ref / playback;
			double root = Frequency.freqToMIDInote(act) - midiOffset;

			rootkey += root;
		}

		rootkey /= size;

		int root = (int) Math.round(rootkey);
		double tune = root - rootkey;

		root += midiOffset; // adjust for MIDI

		if(sampleLoop == 0xFFFC) {
			/* no loop */
			return new EISample(data, sampleStart, sampleLoop, cutoff, root, tune);
		} else {
			assert sampleStart + sampleStartLen + 1 == sampleLoop;
			assert sampleLoop + sampleLoopLen + 1 == sampleDecay;

			return new EISample(data, sampleStart, sampleLoop, sampleLoop + sampleLoopLen + 1, sampleEnd,
					cutoff, root, tune);
		}
	}

	public EISample[] getSamples() {
		return samples;
	}
}
