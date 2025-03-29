package com.unknown.audio.test.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.unknown.audio.meta.riff.RiffWave;

public class TestResources {
	public static InputStream getStream(String name) {
		return TestResources.class.getResourceAsStream(name);
	}

	public static byte[] get(String name) throws IOException {
		try(InputStream in = TestResources.class.getResourceAsStream(name)) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int n;
			byte[] buf = new byte[4096];
			while((n = in.read(buf)) != -1) {
				out.write(buf, 0, n);
			}
			out.close();
			return out.toByteArray();
		}
	}

	public static RiffWave getWav(String name) throws IOException {
		try(InputStream in = TestResources.getStream(name)) {
			return RiffWave.read(in);
		}
	}
}
