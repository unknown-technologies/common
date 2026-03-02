package com.unknown.math.g3d;

import java.util.Objects;

public class Vec2 {
	public final double x;
	public final double y;

	public Vec2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	public String toString() {
		return "(" + x + "|" + y + ")";
	}

	public Vec2 add(Vec2 other) {
		return new Vec2(x + other.x, y + other.y);
	}

	public Vec2 sub(Vec2 other) {
		return new Vec2(x - other.x, y - other.y);
	}

	public Vec2 scale(double scale) {
		return new Vec2(x * scale, y * scale);
	}

	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	public Vec2 normalize() {
		double length = length();
		return new Vec2(x / length, y / length);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(!(o instanceof Vec2)) {
			return false;
		}
		Vec2 v = (Vec2) o;
		return v.x == x && v.y == y;
	}
}
