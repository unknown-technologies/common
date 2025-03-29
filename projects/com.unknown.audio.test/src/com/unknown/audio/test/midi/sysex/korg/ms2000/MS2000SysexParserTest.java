package com.unknown.audio.test.midi.sysex.korg.ms2000;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

import org.junit.Test;

import com.unknown.audio.midi.sysex.korg.ms2000.MS2000;
import com.unknown.audio.midi.sysex.korg.ms2000.MS2000CurrentProgramDataDump;
import com.unknown.audio.midi.sysex.korg.ms2000.MS2000ProgramDataDump;
import com.unknown.audio.midi.sysex.korg.ms2000.MS2000SysexMessage;
import com.unknown.audio.test.resources.TestResources;

public class MS2000SysexParserTest {
	@Test
	public void testParseProgram() throws IOException, InvalidMidiDataException {
		byte[] sysex = TestResources.get("ms2000-past-mind-single.syx");
		MS2000CurrentProgramDataDump data = MS2000SysexMessage.parseSysex(sysex);
		assertEquals("Past Mind", data.getProgram().getName());

		// clear "unused" fields
		sysex[19] = 0;
		sysex[20] = 0;
		byte[] msg = data.getMessage();
		assertArrayEquals(sysex, msg);
	}

	@Test
	public void testParsePrograms() throws IOException, InvalidMidiDataException {
		byte[] sysex = TestResources.get("MS2000Factory.syx");
		MS2000ProgramDataDump data = MS2000SysexMessage.parseSysex(sysex);

		assertEquals(data.getProgram(0).getName(), "Stab Saw");
		assertEquals(data.getProgram(MS2000.BANK_A + 6).getName(), "Ice Field");
		assertEquals(data.getProgram(MS2000.BANK_A + 11).getName(), "Turn Wheel");
		assertEquals(data.getProgram(MS2000.BANK_C + 13).getName(), "IZDISA-WS");
		assertEquals(data.getProgram(MS2000.BANK_C + 15).getName(), "Invaders");
		assertEquals(data.getProgram(MS2000.BANK_H + 8).getName(), "Vocoder Ens");
	}
}
