package com.unknown.audio.test.analysis;

import java.io.IOException;

import org.junit.Test;

import com.unknown.audio.analysis.Loop;
import com.unknown.audio.analysis.SpectraLoop;
import com.unknown.audio.dsp.FFT;
import com.unknown.audio.meta.riff.RiffWave;
import com.unknown.audio.test.resources.TestResources;

public class SpectraLoopTest {
	@Test
	public void testLoop() throws IOException, InterruptedException {
		RiffWave wav = TestResources.getWav("test.wav");
		float[][] samples = wav.getFloatSamples();

		Loop[] loops = SpectraLoop.loop(samples, 0, 16, 16, 64, 10, 512, 128, FFT.HAMMING, 512, 2);
		for(Loop loop : loops) {
			System.out.println(loop);
		}
	}
}
