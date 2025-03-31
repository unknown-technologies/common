package com.unknown.net.telnet;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.unknown.util.encoding.EBCDIC;

public class TN3270 extends Telnet {
	// @formatter:off
	public final static int TERM_UNKNOWN = -1;
	public final static int TERM_ANSI = 0;
	public final static int TERM_DYNAMIC = 1;
	public final static int TERM_3277 = 2;
	public final static int TERM_3270 = 3;
	public final static int TERM_3178 = 4;
	public final static int TERM_3278 = 5;
	public final static int TERM_3179 = 6;
	public final static int TERM_3180 = 7;
	public final static int TERM_3287 = 8;
	public final static int TERM_3279 = 9;

	public final static int TYPE_DISPLAY = 0;
	public final static int TYPE_PRINTERKEYBOARD = 1;
	public final static int TYPE_PRINTER = 2;

	/* 3270 local commands, (CCWs) */
	public final static byte L3270_EAU = (byte) 0x0F;/* Erase All Unprotected */
	public final static byte L3270_EW = (byte) 0x05;/* Erase/Write */
	public final static byte L3270_EWA = (byte) 0x0D;/* Erase/Write Alternate */
	public final static byte L3270_RB = (byte) 0x02;/* Read Buffer */
	public final static byte L3270_RM = (byte) 0x06;/* Read Modified */
	public final static byte L3270_WRT = (byte) 0x01;/* Write */
	public final static byte L3270_WSF = (byte) 0x11;/*Write Structured Field */

	public final static byte L3270_NOP = (byte) 0x03;/* No Operation */
	public final static byte L3270_SELRM = (byte) 0x0B;/* Select RM */
	public final static byte L3270_SELRB = (byte) 0x1B;/* Select RB */
	public final static byte L3270_SELRMP = (byte) 0x2B;/* Select RMP */
	public final static byte L3270_SELRBP = (byte) 0x3B;/* Select RBP */
	public final static byte L3270_SELWRT = (byte) 0x4B;/* Select WRT */
	public final static byte L3270_SENSE = (byte) 0x04;/* Sense */
	public final static byte L3270_SENSEID = (byte) 0xE4;/* Sense ID */

	/* 3270 remote commands */
	public final static byte R3270_EAU = (byte) 0x6F;/* Erase All Unprotected */
	public final static byte R3270_EW = (byte) 0xF5;/* Erase/Write */
	public final static byte R3270_EWA = (byte) 0x7E;/* Erase/Write Alternate */
	public final static byte R3270_RB = (byte) 0xF2;/* Read Buffer */
	public final static byte R3270_RM = (byte) 0xF6;/* Read Modified */
	public final static byte R3270_RMA = (byte) 0x6E;/* Read Modified All */
	public final static byte R3270_WRT = (byte) 0xF1;/* Write */
	public final static byte R3270_WSF = (byte) 0xF3;/* Write Structured Field */

	/* 3270 orders */
	public final static byte O3270_SBA = (byte) 0x11;/* Set Buffer Address */
	public final static byte O3270_SF = (byte) 0x1D;/* Start Field */
	public final static byte O3270_SFE = (byte) 0x29;/* Start Field Extended */
	public final static byte O3270_SA = (byte) 0x28;/* Set Attribute */
	public final static byte O3270_IC = (byte) 0x13;/* Insert Cursor */
	public final static byte O3270_MF = (byte) 0x2C;/* Modify Field */
	public final static byte O3270_PT = (byte) 0x05;/* Program Tab */
	public final static byte O3270_RA = (byte) 0x3C;/* Repeat to Address */
	public final static byte O3270_EUA = (byte) 0x12;/* Erase Unprotected to Addr */
	public final static byte O3270_GE = (byte) 0x08;/* Graphic Escape */

	/* Inbound structured fields */
	public final static byte SF3270_AID = (byte) 0x88;/*Aid value of inbound SF */
	public final static byte SF3270_3270DS = (byte) 0x80;/* SFID of 3270 datastream SF */
	// @formatter:on

	public final static int BUFLEN_3270 = 65536;
	public final static int BUFLEN_1052 = 150;

	public final static byte[] WILL_NAWS = { WILL, NAWS };

	private int type = TYPE_DISPLAY;
	private int terminal = TERM_ANSI;
	private int model = 0;
	private String terminalName = "ANSI";
	private boolean extendedAttributes = false;
	private String luname = null;

	private boolean negotiated = false;

	private final static Map<String, Integer> TERM_TYPES;
	private final static Map<String, Size> TERM_SIZES;

	private EBCDIC encoding = new EBCDIC();

	private int width = 80;
	private int height = 24;

	static {
		TERM_TYPES = new HashMap<>();
		TERM_TYPES.put("DYNAMIC", TERM_DYNAMIC);
		TERM_TYPES.put("3277", TERM_3277);
		TERM_TYPES.put("3270", TERM_3270);
		TERM_TYPES.put("3178", TERM_3178);
		TERM_TYPES.put("3278", TERM_3278);
		TERM_TYPES.put("3179", TERM_3179);
		TERM_TYPES.put("3180", TERM_3180);
		TERM_TYPES.put("3287", TERM_3287);
		TERM_TYPES.put("3279", TERM_3279);
	}

	static class Size {
		public final int width;
		public final int height;

		public Size(int width, int height) {
			this.width = width;
			this.height = height;
		}
	}

	static {
		TERM_SIZES = new HashMap<>();
		TERM_SIZES.put("3277-1", new Size(40, 12));
		TERM_SIZES.put("3277-2", new Size(80, 24));
		TERM_SIZES.put("3278-3", new Size(80, 32));
		TERM_SIZES.put("3278-4", new Size(80, 43));
		TERM_SIZES.put("3278-5", new Size(132, 27));
		TERM_SIZES.put("3290", new Size(160, 62));
		// hack
		TERM_SIZES.put("1", new Size(40, 12));
		TERM_SIZES.put("2", new Size(80, 24));
		TERM_SIZES.put("3", new Size(80, 32));
		TERM_SIZES.put("4", new Size(80, 43));
		TERM_SIZES.put("5", new Size(132, 27));
	}

	public TN3270(Telnet tn) {
		super(tn);
	}

	public <A> void start(A attachment, CompletionHandler<Boolean, A> callback) {
		negotiate(attachment, callback);
	}

	public int getType() {
		return type;
	}

	public int getTerminal() {
		return terminal;
	}

	public int getModel() {
		return model;
	}

	public boolean hasExtendedAttributes() {
		return extendedAttributes;
	}

	public String getLUName() {
		return luname;
	}

	@Override
	public boolean isSizeAvailable() {
		// TODO: what about DYNAMIC?
		return true;
	}

	@Override
	public int getTerminalWidth() {
		if(super.isSizeAvailable()) {
			return super.getTerminalWidth();
		} else {
			return width;
		}
	}

	@Override
	public int getTerminalHeight() {
		if(super.isSizeAvailable()) {
			return super.getTerminalHeight();
		} else {
			return height;
		}
	}

	@Override
	public int read(byte[] data) throws IOException {
		int n = super.read(data);
		if(negotiated) {
			byte[] decoded = encoding.decode(data, n);
			System.arraycopy(decoded, 0, data, 0, n);
		}
		return n;
	}

	private <T> void negotiate(T attachment, CompletionHandler<Boolean, T> handler) {
		// perform terminal-type negotiation
		try {
			setOptionRemote(TERMINAL_TYPE, true);
		} catch(Throwable t) {
			handler.failed(t, attachment);
			return;
		}

		waitForRemote(TERMINAL_TYPE, (terminalTypeRemote) -> {
			if(!terminalTypeRemote) {
				handler.completed(false, attachment);
				return;
			}

			// request terminal type
			try {
				subnegotiate(TERMINAL_TYPE, SEND);
			} catch(Throwable t) {
				handler.failed(t, attachment);
				return;
			}
			getCommand((resp) -> {
				Consumer<byte[]> next = (response) -> {
					if((response.length < 3) || (response[0] != SB) ||
							(response[1] != TERMINAL_TYPE) || (response[2] != IS)) {
						handler.completed(false, attachment);
						return;
					}

					String termtype = new String(response, 3, response.length - 3);

					if(!termtype.startsWith("IBM-")) {
						if(termtype.startsWith("ANSI") || termtype.startsWith("XTERM") ||
								termtype.startsWith("DEC-VT")) {
							terminal = TERM_ANSI;
							try {
								setOptionLocal(ECHO_OPTION, false);
							} catch(Throwable t) {
								handler.failed(t, attachment);
								return;
							}
						}
						type = TYPE_PRINTERKEYBOARD;
						model = 0;
						extendedAttributes = false;
						handler.completed(true, attachment);
						return;
					}

					int luindex = termtype.indexOf('@');
					if(luindex != -1) {
						luname = termtype.substring(luindex + 1);
						termtype = termtype.substring(0, luindex);
					}

					String[] info = termtype.split("-");
					if(info.length < 2) {
						handler.completed(false, attachment);
						return;
					}

					Integer termType = TERM_TYPES.get(info[1]);
					if(termType == null) {
						terminal = TERM_UNKNOWN;
					} else {
						terminal = termType;
					}
					model = 2;
					extendedAttributes = false;
					if(terminal == TERM_DYNAMIC) {
						model = 0;
						extendedAttributes = true;
					}
					if(info.length > 2) {
						model = Integer.parseInt(info[2]);
						if((model < 1) || (model > 5)) {
							handler.completed(false, attachment);
							return;
						}
						terminalName = info[1] + "-" + info[2];
						if(info.length > 3) {
							extendedAttributes = info[3].equals("E");
						}
					}
					type = (terminal == TERM_3287) ? TYPE_PRINTER : TYPE_DISPLAY;

					// get terminal screen size
					if(terminal == TERM_DYNAMIC) {
						// setOptionRemote(NAWS, true);
						// use "Query Structured Field" to get screen size
						// FIXME: implement this
					} else {
						Size size = TERM_SIZES.get(terminalName);
						if(size == null) {
							size = TERM_SIZES.get(String.valueOf(model));
						}
						if(size != null) {
							width = size.width;
							height = size.height;
						}
					}

					try {
						setOptionLocal(EOR, true);
						setOptionRemote(EOR, true);
					} catch(Throwable t) {
						handler.failed(t, attachment);
						return;
					}

					waitForLocal(EOR, (eorLocal) -> {
						waitForRemote(EOR, (eorRemote) -> {
							if(!eorLocal || !eorRemote) {
								handler.completed(false, attachment);
								return;
							}

							try {
								setOptionLocal(BINARY, true);
								setOptionRemote(BINARY, true);
							} catch(Throwable t) {
								handler.failed(t, attachment);
								return;
							}
							waitForLocal(BINARY, (binaryLocal) -> {
								waitForRemote(BINARY, (binaryRemote) -> {
									if(!getOptionActiveLocal(BINARY) ||
											!getOptionActiveRemote(
													BINARY)) {
										handler.completed(false, attachment);
										return;
									}

									handler.completed(true, attachment);
								});
							});
						});
					});
				};
				if(resp.length > 2 && resp[0] == SB && resp[1] == NAWS) {
					getCommand(next);
				} else {
					next.accept(resp);
				}
			});
		});
	}

	public void ew(byte[] data) throws IOException {
		write(R3270_EW);
		write(data);
		command(EOR_MARK);
	}

	public void ewa(byte[] data) throws IOException {
		write(R3270_EWA);
		write(data);
		command(EOR_MARK);
	}

	public void wrt(byte[] data) throws IOException {
		write(R3270_WRT);
		write(data);
		command(EOR_MARK);
	}
}
