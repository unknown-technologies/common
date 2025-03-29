package com.unknown.math;

import java.io.Reader;
import java.io.IOException;

public class Vector {
	private double[] values;

	public Vector(int size) {
		values = new double[size];
	}

	public Vector(double[] values) {
		this.values = values;
	}

	public void clear() {
		for(int i = 0; i < values.length; i++)
			values[i] = 0;
	}

	public double get(int i) {
		return values[i];
	}

	public void set(int i, double v) {
		values[i] = v;
	}

	public Vector add(Vector v) {
		if(v.length() != length())
			throw new IllegalArgumentException(
					"dimensions do not match");
		Vector r = new Vector(length());
		for(int i = 0; i < length(); i++)
			r.set(i, get(i) + v.get(i));
		return r;
	}

	public Vector add(double x) {
		Vector r = new Vector(length());
		for(int i = 0; i < length(); i++)
			r.set(i, get(i) + x);
		return r;
	}

	public Vector subtract(Vector v) {
		if(v.length() != length())
			throw new IllegalArgumentException(
					"dimensions do not match");
		Vector r = new Vector(length());
		for(int i = 0; i < length(); i++)
			r.set(i, get(i) - v.get(i));
		return r;
	}

	public Vector subtract(double x) {
		Vector r = new Vector(length());
		for(int i = 0; i < length(); i++)
			r.set(i, get(i) - x);
		return r;
	}

	public Vector multiply(double x) {
		Vector r = new Vector(length());
		for(int i = 0; i < length(); i++)
			r.set(i, get(i) * x);
		return r;
	}

	public Vector multiply(Vector v) {
		if(v.length() != length())
			throw new IllegalArgumentException(
					"dimensions do not match");
		Vector r = new Vector(length());
		for(int i = 0; i < length(); i++)
			r.set(i, get(i) * v.get(i));
		return r;
	}

	public Vector divide(double x) {
		Vector r = new Vector(length());
		for(int i = 0; i < length(); i++)
			r.set(i, get(i) / x);
		return r;
	}

	public Vector divide(Vector v) {
		if(v.length() != length())
			throw new IllegalArgumentException(
					"dimensions do not match");
		Vector r = new Vector(length());
		for(int i = 0; i < length(); i++)
			r.set(i, get(i) / v.get(i));
		return r;
	}

	public double dot(Vector v) {
		if(v.length() != length())
			throw new IllegalArgumentException(
					"dimensions do not match");
		double result = 0;
		for(int i = 0; i < length(); i++)
			result += get(i) * v.get(i);
		return result;
	}

	public Vector softmax() {
		double sum = 0;
		Vector r = new Vector(length());
		for(int i = 0; i < length(); i++)
			sum += Math.exp(get(i));
		for(int i = 0; i < length(); i++)
			r.set(i, Math.exp(get(i)) / sum);
		return r;
	}

	public Vector tanh() {
		Vector r = new Vector(length());
		for(int i = 0; i < length(); i++)
			r.set(i, Math.tanh(get(i)));
		return r;
	}

	public int argmax() {
		int idx = 0;
		double max = 0;
		for(int i = 1; i < length(); i++) {
			if(get(i) > max) {
				idx = i;
				max = get(i);
			}
		}
		return idx;
	}

	public double abs() {
		double sum = 0;
		for(int i = 0; i < length(); i++)
			sum += get(i) * get(i);
		return Math.sqrt(sum);
	}

	public int length() {
		return values.length;
	}

	public void load(Reader in) throws IOException {
		StringBuilder buf = new StringBuilder();
		int c;
		int i = 0;
		while((c = in.read()) != -1 && i != length()) {
			if(c == '\r' || c == '\n' || c == '\t' || c == ' '
					|| c == ';') {
				double value = Double.parseDouble(buf
						.toString());
				set(i++, value);
				buf = new StringBuilder();
			} else
				buf.append((char) c);
		}
		if(buf.length() != 0) {
			double value = Double.parseDouble(buf.toString());
			set(i++, value);
		}
		if(i != length())
			throw new RuntimeException("not enough data");
	}

	public Matrix mat() {
		Matrix m = new Matrix(length(), 1);
		for(int i = 0; i < length(); i++)
			m.set(i, 0, get(i));
		return m;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder("[");
		if(length() > 0)
			buf.append(get(0));
		for(int i = 1; i < length(); i++)
			buf.append(",").append(get(i));
		return buf.append("]").toString();
	}
}
