package com.unknown.audio.test.gcn;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.unknown.audio.gcn.dsp.encoder.Encoder;
import com.unknown.audio.gcn.dsp.encoder.Grok;
import com.unknown.audio.meta.riff.RiffWave;
import com.unknown.audio.test.resources.TestResources;

public class EncoderTest {
	private static InputStream getInput() {
		return TestResources.getStream("test.wav");
	}

	private static byte[] getOutput() throws IOException {
		return TestResources.get("test.dsp");
	}

	private static RiffWave getWave() throws IOException {
		RiffWave wave;
		try(InputStream in = getInput()) {
			wave = RiffWave.read(getInput());
		}
		return wave;
	}

	@Test
	public void testCoefficients() throws IOException {
		RiffWave wav = getWave();
		short[] samples = wav.get16bitSamples();
		short[] coefs = new short[16];
		short[] ref = { (short) 0xf7b2, (short) 0xfce3, 0x0c51, (short) 0xfaaa, 0x06a1, (short) 0xfcee, 0x0efd,
				(short) 0xf897, (short) 0xff9c, 0x0157, 0x0c99, (short) 0xfb0c, 0x0904, (short) 0xfe01,
				0x0f09, (short) 0xf8d7 };
		Grok.DSPCorrelateCoefs(samples, 0, samples.length, coefs);
		assertArrayEquals(ref, coefs);
	}

	@Test
	public void testEncode() throws IOException {
		try(InputStream in = getInput(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			byte[] ref = getOutput();
			Encoder.encode(getInput(), out);
			byte[] dsp = out.toByteArray();
			assertArrayEquals(ref, dsp);
		}
	}
}
