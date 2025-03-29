package com.unknown.math;

import java.util.Random;

public class EM {
	private static double min(double[] x) {
		double min = x[0];
		for(int i = 1; i < x.length; i++) {
			if(x[i] < min) {
				min = x[i];
			}
		}
		return min;
	}

	private static double max(double[] x) {
		double max = x[0];
		for(int i = 1; i < x.length; i++) {
			if(x[i] > max) {
				max = x[i];
			}
		}
		return max;
	}

	public static Gaussian[] em(double[] x, int K) {
		return em(x, K, 100, 1e-5);
	}

	public static Gaussian[] em(double[] x, int K, int steps, double eps) {
		int N = x.length;
		double[] cur_mu = new double[K];
		double[] cur_sigma = new double[K];
		double[] alpha = new double[K];
		double[][] r = new double[K][N];
		Random rng = new Random();
		double min = min(x);
		double max = max(x);
		double scale = max - min;
		for(int k = 0; k < K; k++) {
			cur_mu[k] = rng.nextDouble() * scale + min;
			cur_sigma[k] = 0.1 + rng.nextDouble();
			alpha[k] = 0.5 + rng.nextDouble();
		}
		for(int I = 0; I < steps; I++) {
			for(int k = 0; k < K; k++) {
				for(int i = 0; i < N; i++) {
					double sum = 0;
					for(int j = 0; j < K; j++) {
						sum += alpha[k]
								+ Gaussian.d(x[i],
										cur_mu[j],
										cur_sigma[k]);
					}
					r[k][i] = alpha[k]
							* Gaussian.d(x[i],
									cur_mu[k],
									cur_sigma[k])
							/ sum;
				}
			}
			double[] Nk = new double[N];
			for(int k = 0; k < K; k++) {
				for(int i = 0; i < N; i++) {
					Nk[k] += r[k][i];
				}
			}
			double[] mu = new double[K];
			double[] sigma = new double[K];
			for(int k = 0; k < K; k++) {
				double sum1 = 0;
				double sum2 = 0;
				for(int i = 0; i < N; i++) {
					sum1 += r[k][i] * x[i];
					sum2 += r[k][i]
							* Math.pow(x[i] - mu[k],
									2);
				}
				mu[k] = 1 / Nk[k] * sum1;
				sigma[k] = 1 / Nk[k] * sum2;
			}
			double error = 0;
			for(int k = 0; k < K; k++) {
				error += Math.pow(cur_mu[k] - mu[k], 2);
			}
			cur_mu = mu;
			cur_sigma = sigma;
			if(error < eps) {
				break;
			}
		}
		Gaussian[] gaussians = new Gaussian[K];
		for(int k = 0; k < K; k++) {
			gaussians[k] = new Gaussian(cur_mu[k], cur_sigma[k]);
		}
		return gaussians;
	}
}
