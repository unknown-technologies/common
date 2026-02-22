package com.unknown.net.shownet;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class Request {
	final int seq;
	final boolean crypted;
	final boolean scrambled;
	final long timestamp;

	final CompletableFuture<byte[]> future;

	Request(int seq, boolean crypted, boolean scrambled) {
		this.seq = seq;
		this.crypted = crypted;
		this.scrambled = scrambled;
		this.timestamp = System.currentTimeMillis();

		future = new CompletableFuture<>();
	}

	void signal(byte[] data, int offset, int length) {
		if(offset == 0 && data.length == length) {
			future.complete(data);
		} else {
			future.complete(Arrays.copyOfRange(data, offset, offset + length));
		}
	}

	void timeout() {
		future.cancel(true);
	}
}
