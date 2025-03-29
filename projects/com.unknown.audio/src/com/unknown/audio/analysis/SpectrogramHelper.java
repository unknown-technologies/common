package com.unknown.audio.analysis;

import java.util.Iterator;

public class SpectrogramHelper {
	public static double getValue(double[] spectrum, double bin0, double bin1, boolean autocorrelation) {
		int index, limitIndex;
		int nBins = spectrum.length;
		if(autocorrelation) {
			// bin = 2 * nBins / (nBins - 1 - array_index);
			// Solve for index
			index = (int) Math.max(0, Math.min(nBins - 1, (nBins - 1) - (2 * nBins) / (Math.max(1, bin0))));
			limitIndex = (int) Math.max(0,
					Math.min(nBins - 1, (nBins - 1) - (2 * nBins) / (Math.max(1, bin1))));
		} else {
			index = Math.min(nBins - 1, (int) (Math.floor(0.5 + bin0)));
			limitIndex = Math.min(nBins, (int) (Math.floor(0.5 + bin1)));
		}
		double value = spectrum[index];
		while(++index <= limitIndex) {
			value = Math.max(value, spectrum[index]);
		}
		value = Math.min(1, Math.max(0, value));
		return value;
	}

	public static double findBin(double frequency, double binUnit) {
		double linearBin = frequency / binUnit;
		if(linearBin < 0)
			return -1;
		else
			return linearBin;
	}

	public static int nBins(int fftSize) {
		return fftSize / 2;
	}

	public static double[] getBins(double minFreq, double maxFreq, double rate, int fftSize, int height,
			NumberScaleType type) {
		int half = fftSize / 2;
		double binUnit = rate / (2 * half);
		int nBins = nBins(fftSize);

		// nearest frequency to each pixel row from number scale, for selecting
		// the desired fft bin(s) for display on that row
		double[] bins = new double[height + 1];
		NumberScale numberScale = new NumberScale(type, minFreq, maxFreq);

		Iterator<Double> it = numberScale.iterator(height);
		double nextBin = Math.max(0, Math.min(nBins - 1, findBin(it.next(), binUnit)));

		int y;
		for(y = 0; y < height; y++) {
			bins[y] = nextBin;
			nextBin = Math.max(0, Math.min(nBins - 1, findBin(it.next(), binUnit)));
		}
		bins[y] = nextBin;
		return bins;
	}

	public static double[] getSpectrumBounds(double rate, double fmin, double fmax, int fftSize,
			SpectrogramType type) {
		double top = rate / 2.;

		double bottom;
		if(type == SpectrogramType.linear) {
			bottom = 0;
		} else if(type == SpectrogramType.period) {
			// special case
			double half = fftSize / 2;
			// EAC returns no data for below this frequency:
			double bin2 = rate / half;
			bottom = bin2;
		} else {
			// logarithmic, etc.
			bottom = 1;
		}

		double min, max;
		double spectrumMax = fmax;
		if(spectrumMax < 0) {
			max = top;
		} else {
			max = Math.max(bottom, Math.min(top, spectrumMax));
		}

		double spectrumMin = fmin;
		if(spectrumMin < 0) {
			min = Math.max(bottom, top / 1000);
		} else {
			min = Math.max(bottom, Math.min(top, spectrumMin));
		}
		return new double[] { min, max };
	}

}
