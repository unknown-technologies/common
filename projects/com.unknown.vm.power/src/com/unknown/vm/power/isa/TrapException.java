package com.unknown.vm.power.isa;

public class TrapException extends RuntimeException {
	private static final long serialVersionUID = 6169661017957147380L;

	private long pc;

	public TrapException(long pc) {
		super("Trap at pc=0x" + Long.toUnsignedString(pc, 16));
		this.pc = pc;
	}

	public long getPC() {
		return pc;
	}
}
