package com.unknown.net.shownet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import com.unknown.util.io.Endianess;
import com.unknown.util.log.Trace;

public class Laser {
	private static final Logger log = Trace.create(Laser.class);

	public static final int PORT = 41855;

	private static final int PKT_FLAG_KEYS = 0x010000;
	private static final int PKT_FLAG_SCRAMBLE = 0x200000;
	private static final int PKT_FLAG_CRYPT = 0x400000;
	private static final int PKT_FLAG_UNKNOWN = 0x900000;

	public static final int FLAG_SCRAMBLE_HDR = 0x01;
	public static final int FLAG_CRYPT_HDR = 0x02;
	public static final int FLAG_SCRAMBLE_PLD = 0x04;
	public static final int FLAG_CRYPT_PLD = 0x08;
	public static final int FLAG_SCRAMBLE_RSP = 0x10;
	public static final int FLAG_CRYPT_RSP = 0x20;
	public static final int FLAG_UNKNOWN = 0x40;
	public static final int FLAG_KEYS = 0x80;
	public static final int FLAG_IGNORE_RSP = 0x100;

	private static final int TIMEOUT = 500;

	private static final Random rng = new Random();

	private final ShowNET shownet;
	private final InetAddress networkAddress;

	private final int[] interfaceId = new int[3];
	private final int[] cryptKey = new int[4];
	private final byte[] mac = new byte[6];
	private int timeInterval;
	private int frameMemorySize;
	private int framePointSize;
	private int revision;
	private int firmware;
	private int generation;
	private int colorFeatures;
	private int colorCount;
	private int status;
	private int memoryAddress;
	private int configAddress;
	private final byte[] config = new byte[2048];

	private int sequenceId = 1000;
	private int frameId = 0;
	private int packetId = 0;

	private ConcurrentHashMap<Integer, Request> requests = new ConcurrentHashMap<>();

	private Object txlock = new Object();
	private Object framelock = new Object();

	public Laser(ShowNET shownet, InetAddress address) {
		this.shownet = shownet;
		this.networkAddress = address;
	}

	public int getTimeInterval() {
		return timeInterval;
	}

	public int getFrameMemorySize() {
		return frameMemorySize;
	}

	public int getFirmware() {
		return firmware;
	}

	public int getGeneration() {
		return generation;
	}

	public int getColorFeatures() {
		return colorFeatures;
	}

	public int getFramePointSize() {
		return framePointSize;
	}

	public void cleanupPackets() {
		long now = System.currentTimeMillis();
		long limit = now - TIMEOUT;
		for(Entry<Integer, Request> entry : requests.entrySet()) {
			Request request = entry.getValue();
			if(request.timestamp < limit) {
				request.timeout();
				requests.remove(entry.getKey());
			}
		}
	}

	public Future<byte[]> send(byte[] data, int length, int flags) throws IOException {
		Scrambler scrambler = new Scrambler();

		int packetFlags = 0;
		int words = (length - 20) / 8;
		int responseCipher = 0;

		byte[] buf = data;
		if(data.length < (words * 8 + 20)) {
			buf = Arrays.copyOf(data, words * 8 + 20);
		}

		if((flags & FLAG_KEYS) != 0) {
			packetFlags |= PKT_FLAG_KEYS;
		}

		if((flags & FLAG_SCRAMBLE_RSP) != 0) {
			responseCipher |= 1;
		}

		if((flags & FLAG_CRYPT_RSP) != 0) {
			responseCipher |= 2;
		}

		if((flags & FLAG_UNKNOWN) != 0) {
			packetFlags = PKT_FLAG_UNKNOWN;
		}

		if((flags & FLAG_SCRAMBLE_HDR) != 0) {
			packetFlags |= PKT_FLAG_SCRAMBLE;
		}

		if((flags & FLAG_CRYPT_HDR) != 0) {
			packetFlags |= PKT_FLAG_CRYPT;
		}

		int cryptWords = ((flags & FLAG_CRYPT_PLD) != 0) ? words : 0;
		int scrambleWords = ((flags & FLAG_SCRAMBLE_PLD) != 0) ? words : 0;

		int seq;
		synchronized(txlock) {
			seq = sequenceId;
			sequenceId = (sequenceId + 1) & 0x7FFFFFFF;
		}

		cleanupPackets();

		Request request = null;
		if((flags & FLAG_IGNORE_RSP) == 0) {
			request = new Request(seq, (flags & FLAG_CRYPT_RSP) != 0, (flags & FLAG_SCRAMBLE_RSP) != 0);
			Request oldRequest = requests.put(seq, request);
			if(oldRequest != null) {
				oldRequest.timeout();
			}
		}

		Endianess.set32bitLE(buf, 0, packetFlags | (cryptWords << 8) | scrambleWords);
		Endianess.set32bitLE(buf, 4, responseCipher << 24);
		Endianess.set32bitLE(buf, 8, seq);

		if((packetFlags & PKT_FLAG_CRYPT) != 0) {
			Crypt.crypt(buf, 4, 16);
		}

		if((packetFlags & PKT_FLAG_SCRAMBLE) != 0) {
			for(int i = 1; i <= 4; i++) {
				int off = i * 4;
				int word = scrambler.scramble(Endianess.get32bitLE(buf, off));
				Endianess.set32bitLE(buf, off, word);
			}
		}

		for(int i = 0; i < cryptWords; i++) {
			Crypt.crypt(buf, i * 8 + 20, 8);
		}

		for(int i = 0; i < 2 * scrambleWords; i++) {
			int off = i * 4 + 20;
			int word = scrambler.scramble(Endianess.get32bitLE(buf, off));
			Endianess.set32bitLE(buf, off, word);
		}

		DatagramPacket packet = new DatagramPacket(buf, length, networkAddress, PORT);
		shownet.send(packet);

		if(request == null) {
			return null;
		} else {
			return request.future;
		}
	}

	public void receive(byte[] data, int length) {
		if(length < 24) {
			log.warning("Malformed packet received (too short): " + length + " bytes");
			return;
		}

		int header = Endianess.get32bitLE(data, 0);
		int seq = header & 0x7FFFFFFF;

		Request request = requests.remove(seq);
		if(request != null) {
			if(request.crypted) {
				int len = (length - 4) >>> 3;
				for(int i = 0; i < len; i++) {
					Crypt.decrypt(data, i * 8 + 4, 8);
				}
			}

			if(request.scrambled) {
				Scrambler scrambler = new Scrambler();
				int len = (length - 4) >>> 2;
				for(int i = 0; i < len; i++) {
					int off = i * 4 + 4;
					int word = scrambler.scramble(Endianess.get32bitLE(data, off));
					Endianess.set32bitLE(data, off, word);
				}
			}

			status = Endianess.get32bitLE(data, 20) >> 28;

			request.signal(data, 0, length);
		}
	}

	void init() throws IOException, TimeoutException {
		cryptKey[0] = Crypt.getKey(0);
		cryptKey[2] = rng.nextInt();

		byte[] buf = new byte[32];
		Endianess.set16bitLE(buf, 12, (short) 1);
		buf[14] = 4;
		Endianess.set32bitLE(buf, 22, cryptKey[2]);
		buf[26] = 0x0A;

		Future<byte[]> result = send(buf, buf.length, FLAG_KEYS | FLAG_CRYPT_HDR | FLAG_SCRAMBLE_HDR |
				FLAG_CRYPT_PLD | FLAG_SCRAMBLE_PLD | FLAG_CRYPT_RSP | FLAG_SCRAMBLE_RSP);
		try {
			byte[] rx = result.get();

			int offset = 0;
			switch(rx.length) {
			case 124:
				offset = 24;
				break;
			case 132:
			case 232:
				offset = 32;
				break;
			default:
				throw new IOException("Unexpected response length: " + rx.length);
			}

			generation = 1;
			for(int i = 0; i < interfaceId.length; i++) {
				interfaceId[i] = Endianess.get32bitLE(rx, offset + 24 + i * 4);
			}
			memoryAddress = Endianess.get32bitLE(rx, offset + 36);

			cryptKey[1] = interfaceId[2];
			cryptKey[3] = Endianess.get16bitLE(rx, offset + 44);

			revision = Endianess.get32bitLE(rx, offset + 8);
			if(revision == 11111) {
				firmware = Endianess.get32bitLE(rx, offset);
			} else {
				firmware = Endianess.get32bitLE(rx, offset + 4);
			}

			if(rx.length == 232) {
				generation = Byte.toUnsignedInt(rx[offset + 52]);
			}

			timeInterval = 24000000;
			if(generation == 2) {
				timeInterval = 28000000;
			}

			colorFeatures = -1;
			if(firmware > 2014082201) {
				colorFeatures = Endianess.get32bitLE(rx, offset + 90);
			}

			if(firmware < 2018091702) {
				if(colorFeatures == 4) {
					colorCount = 3;
				} else {
					colorCount = 6;
				}
			} else {
				colorCount = Byte.toUnsignedInt(rx[offset + 94]);
			}

			frameMemorySize = 0x8000;

			// AdminTool: generation == 2 => framePointSize = 9, otherwise 7
			// but this is incorrect, the following code is based on the encode_frame routine
			if(generation == 2 || colorFeatures == 5) {
				framePointSize = 9;
			} else {
				framePointSize = 7;
			}
		} catch(CancellationException | InterruptedException | ExecutionException e) {
			throw new TimeoutException("Failed to initialize laser: timeout");
		}
	}

	public byte[] receiveBlock(int address, int length) throws IOException, TimeoutException {
		byte[] out = new byte[length];
		receiveBlock(address, length, out, 0, 2);
		return out;
	}

	public void receiveBlock(int address, int length, byte[] out, int offset, int timeout)
			throws IOException, TimeoutException {
		byte[] buf = new byte[28];

		int len = length;
		int addr = address;
		int ptr = offset;
		int timeoutctr = timeout;
		while(len > 0) {
			int size = len;
			if(size > 1024) {
				size = 1024;
			}

			Endianess.set16bitLE(buf, 12, (short) 1);
			buf[14] = 0x0C;
			Endianess.set32bitLE(buf, 20, addr);
			Endianess.set16bitLE(buf, 24, (short) size);

			try {
				byte[] rx = send(buf, buf.length, FLAG_CRYPT_RSP | FLAG_SCRAMBLE_RSP | FLAG_CRYPT_PLD |
						FLAG_SCRAMBLE_PLD | FLAG_CRYPT_HDR | FLAG_SCRAMBLE_HDR).get();

				if(rx.length != size + 32) {
					throw new IOException("Invalid receive size, expected " + (size + 32) +
							", got " + rx.length);
				}

				System.arraycopy(rx, 32, out, ptr, size);

				ptr += size;
				len -= size;
				addr += size;
				timeoutctr = timeout;
			} catch(CancellationException e) {
				if(timeoutctr == 0) {
					throw new TimeoutException("Failed to retrieve block: timeout");
				}
				timeoutctr--;
			} catch(InterruptedException | ExecutionException e) {
				throw new TimeoutException("Failed to retrieve block: timeout");
			}
		}
	}

	public ConfigStatus checkConfigBlock(byte[] data) {
		boolean free = true;
		int sum = 0;

		for(int i = 1; i < 511; i++) {
			int word = Endianess.get32bitLE(data, i * 4);
			if(word == -1) {
				free = false;
			}
			sum += word;
		}

		if(Endianess.get32bitLE(data, 0) == sum && ~Endianess.get32bitLE(data, 511 * 4) == sum) {
			return ConfigStatus.OK;
		} else if(free && Endianess.get32bitLE(data, 0) == -1 && Endianess.get32bitLE(data, 511 * 4) == -1) {
			return ConfigStatus.EMPTY;
		} else {
			return ConfigStatus.BAD;
		}
	}

	public void configure() throws IOException {
		byte[] configA = receiveBlock(0x8020000, 2048);
		byte[] configB = receiveBlock(0x807F800, 2048);

		if(Arrays.equals(configA, configB)) {
			configAddress = 0x8020000;
			System.arraycopy(configA, 0, config, 0, config.length);
		} else {
			ConfigStatus statusA = checkConfigBlock(configA);
			ConfigStatus statusB = checkConfigBlock(configB);
			if(statusA != ConfigStatus.OK && statusB == ConfigStatus.OK) {
				configAddress = 0x807F800;
				System.arraycopy(configB, 0, config, 0, config.length);
			} else if(statusA != ConfigStatus.OK) {
				throw new IOException("Corrupt configuration memory");
			}
		}

		if((config[64] & 1) != 0) {
			System.arraycopy(config, 80, mac, 0, mac.length);
		}

		byte[] txbuf = new byte[500];
		Endianess.set16bitLE(txbuf, 12, (short) 1);
		txbuf[14] = 0x12;
		Endianess.set16bitLE(txbuf, 22, (short) 1);
		Endianess.set32bitLE(txbuf, 24, 520 + 2 * frameMemorySize + 64 + memoryAddress);
		Endianess.set32bitLE(txbuf, 28, 2 * frameMemorySize + 64 + memoryAddress);
		Endianess.set16bitLE(txbuf, 32, (short) 516);
		Endianess.set16bitLE(txbuf, 34, (short) 513);
		Endianess.set16bitLE(txbuf, 36, (short) (generation == 2 ? 254 : 0));
		Endianess.set16bitLE(txbuf, 42, (short) 2);
		Endianess.set16bitLE(txbuf, 44, (short) 468);
		Endianess.set32bitLE(txbuf, 52, 0x8B);
		send(txbuf, txbuf.length, FLAG_CRYPT_RSP | FLAG_SCRAMBLE_RSP | FLAG_CRYPT_PLD | FLAG_SCRAMBLE_PLD |
				FLAG_CRYPT_HDR | FLAG_SCRAMBLE_HDR | FLAG_IGNORE_RSP);
	}

	public void sendNop() throws IOException {
		byte[] buf = new byte[36];
		int fragmentSeq = 0;
		int fragmentCnt = 1;
		int unknown = 2;

		buf[14] = 0x1A;
		Endianess.set16bitLE(buf, 12, (short) (1 | (unknown << 2)));
		Endianess.set32bitLE(buf, 20, (fragmentCnt << 16) | ((fragmentSeq & 0x1F) << 11) | (packetId & 0x7FF));

		send(buf, buf.length, FLAG_SCRAMBLE_HDR | FLAG_CRYPT_HDR | FLAG_SCRAMBLE_PLD | FLAG_SCRAMBLE_RSP |
				FLAG_IGNORE_RSP);
	}

	int nextFrameId() {
		frameId = (frameId + 1) & 0x7FFFFF;
		return frameId++;
	}

	public void sendFrame(List<Point> points, int time) throws IOException {
		Frame frame = new Frame(this, points, time);
		sendFrame(frame);
	}

	public void sendFrame(Frame frame) throws IOException {
		byte[] bitstream = frame.getBitstream();

		int fragmentCnt = 1;
		int fragmentSeq = 0;
		int fragmentOffset = 0;

		int id;
		synchronized(framelock) {
			packetId = (packetId + 1) & 0x7FF;
			id = packetId;
		}

		int fragmentSize = bitstream.length + 8;
		if((fragmentSize & 3) != 0) {
			fragmentSize += 4 - (fragmentSize & 3);
		}

		byte[] buf = new byte[fragmentSize + 36];

		int unknown = 2;
		Endianess.set16bitLE(buf, 12, (short) (1 | (unknown << 2)));
		buf[14] = 0x1A;
		Endianess.set32bitLE(buf, 20, (fragmentCnt << 16) | ((fragmentSeq & 0x1F) << 11) | (id & 0x7FF));
		Endianess.set32bitLE(buf, 24, (fragmentSize << 20) | (fragmentOffset & 0xFFFF));
		Endianess.set32bitLE(buf, 36, (frame.getId() << 8) | (frame.isCompressed() ? 0x10 : 0));
		Endianess.set16bitLE(buf, 40, (short) (timeInterval / frame.getTime()));
		Endianess.set16bitLE(buf, 42, (short) frame.getPointCount());
		System.arraycopy(bitstream, 0, buf, 44, bitstream.length);

		send(buf, buf.length, FLAG_SCRAMBLE_HDR | FLAG_CRYPT_HDR | FLAG_SCRAMBLE_PLD | FLAG_SCRAMBLE_RSP |
				FLAG_IGNORE_RSP);
	}

	public String getDecodedIDString() {
		byte[] bytes = new byte[4];
		char[] chars = new char[8];
		for(int i = 0; i < 2; i++) {
			Endianess.set32bitLE(bytes, 0, interfaceId[i + 1]);
			for(int j = 0; j < 4; j++) {
				int b = Byte.toUnsignedInt(bytes[j]);
				if(b < 0x20 || b >= 0x7F) {
					b = '.';
				}
				chars[i * 4 + j] = (char) b;
			}
		}
		return String.format("%08X:%02X:%s", interfaceId[0], interfaceId[1] & 0xFF,
				new String(chars, 1, chars.length - 1));
	}

	public String getMACAddressString() {
		return String.format("%02X:%02X:%02X:%02X:%02X:%02X", Byte.toUnsignedInt(mac[0]),
				Byte.toUnsignedInt(mac[1]), Byte.toUnsignedInt(mac[2]), Byte.toUnsignedInt(mac[3]),
				Byte.toUnsignedInt(mac[4]), Byte.toUnsignedInt(mac[5]));
	}

	public String getInfo() {

		StringBuilder buf = new StringBuilder();
		buf.append(String.format("REV:   %d\n", revision));
		buf.append(String.format("FW:    %d\n", firmware));
		buf.append(String.format("GEN:   %d\n", generation));
		buf.append(String.format("COLOR: %d (%d CHANNELS)\n", colorFeatures, colorCount));
		buf.append(String.format("TIME:  %d\n", timeInterval));
		buf.append(String.format("ID     %08X:%08X:%08X (%08X:%08X:%08X, %s)\n", interfaceId[0] ^ 0xD49A3433,
				interfaceId[1] ^ 0xD49A3433, interfaceId[2] ^ 0xD49A3433, interfaceId[0],
				interfaceId[1], interfaceId[2], getDecodedIDString()));
		buf.append(String.format("CRYPT: %08X %08X %08X %08X\n", cryptKey[0], cryptKey[1], cryptKey[2],
				cryptKey[3]));
		buf.append(String.format("ADDR:  %08X\n", memoryAddress));
		buf.append(String.format("CFG:   %08X\n", configAddress));
		buf.append(String.format("MAC:   %s\n", getMACAddressString()));
		buf.append(String.format("STATE: %d", status));
		return buf.toString();
	}
}
