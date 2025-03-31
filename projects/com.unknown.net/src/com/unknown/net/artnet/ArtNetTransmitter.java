package com.unknown.net.artnet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ArtNetTransmitter implements AutoCloseable {
	private final DatagramSocket socket;

	public ArtNetTransmitter() throws IOException {
		socket = new DatagramSocket();
	}

	@Override
	public void close() {
		socket.close();
	}

	public void send(InetAddress destination, ArtNetPacket packet) throws IOException {
		byte[] data = packet.write();
		DatagramPacket pkt = new DatagramPacket(data, data.length);
		pkt.setAddress(destination);
		pkt.setPort(0x1936);
		socket.send(pkt);
	}
}
