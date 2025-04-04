package com.unknown.audio.meta.riff;

import java.io.IOException;

import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public class WaveFormatChunk extends Chunk {
	public static final int MAGIC = 0x20746D66; // 'fmt '

	public static final short WAVE_FORMAT_PCM = 0x0001;
	public static final short WAVE_FORAT_ADPCM = 0x0002;
	public static final short WAVE_FORMAT_IEEE_FLOAT = 0x0003;
	public static final short WAVE_FORMAT_ALAW = 0x0006;
	public static final short WAVE_FORMAT_MULAW = 0x0007;
	public static final short WAVE_FORMAT_G723_ADPCM = 0x0014;
	public static final short WAVE_FORMAT_G721_ADPCM = 0x0040;
	public static final short WAVE_FORMAT_EXTENSIBLE = (short) 0xFFFE;

	private short format;
	private short channels;
	private int sampleRate;
	private short frameSize;
	private short bitsPerSample;

	public WaveFormatChunk() {
		super(MAGIC);
	}

	public short getFormat() {
		return format;
	}

	public void setFormat(short format) {
		this.format = format;
	}

	public short getChannels() {
		return channels;
	}

	public void setChannels(short channels) {
		this.channels = channels;
		computeFrameSize();
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public short getBitsPerSample() {
		return bitsPerSample;
	}

	public void setBitsPerSample(short bitsPerSample) {
		this.bitsPerSample = bitsPerSample;
		computeFrameSize();
	}

	public void computeFrameSize() {
		this.frameSize = (short) (channels * ((bitsPerSample + 7) / 8));
	}

	public short getFrameSize() {
		return frameSize;
	}

	public int getAverageBytesPerSecond() {
		return sampleRate * frameSize;
	}

	@Override
	public int size() {
		return 16;
	}

	@Override
	protected void writeData(WordOutputStream out) throws IOException {
		out.write16bit(format);
		out.write16bit(channels);
		out.write32bit(sampleRate);
		out.write32bit(getAverageBytesPerSecond());
		out.write16bit(frameSize);
		out.write16bit(bitsPerSample);
	}

	@Override
	protected void readData(WordInputStream in, int size) throws IOException {
		if(size == 16 || size == 18 || size == 40) {
			format = in.read16bit();
			channels = in.read16bit();
			sampleRate = in.read32bit();
			in.read32bit(); // avgBytesPerSec
			frameSize = in.read16bit();
			bitsPerSample = in.read16bit();
			if(format == WAVE_FORMAT_EXTENSIBLE && size > 16) {
				short extensionSize = in.read16bit();
				if(extensionSize == 0) {
					// fine, ignore
				} else if(extensionSize == 22) {
					@SuppressWarnings("unused")
					short validBitsPerSample = in.read16bit();
					@SuppressWarnings("unused")
					int channelMask = in.read32bit();
					format = in.read16bit();
					in.skip(14);
				}
			}
		} else {
			throw new IOException("unsupported format chunk size: " + size);
		}
	}
}
