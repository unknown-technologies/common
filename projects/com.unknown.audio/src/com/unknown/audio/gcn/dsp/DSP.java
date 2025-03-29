package com.unknown.audio.gcn.dsp;

import com.unknown.audio.io.DataFile;
import com.unknown.audio.io.FileFormatException;
import com.unknown.util.io.Endianess;

import java.io.IOException;

public class DSP implements Stream {
	public final static int HEADER_SIZE = 0x60;

	long sample_count;
	long nibble_count;
	long sample_rate;
	int loop_flag;
	int format;
	long loop_start_offset;
	long loop_end_offset;
	long ca;
	int coef[]; /* really 8x2 */
	int gain;
	int initial_ps;
	int initial_hist1;
	int initial_hist2;
	int loop_ps;
	int loop_hist1;
	int loop_hist2;

	DataFile file;
	DSP ch2;
	long filepos;
	long filesize;
	long startoffset;

	long current_sample;

	ADPCMDecoder decoder;

	public DSP(DataFile file) throws FileFormatException, IOException {
		ch2 = null;
		this.file = file;
		filesize = file.length();
		readHeader();
	}

	public DSP(DataFile ch1, DataFile ch2) throws FileFormatException, IOException {
		this(ch1);
		this.ch2 = new DSP(ch2);
		validateChannel(this.ch2);
	}

	public boolean read_dsp_header(byte[] header) {
		int i;

		coef = new int[16];

		sample_count = Endianess.get32bitBE(header, 0x00);
		nibble_count = Endianess.get32bitBE(header, 0x04);
		sample_rate = Endianess.get32bitBE(header, 0x08);
		loop_flag = Endianess.get16bitBEu(header, 0x0c);
		format = Endianess.get16bitBEu(header, 0x0e);
		loop_start_offset = Endianess.get32bitBE(header, 0x10) / 16 * 8;
		loop_end_offset = Endianess.get32bitBE(header, 0x14) / 16 * 8;
		ca = Endianess.get32bitBE(header, 0x18);
		for(i = 0; i < 16; i++) {
			coef[i] = Endianess.get16bitBE(header, 0x1c + i * 2);
		}
		gain = Endianess.get16bitBEu(header, 0x3c);
		initial_ps = Endianess.get16bitBEu(header, 0x3e);
		initial_hist1 = Endianess.get16bitBE(header, 0x40);
		initial_hist2 = Endianess.get16bitBE(header, 0x42);
		loop_ps = Endianess.get16bitBEu(header, 0x44);
		loop_hist1 = Endianess.get16bitBE(header, 0x46);
		loop_hist2 = Endianess.get16bitBE(header, 0x48);

		if(sample_count > nibble_count) {
			return false;
		}
		if((sample_rate == 0) || (sample_rate < 0)) {
			return false;
		}
		if(loop_start_offset > loop_end_offset) {
			return false;
		}
		return true;
	}

	private void validateChannel(DSP ch) throws FileFormatException {
		boolean invalid = false;
		if(ch.sample_count != sample_count) {
			invalid = true;
		} else if(ch.nibble_count != nibble_count) {
			invalid = true;
		} else if(ch.sample_rate != sample_rate) {
			invalid = true;
		} else if(ch.loop_flag != loop_flag) {
			invalid = true;
		} else if(ch.loop_start_offset != loop_start_offset) {
			invalid = true;
		} else if(ch.loop_end_offset != loop_end_offset) {
			invalid = true;
		}
		if(invalid) {
			throw new FileFormatException("channels do not match");
		}
	}

	@Override
	public long getSampleRate() {
		return sample_rate;
	}

	@Override
	public int getChannels() {
		return ch2 == null ? 1 : 2;
	}

	private void readHeader() throws FileFormatException, IOException {
		seek(0);
		startoffset = 0x60;
		byte[] header = new byte[0x60];
		file.read(header);
		if(!read_dsp_header(header)) {
			throw new FileFormatException("not a devkit DSP file");
		}
		decoder = new ADPCMDecoder();
		decoder.setCoef(coef);
		decoder.setHistory(initial_hist1, initial_hist2);
		filepos = startoffset;
		current_sample = 0;
	}

	@Override
	public void close() throws IOException {
		file.close();
		if(ch2 != null) {
			ch2.close();
			ch2 = null;
		}
	}

	@Override
	public void reset() throws IOException {
		filepos = startoffset;
		seek(filepos);
		decoder.setHistory(initial_hist1, initial_hist2);
	}

	private void seek(long offset) throws IOException {
		file.seek(offset);
	}

	public boolean hasMoreData() {
		return filepos < filesize;
	}

	@Override
	public byte[] decode() throws IOException {
		if(getChannels() == 1) {
			return doDecode();
		}
		byte[] chan1 = doDecode();
		byte[] chan2 = this.ch2.doDecode();
		byte[] r = new byte[chan1.length + chan2.length];
		for(int i = 0; i < chan1.length; i += 2) {
			r[2 * i] = chan1[i];
			r[2 * i + 1] = chan1[i + 1];
			r[2 * i + 2] = chan2[i];
			r[2 * i + 3] = chan2[i + 1];
		}
		return r;
	}

	private byte[] doDecode() throws IOException {
		byte[] rawdata = new byte[8];
		filepos += file.read(rawdata);
		int[] samples = decoder.decode_ngc_dsp(0, 0, (int) (rawdata.length / 8.0 * 14.0), rawdata);
		byte[] buffer = new byte[samples.length * 2];
		for(int i = 0; i < samples.length; i++) {
			Endianess.set16bitBE(buffer, i * 2, (short) samples[i]);
		}
		current_sample += samples.length;
		if((loop_flag != 0) && ((filepos - startoffset) >= loop_end_offset)) {
			filepos = startoffset + (loop_start_offset / 8) * 8;
			seek(filepos);
		}
		return buffer;
	}

	@Override
	public String toString() {
		return "DSP[" + sample_rate + "Hz,16bit," + sample_count + " samples,loop:" +
				((loop_flag != 0) ? "yes" : "no") + "]";
	}
}
