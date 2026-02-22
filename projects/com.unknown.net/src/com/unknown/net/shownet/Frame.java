package com.unknown.net.shownet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class Frame {
	private final Laser laser;

	private final int time;
	private final int id;
	private final int pointCount;
	private boolean compressed;
	private byte[] bitstream;

	public Frame(Laser laser, List<Point> points, int time) {
		this(laser, points, time, false);
	}

	public Frame(Laser laser, List<Point> points, int time, boolean uncompressed) {
		this.laser = laser;
		this.time = time;
		this.pointCount = points.size();
		compressed = !uncompressed;

		if(!uncompressed) {
			bitstream = encodeCompressed(points);
			if(bitstream == null || bitstream.length > points.size() * laser.getFramePointSize()) {
				// the "compressed" encoding needs more space than "uncompressed",
				// use the "uncompressed" encoding instead
				bitstream = encodeUncompressed(points);
				compressed = false;
			}
		} else {
			bitstream = encodeUncompressed(points);
			compressed = false;
		}

		id = laser.nextFrameId();
	}

	private byte[] encodeCompressed(List<Point> points) {
		int colorFeatures = laser.getColorFeatures();
		int generation = laser.getGeneration();
		int scale = generation == 2 ? 256 : 512;

		byte[] buf = new byte[16];
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			int lastX = 0xFFFFFF;
			int lastY = 0xFFFFFF;
			int lastRed = 0xFFFFFF;
			int lastGreen = 0xFFFFFF;
			int lastBlue = 0xFFFFFF;
			int lastColor1 = 0xFFFFFF;
			int lastColor2 = 0xFFFFFF;
			int lastColor3 = 0xFFFFFF;
			for(Point p : points) {
				int flags;
				int ptr = 1;
				int x = (0xFFFF - Short.toUnsignedInt(p.x)) >>> 4;
				int y = (0xFFFF - Short.toUnsignedInt(p.y)) >>> 4;

				int dx = x - lastX;
				int dy = y - lastY;

				if(dy != 0 || dx < -128 || dx > 127) {
					if(dx != 0 || dy < -128 || dy > 127) {
						if(dx < -128 || dx > 127 || dy < -128 || dy > 127) {
							buf[ptr++] = (byte) x;
							buf[ptr++] = (byte) y;
							buf[ptr++] = (byte) (((y & 0xF00) >>> 4) | ((x & 0xF00) >>> 8));
							flags = 3;
						} else {
							buf[ptr++] = (byte) dx;
							buf[ptr++] = (byte) dy;
							flags = 2;
						}
					} else {
						buf[ptr++] = (byte) dy;
						flags = 1;
					}
				} else {
					buf[ptr++] = (byte) dx;
					flags = 0;
				}

				int red = Short.toUnsignedInt(p.red) / scale;
				int green = Short.toUnsignedInt(p.green) / scale;
				int blue = Short.toUnsignedInt(p.blue) / scale;

				if(lastRed != red) {
					buf[ptr++] = (byte) red;
					flags |= 4;
				}
				if(lastGreen != green) {
					buf[ptr++] = (byte) green;
					flags |= 8;
				}
				if(lastBlue != blue) {
					buf[ptr++] = (byte) blue;
					flags |= 0x10;
				}

				int color1 = lastColor1;
				int color2 = lastColor2;
				int color3 = lastColor3;

				if(colorFeatures == 5) {
					color1 = Short.toUnsignedInt(p.userColor1) / scale;
					color2 = Short.toUnsignedInt(p.userColor2) / scale;
					color3 = Short.toUnsignedInt(p.userColor3) / scale;

					if(lastColor1 != color1) {
						buf[ptr++] = (byte) color1;
						flags |= 0x20;
					}
					if(lastColor2 != color2) {
						buf[ptr++] = (byte) color2;
						flags |= 0x40;
					}
					if(lastColor3 != color3) {
						buf[ptr++] = (byte) color3;
						flags |= 0x80;
					}
				} else {
					color1 = Short.toUnsignedInt(p.intensity) / scale;
					if(lastColor1 != color1) {
						buf[ptr++] = (byte) color1;
						flags |= 0x20;
					}

					if(generation == 2) {
						color2 = Short.toUnsignedInt(p.userColor2) / scale;
						color3 = Short.toUnsignedInt(p.userColor3) / scale;

						if(lastColor2 != color2) {
							buf[ptr++] = (byte) color2;
							flags |= 0x40;
						}
						if(lastColor3 != color3) {
							buf[ptr++] = (byte) color3;
							flags |= 0x80;
						}
					}
				}

				buf[0] = (byte) flags;
				out.write(buf, 0, ptr);

				lastX = x;
				lastY = y;
				lastRed = red;
				lastGreen = green;
				lastBlue = blue;
				lastColor1 = color1;
				lastColor2 = color2;
				lastColor3 = color3;
			}
			out.flush();
			return out.toByteArray();
		} catch(IOException e) {
			return null;
		}
	}

	private byte[] encodeUncompressed(List<Point> points) {
		int colorFeatures = laser.getColorFeatures();
		int generation = laser.getGeneration();
		int scale = generation == 2 ? 256 : 512;

		byte[] buf = new byte[points.size() * laser.getFramePointSize()];
		int ptr = 0;

		for(Point p : points) {
			int x = (0xFFFF - Short.toUnsignedInt(p.x)) >> 4;
			int y = (0xFFFF - Short.toUnsignedInt(p.y)) >> 4;

			buf[ptr++] = (byte) x;
			buf[ptr++] = (byte) y;
			buf[ptr++] = (byte) (((y & 0xF00) >> 4) | ((x & 0xF00) >> 8));
			buf[ptr++] = (byte) (Short.toUnsignedInt(p.red) / scale);
			buf[ptr++] = (byte) (Short.toUnsignedInt(p.green) / scale);
			buf[ptr++] = (byte) (Short.toUnsignedInt(p.blue) / scale);

			if(colorFeatures == 5) {
				buf[ptr++] = (byte) (Short.toUnsignedInt(p.userColor1) / scale);
				buf[ptr++] = (byte) (Short.toUnsignedInt(p.userColor2) / scale);
				buf[ptr++] = (byte) (Short.toUnsignedInt(p.userColor3) / scale);
			} else {
				buf[ptr++] = (byte) (Short.toUnsignedInt(p.intensity) / scale);
				if(generation == 2) {
					buf[ptr++] = (byte) (Short.toUnsignedInt(p.userColor1) / scale);
					buf[ptr++] = (byte) (Short.toUnsignedInt(p.userColor2) / scale);
				}
			}
		}
		return buf;
	}

	public int getTime() {
		return time;
	}

	public boolean isCompressed() {
		return compressed;
	}

	public int getId() {
		return id;
	}

	public int getPointCount() {
		return pointCount;
	}

	public byte[] getBitstream() {
		return bitstream;
	}
}
