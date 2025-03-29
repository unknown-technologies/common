package com.unknown.audio.emu.ei;

import java.io.IOException;
import java.io.OutputStream;

import com.unknown.audio.analysis.MIDINames;
import com.unknown.audio.compression.ULaw;
import com.unknown.audio.meta.riff.DataChunk;
import com.unknown.audio.meta.riff.RiffWave;
import com.unknown.audio.meta.riff.SampleChunk;
import com.unknown.audio.meta.riff.SampleChunk.SampleLoop;
import com.unknown.audio.meta.riff.WaveFormatChunk;

public class EISample {
	private byte[] data;

	private int start;
	private int loopStart;
	private int loopEnd;
	private int end;

	private int cutoff;

	private int rootkey;
	private double tune;

	public EISample(byte[] bank, int start, int loopStart, int loopEnd, int end, int cutoff, int rootkey,
			double tune) {
		data = bank;
		this.start = start;
		this.loopStart = loopStart;
		this.loopEnd = loopEnd;
		this.end = end;
		this.cutoff = cutoff;
		this.rootkey = rootkey;
		this.tune = tune;
	}

	public EISample(byte[] bank, int start, int end, int cutoff, int rootkey, double tune) {
		data = bank;
		this.start = start;
		this.loopStart = -1;
		this.loopEnd = -1;
		this.end = end;
		this.cutoff = cutoff;
		this.rootkey = rootkey;
		this.tune = tune;
	}

	public byte[] getData() {
		int len = end - start;
		byte[] result = new byte[len];
		System.arraycopy(data, start, result, 0, len);
		return result;
	}

	public int getLoopStart() {
		if(loopStart == -1) {
			return -1;
		} else {
			return loopStart - start;
		}
	}

	public int getLoopEnd() {
		if(loopStart == -1) {
			return -1;
		} else {
			return loopEnd - start;
		}
	}

	public int getLoopLength() {
		if(loopStart == -1) {
			return -1;
		} else {
			return loopEnd - loopStart;
		}
	}

	public int getLength() {
		return end - start;
	}

	public int getCutoff() {
		return cutoff;
	}

	public int getRootKey() {
		return rootkey;
	}

	public double getTune() {
		return tune;
	}

	public void write(OutputStream out) throws IOException {
		byte[] ulaw = getData();
		short[] raw = new short[ulaw.length];
		for(int i = 0; i < ulaw.length; i++) {
			raw[i] = ULaw.decode6072_16bit(ulaw[i]);
		}

		SampleChunk smpl = new SampleChunk();
		smpl.setSamplePeriod(1_000_000_000 / 27778);
		smpl.setMidiUnityNote(rootkey);
		smpl.setMidiTune(tune * 100.0);

		if(loopStart != -1) {
			smpl.addSampleLoop(
					new SampleLoop(0, SampleLoop.LOOP_FORWARD, getLoopStart(), getLoopEnd(), 0, 0));
		}

		RiffWave wav = new RiffWave();
		wav.set(new WaveFormatChunk());
		wav.set(new DataChunk());
		wav.set(smpl);
		wav.setSampleRate(27778);
		wav.setSampleFormat(WaveFormatChunk.WAVE_FORMAT_PCM);
		wav.setChannels(1);
		wav.setBitsPerSample(16);
		wav.set16bitSamples(raw);
		wav.write(out);
	}

	@Override
	public String toString() {
		return "EISample[start=" + start + ",loopStart=" + loopStart + ",loopEnd=" + loopEnd + ",end=" + end +
				";loop=" + getLoopStart() + "-" + getLoopEnd() + ":" + getLoopLength() + ",root=" +
				MIDINames.getNoteName(rootkey) + ",tune=" + tune + "]";
	}
}
