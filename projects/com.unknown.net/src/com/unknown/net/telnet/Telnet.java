package com.unknown.net.telnet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.unknown.net.NetworkChannel;
import com.unknown.net.telnet.event.CommandListener;
import com.unknown.net.telnet.event.ReceiveListener;
import com.unknown.net.telnet.event.RecordListener;
import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;

public class Telnet extends InputStream {
	private static final Logger log = Trace.create(Telnet.class);

	public final static int BUFSIZE = 1024;
	public final static int DEFAULT_TERMINAL_WIDTH = 80;
	public final static int DEFAULT_TERMINAL_HEIGHT = 24;

	// @formatter:off
	public final static byte NUL = 0; /* Null */
	public final static byte LF = 10; /* Line Feed */
	public final static byte CR = 13; /* Carriage Return */
	public final static byte BEL = 7; /* Bell */
	public final static byte BS = 8;  /* Back Space */
	public final static byte HT = 9;  /* Horizontal Tab */
	public final static byte VT = 11; /* Vertical Tab */
	public final static byte FF = 12; /* Form Feed */

	public final static byte BINARY = 0; /* Binary Transmission */
	public final static byte IS = 0; /* Used by terminal-type negotiation */
	public final static byte SEND = 1; /* Used by terminal-type negotiation */
	public final static byte ECHO_OPTION = 1; /* Echo option */
	public final static byte SUPPRESS_GA = 3; /* Suppress go-ahead option */
	public final static byte TIMING_MARK = 6; /* Timing mark option */
	public final static byte TERMINAL_TYPE = 24; /* Terminal type option */
	public final static byte NAWS = 31; /* Negotiate About Window Size */
	public final static byte TERMINAL_SPEED = 32; /* Terminal type option */
	public final static byte NEW_ENV = 39; /* New Environment option */
	public final static byte TN3270E = 40; /* TN3270E option */
	public final static byte START_TLS = 46; /* Start TLS option */
	public final static byte EOR = 25; /* End of record option */
	public final static byte EOR_MARK = (byte) 239; /* End of record marker */
	public final static byte SE = (byte) 240; /* End of subnegotiation parameters */
	public final static byte NOP = (byte) 241; /* No operation */
	public final static byte DATA_MARK = (byte) 242; /* The data stream portion of a Synch. This should always be accompanied by a TCP Urgent notification */
	public final static byte BRK = (byte) 243; /* Break character */
	public final static byte IP = (byte) 244; /* Interrupt Process */
	public final static byte AO = (byte) 245; /* Abort Output */
	public final static byte AYT = (byte) 246; /* Are You There */
	public final static byte EC = (byte) 247; /* Erase character */
	public final static byte EL = (byte) 248; /* Erase Line */
	public final static byte GA = (byte) 249; /* Go ahead */
	public final static byte SB = (byte) 250; /* Subnegotiation of indicated option */
	public final static byte WILL = (byte) 251; /* Indicates the desire to begin performing, or confirmation that you are now performing, the indicated option */
	public final static byte WONT = (byte) 252; /* Indicates the refusal to perform, or continue performing, the indicated option */
	public final static byte DO = (byte) 253; /* Indicates the request that the other party perform, or confirmation that you are expecting the other party to perform, the indicated option */
	public final static byte DONT = (byte) 254; /* Indicates the demand that the other party stop performing, or confirmation that you are no longer expecting the other party to perform, the indicated option */
	public final static byte IAC = (byte) 255; /* Interpret as Command */

	// https://tools.ietf.org/html/rfc1184
	public final static byte EOF = (byte) 236;
	public final static byte SUSP = (byte) 237;
	public final static byte ABORT = (byte) 238;
	// @formatter:on

	private NetworkChannel channel;

	private Map<Byte, Boolean> optionsLocal;
	private Map<Byte, Boolean> optionsRemote;
	private Map<Byte, Boolean> optionsActiveLocal;
	private Map<Byte, Boolean> optionsActiveRemote;
	private Set<Byte> optionsValidLocal;
	private Set<Byte> optionsValidRemote;
	private List<Byte> rxbuffer;
	private List<byte[]> commands;
	private List<Byte> databuffer;
	private Map<Byte, Set<Consumer<Boolean>>> optionsLocalCallbacks;
	private Map<Byte, Set<Consumer<Boolean>>> optionsRemoteCallbacks;

	private Set<Consumer<byte[]>> commandCallbacks;

	private boolean active;

	private InetAddress address = null;
	private int port = -1;

	// max 10 packets before interrupting io handler
	private int interrupt = 10;

	// event handlers
	private List<CommandListener> commandListeners;
	private List<ReceiveListener> dataListeners;
	private List<RecordListener> recordListeners;

	boolean useEOR;

	protected int terminalWidth;
	protected int terminalHeight;
	protected boolean sizeAvailable;

	public Telnet(NetworkChannel channel) {
		this.channel = channel;
		address = channel.getInetAddress();
		port = channel.getPort();
		optionsLocal = new HashMap<>();
		optionsRemote = new HashMap<>();
		optionsActiveLocal = new HashMap<>();
		optionsActiveRemote = new HashMap<>();
		optionsValidLocal = new HashSet<>();
		optionsValidRemote = new HashSet<>();
		optionsLocalCallbacks = new HashMap<>();
		optionsRemoteCallbacks = new HashMap<>();
		commandCallbacks = new HashSet<>();
		rxbuffer = new ArrayList<>();
		commands = new ArrayList<>();
		databuffer = new ArrayList<>();
		commandListeners = new ArrayList<>();
		dataListeners = new ArrayList<>();
		recordListeners = new ArrayList<>();
		active = true;
		useEOR = false;
		terminalWidth = DEFAULT_TERMINAL_WIDTH;
		terminalHeight = DEFAULT_TERMINAL_HEIGHT;
		sizeAvailable = false;
	}

	public Telnet(Telnet tn) {
		this.channel = tn.channel;
		this.optionsLocal = tn.optionsLocal;
		this.optionsRemote = tn.optionsRemote;
		this.optionsActiveLocal = tn.optionsActiveLocal;
		this.optionsActiveRemote = tn.optionsActiveRemote;
		this.optionsValidLocal = tn.optionsValidLocal;
		this.optionsValidRemote = tn.optionsValidRemote;
		this.optionsLocalCallbacks = tn.optionsLocalCallbacks;
		this.optionsRemoteCallbacks = tn.optionsRemoteCallbacks;
		this.commandCallbacks = tn.commandCallbacks;
		this.rxbuffer = tn.rxbuffer;
		this.commands = tn.commands;
		this.databuffer = tn.databuffer;
		this.active = tn.active;
		this.address = tn.address;
		this.port = tn.port;
		this.commandListeners = tn.commandListeners;
		this.dataListeners = tn.dataListeners;
		this.recordListeners = tn.recordListeners;
		this.useEOR = tn.useEOR;
		this.terminalWidth = tn.terminalWidth;
		this.terminalHeight = tn.terminalHeight;
		this.sizeAvailable = tn.sizeAvailable;
	}

	public int getTerminalWidth() {
		return terminalWidth;
	}

	public int getTerminalHeight() {
		return terminalHeight;
	}

	public boolean isSizeAvailable() {
		return sizeAvailable;
	}

	public void addCommandListener(CommandListener l) {
		commandListeners.add(l);
	}

	public void removeCommandListener(CommandListener l) {
		commandListeners.remove(l);
	}

	public void addRecordListener(RecordListener l) {
		recordListeners.add(l);
	}

	public void removeRecordListener(RecordListener l) {
		recordListeners.remove(l);
	}

	public void addReceiveListener(ReceiveListener l) {
		dataListeners.add(l);
	}

	public void removeReceiveListener(ReceiveListener l) {
		dataListeners.remove(l);
	}

	protected void fireCommandEvent(byte[] command) {
		for(CommandListener l : commandListeners) {
			try {
				l.processCommand(command);
			} catch(Throwable t) {
				log.log(Levels.WARNING, "Failed to process command: " + t.getMessage(), t);
			}
		}
	}

	// FIXME (BUG): only one receiver will work!
	protected void fireDataEvent() throws IOException {
		byte[] data = new byte[available()];
		read(data);
		for(ReceiveListener l : dataListeners) {
			try {
				l.receive(data);
			} catch(Throwable t) {
				log.log(Levels.WARNING, "Failed to process data: " + t.getMessage(), t);
			}
		}
	}

	protected void fireRecordEvent(byte[] record) {
		for(RecordListener l : recordListeners) {
			try {
				l.onRecord(record);
			} catch(Throwable t) {
				log.log(Levels.WARNING, "Failed to process record: " + t.getMessage(), t);
			}
		}
	}

	public NetworkChannel getChannel() {
		return channel;
	}

	public String getAddress() {
		if(address == null) {
			return null;
		}
		return address.getHostAddress();
	}

	public int getPort() {
		return port;
	}

	@Override
	public synchronized void reset() {
		useEOR = false;
		rxbuffer.clear();
		commands.clear();
		databuffer.clear();
		optionsActiveLocal.clear();
		optionsActiveRemote.clear();
		optionsValidLocal.clear();
		optionsValidRemote.clear();
		sizeAvailable = false;
	}

	public void setOptionLocal(byte option, boolean value)
			throws IOException {
		optionsLocal.put(option, value);
		Boolean v = optionsActiveLocal.get(option);
		if(v != null && (v.equals(value))) {
			return;
		}
		optionsValidLocal.remove(option);
		command(value ? WILL : WONT, option);
	}

	public void setOptionRemote(byte option, boolean value)
			throws IOException {
		optionsRemote.put(option, value);
		Boolean v = optionsActiveRemote.get(option);
		if(v != null && (v.equals(value))) {
			return;
		}
		optionsValidRemote.remove(option);
		command(value ? DO : DONT, option);
	}

	public boolean getOptionLocal(byte option) {
		Boolean value = optionsLocal.get(option);
		if(value == null) {
			return false;
		}
		return value;
	}

	public boolean getOptionRemote(byte option) {
		Boolean value = optionsRemote.get(option);
		if(value == null) {
			return false;
		}
		return value;
	}

	public boolean isOptionNegotiatedLocal(byte option) {
		return optionsValidLocal.contains(option);
	}

	public boolean isOptionNegotiatedRemote(byte option) {
		return optionsValidRemote.contains(option);
	}

	public boolean getOptionActiveLocal(byte option) {
		Boolean value = optionsActiveLocal.get(option);
		if(value == null) {
			return false;
		}
		return value;
	}

	public boolean getOptionActiveRemote(byte option) {
		Boolean value = optionsActiveRemote.get(option);
		if(value == null) {
			return false;
		}
		return value;
	}

	public void waitForLocal(byte option, Consumer<Boolean> callback) {
		Set<Consumer<Boolean>> callbacks = optionsLocalCallbacks.get(option);
		if(callbacks == null) {
			callbacks = new HashSet<>();
			optionsLocalCallbacks.put(option, callbacks);
		}
		callbacks.add(callback);
	}

	public void waitForRemote(byte option, Consumer<Boolean> callback) {
		Set<Consumer<Boolean>> callbacks = optionsRemoteCallbacks.get(option);
		if(callbacks == null) {
			callbacks = new HashSet<>();
			optionsRemoteCallbacks.put(option, callbacks);
		}
		callbacks.add(callback);
	}

	private void notifyLocalOptionCallbacks(byte option) {
		Set<Consumer<Boolean>> callbacks = optionsLocalCallbacks.get(option);
		if(callbacks != null) {
			callbacks.stream().forEach((callback) -> {
				try {
					callback.accept(getOptionActiveLocal(option));
				} catch(Throwable t) {
					log.log(Levels.ERROR, "Failed to process local option: " + t.getMessage(), t);
				}
			});
			callbacks.clear();
		}
	}

	private void notifyRemoteOptionCallbacks(byte option) {
		Set<Consumer<Boolean>> callbacks = optionsRemoteCallbacks.get(option);
		if(callbacks != null) {
			callbacks.stream().forEach((callback) -> {
				try {
					callback.accept(getOptionActiveRemote(option));
				} catch(Throwable t) {
					log.log(Levels.ERROR, "Failed to process remote option: " + t.getMessage(), t);
				}
			});
			callbacks.clear();
		}
	}

	private void handleOption(byte action, byte option) throws IOException {
		Boolean ol = optionsLocal.get(option);
		Boolean or = optionsRemote.get(option);
		Boolean al = optionsActiveLocal.get(option);
		Boolean ar = optionsActiveRemote.get(option);
		switch(action) {
		case DO:
			if(ol != null && al == null) { // we sent WILL/WONT, they sent DO
				optionsActiveLocal.put(option, true);
			} else if(ol != null) { // we sent nothing, they sent DO
				optionsActiveLocal.put(option, ol);
				command(ol ? WILL : WONT, option);
			} else { // we don't know that option
				optionsActiveLocal.put(option, false);
				command(WONT, option);
			}
			optionsValidLocal.add(option);
			notifyLocalOptionCallbacks(option);
			break;
		case DONT:
			if(ol != null && al == null) { // we sent WILL/WONT, they sent DONT
				optionsActiveLocal.put(option, true);
			} else if(ol != null) { // we sent nothing, they sent DONT
				optionsActiveLocal.put(option, false);
				command(WONT, option);
			} else { // we don't know that option
				optionsActiveLocal.put(option, false);
				command(WONT, option);
			}
			optionsValidLocal.add(option);
			notifyLocalOptionCallbacks(option);
			break;
		case WILL:
			if(or != null && ar == null) { // we sent DO/DONT, they sent WILL
				optionsActiveRemote.put(option, true);
				if(option == EOR) {
					useEOR = true;
				}
			} else if(or != null) { // we sent nothing, they sent WILL
				optionsActiveRemote.put(option, or);
				command(or ? DO : DONT, option);
				if(option == EOR) {
					useEOR = or;
				}
			} else { // we don't know that option
				optionsActiveRemote.put(option, false);
				command(DONT, option);
				if(option == EOR) {
					useEOR = false;
				}
			}
			optionsValidRemote.add(option);
			notifyRemoteOptionCallbacks(option);
			break;
		case WONT:
			if(or != null && ar == null) { // we sent DO/DONT, they sent WONT
				optionsActiveRemote.put(option, false);
				if(option == EOR) {
					useEOR = false;
				}
			} else if(or != null) { // we sent nothing, they sent WILL
				optionsActiveRemote.put(option, false);
				command(DONT, option);
				if(option == EOR) {
					useEOR = false;
				}
			} else { // we don't know that option
				optionsActiveRemote.put(option, false);
				command(DONT, option);
				if(option == EOR) {
					useEOR = false;
				}
			}
			optionsValidRemote.add(option);
			notifyRemoteOptionCallbacks(option);
			break;
		}
	}

	private synchronized void send(byte[] sequence) throws IOException {
		channel.write(sequence);
	}

	public synchronized void receive(byte[] bytes) throws IOException {
		for(byte b : bytes) {
			rxbuffer.add(b);
		}
		parse();
		process();
	}

	private void receiveCommand(byte[] cmd) {
		commands.add(cmd);
		commandCallbacks.forEach((callback) -> {
			try {
				callback.accept(cmd);
			} catch(Throwable t) {
				log.log(Levels.ERROR, "Failed to process command: " + t.getMessage(), t);
			}
		});
		commandCallbacks.clear();
	}

	private void parse() throws IOException {
		int i;
		loop: for(i = 0; i < rxbuffer.size(); i++) {
			byte b = rxbuffer.get(i);
			if(b == IAC) {
				if((i + 1) < rxbuffer.size()) {
					b = rxbuffer.get(i + 1);
					switch(b) {
					case WILL:
					case WONT:
					case DO:
					case DONT:
						if((i + 2) < rxbuffer.size()) {
							byte what = rxbuffer.get(i + 2);
							// receiveCommand(new byte[] { b, what });
							i += 2;
							handleOption(b, what);
						} else {
							// FIXME: what do we do here?
							System.out.printf("brk1 cmd: %02x\n", Byte.toUnsignedInt(b));
							break loop;
						}
						break;
					case IAC:
						databuffer.add(IAC);
					case NOP:
						i++;
						break;
					case SB:
						int j = i + 1;
						boolean found = false;
						while(j < (rxbuffer.size() - 1)) {
							if((rxbuffer.get(j) == IAC) && (rxbuffer.get(j + 1) == SE)) {
								found = true;
								byte[] cmd = new byte[j - i - 1];
								for(int x = 0, y = i + 1; y < j; x++, y++) {
									cmd[x] = rxbuffer.get(y);
								}
								if(cmd[1] == NAWS) {
									terminalWidth = ((cmd[2] & 0xFF) << 8) |
											(cmd[3] & 0xFF);
									terminalHeight = ((cmd[4] & 0xFF) << 8) |
											(cmd[5] & 0xFF);
									if(terminalWidth == 0 || terminalHeight == 0) {
										terminalWidth = DEFAULT_TERMINAL_WIDTH;
										terminalHeight = DEFAULT_TERMINAL_HEIGHT;
										sizeAvailable = false;
									} else {
										sizeAvailable = true;
									}
								}
								receiveCommand(cmd);
								break;
							} else {
								j++;
							}
						}
						if(!found) {
							break loop;
						} else {
							i = j + 1;
						}
						break;
					case EOR_MARK:
						if(useEOR) {
							byte[] record = new byte[available()];
							read(record);
							fireRecordEvent(record);
							i++;
							break;
						}
					default:
						receiveCommand(new byte[] { b });
						i++;
					}
				} else {
					// FIXME: what do we do here?
					System.out.printf("brk2 cmd: %02x\n", Byte.toUnsignedInt(b));
					break loop;
				}
			} else {
				databuffer.add(b);
			}
		}
		for(int x = 0; x < i; x++) {
			rxbuffer.remove(0);
		}
	}

	@Override
	public int read() throws IOException {
		while(active && (databuffer.size() == 0)) {
			throw new IOException("no data available");
		}
		if(!active) {
			return -1;
		}
		byte b = databuffer.remove(0);
		return b & 0xFF;
	}

	@Override
	public int read(byte[] data) throws IOException {
		while(active && (databuffer.size() == 0)) {
			return 0;
		}
		if(!active) {
			return -1;
		}
		Byte[] b = new Byte[databuffer.size()];
		databuffer.toArray(b);
		int n = Math.min(data.length, b.length);
		for(int i = 0; i < n; i++) {
			if(b[i] != null) {
				data[i] = b[i];
			}
		}
		for(int i = 0; i < n; i++) {
			databuffer.remove(0);
		}
		return n;
	}

	public void write(byte data) throws IOException {
		write(new byte[] { data });
	}

	public void write(byte[] data) throws IOException {
		send(encodeIAC(data));
	}

	public void print(String s) throws IOException {
		write(s.getBytes());
	}

	public void println(String s) throws IOException {
		print(s + "\r\n");
	}

	public void printf(String format, Object... args) throws IOException {
		print(String.format(format, args).replace("\n", "\r\n"));
	}

	public void process() throws IOException {
		int cnt = interrupt;
		if(commandListeners.size() > 0) {
			while(getCommandCount() > 0 && (cnt-- > 0)) {
				byte[] command = getCommand();
				fireCommandEvent(command);
			}
		}

		if(dataListeners.size() > 0 && available() > 0) {
			fireDataEvent();
		}
	}

	public byte[] getCommand() {
		if(commands.size() == 0) {
			return null;
		}
		byte[] b = commands.get(0);
		commands.remove(0);
		return b;
	}

	public void getCommand(Consumer<byte[]> consumer) {
		byte[] command = getCommand();
		if(command != null)
			consumer.accept(command);
		else
			commandCallbacks.add(consumer);
	}

	@Override
	public void close() throws IOException {
		if(channel.active()) {
			channel.close();
		}
	}

	public int getCommandCount() {
		return commands.size();
	}

	@Override
	public int available() throws IOException {
		return databuffer.size();
	}

	private static byte[] encodeIAC(byte[] data) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream(
				data.length);
		for(byte b : data) {
			if(b == IAC) {
				buf.write(IAC);
			}
			buf.write(b);
		}
		try {
			buf.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return buf.toByteArray();
	}

	public void command(byte cmd, byte... parameters) throws IOException {
		byte[] params = parameters == null ? new byte[0] : parameters;
		byte[] sequence = new byte[params.length + 2];
		sequence[0] = IAC;
		sequence[1] = cmd;
		for(int i = 0; i < params.length; i++) {
			sequence[i + 2] = params[i];
		}
		send(sequence);
	}

	public void subnegotiate(byte... rawdata) throws IOException {
		byte[] data = encodeIAC(rawdata);
		byte[] params = new byte[data.length + 4];
		params[0] = IAC;
		params[1] = SB;
		params[params.length - 2] = IAC;
		params[params.length - 1] = SE;
		for(int i = 0; i < data.length; i++) {
			params[i + 2] = data[i];
		}
		send(params);
	}

	public boolean isOpen() {
		return channel.active();
	}
}
