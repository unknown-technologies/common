package com.unknown.math;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

public class Matrix {
	private int lines;
	private int cols;
	private double[][] values;

	public Matrix(int lines, int cols) {
		this.lines = lines;
		this.cols = cols;
		values = new double[lines][cols];
	}

	public Matrix(double[][] values) {
		this.values = values;
		this.lines = values.length;
		this.cols = values[0].length;
	}

	public double get(int line, int col) {
		return values[line][col];
	}

	public void set(int line, int col, double value) {
		values[line][col] = value;
	}

	public Matrix T() {
		Matrix m = new Matrix(cols, lines);
		for(int i = 0; i < lines; i++)
			for(int j = 0; j < cols; j++)
				m.set(j, i, get(i, j));
		return m;
	}

	// (m, n) x (n, o) = (m, o)
	public Matrix multiply(Matrix v) {
		if(cols != v.lines)
			throw new IllegalArgumentException(
					"dimensions do not match: " + cols
							+ " vs " + v.lines);
		Matrix r = new Matrix(lines, v.cols);
		for(int i = 0; i < lines; i++) {
			for(int j = 0; j < v.cols; j++) {
				double sum = 0;
				for(int k = 0; k < cols; k++)
					sum += get(i, k) * v.get(k, j);
				r.set(i, j, sum);
			}
		}
		return r;
	}

	public Vector multiply(Vector v) {
		if(v.length() != cols)
			throw new IllegalArgumentException(
					"dimensions do not match");
		Vector r = new Vector(lines);
		for(int i = 0; i < lines; i++) {
			double sum = 0;
			for(int j = 0; j < cols; j++)
				sum += get(i, j) * v.get(j);
			r.set(i, sum);
		}
		return r;
	}

	public Vector vec() {
		if(lines == 1) {
			Vector v = new Vector(cols);
			for(int i = 0; i < cols; i++)
				v.set(i, get(0, i));
			return v;
		} else if(cols == 1) {
			Vector v = new Vector(lines);
			for(int i = 0; i < lines; i++)
				v.set(i, get(i, 0));
			return v;
		} else
			throw new RuntimeException("invalid dimension: "
					+ lines + "x" + cols);
	}

	public Matrix minor(int line, int col) {
		Matrix min = new Matrix(lines - 1, cols - 1);
		int __x = 0;
		int __y;
		for(int _x = 0; _x < lines; _x++) {
			if(_x == line)
				continue;
			__y = 0;
			for(int _y = 0; _y < cols; _y++) {
				if(_y != col) {
					min.set(__y, __x, get(_y, _x));
					__y++;
				}
			}
			__x++;
		}
		return min;
	}

	public double det() {
		if(lines != cols) {
			throw new IllegalStateException("matrix is not square");
		}
		if(lines == 1 && cols == 1) {
			return get(0, 0);
		} else if(lines == 2 && cols == 2) {
			// @formatter:off
			return get(0, 0) * get(1, 1) -
			       get(0, 1) * get(1, 0);
			// @formatter:on
		} else {
			double sum = 0;
			for(int i = 0; i < cols; i++) {
				sum += (((i & 1) == 0) ? 1 : -1) * get(0, i) *
						minor(i, 0).det();
			}
			return sum;
		}
	}

	public int lines() {
		return lines;
	}

	public int cols() {
		return cols;
	}

	public void load(Reader in) throws IOException {
		StringBuilder buf = new StringBuilder();
		int c;
		int i = 0;
		int j = 0;
		while((c = in.read()) != -1 && (i != lines || j != 0)) {
			if(c == '\r' || c == '\n' || c == '\t' || c == ' '
					|| c == ';') {
				double value = Double.parseDouble(buf
						.toString());
				set(i, j++, value);
				if(j >= cols) {
					j = 0;
					i++;
				}
				buf = new StringBuilder();
			} else
				buf.append((char) c);
		}
		if(buf.length() != 0) {
			double value = Double.parseDouble(buf.toString());
			set(i, j++, value);
			if(j >= cols) {
				j = 0;
				i++;
			}
		}
		if(i != lines || j != 0)
			throw new RuntimeException("not enough data");
	}

	public void load(double[] data) {
		if(data.length != lines * cols)
			throw new IllegalArgumentException("illegal size");
		int i = 0;
		for(int col = 0; col < cols; col++)
			for(int line = 0; line < lines; line++)
				values[line][col] = data[i++];
	}

	public double[][] values() {
		double[][] v = new double[lines][cols];
		for(int i = 0; i < lines; i++) {
			for(int j = 0; j < cols; j++) {
				v[i][j] = values[i][j];
			}
		}
		return v;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder("[");
		for(int i = 0; i < lines; i++) {
			if(i != 0)
				buf.append(",");
			buf.append("[").append(get(i, 0));
			for(int j = 1; j < cols; j++)
				buf.append(",").append(get(i, j));
			buf.append("]");
		}
		return buf.append("]").toString();
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(!(o instanceof Matrix)) {
			return false;
		}
		Matrix m = (Matrix) o;
		if(m.lines != lines || m.cols != cols) {
			return false;
		}
		for(int i = 0; i < lines; i++) {
			for(int j = 0; j < cols; j++) {
				if(get(i, j) != m.get(i, j)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(values);
	}
}