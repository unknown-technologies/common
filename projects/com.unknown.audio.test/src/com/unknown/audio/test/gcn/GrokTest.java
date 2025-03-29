package com.unknown.audio.test.gcn;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.unknown.audio.gcn.dsp.encoder.Grok;

public class GrokTest {
	@Test
	public void testInnerProductMerge() {
		short[] pcmBuf = { 0x12a0, 0x100d, 0x0bae, 0x06ff, 0x034d, 0x001e, 0xfffffcab, 0xfffff80d, 0xfffff33f,
				0xffffeef1, 0xffffea4a, 0xffffe86d, 0xffffeaf1, 0xffffed95 };
		short[] pcmBufPrev = { 0x1263, 0x121a, 0x12f4, 0x15e0, 0x189c, 0x1a7c, 0x19b0, 0x170b, 0x1782, 0x1921,
				0x178e, 0x1698, 0x16fc, 0x1523 };

		double[] vec = new double[3];
		Grok.InnerProductMerge(vec, pcmBuf, pcmBufPrev);

		assertEquals(-205686854.000000, vec[0], 0);
		assertEquals(-202951466.000000, vec[1], 0);
		assertEquals(-188294503.000000, vec[2], 0);
	}

	@Test
	public void testOuterProductMerge() {
		short[] pcmBuf = { 0x2bbf, 0x2c6b, 0x2d6f, 0x2c06, 0x24d0, 0x1e0e, 0x1811, 0x0bc4, 0x00ae, 0xfffffa75,
				0xffffef56, 0xffffead1, 0xfffff83e, 0x0000 };
		short[] pcmBufPrev = { 0x2a59, 0x2508, 0x21e7, 0x1cda, 0x18a6, 0x1814, 0x1283, 0x0d6d, 0x1571, 0x2037,
				0x228c, 0x23ba, 0x278a, 0x2a50 };
		double[][] mtx = new double[3][3];

		Grok.OuterProductMerge(mtx, pcmBuf, pcmBufPrev);

		assertEquals(882977722.000000, mtx[1][1], 0);
		assertEquals(906542006.000000, mtx[1][2], 0);
		assertEquals(906542006.000000, mtx[2][1], 0);
		assertEquals(981488410.000000, mtx[2][2], 0);
	}

	@Test
	public void testAnalyizeRanges() {
		double[][] mtx = { { 0.000000, 0.000000, 0.000000 }, { 0.000000, 1342951155.000000, 1330471857.000000 },
				{ 0.000000, 1330471857.000000, 1324594215.000000 } };

		int[] out = new int[3];
		boolean val = Grok.AnalyzeRanges(mtx, out);
		assertEquals(0, out[0]);
		assertEquals(2, out[1]);
		assertEquals(2, out[2]);
		assertFalse(val);
	}

	@Test
	public void testFinishRecord1() {
		double[] vec1 = { 1.000000000000000000000, -0.978125693878482582733, 0.999999999899999991726 };
		double[] record = { 1.000000000000000000000, -1.956251387659152740639, 0.999999999899999991726 };

		double[] out = new double[3];
		Grok.FinishRecord(vec1, out);

		assertEquals(record[0], out[0], 0);
		assertEquals(record[1], out[1], 0);
		assertEquals(record[2], out[2], 0);
	}

	@Test
	public void testFinishRecord2() {
		double[] vec1 = { 1.000000000000000000000, -0.992828794687776516881, 0.965754385398151726783 };
		double[] record = { 1.000000000000000000000, -1.951657557107057794354, 0.965754385398151726783 };

		double[] out = new double[3];
		Grok.FinishRecord(vec1, out);

		assertEquals(record[0], out[0], 0);
		assertEquals(record[1], out[1], 0);
		assertEquals(record[2], out[2], 0);
	}

	@Test
	public void testMatrixFilter1() {
		double[] src = { 1.000000000000000000000, -1.924982534922307308989, 0.999999999899999991726 };
		double[] dst = { 1.000000000000000000000, 0.962491118216537877572, 0.852778592684677150260 };

		double[] out = new double[3];
		Grok.MatrixFilter(src, out);

		assertEquals(dst[0], out[0], 0);
		assertEquals(dst[0], out[0], 1);
		assertEquals(dst[0], out[0], 2);
	}

	@Test
	public void testMatrixFilter2() {
		double[] src = { 1.000000000000000000000, -1.300770937470605126407, 0.322680558920551252555 };
		double[] dst = { 1.000000000000000000000, 0.983435440022021101214, 0.956543680338710178290 };

		double[] out = new double[3];
		Grok.MatrixFilter(src, out);

		assertEquals(dst[0], out[0], 0);
		assertEquals(dst[0], out[0], 1);
		assertEquals(dst[0], out[0], 2);
	}

	@Test
	public void testBidirectionalFilter1() {
		double[] vecIn = { -140343970.000000000000000000000, -145925475.000000000000000000000,
				-131599329.000000000000000000000 };
		double[][] mtx = { { 0.000000000000000000000, 0.000000000000000000000, 0.000000000000000000000 },
				{ 0.000000000000000000000, 189379618.000000000000000000000,
						199952603.000000000000000000000 },
				{ 0.000000000000000000000, 1.055829582463303939477, 37558000.662059217691421508789 } };
		int[] vecIdxs = { 0, 1, 2 };
		double[] vecOut = { 1.000000000000000000000, -1.402308125701840157973, 0.598357312526261453378 };

		double[] out = new double[3];
		for(int i = 0; i < 3; i++) {
			out[i] = vecIn[i];
		}
		Grok.BidirectionalFilter(mtx, vecIdxs, out);

		assertEquals(vecOut[0], out[0], 0);
		assertEquals(vecOut[1], out[1], 0);
		assertEquals(vecOut[2], out[2], 0);
	}

	@Test
	public void testBidirectionalFilter2() {
		double[] vecIn = { -3630283278.000000000000000000000, -3581116023.000000000000000000000,
				-3459241109.000000000000000000000 };
		double[][] mtx = { { 0.000000000000000000000, 0.000000000000000000000, 0.000000000000000000000 },
				{ 0.000000000000000000000, 3461604677.000000000000000000000,
						3400784098.000000000000000000000 },
				{ 0.000000000000000000000, 1.027335691631317882155, -32142206.407617568969726562500 } };
		int[] vecIdxs = { 0, 2, 2 };
		double[] vecOut = { 1.000000000000000000000, -1.834177577048686336170, 0.849791250158997923947 };

		double[] out = new double[3];
		for(int i = 0; i < 3; i++) {
			out[i] = vecIn[i];
		}
		Grok.BidirectionalFilter(mtx, vecIdxs, out);

		assertEquals(vecOut[0], out[0], 0);
		assertEquals(vecOut[1], out[1], 0);
		assertEquals(vecOut[2], out[2], 0);
	}

	@Test
	public void testBidirectionalFilter3() {
		double[] vecIn = { -904806581.000000000000000000000, -833391793.000000000000000000000,
				-756946501.000000000000000000000 };
		double[][] mtx = { { 0.000000000000000000000, 0.000000000000000000000, 0.000000000000000000000 },
				{ 0.000000000000000000000, 704231033.000000000000000000000,
						646516808.000000000000000000000 },
				{ 0.000000000000000000000, 1.097682579688305182231, -5439204.617288708686828613281 } };
		int[] vecIdxs = { 0, 2, 2 };
		double[] vecOut = { 1.000000000000000000000, -1.497624426680327802686, 0.460509444399657352776 };

		double[] out = new double[3];
		for(int i = 0; i < 3; i++) {
			out[i] = vecIn[i];
		}
		Grok.BidirectionalFilter(mtx, vecIdxs, out);

		assertEquals(vecOut[0], out[0], 0);
		assertEquals(vecOut[1], out[1], 0);
		assertEquals(vecOut[2], out[2], 0);
	}

	@Test
	public void testQuadraticMerge1() {
		double[] vecIn = { 1.000000000000000000000, -1.409821101030063195125, 0.258953054399207571734 };
		double[] vecOut = { 1.000000000000000000000, -1.119836117878797665526, 0.258953054399207571734 }; // TRUE

		double[] vec = new double[3];
		for(int i = 0; i < 3; i++) {
			vec[i] = vecIn[i];
		}

		boolean result = Grok.QuadraticMerge(vec);

		assertTrue(result);
		assertEquals(vecOut[0], vec[0], 0);
		assertEquals(vecOut[1], vec[1], 0);
		assertEquals(vecOut[2], vec[2], 0);
	}

	@Test
	public void testQuadraticMerge2() {
		double[] vecIn = { 1.000000000000000000000, -1.914169416913987120665, 0.940021283778287308763 };
		double[] vecOut = { 1.000000000000000000000, -0.986674441625735698658, 0.940021283778287308763 }; // FALSE

		double[] vec = new double[3];
		for(int i = 0; i < 3; i++) {
			vec[i] = vecIn[i];
		}

		boolean result = Grok.QuadraticMerge(vec);

		assertFalse(result);
		assertEquals(vecOut[0], vec[0], 0);
		assertEquals(vecOut[1], vec[1], 0);
		assertEquals(vecOut[2], vec[2], 0);
	}

	@Test
	public void testCorrelatePart1() {
		short[] pcmHistBuffer0 = { 0x2cda, 0x2a38, 0x26e8, 0x22e8, 0x1caa, 0x15ee, 0x1018, 0x098a, 0x0203,
				0xfffffb72, 0xfffff5da, 0xffffef87, 0xffffe8ab, 0xffffe351 };
		short[] pcmHistBuffer1 = { 0xffffdfb2, 0xffffdd9c, 0xffffdeb1, 0xffffe1bd, 0xffffe39a, 0xffffe70e,
				0xffffec2a, 0xffffeb85, 0xffffe981, 0xffffee03, 0xffffef12, 0xffffeaee, 0xfffff482,
				0x077d };
		double[] vec1 = { 1.000000000000000000000, -0.963038615041210688261, 0.786929359140241535542 };
		double[] record = { 1.000000000000000000000, -1.720881975202896541077, 0.786929359140241535542 };

		double[] vec = new double[3];
		double[][] mtx = new double[3][3];
		int[] vecIdxs = new int[3];
		double[] rec = new double[3];

		Grok.InnerProductMerge(vec, pcmHistBuffer1, pcmHistBuffer0);
		assertTrue(Math.abs(vec[0]) > 10.0);
		Grok.OuterProductMerge(mtx, pcmHistBuffer1, pcmHistBuffer0);
		assertFalse(Grok.AnalyzeRanges(mtx, vecIdxs));
		Grok.BidirectionalFilter(mtx, vecIdxs, vec);
		assertFalse(Grok.QuadraticMerge(vec));

		assertEquals(vec1[0], vec[0], 0);
		assertEquals(vec1[1], vec[1], 0);
		assertEquals(vec1[2], vec[2], 0);

		Grok.FinishRecord(vec, rec);

		assertEquals(record[0], rec[0], 0);
		assertEquals(record[1], rec[1], 0);
		assertEquals(record[2], rec[2], 0);
	}

	@Test
	public void testCorrelatePart2() {
		short[] pcmHistBuffer0 = { 0xffffffff, 0x0002, 0xffffffff, 0xffffffff, 0x0001, 0x0000, 0xfffffffe,
				0x0006, 0xfffffff5, 0xfffffffd, 0x0004, 0xfffffff7, 0xfffffff7, 0x0001 };
		short[] pcmHistBuffer1 = { 0x0002, 0xfffffffb, 0x0007, 0x0007, 0x0001, 0x0009, 0x0001, 0x0006,
				0xfffffffa, 0x0006, 0x0004, 0xfffffffd, 0x0001, 0x0002 };
		double[] vec1 = { 1.000000000000000000000, 0.063796954314720821788, -0.153198074277854201508 };
		double[] record = { 1.000000000000000000000, 0.054023383768913350866, -0.153198074277854201508 };

		double[] vec = new double[3];
		double[][] mtx = new double[3][3];
		int[] vecIdxs = new int[3];
		double[] rec = new double[3];

		Grok.InnerProductMerge(vec, pcmHistBuffer1, pcmHistBuffer0);
		assertTrue(Math.abs(vec[0]) > 10.0);
		Grok.OuterProductMerge(mtx, pcmHistBuffer1, pcmHistBuffer0);
		assertFalse(Grok.AnalyzeRanges(mtx, vecIdxs));
		Grok.BidirectionalFilter(mtx, vecIdxs, vec);
		assertFalse(Grok.QuadraticMerge(vec));

		assertEquals(vec1[0], vec[0], 0);
		assertEquals(vec1[1], vec[1], 0);
		assertEquals(vec1[2], vec[2], 0);

		Grok.FinishRecord(vec, rec);

		assertEquals(record[0], rec[0], 0);
		assertEquals(record[1], rec[1], 0);
		assertEquals(record[2], rec[2], 0);
	}

	@Test
	public void testCorrelatePart3() {
		short[] pcmHistBuffer0 = { 0x0002, 0xfffffffb, 0x0007, 0x0007, 0x0001, 0x0009, 0x0001, 0x0006,
				0xfffffffa, 0x0006, 0x0004, 0xfffffffd, 0x0001, 0x0002 };
		short[] pcmHistBuffer1 = { 0xfffffffd, 0xffffffff, 0x0005, 0xfffffffb, 0x0001, 0x0005, 0xfffffff9,
				0x0002, 0x0006, 0xfffffff5, 0x0008, 0x0001, 0xfffffff9, 0x0006 };
		double[] vec1 = { 1.000000000000000000000, 0.586696967641279432115, 1.009816620083168414723 };
		double[] record = { 1.000000000000000000000, 1.173393935223889128494, 0.999999999899999991726 };

		double[] vec = new double[3];
		double[][] mtx = new double[3][3];
		int[] vecIdxs = new int[3];
		double[] rec = new double[3];

		Grok.InnerProductMerge(vec, pcmHistBuffer1, pcmHistBuffer0);
		assertTrue(Math.abs(vec[0]) > 10.0);
		Grok.OuterProductMerge(mtx, pcmHistBuffer1, pcmHistBuffer0);
		assertFalse(Grok.AnalyzeRanges(mtx, vecIdxs));
		Grok.BidirectionalFilter(mtx, vecIdxs, vec);
		assertFalse(Grok.QuadraticMerge(vec));

		assertEquals(vec1[0], vec[0], 0);
		assertEquals(vec1[1], vec[1], 0);
		assertEquals(vec1[2], vec[2], 0);

		Grok.FinishRecord(vec, rec);

		assertEquals(record[0], rec[0], 0);
		assertEquals(record[1], rec[1], 0);
		assertEquals(record[2], rec[2], 0);
	}

	@Test
	public void testDSPEncodeFrames1() {
		short[][] coefs = { { 0xfffff7b2, 0xfffffce3 }, { 0x0c51, 0xfffffaaa }, { 0x06a1, 0xfffffcee },
				{ 0x0efd, 0xfffff897 }, { 0xffffff9c, 0x0157 }, { 0x0c99, 0xfffffb0c },
				{ 0x0904, 0xfffffe01 }, { 0x0f09, 0xfffff8d7 } };
		short[] convSamps = { 0x0000, 0x0000, 0xffffffff, 0x0003, 0xfffffffc, 0x0004, 0xfffffffd, 0x0001,
				0x0001, 0xfffffffe, 0x0001, 0x0000, 0x0000, 0x0000, 0xffffffff, 0x0002 };
		byte[] block = { 0x60, (byte) 0xf4, (byte) 0x97, (byte) 0x95, 0x0c, 0x30, 0x00, (byte) 0xf3 };
		short[] convSampsOut = { 0x0000, 0x0000, 0xffffffff, 0x0003, 0xfffffffd, 0x0003, 0xfffffffd, 0x0001,
				0x0002, 0xfffffffe, 0x0000, 0x0000, 0x0000, 0x0000, 0xffffffff, 0x0002, };

		byte[] adpcmOut = new byte[8];
		Grok.DSPEncodeFrame(convSamps, 14, adpcmOut, coefs);

		assertArrayEquals(convSampsOut, convSamps);
		assertArrayEquals(block, adpcmOut);
	}

	@Test
	public void testDSPEncodeFrames2() {
		short[][] coefs = { { 0xfffff7b2, 0xfffffce3 }, { 0x0c51, 0xfffffaaa }, { 0x06a1, 0xfffffcee },
				{ 0x0efd, 0xfffff897 }, { 0xffffff9c, 0x0157 }, { 0x0c99, 0xfffffb0c },
				{ 0x0904, 0xfffffe01 }, { 0x0f09, 0xfffff8d7 } };
		short[] convSamps = { 0xfffff493, 0xfffff38a, 0xfffff3a1, 0xfffff4f5, 0xfffff55c, 0xfffff619,
				0xfffff6f2, 0xfffff52a, 0xfffff24d, 0xffffef5c, 0xffffebc9, 0xffffec66, 0xfffff1ad,
				0xfffff3cd, 0xfffff347, 0xfffff53b };
		byte[] block = { 0x17, (byte) 0xe0, (byte) 0xce, (byte) 0xea, (byte) 0x9b, (byte) 0x81, 0x59,
				(byte) 0xa0 };

		byte[] adpcmOut = new byte[8];
		Grok.DSPEncodeFrame(convSamps, 14, adpcmOut, coefs);

		assertArrayEquals(block, adpcmOut);
	}
}
