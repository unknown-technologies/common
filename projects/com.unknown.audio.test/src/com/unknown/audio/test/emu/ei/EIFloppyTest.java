package com.unknown.audio.test.emu.ei;

import org.junit.Test;

import com.unknown.audio.emu.ei.EIBank;
import com.unknown.audio.emu.ei.EIFloppy;
import com.unknown.audio.emu.ei.EISample;
import com.unknown.audio.emu.ei.EISound;
import com.unknown.util.ResourceLoader;

public class EIFloppyTest {
	@Test
	public void testFloppy() throws Exception {
		byte[] data = ResourceLoader.load(EIFloppyTest.class, "resources/#17 Male Voices - Mixed Choir.emufd");
		EIFloppy floppy = new EIFloppy(data);
		EIBank bank = floppy.getBank();
		System.out.println("lower:");
		EISound lower = bank.getLower();
		for(EISample sample : lower.getSamples()) {
			System.out.println(sample);
		}
		System.out.println("upper:");
		EISound upper = bank.getUpper();
		for(EISample sample : upper.getSamples()) {
			System.out.println(sample);
		}
	}
}
