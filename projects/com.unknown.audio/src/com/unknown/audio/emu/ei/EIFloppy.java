package com.unknown.audio.emu.ei;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.unknown.audio.analysis.MIDINames;

/*
 * Format:
 * u8 os[0x1C00];
 * u8 bank[0x1C000];
 * u8 seq[0xE00];
 */
public class EIFloppy {
	private final byte[] os = new byte[0x1C00];
	private final byte[] bank = new byte[0x1C000];
	private final byte[] seq = new byte[0xE00];

	public EIFloppy() {
		this(new byte[125440]);
	}

	public EIFloppy(byte[] data) {
		System.arraycopy(data, 0, os, 0, os.length);
		System.arraycopy(data, os.length, bank, 0, bank.length);
		System.arraycopy(data, os.length + bank.length, seq, 0, seq.length);
	}

	public byte[] getOS() {
		return os;
	}

	public EIBank getBank() {
		return new EIBank(bank);
	}

	public static void main(String[] args) throws Exception {
		if(args.length != 2) {
			System.out.println("Usage: EIFloppy input.emufd output-dir");
			System.exit(1);
		}

		Path inpath = Paths.get(args[0]);

		byte[] data = Files.readAllBytes(inpath);
		if(data.length != 125440) {
			System.out.println("Not an EI floppy!");
			System.exit(1);
		}

		Path outpath = Paths.get(args[1]);

		EIFloppy floppy = new EIFloppy(data);
		EIBank bank = floppy.getBank();

		EISample[] lower = bank.getLower().getSamples();
		EISample[] upper = bank.getUpper().getSamples();

		String basename = inpath.getFileName().toString();
		int ext = basename.lastIndexOf('.');
		if(ext != -1) {
			basename = basename.substring(0, ext).trim();
			if(basename.length() == 0) {
				System.out.println("Invalid filename");
				System.exit(1);
			}
		}

		int i;
		for(i = 0; i < lower.length; i++) {
			EISample sample = lower[i];
			Path filename = outpath.resolve(basename + "_S" + i + "_L.wav");
			System.out.println(filename.getFileName() + " [root=" +
					MIDINames.getNoteNamePadded(sample.getRootKey()) + ",tune=" +
					sample.getTune() + "]");
			try(OutputStream out = new FileOutputStream(filename.toFile())) {
				sample.write(out);
			}
		}

		for(int j = 0; j < upper.length; i++, j++) {
			EISample sample = upper[j];
			Path filename = outpath.resolve(basename + "_S" + i + "_U.wav");
			System.out.println(filename.getFileName() + " [root=" +
					MIDINames.getNoteNamePadded(sample.getRootKey()) + ",tune=" +
					sample.getTune() + "]");
			try(OutputStream out = new FileOutputStream(filename.toFile())) {
				sample.write(out);
			}
		}
	}
}
