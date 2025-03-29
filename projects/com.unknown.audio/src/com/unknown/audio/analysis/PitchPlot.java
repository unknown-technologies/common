package com.unknown.audio.analysis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.unknown.audio.dsp.EAC;
import com.unknown.audio.meta.riff.RiffWave;

public class PitchPlot {
	public static BufferedImage plot(RiffWave wav, int plotHeight, double fmin, double fmax) {
		return plot(wav, 0, wav.getSampleCount(), plotHeight, fmin, fmax, 2048, 64);
	}

	public static BufferedImage plot(RiffWave wav, int plotHeight, double fmin, double fmax, int fftSize,
			int fftSpacing) {
		return plot(wav, 0, wav.getSampleCount(), plotHeight, fmin, fmax, fftSize, fftSpacing);
	}

	public static BufferedImage plot(RiffWave wav, int start, int end, int plotHeight, double fmin, double fmax,
			int fftSize, int fftSpacing) {
		short[] samples = wav.get16bitMono(start, end);
		double[] normalized = new double[samples.length];
		for(int i = 0; i < normalized.length; i++) {
			normalized[i] = samples[i] / 32768.0;
		}
		double[][] result = EAC.eac(normalized, fftSize, fftSpacing);

		BufferedImage img = new BufferedImage(result.length, plotHeight, BufferedImage.TYPE_INT_RGB);

		double max = -Double.MAX_VALUE;
		double[] frange = SpectrogramHelper.getSpectrumBounds(wav.getSampleRate(), fmin, fmax, fftSize,
				SpectrogramType.linear);
		double[] bins = SpectrogramHelper.getBins(frange[0], frange[1], wav.getSampleRate(), fftSize,
				plotHeight, NumberScaleType.nstLinear);

		// find maximum value
		for(int x = 0; x < result.length; x++) {
			for(int y = 0; y < plotHeight; y++) {
				double bin = bins[y];
				double nextBin = bins[y + 1];
				double value = SpectrogramHelper.getValue(result[x], bin, nextBin, true);
				if(value > max) {
					max = value;
				}
			}
		}

		// create plot
		for(int x = 0; x < result.length; x++) {
			for(int y = 0; y < plotHeight; y++) {
				int py = plotHeight - y - 1;
				double bin = bins[y];
				double nextBin = bins[y + 1];
				double value = SpectrogramHelper.getValue(result[x], bin, nextBin, true) /
						max;
				if(value < 0) {
					value = 0;
				} else if(value > 1.0) {
					value = 1.0;
				}
				int i = (int) (value * 255);
				int rgb = i | i << 8 | i << 16;
				img.setRGB(x, py, rgb);
			}
		}
		return img;
	}

	public static void main(String[] args) throws IOException {
		if(args.length < 3 || args.length > 6) {
			System.out.println("usage: PitchPlot in.wav out.png secs [high-freq [fft-size [fft-spacing]]]");
			return;
		}
		try(InputStream in = new FileInputStream(args[0])) {
			RiffWave wav = RiffWave.read(in);
			int length = wav.getSampleRate() * Integer.parseInt(args[2]);
			int highFreq = 1000;
			int fftSize = 2048;
			int fftSpacing = 64;
			if(args.length > 3) {
				highFreq = Integer.parseInt(args[3]);
			}
			if(args.length > 4) {
				fftSize = Integer.parseInt(args[4]);
			}
			if(args.length > 5) {
				fftSpacing = Integer.parseInt(args[5]);
			}
			BufferedImage img = plot(wav, 0, length, 2000, 0, highFreq, fftSize, fftSpacing);
			ImageIO.write(img, "png", new File(args[1]));
		}
	}
}
