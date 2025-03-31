package com.unknown.audio.s550;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.unknown.util.io.BEInputStream;
import com.unknown.util.io.BEOutputStream;
import com.unknown.util.io.RandomAccessMemoryInputStream;
import com.unknown.util.io.WordInputStream;
import com.unknown.util.io.WordOutputStream;

public class CDImage {
	public static final int SECTOR_SIZE = 512;
	private final CDHeader header = new CDHeader();
	private byte[] disc;

	public CDImage() {
		// nothing
	}

	public CDImage(byte[] disc) throws IOException {
		this.disc = disc;
		try(RandomAccessMemoryInputStream in = new RandomAccessMemoryInputStream(disc)) {
			header.read(in);
		}
	}

	public CDSoundDirectory getSoundDirectory() {
		return header.getSoundDirectory();
	}

	public String getVirtualFloppyName(int i) {
		return getSoundDirectory().get(i).getName().trim();
	}

	public String getVirtualFloppyShortName(int i) {
		return getSoundDirectory().get(i).getName().trim();
	}

	public int getVirtualFloppyCount() {
		return getSoundDirectory().getCount();
	}

	public CDVirtualFloppy getFloppy(int i) throws IOException {
		if(disc == null) {
			throw new IllegalStateException("no disc data available");
		}
		if(i >= getVirtualFloppyCount()) {
			throw new IndexOutOfBoundsException(
					"only " + getVirtualFloppyCount() + " virtual floppies available");
		}

		CDVirtualFloppy floppy = new CDVirtualFloppy();
		try(RandomAccessMemoryInputStream in = new RandomAccessMemoryInputStream(disc);
				WordInputStream win = new BEInputStream(in)) {
			CDSoundDirectoryEntry floppyMetadata = getSoundDirectory().get(i);
			int start = floppyMetadata.getOffset() * CDImage.SECTOR_SIZE;
			in.seek(start);
			int end = in.tell();
			int size = end - start;
			assert size <= floppyMetadata.getSize() * CDImage.SECTOR_SIZE : ("invalid size: " + size);
			floppy.read(win);
		}

		return floppy;
	}

	private static String sanitizePath(String name) {
		StringBuilder buf = new StringBuilder(name.length());
		char last = 0;
		for(char c : name.toCharArray()) {
			if(c >= 'A' && c <= 'Z') {
				buf.append(c);
			} else if(c >= 'a' && c <= 'z') {
				buf.append(c);
			} else if(c >= '0' && c <= '9') {
				buf.append(c);
			} else if(c == ' ') {
				if(last == ' ') {
					// nothing
				} else {
					buf.append(c);
				}
			} else {
				switch(c) {
				case '_':
				case '&':
				case '+':
				case '-':
				case '.':
				case ',':
				case ';':
				case '=':
				case '$':
				case '!':
				case '\'':
				case '#':
				case '%':
				case '(':
				case ')':
				case '[':
				case ']':
				case '{':
				case '}':
				case '^':
				case '°':
				case '~':
					buf.append(c);
					break;
				default:
					buf.append('_');
					break;
				}
			}
			last = c;
		}
		return buf.toString().trim();
	}

	private static String fmt(int id, int width) {
		String s = Integer.toString(id);
		if(s.length() < width) {
			StringBuilder buf = new StringBuilder(width);
			for(int i = s.length(); i < width; i++) {
				buf.append('0');
			}
			buf.append(s);
			return buf.toString();
		} else {
			return s;
		}
	}

	public void extractAll(Path basePath, String prefix) throws IOException {
		int count = getVirtualFloppyCount();
		SystemProgram systemProgram = new SystemProgram();
		for(int i = 0; i < count; i++) {
			String name = getVirtualFloppyName(i);
			String id = fmt(i, 3);
			String filename = prefix + "_" + id + "_" + sanitizePath(name) + ".img";
			Path path = basePath.resolve(filename);
			System.out.printf("extracting %s to %s...\n", name, path.toString());
			CDVirtualFloppy floppy = getFloppy(i);
			try(WordOutputStream out = new BEOutputStream(
					new BufferedOutputStream(new FileOutputStream(path.toFile())))) {
				floppy.writeFloppy(out, systemProgram);
			}
		}
	}

	public static void extract(String iso, String folder, String prefix) throws IOException {
		byte[] disc = Files.readAllBytes(Paths.get(iso));
		CDImage cd = new CDImage(disc);
		CDSoundDirectory dir = cd.getSoundDirectory();
		System.out.printf("%d entries in SoundDirectory\n", dir.getCount());
		cd.extractAll(Paths.get(folder), prefix);
	}
}
