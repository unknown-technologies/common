package com.unknown.net.telnet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.logging.Logger;

import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;

public class TelnetInputStream extends InputStream {
	private static Logger log = Trace.create(TelnetInputStream.class);

	private PipedInputStream in;
	private PipedOutputStream out;

	public TelnetInputStream(Telnet telnet) throws IOException {
		out = new PipedOutputStream();
		in = new PipedInputStream(out);
		telnet.addReceiveListener((b) -> {
			try {
				out.write(b);
			} catch(IOException e) {
				log.log(Levels.ERROR, "Failed to write data: " + e.getMessage());
			}
		});
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return in.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}

	@Override
	public void close() throws IOException {
		IOException exc = null;
		try {
			out.close();
		} catch(IOException e) {
			exc = e;
		}
		try {
			in.close();
		} catch(IOException e) {
			exc = e;
		}
		if(exc != null) {
			throw exc;
		}
	}
}
