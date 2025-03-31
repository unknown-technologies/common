package com.unknown.net.telnet.event;

import java.io.IOException;

public interface CommandListener {
	void processCommand(byte[] cmd) throws IOException;
}
