package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Lhz extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int d;

	public Lhz(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		d = insn.D.get();
	}

	@Override
	protected void execute(PowerState state) {
		long b = state.getGPR0(ra);
		long ea = b + d;
		short data = state.getMemory().getI16(ea);
		long t = Short.toUnsignedLong(data);
		state.setGPR(rt, t);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lhz", "r" + rt, d + "(" + r0(ra) + ")" };
	}
}
