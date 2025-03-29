package com.unknown.math;

import java.util.Arrays;

/**
 * This class handles the calculation of a <a
 * href="https://en.wikipedia.org/wiki/Moving_average">moving average</a>.
 * Additionally the median can be calculated.
 *
 */
public class MovingAverage {
	private double sum = 0;
	private int index = 0;
	private int used = 0;
	private double[] values;

	// cached median
	private boolean validMedian = true;
	private double median = 0;
	private double q1 = 0;
	private double q3 = 0;

	/**
	 * Constructs a new MovingAverage of the given window size.
	 *
	 * @param size
	 *                window size
	 */
	public MovingAverage(int size) {
		values = new double[size];
	}

	/**
	 * Constructs a new MovingAverage of the given window size and
	 * initializes all values with the given value.
	 *
	 * @param size
	 *                window size
	 * @param value
	 *                initial value
	 */
	public MovingAverage(int size, double value) {
		this(size);
		for(int i = 0; i < values.length; i++) {
			values[i] = value;
			sum += value;
		}
	}

	/**
	 * Inserts a value into the queue.
	 *
	 * @param value
	 *                the value
	 */
	public void record(double value) {
		sum -= values[index];
		values[index++] = value;
		sum += value;
		index %= values.length;
		if(used < values.length) {
			used++;
		}
		validMedian = false;
	}

	/**
	 * Calculates the arithmetic average.
	 *
	 * @return arithmetic average
	 */
	public double getAverage() {
		return sum / used;
	}

	/**
	 * Calculates the median. The median is cached: repeated calls to this
	 * function will calculate the median only once.
	 *
	 * @return the median
	 */
	public double getMedian() {
		if(validMedian) {
			return median;
		}

		double[] sorted = Arrays.copyOf(values, used);
		Arrays.sort(sorted);
		int half = sorted.length / 2;
		int lowerHalf, upperHalf;
		if(sorted.length % 2 == 1) {
			lowerHalf = half;
			upperHalf = half;
		} else {
			lowerHalf = half - 1;
			upperHalf = half;
		}

		median = median(sorted, 0, sorted.length);
		q1 = median(sorted, 0, lowerHalf + 1);
		q3 = median(sorted, upperHalf, sorted.length);

		validMedian = true;
		return median;
	}

	public double getQuantile1() {
		getMedian();
		return q1;
	}

	public double getQuantile3() {
		getMedian();
		return q3;
	}

	/*
	 * Calculates the median of a sorted list.
	 */
	private static double median(double[] list, int lower, int upper) {
		int length = upper - lower;
		int mid = lower + length / 2;

		if(length == 0) {
			return 0;
		}
		if((length & 1) == 1) {
			return list[mid];
		} else {
			return (list[mid] + list[mid - 1]) / 2;
		}
	}

	/**
	 * Returns the complete history.
	 *
	 * @return the history
	 */
	public double[] getHistory() {
		return Arrays.copyOf(values, used);
	}

	/**
	 * Returns the configured window size.
	 *
	 * @return the window size
	 */
	public int getWindowSize() {
		return values.length;
	}

	/**
	 * Resets the stored information.
	 */
	public void reset() {
		used = 0;
		sum = 0;
		index = 0;
		values[0] = 0;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("MovingAverage[");
		if(used > 0) {
			buf.append(values[0]);
		}
		for(int i = 1; i < used; i++) {
			buf.append(",").append(values[i]);
		}
		return buf.append("]").toString();
	}
}