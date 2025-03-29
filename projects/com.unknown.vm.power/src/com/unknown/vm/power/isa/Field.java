package com.unknown.vm.power.isa;

public class Field {
	public final int from;
	public final int to;
	public final int lo;
	public final int hi;
	public final int mask;
	public final int imask;
	public final InstructionFormat insn;
	public final boolean signed;

	public Field(InstructionFormat insn, int from, int to) {
		this(insn, from, to, false);
	}

	public Field(InstructionFormat insn, int from, int to, boolean signed) {
		if(from > to) {
			throw new IllegalArgumentException("from > to");
		}
		this.from = from;
		this.to = to;
		this.lo = bit(to);
		this.hi = bit(from);
		this.insn = insn;
		this.signed = signed;
		this.mask = mask();
		this.imask = ~mask;
	}

	public Field(int from, int to) {
		this(null, from, to);
	}

	private static int bit(int i) {
		return 31 - i;
	}

	private int mask() {
		int result = 0;
		for(int i = from; i <= to; i++) {
			result |= 1 << bit(i);
		}
		return result;
	}

	public int get(int mw) {
		if(signed) {
			return (mw << (31 - hi)) >> ((31 - hi) + lo);
		} else {
			return (mw >>> lo) & (mask >> lo);
		}
	}

	public int set(int insn, int value) {
		return (insn & imask) | ((value << lo) & mask);
	}

	public int get() {
		if(insn == null) {
			throw new IllegalStateException("no insn set");
		}
		return get(insn.value);
	}

	public void set(int value) {
		if(insn == null) {
			throw new IllegalStateException("no insn set");
		}
		insn.value = set(insn.value, value);
	}

	public boolean getBit() {
		if(insn == null) {
			throw new IllegalStateException("no insn set");
		}
		if(from != to) {
			throw new IllegalStateException("not a single bit");
		}
		return get() != 0;
	}

	public void setBit(boolean value) {
		if(insn == null) {
			throw new IllegalStateException("no insn set");
		}
		if(from != to) {
			throw new IllegalStateException("not a single bit");
		}
		set(value ? 1 : 0);
	}

	public int size() {
		return to - from + 1;
	}

	@Override
	public String toString() {
		return String.format("Field[%d;%d;mask=%08X]", from, to, mask);
	}
}
