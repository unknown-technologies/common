package com.unknown.math.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.unknown.math.g3d.Mtx44;
import com.unknown.math.g3d.Vec3;

public class Math3DTest {
	private static void check(Vec3 expected, Vec3 actual) {
		String msg = "expected " + expected + ", was " + actual;
		assertEquals(msg, expected.x, actual.x, 1e-6);
		assertEquals(msg, expected.y, actual.y, 1e-6);
		assertEquals(msg, expected.z, actual.z, 1e-6);
	}

	@Test
	public void testView() {
		int width = 640;
		int height = 480;

		Mtx44 viewport = Mtx44.trans(width / 2.0, height / 2.0, 0)
				.concat(Mtx44.scale(width / 2.0, -height / 2.0, 1));

		check(new Vec3(0, 0, 0), viewport.mult(new Vec3(-1, 1, 0)));
		check(new Vec3(320, 240, 0), viewport.mult(new Vec3(0, 0, 0)));
		check(new Vec3(640, 480, 0), viewport.mult(new Vec3(1, -1, 0)));
	}
}
