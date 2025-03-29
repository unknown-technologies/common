package com.unknown.audio.midi.smf;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class VariableInt {
	private VariableInt() {
		// no instances allowed
	}

	public static int length(int value) {
		if(value > 0x0FFFFFFF || value < 0) {
			throw new IllegalArgumentException("invalid value");
		}

		if(value <= 0x7F) {
			return 1;
		} else if(value <= 0x3FFF) {
			return 2;
		} else if(value <= 0x1FFFFF) {
			return 3;
		} else {
			return 4;
		}
	}

	public static int read(InputStream in) throws IOException {
		int value = 0;

		while(true) {
			int word = in.read();
			if(word == -1) {
				throw new EOFException("unexpected EOF");
			}

			value <<= 7;
			value |= word & 0x7F;
			if((word & 0x80) == 0) {
				return value;
			}
		}
	}

	public static void write(int value, OutputStream out) throws IOException {
		if(value > 0x0FFFFFFF || value < 0) {
			throw new IOException("invalid value");
		}

		int tmp = value;

		int buf = tmp & 0x7F;
		tmp >>= 7;

		while(tmp != 0) {
			buf <<= 8;
			buf |= (tmp & 0x7F) | 0x80;
			tmp >>= 7;
		}

		while(true) {
			out.write(buf & 0xFF);
			if((buf & 0x80) != 0) {
				buf >>= 8;
			} else {
				break;
			}
		}
	}
}
