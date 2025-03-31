package com.unknown.net.telnet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TelnetOutputStream extends OutputStream {
	private final Telnet telnet;
	private final boolean useCRLF;

	public TelnetOutputStream(Telnet telnet) {
		this(telnet, false);
	}

	public TelnetOutputStream(Telnet telnet, boolean useCRLF) {
		this.telnet = telnet;
		this.useCRLF = useCRLF;
	}

	@Override
	public void write(int data) throws IOException {
		if(useCRLF && data == 0x0A) {
			telnet.write(new byte[] { 0x0D, 0x0A });
		} else {
			telnet.write((byte) data);
		}
	}

	@Override
	public void write(byte[] data, int offset, int length) throws IOException {
		try(ByteArrayOutputStream buf = new ByteArrayOutputStream(data.length)) {
			for(int i = offset; i < offset + length; i++) {
				byte b = data[i];
				if(useCRLF && b == 0x0A) {
					buf.write(new byte[] { 0x0D, 0x0A });
				} else {
					buf.write(b);
				}
			}
			buf.flush();
			telnet.write(buf.toByteArray());
		}
	}
}
