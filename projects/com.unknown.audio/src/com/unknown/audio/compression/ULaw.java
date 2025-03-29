package com.unknown.audio.compression;

public class ULaw {
	private static final int SIGN_BIT = 0x80;
	private static final int QUANT_MASK = 0x0F;
	private static final int SEG_SHIFT = 4;
	private static final int SEG_MASK = 0x70;
	private static final int BIAS = 0x84;
	private static final int CLIP = 8159;

	private static final short[] seg_uend = { 0x3F, 0x7F, 0xFF, 0x1FF, 0x3FF, 0x7FF, 0xFFF, 0x1FFF };

	public static short decodeG711(byte uval) {
		byte nuval = (byte) ~uval;

		short t = (short) (((nuval & QUANT_MASK) << 3) + BIAS);
		t <<= (Byte.toUnsignedInt(nuval) & SEG_MASK) >> SEG_SHIFT;
		return (short) ((nuval & SIGN_BIT) != 0 ? (BIAS - t) : (t - BIAS));
	}

	public static short decode(byte uval) {
		short t = (short) (((uval & QUANT_MASK) << 3) + BIAS);
		t <<= (uval & SEG_MASK) >> SEG_SHIFT;
		return (short) ((uval & SIGN_BIT) != 0 ? (BIAS - t) : (t - BIAS));
	}

	private static short search(short val, short[] table, short size) {
		for(short i = 0; i < size; i++) {
			if(val <= table[i])
				return i;
		}
		return size;
	}

	public static byte encodeG711(short pcm_val) {
		short mask;

		// Get the sign and the magnitude of the value.
		short pcm = (short) (pcm_val >> 2);
		if(pcm < 0) {
			pcm = (short) -pcm;
			mask = 0x7F;
		} else {
			mask = 0xFF;
		}
		if(pcm > CLIP) {
			pcm = CLIP; // clip the magnitude
		}
		pcm += (BIAS >> 2);

		// Convert the scaled magnitude to segment number.
		short seg = search(pcm, seg_uend, (short) 8);

		// Combine the sign, segment, quantization bits; and complement the code word.
		if(seg >= 8) { // out of range, return maximum value.
			return (byte) (0x7F ^ mask);
		} else {
			byte uval = (byte) ((seg << 4) | ((pcm >> (seg + 1)) & 0xF));
			return (byte) (uval ^ mask);
		}
	}

	public static byte encode(short pcm) {
		return (byte) ~encodeG711(pcm);
	}

	// Decoding function of the Am6072 DAC chip according to Bell μ-225 law
	public static short decode6072(byte val) {
		boolean sign = val < 0;
		int abs = val & 0x7F;
		int c = abs >>> 4;
		int s = val & 0x0F;
		int sgn = sign ? -1 : 1;
		// return (short) (sgn * Math.round(2 * ((1 << c) * (s + 16.5) - 16.5)));
		return (short) (sgn * ((1 << c) * (2 * s + 33) - 33));
	}

	public static short decode6072_16bit(byte val) {
		return (short) (decode6072(val) << 2);
	}
}
