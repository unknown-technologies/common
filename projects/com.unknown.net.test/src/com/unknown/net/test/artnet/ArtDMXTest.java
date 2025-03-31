package com.unknown.net.test.artnet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.unknown.net.artnet.ArtDMXPacket;
import com.unknown.net.artnet.ArtNetPacket;

public class ArtDMXTest {
	// @formatter:off
	private static final byte[] ARTDMX = {
			'A',  'r',  't',  '-',
			'N',  'e',  't',  0x00,
			0x00, 0x50, 0,    14,
			7,    2,    1,    0,
			0,    4,    1,    42,
			97,   0
	};
	private static final byte[] PARAMS = { 1, 42, 97, 0 };
	// @formatter:on

	@Test
	public void testWrite() {
		ArtDMXPacket artdmx = new ArtDMXPacket(1, 2, PARAMS);
		artdmx.setSequence(7);
		byte[] act = artdmx.write();
		assertArrayEquals(ARTDMX, act);
	}

	@Test
	public void testRead() throws IOException {
		ArtNetPacket pkt = ArtNetPacket.read(ARTDMX);
		assertTrue(pkt instanceof ArtDMXPacket);

		ArtDMXPacket artdmx = (ArtDMXPacket) pkt;
		assertEquals(7, artdmx.getSequence());
		assertEquals(2, artdmx.getPhysical());
		assertEquals(1, artdmx.getUniverse());
		assertArrayEquals(PARAMS, artdmx.getParameters());
	}
}
