package com.unknown.net.shownet;

public interface LaserDiscoveryListener {
	void laserDiscovered(LaserInfo info);

	void laserLost(LaserInfo info);
}
