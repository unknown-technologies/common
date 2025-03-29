package com.unknown.audio.test.midi;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.unknown.audio.midi.smf.SMF;
import com.unknown.audio.test.resources.TestResources;

public class SMFTest {
	public SMF load(String name) throws IOException {
		try(InputStream in = TestResources.getStream(name)) {
			return new SMF(in);
		}
	}

	public byte[] write(SMF smf) throws IOException {
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			smf.write(out);
			out.flush();
			return out.toByteArray();
		}
	}

	@Test
	public void testReadType1() throws Exception {
		SMF smf = load("item-room-ch1.mid");

		assertEquals(6, smf.getHeader().size());
		assertEquals(1, smf.getHeader().getFormat());
		assertEquals(2, smf.getHeader().getTracks());
		assertEquals(96, smf.getHeader().getPPQ());
	}

	@Test
	public void testReadWriteType1() throws Exception {
		SMF smf = load("item-room-ch1.mid");
		byte[] ref = TestResources.get("item-room-ch1.mid");
		byte[] act = write(smf);
		assertArrayEquals(ref, act);
	}
}
