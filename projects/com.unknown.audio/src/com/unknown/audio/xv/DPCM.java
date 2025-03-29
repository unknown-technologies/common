package com.unknown.audio.xv;

public class DPCM {
	public static int[] decode(byte[] coefs, byte[] deltas) {
		int[] result = new int[coefs.length * 2 * 16];

		int value = 0;

		for(int frame = 0; frame < 2 * coefs.length; frame++) {
			int coef = coefs[frame / 2];

			int exp;
			if((frame & 1) == 0) {
				exp = coef & 0x0F;
			} else {
				exp = (coef >> 4) & 0x0F;
			}

			for(int i = 0; i < 16; i++) {
				int offset = frame * 16 + i;
				int sample = deltas[offset];

				int delta = sample << exp;
				value += delta;

				result[offset] = value << 14; // padded to 32bit
			}
		}

		return result;
	}

	public static int[] stream(byte[] coefs, byte[] deltas, int start, int initial, int length, int sampleStart,
			int loop, int sampleEnd) {
		int[] result = new int[length];

		int value = initial;

		int addr = start;
		for(int i = 0; i < length; i++) {
			int sampleId = addr - sampleStart;
			int frame = sampleId / 16;
			int coef = coefs[frame / 2];

			int exp;
			if((frame & 1) == 0) {
				exp = coef & 0x0F;
			} else {
				exp = (coef >> 4) & 0x0F;
			}

			int sample = deltas[addr];

			int delta = sample << exp;
			value += delta;

			result[i] = value << 14; // padded to 32bit

			addr++;
			if(addr > sampleEnd) {
				addr = loop;
			}
		}

		return result;
	}

	private static final int log2(int x) {
		if(x < 2) {
			return 0;
		} else if(x < 4) {
			return 1;
		} else if(x < 8) {
			return 2;
		} else if(x < 16) {
			return 3;
		} else if(x < 32) {
			return 4;
		} else if(x < 64) {
			return 5;
		} else if(x < 128) {
			return 6;
		} else if(x < 256) {
			return 7;
		} else if(x < 512) {
			return 8;
		} else if(x < 1024) {
			return 9;
		} else if(x < 2048) {
			return 10;
		} else if(x < 4096) {
			return 11;
		} else if(x < 8192) {
			return 12;
		} else if(x < 16384) {
			return 13;
		} else if(x < 32768) {
			return 14;
		} else if(x < 65536) {
			return 15;
		} else if(x < 131072) {
			return 16;
		} else if(x < 262144) {
			return 17;
		} else {
			return 18;
		}
	}

	public static void encode(byte[] coefs, byte[] deltas, int[] samples) {
		int value = 0;
		int invalue = 0;

		for(int frame = 0; frame < 2 * coefs.length; frame++) {
			// find minimum/maximum of delta in the current frame
			int maxdelta = Integer.MIN_VALUE;
			for(int i = 0; i < 16; i++) {
				int offset = frame * 16 + i;
				int sample = samples[offset] >> 14; // 18bit
				int delta = sample - invalue;
				int absdelta = delta < 0 ? -delta : delta;
				if(absdelta > maxdelta) {
					maxdelta = absdelta;
				}
				invalue = sample;
			}

			// decide on coefficient
			int exp = log2(maxdelta) - 3;
			if(exp < 0) {
				exp = 0;
			}
			if(exp > 15) {
				exp = 15;
			}

			// store exponent
			if((frame & 1) == 0) {
				coefs[frame / 2] = (byte) exp;
			} else {
				coefs[frame / 2] |= (byte) (exp << 4);
			}

			// compute coefficient only once
			int coef = 1 << exp;

			// compute compressed sample values
			for(int i = 0; i < 16; i++) {
				int offset = frame * 16 + i;
				int sample = samples[offset] >> 14; // 18bit
				int delta = sample - value;

				// quantize delta
				deltas[offset] = (byte) (delta / coef);

				// predict
				int qsample = deltas[offset];
				int preddelta = qsample << exp;
				value += preddelta;
			}
		}
	}

	public static void encodeLoop(byte[] coefs, byte[] deltas, int[] samples, int offset, int sampleStart,
			int sampleLoop, int sampleEnd) {
		int value = 0;
		int loopValue = 0;
		int invalue = 0;

		int loopDC = (samples[sampleEnd] - samples[sampleLoop - 1]) >> 14;
		int loopLength = sampleEnd - sampleLoop + 1;
		double loopAdjust = loopDC / (double) loopLength;

		System.out.println("sample loop DC offset: " + loopDC + " (adj=" + loopAdjust + ")");

		int frameCount = sampleEnd / 16;
		if(frameCount * 16 + 15 < sampleEnd) {
			frameCount++;
		}

		if((offset & 0x0F) != 0) {
			throw new IllegalArgumentException("wave data has to be aligned to a frame");
		}

		// encode all frames except the last one
		for(int frame = 0; frame < frameCount - 1; frame++) {
			// find minimum/maximum of delta in the current frame
			int maxdelta = Integer.MIN_VALUE;
			for(int i = 0; i < 16; i++) {
				int off = frame * 16 + i;
				int sample = samples[off] >> 14; // 18bit
				int delta = sample - invalue;
				int absdelta = delta < 0 ? -delta : delta;
				if(absdelta > maxdelta) {
					maxdelta = absdelta;
				}
				invalue = sample;
			}

			// decide on coefficient
			int exp = log2((int) (maxdelta + loopAdjust)) - 3;
			if(exp < 0) {
				exp = 0;
			}
			if(exp > 15) {
				exp = 15;
			}

			// store exponent
			if((frame & 1) == 0) {
				coefs[frame / 2] = (byte) ((coefs[frame / 2] & 0xF0) | exp);
			} else {
				coefs[frame / 2] = (byte) ((coefs[frame / 2] & 0x0F) | (exp << 4));
			}

			// compute coefficient only once
			int coef = 1 << exp;

			// compute compressed sample values
			for(int i = 0; i < 16; i++) {
				int off = frame * 16 + i;
				int sample = samples[off] >> 14; // 18bit
				if(off >= sampleLoop) {
					int adj = (int) (loopAdjust * (off - sampleLoop));
					sample -= adj;
				}
				int delta = sample - value;

				// quantize delta
				deltas[off] = (byte) (delta / coef);

				// predict
				int qsample = deltas[off];
				int preddelta = qsample << exp;

				if(off == sampleLoop) {
					loopValue = value;
				}

				value += preddelta;
			}
		}

		// second pass: find smallest exponent in the loop
		int minexp = Integer.MAX_VALUE;
		for(int i = sampleLoop; i < (frameCount - 1) * 16; i += 16) {
			int sampleId = i - sampleStart;
			int frame = sampleId / 16;
			int coef = coefs[frame / 2];

			int exp;
			if((frame & 1) == 0) {
				exp = coef & 0x0F;
			} else {
				exp = (coef >> 4) & 0x0F;
			}

			if(exp < minexp) {
				minexp = exp;
			}
		}

		// third pass: count frames with minimal exponent in the loop
		int minexpcnt = 0;
		for(int i = sampleLoop; i < (frameCount - 1) * 16; i += 16) {
			int sampleId = i - sampleStart;
			int frame = sampleId / 16;
			int coef = coefs[frame / 2];

			int exp;
			if((frame & 1) == 0) {
				exp = coef & 0x0F;
			} else {
				exp = (coef >> 4) & 0x0F;
			}

			if(exp == minexp) {
				minexpcnt++;
			}
		}

		System.out.println("minimal exponent = " + minexp + " (" + minexpcnt + "x)");

		// third pass: decode loop part and adjust DC offset
		int decodeValue = loopValue;
		for(int i = sampleLoop; i < (frameCount - 1) * 16; i++) {
			int sampleId = i - sampleStart;
			int frame = sampleId / 16;
			int coef = coefs[frame / 2];

			int exp;
			if((frame & 1) == 0) {
				exp = coef & 0x0F;
			} else {
				exp = (coef >> 4) & 0x0F;
			}

			int sample = deltas[i];

			int delta = sample << exp;
			decodeValue += delta;
		}

		System.out.println("value=" + value + ", decodeValue=" + decodeValue);

		int frame = frameCount - 1;
		int end = sampleEnd & 0x0F;

		// find minimum/maximum of delta in the current frame
		int maxdelta = Integer.MIN_VALUE;
		for(int i = 0; i < 16; i++) {
			int off = frame * 16 + i;
			int sample = off >= sampleEnd ? 0 : samples[off] >> 14; // 18bit
			int delta = sample - invalue;
			int absdelta = delta < 0 ? -delta : delta;
			if(absdelta > maxdelta) {
				maxdelta = absdelta;
			}
			invalue = sample;
		}

		int lastSample = samples[sampleEnd] >> 14;
		lastSample -= (int) (loopAdjust * (sampleEnd - sampleLoop));
		int loopDelta = loopValue - lastSample;
		System.out.println(
				"loop delta: " + loopDelta + " (" +
						((samples[sampleLoop - 1] - samples[sampleEnd]) >> 14) +
						")");

		if(loopDelta / end > maxdelta) {
			maxdelta = loopDelta / end;
		}

		int adjust = loopDelta / (end + 1);
		System.out.println("loop adjust: " + adjust + " over " + (end + 1) + " samples");

		// decide on coefficient
		int exp = log2(maxdelta) - 3;
		if(exp < 0) {
			exp = 0;
		}
		if(exp > 15) {
			exp = 15;
		}

		// compute coefficient only once
		int coef = 1 << exp;

		int quant = adjust / coef * (end + 1);
		quant *= coef;
		if(quant != loopDelta) {
			System.out.println("inherent DC offset: " + (quant - loopDelta));
		}

		// store exponent
		if((frame & 1) == 0) {
			coefs[frame / 2] = (byte) ((coefs[frame / 2] & 0xF0) | exp);
		} else {
			coefs[frame / 2] = (byte) ((coefs[frame / 2] & 0x0F) | (exp << 4));
		}

		for(int i = 0; i <= end; i++) {
			int off = frame * 16 + i;
			int sample = samples[off] >> 14; // 18bit
			int adj = (int) (loopAdjust * (off - sampleLoop));
			sample -= adj;
			int delta = sample - value + adjust;

			// quantize delta
			deltas[off] = (byte) (delta / coef);

			// predict
			int qsample = deltas[off];
			int preddelta = qsample << exp;
			value += preddelta;
		}

		// last pass: adjust using minimum
		int residualDC = loopValue - value;
		if((residualDC % (1 << minexp)) == 0) {
			int adjustment = residualDC >> minexp;
			System.out.println("correcting error exactly: " + adjustment + " (" + residualDC + ")");
			int adjSign = residualDC > 0 ? 1 : -1;

			// adjusting once per sample
			for(int i = sampleLoop; i < (frameCount - 1) * 16; i++) {
				int sampleId = i - sampleStart;
				frame = sampleId / 16;
				coef = coefs[frame / 2];

				if((frame & 1) == 0) {
					exp = coef & 0x0F;
				} else {
					exp = (coef >> 4) & 0x0F;
				}

				if(exp == minexp && adjustment != 0) {
					// perform adjustment
					if(adjSign > 0 && deltas[i] < 127) {
						deltas[i] += adjSign;
						adjustment -= adjSign;
					} else if(adjSign < 0 && deltas[i] > -127) {
						deltas[i] += adjSign;
						adjustment -= adjSign;
					}
				}
			}

			System.out.println("adjustment: " + adjustment);
		}

		decodeValue = 0;
		for(int i = sampleLoop; i <= sampleEnd; i++) {
			int sampleId = i - sampleStart;
			frame = sampleId / 16;
			coef = coefs[frame / 2];

			if((frame & 1) == 0) {
				exp = coef & 0x0F;
			} else {
				exp = (coef >> 4) & 0x0F;
			}

			int sample = deltas[i];

			int delta = sample << exp;
			decodeValue += delta;
		}

		// check if there is some DC offset
		if(decodeValue != 0) {
			System.out.println("DC offset: " + decodeValue);
		} else {
			System.out.println("no DC offset");
		}
	}
}
