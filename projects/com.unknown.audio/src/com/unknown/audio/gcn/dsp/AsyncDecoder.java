package com.unknown.audio.gcn.dsp;

import java.io.IOException;
import java.util.logging.Logger;

import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;

public class AsyncDecoder extends Thread implements Stream {
	private static final Logger log = Trace.create(AsyncDecoder.class);

	private Stream stream;
	private byte[] data = null;
	private boolean moreData = false;
	private volatile boolean closed = false;

	public AsyncDecoder(Stream stream) {
		this.stream = stream;
		this.moreData = stream.hasMoreData();
	}

	@Override
	public synchronized boolean hasMoreData() {
		return moreData || data != null;
	}

	@Override
	public synchronized byte[] decode() throws IOException {
		if(!hasMoreData()) {
			return null;
		}
		while(data == null) {
			try {
				wait();
			} catch(InterruptedException e) {
				throw new IOException(e);
			}
		}
		byte[] tmp = data;
		data = null;
		notify();
		return tmp;
	}

	@Override
	public int getChannels() {
		return stream.getChannels();
	}

	@Override
	public long getSampleRate() {
		return stream.getSampleRate();
	}

	@Override
	public void close() throws IOException {
		closed = true;
		interrupt();
		stream.close();
	}

	@Override
	public void run() {
		while(!closed && stream.hasMoreData()) {
			try {
				synchronized(this) {
					if(data != null) {
						wait();
					}
					data = stream.decode();
					moreData = stream.hasMoreData();
					notifyAll();
				}
			} catch(InterruptedException e) {
			} catch(Exception e) {
				log.log(Levels.WARNING, "Error while decoding data: " + e.getMessage(), e);
				return;
			}
		}
	}

	public void reset() throws IOException {
		stream.reset();
	}
}
