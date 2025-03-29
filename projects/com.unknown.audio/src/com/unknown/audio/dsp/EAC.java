package com.unknown.audio.dsp;

import java.util.Arrays;

public class EAC {
	public static double[][] eac(double[] data) {
		return eac(data, 2048);
	}

	public static double[][] eac(double[] data, int windowSize) {
		return eac(data, windowSize, windowSize / 2);
	}

	public static double[][] eac(double[] data, int windowSize, int spacing) {
		if(data.length < windowSize) {
			throw new IllegalArgumentException("need at least + " + windowSize + " samples");
		}
		double[] win = FFT.makeWindow(FFT.HANN, windowSize, windowSize);
		int windows = 0;
		int half = windowSize / 2;
		int length = 0;
		for(int start = 0; start + windowSize <= data.length; start += spacing) {
			length++;
		}
		assert length > 0;
		double[][] processed = new double[length][half];
		for(int start = 0; start + windowSize <= data.length; start += spacing) {
			double[] re = new double[windowSize];
			for(int i = 0; i < re.length; i++) {
				re[i] = data[start + i] * win[i];
			}
			double[] im = new double[windowSize];
			FFT.fft(re, im, FFT.FORWARD);
			for(int i = 0; i < re.length; i++) {
				re[i] = Math.pow(Math.pow(re[i], 2) + Math.pow(im[i], 2), 1.0 / 3.0);
			}
			Arrays.fill(im, 0.0);
			FFT.fft(re, im, FFT.FORWARD);

			for(int i = 0; i < half; i++) {
				// Clip at zero, copy to temp array
				if(re[i] < 0.0) {
					re[i] = 0;
				}
				im[i] = re[i];
				// Subtract a time-doubled signal (linearly interp.) from the original (clipped) signal
				if((i % 2) == 0) {
					re[i] -= im[i / 2];
				} else {
					re[i] -= ((im[i / 2] + im[i / 2 + 1]) / 2);
				}

				// Clip at zero again
				if(re[i] < 0.0) {
					re[i] = 0;
				}
			}
			for(int i = 0; i < half; i++) {
				processed[windows][half - 1 - i] = re[i] / (windowSize / 4);
			}
			windows++;
		}
		assert windows == length;
		return processed;
	}
}
