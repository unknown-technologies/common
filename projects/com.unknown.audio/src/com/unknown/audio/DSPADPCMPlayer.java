package com.unknown.audio;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.unknown.audio.gcn.dsp.AsyncDecoder;
import com.unknown.audio.gcn.dsp.BFSTM;
import com.unknown.audio.gcn.dsp.BRSTM;
import com.unknown.audio.gcn.dsp.DSP;
import com.unknown.audio.gcn.dsp.RS02;
import com.unknown.audio.gcn.dsp.RS03;
import com.unknown.audio.gcn.dsp.Stream;
import com.unknown.audio.io.DataFile;
import com.unknown.audio.io.FileFormatException;
import com.unknown.audio.io.RandomAccessDataFile;
import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;

public class DSPADPCMPlayer implements Closeable {
	private static final Logger log = Trace.create(DSPADPCMPlayer.class);

	private Stream stream;
	private Worker player;
	private SourceDataLine out;

	public DSPADPCMPlayer(String filename) throws IOException {
		load(filename);
	}

	public DSPADPCMPlayer(DataFile file) throws IOException {
		load(file);
	}

	private class Worker extends Thread {
		private volatile boolean stop = false;

		@Override
		public void run() {
			out.start();
			log.log(Levels.INFO, Messages.PLAYER_LINE_START.format());

			while(stream.hasMoreData() && !stop) {
				try {
					byte[] buffer = stream.decode();
					buffer = sum(buffer, stream.getChannels());
					out.write(buffer, 0, buffer.length);
				} catch(Throwable t) {
					if(stop) {
						player = null;
						log.log(Levels.INFO, Messages.PLAYER_STOP.format());
						return;
					}
					if(!(t instanceof InterruptedException)) {
						log.log(Levels.ERROR, Messages.PLAYER_THREAD_FAIL.formatAlways(t),
								t);
					}
					break;
				}
			}
			if(stop) {
				player = null;
				log.log(Levels.INFO, Messages.PLAYER_STOP.format());
				return;
			}

			out.stop();
			log.log(Levels.INFO, Messages.PLAYER_LINE_STOP.format());
			out.close();
			log.log(Levels.INFO, Messages.PLAYER_LINE_CLOSE.format());
			out = null;
			log.log(Levels.INFO, Messages.PLAYER_STOP_EOF.format());
			player = null;
		}
	}

	private void load(DataFile file) throws IOException {
		try {
			stream = new BRSTM(file);
		} catch(FileFormatException e) {
			try {
				stream = new BFSTM(file);
			} catch(FileFormatException ex) {
				try {
					stream = new RS03(file);
				} catch(FileFormatException exc) {
					try {
						stream = new RS02(file);
					} catch(FileFormatException exce) {
						stream = new DSP(file);
					}
				}
			}
		}
	}

	private void load(String filename) throws IOException {
		String filenameLeft = null;
		String filenameRight = null;
		int lext = filename.lastIndexOf('.');
		if(lext > 1) {
			char[] data = filename.toCharArray();
			char c = data[lext - 1];
			if(c == 'L') {
				data[lext - 1] = 'R';
				filenameLeft = filename;
				filenameRight = new String(data);
			} else if(c == 'R') {
				data[lext - 1] = 'L';
				filenameLeft = new String(data);
				filenameRight = filename;
			}
		}
		RandomAccessDataFile file = new RandomAccessDataFile(filename, "r");
		try {
			stream = new BRSTM(file);
		} catch(FileFormatException e) {
			try {
				stream = new BFSTM(file);
			} catch(FileFormatException ex) {
				try {
					stream = new RS03(file);
				} catch(FileFormatException exc) {
					if(filenameLeft != null && new File(filenameLeft).exists() &&
							new File(filenameRight).exists()) {
						file.close();
						RandomAccessDataFile left = new RandomAccessDataFile(filenameLeft, "r");
						RandomAccessDataFile right = new RandomAccessDataFile(filenameRight,
								"r");
						try {
							stream = new DSP(left, right);
						} catch(FileFormatException exce) {
							left.close();
							right.close();
							file = new RandomAccessDataFile(filename, "r");
							stream = new DSP(file);
						}
					} else {
						try {
							stream = new RS02(file);
						} catch(FileFormatException exce) {
							stream = new DSP(file);
						}
					}
				}
			}
		}
	}

	public Stream getStream() {
		return stream;
	}

	public void playSynchronous() throws Exception {
		AsyncDecoder decoder = new AsyncDecoder(stream);
		decoder.start();
		play(decoder);
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}

	public static byte[] sum(byte[] data, int channels) {
		if(channels == 1 || channels == 2) {
			return data;
		}
		int samples = data.length / (channels * 2);
		byte[] result = new byte[samples * 4]; // 2 channels, 16bit
		for(int i = 0; i < samples; i++) {
			int l = 0;
			int r = 0;
			for(int ch = 0; ch < channels; ch++) {
				int idx = (i * channels + ch) * 2;
				short val = (short) (Byte.toUnsignedInt(data[idx]) << 8 |
						Byte.toUnsignedInt(data[idx + 1]));
				if((ch & 1) == 0) {
					l += val;
				} else {
					r += val;
				}
			}
			// clamp
			if(l < -32768) {
				l = -32768;
			} else if(l > 32767) {
				l = 32767;
			}
			if(r < -32768) {
				r = -32768;
			} else if(r > 32767) {
				r = 32767;
			}
			// write back
			result[i * 4] = (byte) (l >> 8);
			result[i * 4 + 1] = (byte) l;
			result[i * 4 + 2] = (byte) (r >> 8);
			result[i * 4 + 3] = (byte) r;
		}
		return result;
	}

	private static void play(Stream stream) throws IOException, LineUnavailableException {
		stream.reset();
		int channels = stream.getChannels();
		if(channels > 2) {
			channels = 2;
		}
		// @formatter:off
		AudioFormat format = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,	// encoding
				stream.getSampleRate(),			// sample rate
				16,					// bit/sample
				channels,				// channels
				2 * channels,
				stream.getSampleRate(),
				true					// big-endian
		);
		// @formatter:on

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		if(!AudioSystem.isLineSupported(info)) {
			throw new LineUnavailableException("Line matching " + info + " not supported");
		}

		SourceDataLine waveout;
		waveout = (SourceDataLine) AudioSystem.getLine(info);
		waveout.open(format, 16384);

		waveout.start();
		while(stream.hasMoreData()) {
			byte[] buffer = stream.decode();
			buffer = sum(buffer, stream.getChannels());
			waveout.write(buffer, 0, buffer.length);
		}
		waveout.stop();
	}

	public void start() throws IOException, LineUnavailableException {
		if(out != null) {
			log.log(Levels.WARNING, Messages.PLAYER_LINE_STILL_OPEN.format());
			out.stop();
			out.close();
		}
		if(player != null) {
			log.log(Levels.WARNING, Messages.PLAYER_THREAD_STILL_OPEN.format());
			player.interrupt();
		}
		stream.reset();
		int channels = stream.getChannels();
		if(channels > 2) {
			channels = 2;
		}

		// @formatter:off
		AudioFormat format = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,	// encoding
				stream.getSampleRate(),			// sample rate
				16,					// bit/sample
				channels,				// channels
				2 * channels,
				stream.getSampleRate(),
				true					// big-endian
		);
		// @formatter:on

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		if(!AudioSystem.isLineSupported(info)) {
			throw new LineUnavailableException("Line matching " + info + " not supported");
		}

		out = (SourceDataLine) AudioSystem.getLine(info);
		out.open(format, 16384);
		log.log(Levels.INFO, Messages.PLAYER_LINE_OPEN.format());

		player = new Worker();
		player.start();
		log.log(Levels.INFO, Messages.PLAYER_START.format());
	}

	public void stop() {
		if(player == null) {
			log.log(Levels.INFO, Messages.PLAYER_ALREADY_STOPPED.format());
			return;
		}
		player.stop = true;
		out.stop();
		log.log(Levels.INFO, Messages.PLAYER_LINE_STOP.format());
		out.close();
		out = null;
		log.log(Levels.INFO, Messages.PLAYER_LINE_CLOSE.format());
	}

	public static void main(String[] args) throws Exception {
		Trace.setupConsoleApplication();
		if(args.length < 1) {
			System.err.println("Usage: player FILE");
			System.exit(1);
		}
		try(DSPADPCMPlayer player = new DSPADPCMPlayer(args[0])) {
			System.out.printf("%d Channels, %d Hz\n", player.getStream().getChannels(),
					player.getStream().getSampleRate());
			player.playSynchronous();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
