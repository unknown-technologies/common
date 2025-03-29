package com.unknown.util.x11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class BDFFont {
	private final Map<Integer, BDFGlyph> glyphs;

	public BDFFont(String bdf) throws IOException {
		glyphs = new HashMap<>();

		parse(bdf);
	}

	private void parse(String bdf) throws IOException {
		int width = 0;
		int height = 0;

		String name = null;
		int codepoint = 0;
		int py = 0;
		boolean bitmap = false;
		BDFGlyph glyph = null;

		try(BufferedReader in = new BufferedReader(new StringReader(bdf))) {
			String line;
			while((line = in.readLine()) != null) {
				if(bitmap && py < height) {
					for(int i = 0; i < width; i++) {
						char c = line.charAt(i / 4);
						int hex = c - '0';
						if(c >= 'A' && c <= 'F') {
							hex = c - 'A' + 10;
						} else if(c >= 'a' && c <= 'f') {
							hex = c - 'a' + 10;
						}
						boolean bit = ((hex >> (3 - (i & 3))) & 1) != 0;
						glyph.setPixel(i, py, bit);
					}
					py++;
				} else {
					String[] parts = line.split("\\s");
					switch(parts[0]) {
					case "STARTCHAR":
						name = parts[1];
						break;
					case "ENCODING":
						codepoint = Integer.parseInt(parts[1]);
						break;
					case "BBX":
						width = Integer.parseInt(parts[1]);
						height = Integer.parseInt(parts[2]);

						break;
					case "BITMAP":
						py = 0;
						bitmap = true;
						glyph = new BDFGlyph(name, codepoint, width, height);
						glyphs.put(codepoint, glyph);
						break;
					}
				}
			}
		}
	}

	public BDFGlyph getGlyph(char c) {
		return glyphs.get((int) c);
	}
}
