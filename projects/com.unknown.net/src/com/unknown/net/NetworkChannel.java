package com.unknown.net;

import java.io.IOException;
import java.net.InetAddress;
import java.util.function.Consumer;

public interface NetworkChannel {
	void read(Consumer<byte[]> consumer) throws IOException;

	void write(byte[] data) throws IOException;

	void close() throws IOException;

	InetAddress getInetAddress();

	int getPort();

	boolean active();
}
