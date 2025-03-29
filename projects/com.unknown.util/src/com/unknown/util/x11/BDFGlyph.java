package com.unknown.util.x11;

public class BDFGlyph {
	private final String name;
	private final int codepoint;

	private final int width;
	private final int height;
	private final boolean[][] pixels;

	public BDFGlyph(String name, int codepoint, int width, int height) {
		this.name = name;
		this.codepoint = codepoint;
		this.width = width;
		this.height = height;
		pixels = new boolean[height][width];
	}

	public String getName() {
		return name;
	}

	public int getCodepoint() {
		return codepoint;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean getPixel(int x, int y) {
		return pixels[y][x];
	}

	void setPixel(int x, int y, boolean bit) {
		pixels[y][x] = bit;
	}

	@Override
	public String toString() {
		return "BDFGlyph[name=" + name + ",width=" + width + ",height=" + height + "]";
	}
}
