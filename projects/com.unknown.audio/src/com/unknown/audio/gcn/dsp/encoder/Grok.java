package com.unknown.audio.gcn.dsp.encoder;

public class Grok {
	public static void InnerProductMerge(double[] vecOut,
			short[] pcmBuf, short[] pcmBufPrev) {
		for(int i = 0; i <= 2; i++) {
			vecOut[i] = 0;
			for(int x = 0; x < 14; x++) {
				if(x - i < 0) {
					vecOut[i] -= pcmBufPrev[pcmBufPrev.length + x - i] * pcmBuf[x];
				} else {
					vecOut[i] -= pcmBuf[x - i] * pcmBuf[x];
				}
			}
		}
	}

	public static void OuterProductMerge(double[][] mtxOut,
			short[] pcmBuf, short[] pcmBufPrev) {
		for(int x = 1; x <= 2; x++) {
			for(int y = 1; y <= 2; y++) {
				mtxOut[x][y] = 0;
				for(int z = 0; z < 14; z++) {
					short pcm1 = (z - x < 0) ? pcmBufPrev[pcmBufPrev.length + z - x]
							: pcmBuf[z - x];
					short pcm2 = (z - y < 0) ? pcmBufPrev[pcmBufPrev.length + z - y]
							: pcmBuf[z - y];
					mtxOut[x][y] += pcm1 * pcm2;
				}
			}
		}
	}

	public static boolean AnalyzeRanges(double[][] mtx,
			int[] vecIdxsOut) {
		double[] recips = new double[3];
		double val;
		double tmp;
		double min;
		double max;

		// Get greatest distance from zero
		for(int x = 1; x <= 2; x++) {
			val = Math.max(Math.abs(mtx[x][1]),
					Math.abs(mtx[x][2]));
			if(val < Math.ulp(1.0)) {
				return true;
			}
			recips[x] = 1.0 / val;
		}

		int maxIndex = 0;
		for(int i = 1; i <= 2; i++) {
			for(int x = 1; x < i; x++) {
				tmp = mtx[x][i];
				for(int y = 1; y < x; y++) {
					tmp -= mtx[x][y] * mtx[y][i];
				}
				mtx[x][i] = tmp;
			}

			val = 0;
			for(int x = i; x <= 2; x++) {
				tmp = mtx[x][i];
				for(int y = 1; y < i; y++) {
					tmp -= mtx[x][y] * mtx[y][i];
				}

				mtx[x][i] = tmp;
				tmp = Math.abs(tmp) * recips[x];
				if(tmp >= val) {
					val = tmp;
					maxIndex = x;
				}
			}

			if(maxIndex != i) {
				for(int y = 1; y <= 2; y++) {
					tmp = mtx[maxIndex][y];
					mtx[maxIndex][y] = mtx[i][y];
					mtx[i][y] = tmp;
				}
				recips[maxIndex] = recips[i];
			}

			vecIdxsOut[i] = maxIndex;

			if(mtx[i][i] == 0) {
				return true;
			}

			if(i != 2) {
				tmp = 1.0 / mtx[i][i];
				for(int x = i + 1; x <= 2; x++) {
					mtx[x][i] *= tmp;
				}
			}
		}

		/* Get range */
		min = 1e10;
		max = 0;
		for(int i = 1; i <= 2; i++) {
			tmp = Math.abs(mtx[i][i]);
			if(tmp < min) {
				min = tmp;
			}
			if(tmp > max) {
				max = tmp;
			}
		}

		if(min / max < 1e-10) {
			return true;
		}

		return false;
	}

	public static void BidirectionalFilter(double[][] mtx,
			int[] vecIdxs, double[] vecInOut) {
		double tmp;

		for(int i = 1, x = 0; i <= 2; i++) {
			int index = vecIdxs[i];
			tmp = vecInOut[index];
			vecInOut[index] = vecInOut[i];
			if(x != 0) {
				for(int y = x; y <= i - 1; y++) {
					tmp -= vecInOut[y] * mtx[i][y];
				}
			} else if(tmp != 0) {
				x = i;
			}
			vecInOut[i] = tmp;
		}

		for(int i = 2; i > 0; i--) {
			tmp = vecInOut[i];
			for(int y = i + 1; y <= 2; y++) {
				tmp -= vecInOut[y] * mtx[i][y];
			}
			vecInOut[i] = tmp / mtx[i][i];
		}

		vecInOut[0] = 1;
	}

	public static boolean QuadraticMerge(double[] inOutVec) {
		double v0;
		double v1;
		double v2 = inOutVec[2];
		double tmp = 1.0 - (v2 * v2);

		if(tmp == 0) {
			return true;
		}

		v0 = (inOutVec[0] - (v2 * v2)) / tmp;
		v1 = (inOutVec[1] - (inOutVec[1] * v2)) / tmp;

		inOutVec[0] = v0;
		inOutVec[1] = v1;

		return Math.abs(v1) > 1;
	}

	public static void FinishRecord(double[] in, double[] out) {
		for(int z = 1; z <= 2; z++) {
			if(in[z] >= 1) {
				in[z] = 0.9999999999;
			} else if(in[z] <= -1) {
				in[z] = -0.9999999999;
			}
		}

		out[0] = 1;
		out[1] = (in[2] * in[1]) + in[1];
		out[2] = in[2];
	}

	public static void MatrixFilter(double[] src, double[] dst) {
		double[][] mtx = new double[3][3];

		mtx[2][0] = 1;
		for(int i = 1; i <= 2; i++) {
			mtx[2][i] = -src[i];
		}

		for(int i = 2; i > 0; i--) {
			double val = 1 - (mtx[i][i] * mtx[i][i]);
			for(int y = 1; y <= i; y++) {
				mtx[i - 1][y] = ((mtx[i][i] * mtx[i][y]) + mtx[i][y]) / val;
			}
		}

		dst[0] = 1;
		for(int i = 1; i <= 2; i++) {
			dst[i] = 0;
			for(int y = 1; y <= i; y++) {
				dst[i] += mtx[i][y] * dst[i - y];
			}
		}
	}

	private static void MergeFinishRecord(double[] src,
			double[] dst) {
		double[] tmp = new double[3];
		double val = src[0];

		dst[0] = 1;
		for(int i = 1; i <= 2; i++) {
			double v2 = 0;
			for(int y = 1; y < i; y++) {
				v2 += dst[y] * src[i - y];
			}

			if(val > 0) {
				dst[i] = -(v2 + src[i]) / val;
			} else {
				dst[i] = 0;
			}

			tmp[i] = dst[i];

			for(int y = 1; y < i; y++) {
				dst[y] += dst[i] * dst[i - y];
			}

			val *= 1.0 - (dst[i] * dst[i]);
		}

		FinishRecord(tmp, dst);
	}

	private static double ContrastVectors(double[] source1, double[] source2) {
		double val = (source2[2] * source2[1] + -source2[1]) / (1.0 - source2[2] * source2[2]);
		double val1 = (source1[0] * source1[0]) + (source1[1] * source1[1]) + (source1[2] * source1[2]);
		double val2 = (source1[0] * source1[1]) + (source1[1] * source1[2]);
		double val3 = source1[0] * source1[2];
		return val1 + (2.0 * val * val2) + (2.0 * (-source2[1] * val + -source2[2]) * val3);
	}

	private static void FilterRecords(double[][] vecBest, int exp, double[][] records, int recordCount) {
		double[][] bufferList = new double[8][3];

		int[] buffer1 = new int[8];
		double[] buffer2 = new double[3];

		int index;
		double value;
		double tempVal = 0;

		for(int x = 0; x < 2; x++) {
			for(int y = 0; y < exp; y++) {
				buffer1[y] = 0;
				for(int i = 0; i <= 2; i++) {
					bufferList[y][i] = 0;
				}
			}
			for(int z = 0; z < recordCount; z++) {
				index = 0;
				value = 1e30;
				for(int i = 0; i < exp; i++) {
					tempVal = ContrastVectors(
							vecBest[i],
							records[z]);
					if(tempVal < value) {
						value = tempVal;
						index = i;
					}
				}
				buffer1[index]++;
				MatrixFilter(records[z], buffer2);
				for(int i = 0; i <= 2; i++) {
					bufferList[index][i] += buffer2[i];
				}
			}

			for(int i = 0; i < exp; i++) {
				if(buffer1[i] > 0) {
					for(int y = 0; y <= 2; y++) {
						bufferList[i][y] /= buffer1[i];
					}
				}
			}

			for(int i = 0; i < exp; i++) {
				MergeFinishRecord(bufferList[i],
						vecBest[i]);
			}
		}
	}

	public static void DSPCorrelateCoefs(short[] source,
			int sourceIndex, int samples, short[] coefsOut) {
		int srcIdx = sourceIndex;
		int numFrames = (samples + 13) / 14;
		int frameSamples;

		short[] blockBuffer = new short[0x3800];
		short[][] pcmHistBuffer = new short[2][14];

		double[] vec1 = new double[3];
		double[] vec2 = new double[3];

		double[][] mtx = new double[3][3];
		int[] vecIdxs = new int[3];

		double[][] records = new double[numFrames * 2][3];
		int recordCount = 0;

		double[][] vecBest = new double[8][3];

		// Iterate through 1024-block frames
		for(int x = samples; x > 0;) {
			if(x > 0x3800) { // Full 1024-block frame
				frameSamples = 0x3800;
				x -= 0x3800;
			} else { // Partial frame
				 // Zero lingering block samples
				frameSamples = x;
				for(int z = 0; z < 14 && z + frameSamples < 0x3800; z++) {
					blockBuffer[frameSamples + z] = 0;
				}
				x = 0;
			}

			// Copy (potentially non-frame-aligned PCM samples
			// into aligned buffer
			System.arraycopy(source, srcIdx, blockBuffer, 0, frameSamples);
			srcIdx += frameSamples;

			for(int i = 0; i < frameSamples;) {
				for(int z = 0; z < 14; z++) {
					pcmHistBuffer[0][z] = pcmHistBuffer[1][z];
				}
				for(int z = 0; z < 14; z++) {
					pcmHistBuffer[1][z] = blockBuffer[i++];
				}

				InnerProductMerge(vec1, pcmHistBuffer[1], pcmHistBuffer[0]);
				if(Math.abs(vec1[0]) > 10) {
					OuterProductMerge(mtx, pcmHistBuffer[1], pcmHistBuffer[0]);
					if(!AnalyzeRanges(mtx, vecIdxs)) {
						BidirectionalFilter(mtx, vecIdxs, vec1);
						if(!QuadraticMerge(vec1)) {
							FinishRecord(vec1, records[recordCount]);
							recordCount++;
						}
					}
				}
			}
		}

		vec1[0] = 1;
		vec1[1] = 0;
		vec1[2] = 0;

		for(int z = 0; z < recordCount; z++) {
			MatrixFilter(records[z], vecBest[0]);
			for(int y = 1; y <= 2; y++) {
				vec1[y] += vecBest[0][y];
			}
		}

		for(int y = 1; y <= 2; y++) {
			vec1[y] /= recordCount;
		}

		MergeFinishRecord(vec1, vecBest[0]);

		int exp = 1;
		for(int w = 0; w < 3;) {
			vec2[0] = 0;
			vec2[1] = -1;
			vec2[2] = 0;
			for(int i = 0; i < exp; i++) {
				for(int y = 0; y <= 2; y++) {
					vecBest[exp + i][y] = (0.01 * vec2[y]) +
							vecBest[i][y];
				}
			}
			w++;
			exp = 1 << w;
			FilterRecords(vecBest, exp, records,
					recordCount);
		}

		// Write output
		for(int z = 0; z < 8; z++) {
			double d;
			d = -vecBest[z][1] * 2048;
			if(d > 0) {
				coefsOut[z * 2] = (d > 32767) ? 32767 : (short) Math.round(d);
			} else {
				coefsOut[z * 2] = (d < -32768) ? -32768 : (short) Math.round(d);
			}

			d = -vecBest[z][2] * 2048;
			if(d > 0) {
				coefsOut[z * 2 + 1] = (d > 32767) ? 32767 : (short) Math.round(d);
			} else {
				coefsOut[z * 2 + 1] = (d < -32768) ? -32768 : (short) Math.round(d);
			}
		}
	}

	public static void DSPEncodeFrame(short[] pcmInOut, int sampleCount, byte[] adpcmOut, short[][] coefsIn) {
		int[][] inSamples = new int[8][16];
		int[][] outSamples = new int[8][14];

		int bestIndex = 0;

		int[] scale = new int[8];
		double[] distAccum = new double[8];

		// Iterate through each coef set, finding the set with the smallest error
		for(int i = 0; i < 8; i++) {
			int v1, v2, v3;
			int distance, index;

			// Set yn values
			inSamples[i][0] = pcmInOut[0];
			inSamples[i][1] = pcmInOut[1];

			// Round and clamp samples for this coef set
			distance = 0;
			for(int s = 0; s < sampleCount; s++) {
				// Multiply previous samples by coefs
				inSamples[i][s + 2] = v1 = ((pcmInOut[s] * coefsIn[i][1]) +
						(pcmInOut[s + 1] * coefsIn[i][0])) / 2048;
				// Subtract from current sample
				v2 = pcmInOut[s + 2] - v1;
				// Clamp
				v3 = (v2 >= 32767) ? 32767 : (v2 <= -32768) ? -32768 : v2;
				// Compare distance
				if(Math.abs(v3) > Math.abs(distance)) {
					distance = v3;
				}
			}

			// Set initial scale
			for(scale[i] = 0; (scale[i] <= 12) &&
					((distance > 7) || (distance < -8)); scale[i]++, distance /= 2) {
			}
			scale[i] = (scale[i] <= 1) ? -1 : scale[i] - 2;

			do {
				scale[i]++;
				distAccum[i] = 0;
				index = 0;

				for(int s = 0; s < sampleCount; s++) {
					// Multiply previous
					v1 = ((inSamples[i][s] * coefsIn[i][1]) +
							(inSamples[i][s + 1] * coefsIn[i][0]));
					// Evaluate from real sample
					v2 = ((pcmInOut[s + 2] << 11) - v1) / 2048;
					// Round to nearest sample
					v3 = (v2 > 0) ? (int) ((double) v2 / (1 << scale[i]) + 0.4999999f)
							: (int) ((double) v2 / (1 << scale[i]) - 0.4999999f);

					// Clamp sample and set index
					if(v3 < -8) {
						if(index < (v3 = -8 - v3)) {
							index = v3;
						}
						v3 = -8;
					} else if(v3 > 7) {
						if(index < (v3 -= 7)) {
							index = v3;
						}
						v3 = 7;
					}

					// Store result
					outSamples[i][s] = v3;

					// Round and expand
					v1 = (v1 + ((v3 * (1 << scale[i])) << 11) + 1024) >> 11;
					// Clamp and store
					inSamples[i][s + 2] = v2 = (v1 >= 32767) ? 32767 : (v1 <= -32768) ? -32768 : v1;
					// Accumulate distance
					v3 = pcmInOut[s + 2] - v2;
					distAccum[i] += v3 * (double) v3;
				}

				for(int x = index + 8; x > 256; x >>= 1) {
					if(++scale[i] >= 12) {
						scale[i] = 11;
					}
				}
			} while((scale[i] < 12) && (index > 1));
		}

		double min = Double.MAX_VALUE;
		for(int i = 0; i < 8; i++) {
			if(distAccum[i] < min) {
				min = distAccum[i];
				bestIndex = i;
			}
		}

		// Write converted samples
		for(int s = 0; s < sampleCount; s++) {
			pcmInOut[s + 2] = (short) inSamples[bestIndex][s + 2];
		}

		// Write ps
		adpcmOut[0] = (byte) ((bestIndex << 4) | (scale[bestIndex] & 0xF));

		// Zero remaining samples
		for(int s = sampleCount; s < 14; s++) {
			outSamples[bestIndex][s] = 0;
		}

		// Write output samples
		for(int y = 0; y < 7; y++) {
			adpcmOut[y + 1] = (byte) ((outSamples[bestIndex][y * 2] << 4) |
					(outSamples[bestIndex][y * 2 + 1] & 0xF));
		}
	}
}
