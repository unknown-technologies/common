package com.unknown.audio.test.meta;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.unknown.audio.meta.riff.Riff;
import com.unknown.audio.meta.riff.RiffWave;
import com.unknown.audio.meta.riff.WaveFormatChunk;
import com.unknown.audio.test.resources.TestResources;

public class RiffWaveTest {
	@Test
	public void testReadWav() throws IOException {
		try(InputStream in = TestResources.getStream("test.wav")) {
			RiffWave wave = Riff.read(in);
			assertNotNull(wave);
		}
	}

	@Test
	public void testReadWriteWav() throws IOException {
		byte[] data = TestResources.get("test.wav");
		try(InputStream in = new ByteArrayInputStream(data);
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			RiffWave wave = Riff.read(in);
			wave.write(out);
			out.flush();
			byte[] written = out.toByteArray();
			assertEquals(data.length, written.length);
			assertArrayEquals(data, written);
		}
	}

	@Test
	public void testAvgBytesPerSec() {
		WaveFormatChunk fmt = new WaveFormatChunk();
		fmt.setBitsPerSample((short) 24);
		fmt.setChannels((short) 2);
		fmt.setFormat(WaveFormatChunk.WAVE_FORMAT_PCM);
		fmt.setSampleRate(48000);

		assertEquals(6, fmt.getFrameSize());
		assertEquals(288000, fmt.getAverageBytesPerSecond());
	}
}
