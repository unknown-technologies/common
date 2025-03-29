package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Lhbrx extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int rb;

	public Lhbrx(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long a = state.getGPR0(ra);
		long b = state.getGPR(rb);
		long ea = a + b;
		short data = state.getMemory().getI16(ea);
		long t = Short.toUnsignedLong(Short.reverseBytes(data));
		state.setGPR(rt, t);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lhbrx", "r" + rt, r0(ra), "r" + rb };
	}
}
