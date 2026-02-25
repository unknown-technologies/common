package com.unknown.net.shownet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;

public class ShowNET implements AutoCloseable {
	private static final Logger log = Trace.create(ShowNET.class);

	private static final int DISCOVERY_TIMEOUT = 10_000;
	private static final int LASER_TIMEOUT = 1_000;

	private static final byte[] DISCOVERY = { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			(byte) 0x8d, 0x0, 0x2, 0x0, 0x0, 0x0, 0x0, 0x0, (byte) 0x80, (byte) 0xa3, 0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0 };

	private final DatagramSocket discoverySocket;
	private final DatagramSocket socket;

	private final Map<InetAddress, Laser> lasers = new ConcurrentHashMap<>();

	private final Map<InetAddress, LaserInfo> discoveredLasers = new ConcurrentHashMap<>();

	private final Set<InetAddress> additionalDiscoveryAddresses = new HashSet<>();

	private final Thread discoveryThread;
	private final Thread receiveThread;

	private final List<LaserDiscoveryListener> discoveryListeners = new ArrayList<>();

	public ShowNET() throws IOException {
		this(true);
	}

	public ShowNET(boolean discover) throws IOException {
		if(discover) {
			byte[] broadcastAddress = { -1, -1, -1, -1 };
			InetAddress broadcast = InetAddress.getByAddress(broadcastAddress);

			discoverySocket = new DatagramSocket(41856);
			discoverySocket.setBroadcast(true);
			discoverySocket.setSoTimeout(DISCOVERY_TIMEOUT);

			discoveryThread = new Thread() {
				@Override
				public void run() {
					byte[] data = new byte[1600];
					// send initial discovery
					DatagramPacket discovery = new DatagramPacket(DISCOVERY, DISCOVERY.length,
							broadcast, Laser.PORT);

					try {
						discoverySocket.send(discovery);
					} catch(IOException e) {
						log.log(Levels.ERROR, "Failed to send discovery packet: " +
								e.getMessage(), e);
						return;
					}

					while(!discoverySocket.isClosed()) {
						// clean up list
						long now = System.currentTimeMillis();
						long limit = now - DISCOVERY_TIMEOUT * 3;
						for(Entry<InetAddress, LaserInfo> entry : discoveredLasers.entrySet()) {
							if(entry.getValue().getDiscoveryTimestamp() < limit) {
								discoveredLasers.remove(entry.getKey());
								fireLaserLost(entry.getValue());
							}
						}

						// receive new laser info ...
						DatagramPacket packet = new DatagramPacket(data, data.length);
						try {
							discoverySocket.receive(packet);
							LaserInfo info = new LaserInfo(packet.getAddress(), data);
							discoveredLasers.put(packet.getAddress(), info);
							fireLaserDiscovered(info);
						} catch(SocketTimeoutException e) {
							// clean up laser response queue
							packetCleanup();

							// send a new discovery packet
							try {
								discoverySocket.send(discovery);
							} catch(IOException ex) {
								log.log(Levels.ERROR,
										"Failed to send discovery packet: " +
												ex.getMessage(),
										ex);
								return;
							}

							synchronized(additionalDiscoveryAddresses) {
								for(InetAddress addr : additionalDiscoveryAddresses) {
									DatagramPacket discoveryPacket = new DatagramPacket(
											DISCOVERY, DISCOVERY.length,
											addr, Laser.PORT);
									try {
										discoverySocket.send(discoveryPacket);
									} catch(IOException ex) {
										log.log(Levels.ERROR,
												"Failed to send discovery packet: " +
														ex.getMessage(),
												ex);
										return;
									}
								}
							}
						} catch(IOException e) {
							log.log(Levels.ERROR, "Failed to receive discovery packet: " +
									e.getMessage(), e);
						}
					}
				}
			};
			discoveryThread.setDaemon(true);
			discoveryThread.start();
		} else {
			discoverySocket = null;
			discoveryThread = null;
		}

		socket = new DatagramSocket();
		socket.setSoTimeout(LASER_TIMEOUT);
		receiveThread = new Thread() {
			@Override
			public void run() {
				while(!socket.isClosed()) {
					byte[] data = new byte[1600];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
						InetAddress addr = packet.getAddress();
						Laser laser = lasers.get(addr);
						if(laser != null) {
							laser.receive(packet.getData(), packet.getLength());
						}
					} catch(SocketTimeoutException e) {
						// clean up laser response queue
						packetCleanup();
					} catch(IOException e) {
						log.log(Levels.ERROR, "Failed to receive laser response packet: " +
								e.getMessage(), e);
					}
				}
			}
		};
		receiveThread.setDaemon(true);
		receiveThread.start();
	}

	private void packetCleanup() {
		// clean up laser packets here too
		for(Entry<InetAddress, Laser> entry : lasers.entrySet()) {
			entry.getValue().cleanupPackets();
		}
	}

	@Override
	public void close() {
		socket.close();
		discoverySocket.close();

		try {
			discoveryThread.join();
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		try {
			receiveThread.join();
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public void addDiscoveryAddress(InetAddress addr) {
		if(discoverySocket == null) {
			throw new IllegalStateException("Cannot use discovery when it is disabled");
		}

		synchronized(additionalDiscoveryAddresses) {
			additionalDiscoveryAddresses.add(addr);
		}

		DatagramPacket discoveryPacket = new DatagramPacket(DISCOVERY, DISCOVERY.length, addr, Laser.PORT);
		try {
			discoverySocket.send(discoveryPacket);
		} catch(IOException e) {
			log.log(Levels.ERROR, "Failed to send discovery packet: " + e.getMessage(), e);
		}
	}

	public void removeDiscoveryAddress(InetAddress addr) {
		synchronized(additionalDiscoveryAddresses) {
			additionalDiscoveryAddresses.remove(addr);
		}
	}

	public Set<InetAddress> getDiscoveryAddresses() {
		synchronized(additionalDiscoveryAddresses) {
			return Collections.unmodifiableSet(new HashSet<>(additionalDiscoveryAddresses));
		}
	}

	public Set<LaserInfo> getDiscoveredLasers() {
		return Collections.unmodifiableSet(new HashSet<>(discoveredLasers.values()));
	}

	protected void fireLaserDiscovered(LaserInfo info) {
		synchronized(discoveryListeners) {
			for(LaserDiscoveryListener listener : discoveryListeners) {
				try {
					listener.laserDiscovered(info);
				} catch(Throwable t) {
					log.log(Levels.WARNING, "Laser discovery listener failed: " + t.getMessage(),
							t);
				}
			}
		}
	}

	protected void fireLaserLost(LaserInfo info) {
		synchronized(discoveryListeners) {
			for(LaserDiscoveryListener listener : discoveryListeners) {
				try {
					listener.laserLost(info);
				} catch(Throwable t) {
					log.log(Levels.WARNING, "Laser discovery listener failed: " + t.getMessage(),
							t);
				}
			}
		}
	}

	public void addLaserDiscoveryListener(LaserDiscoveryListener listener) {
		synchronized(discoveryListeners) {
			discoveryListeners.add(listener);
		}
	}

	public void removeLaserDiscoveryListener(LaserDiscoveryListener listener) {
		synchronized(discoveryListeners) {
			discoveryListeners.add(listener);
		}
	}

	public Laser connect(InetAddress addr) throws IOException {
		Laser laser = lasers.get(addr);
		if(laser == null) {
			laser = new Laser(this, addr);
			lasers.put(addr, laser);
			laser.init();
			laser.configure();
		}
		return laser;
	}

	public void disconnect(Laser laser) {
		lasers.remove(laser.getAddress());
	}

	public Set<Laser> getConnectedLasers() {
		return Collections.unmodifiableSet(new HashSet<>(lasers.values()));
	}

	public Laser getLaser(InetAddress addr) {
		return lasers.get(addr);
	}

	public InetAddress getLaserAddress(InterfaceId interfaceId) {
		for(Laser laser : lasers.values()) {
			InterfaceId id = laser.getInterfaceId();
			if(id != null && id.equals(interfaceId)) {
				return laser.getAddress();
			}
		}

		for(LaserInfo laser : discoveredLasers.values()) {
			InterfaceId id = laser.getInterfaceId();
			if(id != null && id.equals(interfaceId)) {
				return laser.getAddress();
			}
		}

		return null;
	}

	void send(DatagramPacket packet) throws IOException {
		socket.send(packet);
	}
}
