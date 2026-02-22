package com.unknown.net.shownet;

import java.util.Objects;

public class Point {
	public short x;
	public short y;
	public short red;
	public short green;
	public short blue;
	public short intensity;
	public short userColor1;
	public short userColor2;
	public short userColor3;

	public Point() {
		// this creates a "zero point"
		x = (short) 0x8000;
		y = (short) 0x8000;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Point)) {
			return false;
		}

		Point p = (Point) o;
		return p.x == x && p.y == y && p.red == red && p.green == green && p.blue == blue &&
				p.intensity == intensity && p.userColor1 == userColor1 && p.userColor2 == userColor2 &&
				p.userColor3 == userColor3;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, red, green, blue, intensity, userColor1, userColor2, userColor3);
	}
}
