package com.unknown.net.telnet.event;

import java.io.IOException;

public interface CommandListener {
	public void processCommand(byte[] cmd) throws IOException;
}
