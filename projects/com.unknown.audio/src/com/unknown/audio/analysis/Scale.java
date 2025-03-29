package com.unknown.audio.analysis;

public class Scale {
	public static double hzToMel(double hz) {
		return 1127 * Math.log(1 + hz / 700);
	}

	public static double melToHz(double mel) {
		return 700 * (Math.exp(mel / 1127) - 1);
	}

	public static double hzToBark(double hz) {
		// Traunmueller's formula
		double z1 = 26.81 * hz / (1960 + hz) - 0.53;
		if(z1 < 2.0) {
			return z1 + 0.15 * (2.0 - z1);
		} else if(z1 > 20.1) {
			return z1 + 0.22 * (z1 - 20.1);
		} else {
			return z1;
		}
	}

	public static double barkToHz(double z1) {
		double z = z1;
		if(z1 < 2.0) {
			z = 2.0 + (z1 - 2.0) / 0.85;
		} else if(z1 > 20.1) {
			z = 20.1 + (z1 - 20.1) / 1.22;
		}
		return 1960 * (z + 0.53) / (26.28 - z);
	}

	public static double hzToErb(double hz) {
		return 11.17268 * Math.log(1 + (46.06538 * hz) / (hz + 14678.49));
	}

	public static double erbToHz(double erb) {
		return 676170.4 / (47.06538 - Math.exp(0.08950404 * erb)) - 14678.49;
	}

	public static double hzToPeriod(double hz) {
		return -1.0 / Math.max(1.0f, hz);
	}

	public static double periodToHz(double u) {
		return -1.0 / u;
	}
}
