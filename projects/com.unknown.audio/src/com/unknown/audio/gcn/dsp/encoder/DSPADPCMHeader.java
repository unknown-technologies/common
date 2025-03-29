package com.unknown.audio.gcn.dsp.encoder;

import java.io.IOException;

import com.unknown.util.io.WordOutputStream;

public class DSPADPCMHeader {
	public int num_samples;
	public int num_nibbles;
	public int sample_rate;
	public short loop_flag;
	public short format;
	public int loop_start;
	public int loop_end;
	public int ca;
	short[] coef = new short[16];
	short gain;
	short ps;
	short hist1;
	short hist2;
	short loop_ps;
	short loop_hist1;
	short loop_hist2;
	// uint16_t pad[11];

	public void write(WordOutputStream out) throws IOException {
		if(coef == null) {
			throw new NullPointerException("no coefs");
		}
		if(coef.length != 16) {
			throw new IllegalStateException("16 coefs required, " + coef.length + " coefs given");
		}
		out.write32bit(num_samples);
		out.write32bit(num_nibbles);
		out.write32bit(sample_rate);
		out.write16bit(loop_flag);
		out.write16bit(format);
		out.write32bit(loop_start);
		out.write32bit(loop_end);
		out.write32bit(ca);
		for(short c : coef) {
			out.write16bit(c);
		}
		out.write16bit(gain);
		out.write16bit(ps);
		out.write16bit(hist1);
		out.write16bit(hist2);
		out.write16bit(loop_ps);
		out.write16bit(loop_hist1);
		out.write16bit(loop_hist2);
		for(int i = 0; i < 11; i++) {
			out.write16bit((short) 0);
		}
	}
}
