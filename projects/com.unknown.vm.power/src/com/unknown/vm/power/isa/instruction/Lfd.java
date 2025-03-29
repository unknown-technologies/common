package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Lfd extends PowerInstruction {
	private final int frt;
	private final int ra;
	private final int d;

	public Lfd(long pc, InstructionFormat insn) {
		super(pc, insn);
		frt = insn.FRT.get();
		ra = insn.RA.get();
		d = insn.D.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra) + d;
		long val = state.getMemory().getI64(ea);
		state.setFPR(frt, val);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lfd", "f" + frt, d + "(" + r0(ra) + ")" };
	}
}
