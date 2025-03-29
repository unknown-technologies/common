package com.unknown.audio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.unknown.audio.meta.riff.RiffWave;
import com.unknown.audio.meta.riff.WaveFormatChunk;

public class WavFixer {
	public static void main(String[] args) throws IOException {
		try(InputStream in = new FileInputStream(args[0]);
				OutputStream out = new FileOutputStream(args[1])) {
			RiffWave wav = RiffWave.read(in);
			WaveFormatChunk fmt = wav.get(WaveFormatChunk.MAGIC);
			fmt.computeFrameSize();
			System.out.printf("frame=%d, avgbytespersec=%d\n", fmt.getFrameSize(),
					fmt.getAverageBytesPerSecond());
			wav.write(out);
		}
	}
}
