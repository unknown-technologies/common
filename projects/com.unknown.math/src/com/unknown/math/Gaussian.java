package com.unknown.math;

public class Gaussian {
	public Vector mu;
	public Matrix sigma;

	public Gaussian(double mu, double sigma) {
		this(new Vector(new double[] { mu }), new Matrix(
				new double[][] { { sigma } }));
	}

	public Gaussian(Vector mu, Matrix sigma) {
		this.mu = mu;
		this.sigma = sigma;
		if(mu.length() != sigma.lines() || mu.length() != sigma.cols()) {
			throw new IllegalArgumentException("invalid mu/sigma");
		}
	}

	public static double d(double x, double mu, double sigma) {
		return 1.0
				/ Math.sqrt(2 * Math.PI * sigma * sigma)
				* Math.exp(-(Math.pow(x - mu, 2) / (2 * sigma * sigma)));
	}

	public double d(Vector x) {
		if(x.length() == 1) {
			double _x = x.get(0);
			double s = sigma.get(0, 0);
			double m = mu.get(0);
			return 1.0
					/ Math.sqrt(2 * Math.PI * s * s)
					* Math.exp(-(Math.pow(_x - m, 2) / (2 * s * s)));
		} else {
			throw new AssertionError("Not Implemented");
		}
	}

	public double mu() {
		if(mu.length() != 1) {
			throw new IllegalStateException("not a 1D normal distribution");
		}
		return mu.get(0);
	}

	public double sigma() {
		if(mu.length() != 1) {
			throw new IllegalStateException("not a 1D normal distribution");
		}
		return sigma.get(0, 0);
	}
}
