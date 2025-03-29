package com.unknown.math;

/**
 * This class computes per-component moving averages on a vector.
 */
public class MovingVectorAverage {
	private MovingAverage[] components;

	public MovingVectorAverage(int history, int components) {
		this.components = new MovingAverage[components];
		for(int i = 0; i < components; i++)
			this.components[i] = new MovingAverage(history);
	}

	public void record(Vector v) {
		if(v.length() != components.length)
			throw new IllegalArgumentException(
					"dimensions do not match");
		for(int i = 0; i < v.length(); i++)
			components[i].record(v.get(i));
	}

	public Vector getAverage() {
		Vector v = new Vector(components.length);
		for(int i = 0; i < components.length; i++)
			v.set(i, components[i].getAverage());
		return v;
	}

	public Vector getMedian() {
		Vector v = new Vector(components.length);
		for(int i = 0; i < components.length; i++)
			v.set(i, components[i].getMedian());
		return v;
	}

	public Vector getQuantile1() {
		Vector v = new Vector(components.length);
		for(int i = 0; i < components.length; i++)
			v.set(i, components[i].getQuantile1());
		return v;
	}

	public Vector getQuantile3() {
		Vector v = new Vector(components.length);
		for(int i = 0; i < components.length; i++)
			v.set(i, components[i].getQuantile3());
		return v;
	}
}
