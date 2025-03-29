package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;
import com.unknown.vm.register.Vector128;

public class Xxlxor extends PowerInstruction {
	private final int xt;
	private final int xa;
	private final int xb;

	public Xxlxor(long pc, InstructionFormat insn) {
		super(pc, insn);
		int t = insn.T.get();
		int a = insn.A.get();
		int b = insn.B.get();
		int ax = insn.AX.get() << 5;
		int bx = insn.BX.get() << 5;
		int tx = insn.TX.get() << 5;
		xt = tx | t;
		xa = ax | a;
		xb = bx | b;
	}

	@Override
	protected void execute(PowerState state) {
		Vector128 a = state.getVSR(xa);
		Vector128 b = state.getVSR(xb);
		Vector128 t = a.xor(b);
		state.setVSR(xt, t);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "xxlxor", "vs" + xt, "vs" + xa, "vs" + xb };
	}
}
