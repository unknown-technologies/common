package com.unknown.audio;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.unknown.audio.analysis.Spectrogram;
import com.unknown.audio.dsp.FFT;
import com.unknown.audio.meta.riff.RiffWave;

public class SpectrogramRenderer {
	public static void main(String[] args) throws IOException {
		int fftSize = 512;
		int stepSize = fftSize / 4;
		int windowSize = fftSize;

		RiffWave wav;
		try(InputStream in = new FileInputStream(args[0])) {
			wav = RiffWave.read(in);
		}

		if(args.length > 2) {
			fftSize = Integer.parseInt(args[2]);
			stepSize = fftSize / 4;
			windowSize = fftSize;
		}

		if(args.length > 3) {
			stepSize = Integer.parseInt(args[3]);
		}

		if(args.length > 4) {
			windowSize = Integer.parseInt(args[4]);
		}

		float[][] samples = wav.getFloatSamples();

		float[][][] fft = Spectrogram.spectrogram(samples, fftSize, stepSize, FFT.HAMMING, windowSize);

		float max = 0;
		for(int i = 0; i < fft.length; i++) {
			for(int j = 0; j < fft[i].length; j++) {
				for(int k = 0; k < fft[i][j].length; k++) {
					if(fft[i][j][k] > max) {
						max = fft[i][j][k];
					}
				}
			}
		}

		BufferedImage img = new BufferedImage(fft[0].length, fft[0][0].length, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < fft[0].length; i++) {
			for(int f = 0; f < fft[0][i].length; f++) {
				float value = scale(fft[0][i][f] / max);
				// float value = (float) i / fft[0].length;
				int color = getColor(value);
				img.setRGB(i, fft[0][i].length - f - 1, color);
			}
		}
		ImageIO.write(img, "png", new File(args[1]));
	}

	private static float scale(float x) {
		return (float) Math.sqrt(x);
	}

	private static int getColor(float x) {
		// black, blue, green, red
		float value = x;
		if(value < 0) {
			value = 0;
		} else if(value > 1) {
			value = 1;
		}
		int count = 5;
		float threshold = (float) (1.0 / count);
		if(value < threshold) {
			int intensity = (int) (value * count * 256);
			if(intensity > 255) {
				intensity = 255;
			}
			return intensity;
		} else if(value < 2 * threshold) {
			int intensity = (int) ((value * count - 1) * 256);
			if(intensity > 255) {
				intensity = 255;
			}
			int green = intensity;
			int blue = 255;
			return (green << 8) | blue;
		} else if(value < 3 * threshold) {
			int intensity = (int) ((value * count - 2) * 256);
			if(intensity > 255) {
				intensity = 255;
			}
			int green = 255;
			int blue = 255 - intensity;
			return (green << 8) | blue;
		} else if(value < 4 * threshold) {
			int intensity = (int) ((value * count - 3) * 256);
			if(intensity > 255) {
				intensity = 255;
			}
			int red = intensity;
			int green = 255;
			return (red << 16) | (green << 8);
		} else {
			int intensity = (int) ((value * count - 4) * 256);
			if(intensity > 255) {
				intensity = 255;
			}
			int red = 255;
			int green = 255 - intensity;
			return (red << 16) | (green << 8);
		}
	}
}
