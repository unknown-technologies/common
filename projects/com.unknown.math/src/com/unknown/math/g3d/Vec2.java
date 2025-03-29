package com.unknown.math.g3d;

public class Vec2 {
	public final double x;
	public final double y;

	public Vec2(double x, double y) {
		this.x = x;
		this.y = y;
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
}
