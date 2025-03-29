package com.unknown.math;

import java.util.Random;

public class GaussianDistribution {
	private final Random random = new Random();

	public double sample() {
		return random.nextGaussian();
	}

	public double sample(double μ) {
		return random.nextGaussian() + μ;
	}

	public double sample(double μ, double σ) {
		return random.nextGaussian() * σ + μ;
	}
}
