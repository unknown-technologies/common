package com.unknown.audio.gcn.dsp.encoder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.unknown.util.io.BEOutputStream;
import com.unknown.util.io.Endianess;
import com.unknown.util.io.LEInputStream;
import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public class Encoder {
	public static final int CORRELATE_SAMPLES = 0x3800; /* 1024 packets */

	public static final int PACKET_NIBBLES = 16;
	public static final int PACKET_SAMPLES = 14;
	public static final int PACKET_BYTES = 8;

	private static int getNibbleFromSample(int samples) {
		int packets = samples / PACKET_SAMPLES;
		int extraSamples = samples % PACKET_SAMPLES;
		int extraNibbles = extraSamples == 0 ? 0 : extraSamples + 2;
		return PACKET_NIBBLES * packets + extraNibbles;
	}

	private static int getNibbleAddress(int sample) {
		int packets = sample / PACKET_SAMPLES;
		int extraSamples = sample % PACKET_SAMPLES;

		return PACKET_NIBBLES * packets + extraSamples + 2;
	}

	private static int getBytesForAdpcmSamples(int samples) {
		int extraBytes = 0;
		int packets = samples / PACKET_SAMPLES;
		int extraSamples = samples % PACKET_SAMPLES;

		if(extraSamples != 0) {
			extraBytes = (extraSamples / 2) + (extraSamples % 2) + 1;
		}

		return PACKET_BYTES * packets + extraBytes;
	}

	public static void main(String[] args) throws IOException {
		if(args.length < 2) {
			System.out.println("Usage: Encoder <wavin> <dspout>");
			System.exit(1);
		}

		try(InputStream in = new FileInputStream(args[0]); OutputStream out = new FileOutputStream(args[1])) {
			encode(in, out, true);
		}
	}

	public static void encode(InputStream data, OutputStream dspOut) throws IOException {
		encode(data, dspOut, false);
	}

	public static void encode(InputStream data, OutputStream dspOut, boolean printStatus) throws IOException {
		WordInputStream in = new LEInputStream(data);
		WordOutputStream out = new BEOutputStream(dspOut);

		byte[] riffcheck = new byte[4];
		in.read(riffcheck);
		if(!new String(riffcheck).equals("RIFF")) {
			throw new IOException("not a valid RIFF file");
		}

		in.skip(4);
		in.read(riffcheck);
		if(!new String(riffcheck).equals("WAVE")) {
			throw new IOException("not a valid RIFF file");
		}

		int samplerate = 0;
		int samplecount = 0;
		while(in.read(riffcheck) == 4) {
			int chunkSz = in.read32bit();
			if(new String(riffcheck).equals("fmt ")) {
				int fmt = in.read16bit();
				if(fmt != 1) {
					throw new IOException("invalid format " + fmt);
				}

				short nchan = in.read16bit();
				if(nchan != 1) {
					throw new IOException("must have 1 channel, not " + nchan);
				}

				samplerate = in.read32bit();

				in.skip(4);

				short bytesPerSample = in.read16bit();
				if(bytesPerSample != 2) {
					throw new IOException("must have 2 bytes per sample, not " + bytesPerSample);
				}

				short bitsPerSample = in.read16bit();
				if(bitsPerSample != 16) {
					throw new IOException("must have 16 bits per sample, not " + bitsPerSample);
				}
			} else if(new String(riffcheck).equals("data")) {
				samplecount = chunkSz / 2;
				break;
			} else {
				in.skip(chunkSz);
			}
		}
		if(samplerate == 0 || samplecount == 0) {
			throw new IOException("must have a valid data chunk following a fmt chunk");
		}

		int packetCount = samplecount / PACKET_SAMPLES + ((samplecount % PACKET_SAMPLES != 0) ? 1 : 0);
		int sampsBufSz = samplecount * 2;
		short[] sampsBuf = new short[samplecount];
		byte[] buf = new byte[sampsBufSz];
		in.read(buf);
		for(int i = 0; i < samplecount; i++) {
			sampsBuf[i] = Endianess.get16bitLE(buf, i * 2);
		}

		short[] coefs = new short[16];
		Grok.DSPCorrelateCoefs(sampsBuf, 0, samplecount, coefs);

		DSPADPCMHeader header = new DSPADPCMHeader();
		header.num_samples = samplecount;
		header.num_nibbles = getNibbleFromSample(samplecount);
		header.sample_rate = samplerate;
		header.loop_start = getNibbleAddress(0);
		header.loop_end = getNibbleAddress(samplecount - 1);
		header.ca = getNibbleAddress(0);
		for(int i = 0; i < 16; i++) {
			header.coef[i] = coefs[i];
		}

		short[][] coef = new short[8][2];
		for(int i = 0; i < 16; i++) {
			coef[i / 2][i % 2] = coefs[i];
		}

		short[] convSamps = new short[16];
		byte[] block = new byte[8];

		for(int p = 0; p < packetCount; p++) {
			for(int i = 0; i < PACKET_SAMPLES; i++) {
				convSamps[i + 2] = 0;
			}
			int numSamples = Math.min(samplecount - p * PACKET_SAMPLES, PACKET_SAMPLES);

			for(int s = 0; s < numSamples; s++) {
				convSamps[s + 2] = sampsBuf[p * PACKET_SAMPLES + s];
			}

			Grok.DSPEncodeFrame(convSamps, PACKET_SAMPLES, block, coef);

			convSamps[0] = convSamps[14];
			convSamps[1] = convSamps[15];

			if(p == 0) {
				header.ps = block[0];
				header.write(out);
			}

			out.write(block, 0, getBytesForAdpcmSamples(numSamples));

			if(printStatus && (p % 48) == 0) {
				System.out.printf("\rPREDICT [ %d / %d ]", p + 1, packetCount);
			}
		}
		if(printStatus) {
			System.out.printf("\rPREDICT [ %d / %d ]", packetCount, packetCount);
			System.out.printf("\nDONE! %d samples processed\n", samplecount);
		}
	}
}
