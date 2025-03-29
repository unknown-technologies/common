package com.unknown.math.g3d;

import java.util.Objects;

public class Vec3 {
	public final double x;
	public final double y;
	public final double z;

	public Vec3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return "(" + x + "|" + y + "|" + z + ")";
	}

	public Vec3 add(Vec3 other) {
		return new Vec3(x + other.x, y + other.y, z + other.z);
	}

	public Vec3 sub(Vec3 other) {
		return new Vec3(x - other.x, y - other.y, z - other.z);
	}

	public Vec3 scale(double scale) {
		return new Vec3(x * scale, y * scale, z * scale);
	}

	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public Vec3 normalize() {
		double length = length();
		return new Vec3(x / length, y / length, z / length);
	}

	public Vec3 cross(Vec3 vec) {
		double vx = y * vec.z - z * vec.y;
		double vy = x * vec.z - z * vec.x;
		double vz = x * vec.y - y * vec.x;

		return new Vec3(vx, vy, vz);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(!(o instanceof Vec3)) {
			return false;
		}
		Vec3 v = (Vec3) o;
		return v.x == x && v.y == y && v.z == z;
	}
}
