package com.unknown.math.g3d;

public class Mtx44 {
	public double _00, _01, _02, _03;
	public double _10, _11, _12, _13;
	public double _20, _21, _22, _23;
	public double _30, _31, _32, _33;

	public Mtx44() {
		// identity matrix
		// @formatter:off
		_00 = 1.0;	_01 = 0.0;	_02 = 0.0;	_03 = 0.0;
		_10 = 0.0;	_11 = 1.0;	_12 = 0.0;	_13 = 0.0;
		_20 = 0.0;	_21 = 0.0;	_22 = 1.0;	_23 = 0.0;
		_30 = 0.0;	_31 = 0.0;	_32 = 0.0;	_33 = 1.0;
		// @formatter:on
	}

	public Mtx44(Mtx44 mtx) {
		// @formatter:off
		_00 = mtx._00;	_01 = mtx._01;	_02 = mtx._02;	_03 = mtx._03;
		_10 = mtx._10;	_11 = mtx._11;	_12 = mtx._12;	_13 = mtx._13;
		_20 = mtx._20;	_21 = mtx._21;	_22 = mtx._22;	_23 = mtx._23;
		_30 = mtx._30;	_31 = mtx._31;	_32 = mtx._32;	_33 = mtx._33;
		// @formatter:on
	}

	public double get(int row, int col) {
		switch(row) {
		case 0:
			switch(col) {
			case 0:
				return _00;
			case 1:
				return _01;
			case 2:
				return _02;
			case 3:
				return _03;
			}
		case 1:
			switch(col) {
			case 0:
				return _10;
			case 1:
				return _11;
			case 2:
				return _12;
			case 3:
				return _13;
			}
		case 2:
			switch(col) {
			case 0:
				return _20;
			case 1:
				return _21;
			case 2:
				return _22;
			case 3:
				return _23;
			}
		case 3:
			switch(col) {
			case 0:
				return _30;
			case 1:
				return _31;
			case 2:
				return _32;
			case 3:
				return _33;
			}
		}
		return 0;
	}

	public void set(int row, int col, double value) {
		switch(row) {
		case 0:
			switch(col) {
			case 0:
				_00 = value;
				break;
			case 1:
				_01 = value;
				break;
			case 2:
				_02 = value;
				break;
			case 3:
				_03 = value;
				break;
			}
			break;
		case 1:
			switch(col) {
			case 0:
				_10 = value;
				break;
			case 1:
				_11 = value;
				break;
			case 2:
				_12 = value;
				break;
			case 3:
				_13 = value;
				break;
			}
			break;
		case 2:
			switch(col) {
			case 0:
				_20 = value;
				break;
			case 1:
				_21 = value;
				break;
			case 2:
				_22 = value;
				break;
			case 3:
				_23 = value;
				break;
			}
			break;
		case 3:
			switch(col) {
			case 0:
				_30 = value;
				break;
			case 1:
				_31 = value;
				break;
			case 2:
				_32 = value;
				break;
			case 3:
				_33 = value;
				break;
			}
			break;
		}
	}

	public Mtx44 concat(Mtx44 b) {
		Mtx44 a = this;
		Mtx44 m = new Mtx44();

		// @formatter:off
		m._00 = a._00 * b._00 + a._01 * b._10 + a._02 * b._20 + a._03 * b._30;
		m._01 = a._00 * b._01 + a._01 * b._11 + a._02 * b._21 + a._03 * b._31;
		m._02 = a._00 * b._02 + a._01 * b._12 + a._02 * b._22 + a._03 * b._32;
		m._03 = a._00 * b._03 + a._01 * b._13 + a._02 * b._23 + a._03 * b._33;

		m._10 = a._10 * b._00 + a._11 * b._10 + a._12 * b._20 + a._13 * b._30;
		m._11 = a._10 * b._01 + a._11 * b._11 + a._12 * b._21 + a._13 * b._31;
		m._12 = a._10 * b._02 + a._11 * b._12 + a._12 * b._22 + a._13 * b._32;
		m._13 = a._10 * b._03 + a._11 * b._13 + a._12 * b._23 + a._13 * b._33;

		m._20 = a._20 * b._00 + a._21 * b._10 + a._22 * b._20 + a._23 * b._30;
		m._21 = a._20 * b._01 + a._21 * b._11 + a._22 * b._21 + a._23 * b._31;
		m._22 = a._20 * b._02 + a._21 * b._12 + a._22 * b._22 + a._23 * b._32;
		m._23 = a._20 * b._03 + a._21 * b._13 + a._22 * b._23 + a._23 * b._33;

		m._30 = a._30 * b._00 + a._31 * b._10 + a._32 * b._20 + a._33 * b._30;
		m._31 = a._30 * b._01 + a._31 * b._11 + a._32 * b._21 + a._33 * b._31;
		m._32 = a._30 * b._02 + a._31 * b._12 + a._32 * b._22 + a._33 * b._32;
		m._33 = a._30 * b._03 + a._31 * b._13 + a._32 * b._23 + a._33 * b._33;
		// @formatter:on

		return m;
	}

	public Mtx44 clearRot() {
		Mtx44 src = this;
		Mtx44 dst = new Mtx44();

		dst._00 = Math.sqrt(src._00 * src._00 + src._01 * src._01 + src._02 * src._02);
		dst._01 = 0;
		dst._02 = 0;
		dst._03 = src._03;
		dst._10 = 0;
		dst._11 = Math.sqrt(src._10 * src._10 + src._11 * src._11 + src._12 * src._12);
		dst._12 = 0;
		dst._13 = src._13;
		dst._20 = 0;
		dst._21 = 0;
		dst._22 = Math.sqrt(src._20 * src._20 + src._21 * src._21 + src._22 * src._22);
		dst._23 = src._23;
		dst._30 = src._30;
		dst._31 = src._31;
		dst._32 = src._32;
		dst._33 = src._33;

		return dst;
	}

	public Mtx44 inverse() {
		int NUM = 4;

		Mtx44 gjm = new Mtx44(this);
		Mtx44 inv = new Mtx44();
		int i, j, k;
		double w;

		for(i = 0; i < NUM; ++i) {
			double max = 0.0;
			int swp = i;

			// ---- partial pivoting -----
			for(k = i; k < NUM; k++) {
				double ftmp = Math.abs(gjm.get(k, i));
				if(ftmp > max) {
					max = ftmp;
					swp = k;
				}
			}

			// check singular matrix
			// (or can't solve inverse matrix with this algorithm)
			if(max == 0.0) {
				return null;
			}

			// swap row
			if(swp != i) {
				for(k = 0; k < NUM; k++) {
					// SWAPF(gjm[i][k], gjm[swp][k]);
					double tmp = gjm.get(i, k);
					gjm.set(i, k, gjm.get(swp, k));
					gjm.set(swp, k, tmp);

					// SWAPF(inv[i][k], inv[swp][k]);
					tmp = inv.get(i, k);
					inv.set(i, k, inv.get(swp, k));
					inv.set(swp, k, tmp);
				}
			}

			// ---- pivoting end ----

			w = 1.0 / gjm.get(i, i);
			for(j = 0; j < NUM; ++j) {
				gjm.set(i, j, gjm.get(i, j) * w);
				inv.set(i, j, inv.get(i, j) * w);
			}

			for(k = 0; k < NUM; ++k) {
				if(k == i)
					continue;

				w = gjm.get(k, i);
				for(j = 0; j < NUM; ++j) {
					gjm.set(k, j, gjm.get(k, j) - gjm.get(i, j) * w);
					inv.set(k, j, inv.get(k, j) - inv.get(i, j) * w);
				}
			}

		}

		return inv;
	}

	public Vec3 mult33(Vec3 vec) {
		Mtx44 m = this;

		double x = vec.x * m._00 + vec.y * m._01 + vec.z * m._02;
		double y = vec.x * m._10 + vec.y * m._11 + vec.z * m._12;
		double z = vec.x * m._20 + vec.y * m._21 + vec.z * m._22;

		return new Vec3(x, y, z);
	}

	public Vec3 mult(Vec3 vec) {
		Mtx44 m = this;

		// A Vec3 has a 4th implicit 'w' coordinate of 1
		double x = m._00 * vec.x + m._01 * vec.y + m._02 * vec.z + m._03;
		double y = m._10 * vec.x + m._11 * vec.y + m._12 * vec.z + m._13;
		double z = m._20 * vec.x + m._21 * vec.y + m._22 * vec.z + m._23;
		double w = m._30 * vec.x + m._31 * vec.y + m._32 * vec.z + m._33;

		w = 1.0f / w;

		// Copy back
		return new Vec3(x * w, y * w, z * w);
	}

	public static Mtx44 trans(double x, double y, double z) {
		Mtx44 m = new Mtx44();

		// @formatter:off
		m._00 = 1.0;	m._01 = 0.0;	m._02 = 0.0;	m._03 = x;
		m._10 = 0.0;	m._11 = 1.0;	m._12 = 0.0;	m._13 = y;
		m._20 = 0.0;	m._21 = 0.0;	m._22 = 1.0;	m._23 = z;
		m._30 = 0.0;	m._31 = 0.0;	m._32 = 0.0;	m._33 = 1.0;
		// @formatter:on

		return m;
	}

	public static Mtx44 scale(double x, double y, double z) {
		Mtx44 m = new Mtx44();

		// @formatter:off
		m._00 = x;	m._01 = 0.0;	m._02 = 0.0;	m._03 = 0.0;
		m._10 = 0.0;	m._11 = y;	m._12 = 0.0;	m._13 = 0.0;
		m._20 = 0.0;	m._21 = 0.0;	m._22 = z;	m._23 = 0.0;
		m._30 = 0.0;	m._31 = 0.0;	m._32 = 0.0;	m._33 = 1.0;
		// @formatter:on

		return m;
	}

	public static Mtx44 rotTrigX(double sinA, double cosA) {
		Mtx44 m = new Mtx44();

		// @formatter:off
		m._00 = 1.0;	m._01 = 0.0;	m._02 = 0.0;	m._03 = 0.0;
		m._10 = 0.0;	m._11 = cosA;	m._12 = -sinA;	m._13 = 0.0;
		m._20 = 0.0;	m._21 = sinA;	m._22 = cosA;	m._23 = 0.0;
		m._30 = 0.0;	m._31 = 0.0;	m._32 = 0.0;	m._33 = 1.0;
		// @formatter:on

		return m;
	}

	public static Mtx44 rotTrigY(double sinA, double cosA) {
		Mtx44 m = new Mtx44();

		// @formatter:off
		m._00 = cosA;	m._01 = 0.0;	m._02 = sinA;	m._03 = 0.0;
		m._10 = 0.0;	m._11 = 1.0;	m._12 = 0.0;	m._13 = 0.0;
		m._20 = -sinA;	m._21 = 0.0;	m._22 = cosA;	m._23 = 0.0;
		m._30 = 0.0;	m._31 = 0.0;	m._32 = 0.0;	m._33 = 1.0;
		// @formatter:on

		return m;
	}

	public static Mtx44 rotTrigZ(double sinA, double cosA) {
		Mtx44 m = new Mtx44();

		// @formatter:off
		m._00 = cosA;	m._01 = -sinA;	m._02 = 0.0;	m._03 = 0.0;
		m._10 = sinA;	m._11 = cosA;	m._12 = 0.0;	m._13 = 0.0;
		m._20 = 0.0;	m._21 = 0.0;	m._22 = 1.0;	m._23 = 0.0;
		m._30 = 0.0;	m._31 = 0.0;	m._32 = 0.0;	m._33 = 1.0;
		// @formatter:on

		return m;
	}

	public static Mtx44 rotRadX(double rad) {
		return rotTrigX(Math.sin(rad), Math.cos(rad));
	}

	public static Mtx44 rotRadY(double rad) {
		return rotTrigY(Math.sin(rad), Math.cos(rad));
	}

	public static Mtx44 rotRadZ(double rad) {
		return rotTrigZ(Math.sin(rad), Math.cos(rad));
	}

	public static Mtx44 rotDegX(double deg) {
		return rotRadX(deg / 180.0 * Math.PI);
	}

	public static Mtx44 rotDegY(double deg) {
		return rotRadY(deg / 180.0 * Math.PI);
	}

	public static Mtx44 rotDegZ(double deg) {
		return rotRadZ(deg / 180.0 * Math.PI);
	}

	public static Mtx44 perspective(double fovY, double aspect, double n, double f) {
		Mtx44 m = new Mtx44();

		double angle = fovY / 360.0 * Math.PI;
		double cot = 1.0f / Math.tan(angle);

		m._00 = cot / aspect;
		m._01 = 0.0;
		m._02 = 0.0;
		m._03 = 0.0;

		m._10 = 0.0;
		m._11 = cot;
		m._12 = 0.0;
		m._13 = 0.0;

		m._20 = 0.0;
		m._21 = 0.0;

		double tmp = 1.0 / (f - n);

		m._22 = -(f + n) * tmp;
		m._23 = -2.0 * f * n * tmp;

		m._30 = 0.0;
		m._31 = 0.0;
		m._32 = -1.0;
		m._33 = 0.0;

		return m;
	}
}
