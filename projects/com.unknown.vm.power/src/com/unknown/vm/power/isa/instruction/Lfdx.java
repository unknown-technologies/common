package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Lfdx extends PowerInstruction {
	private final int frt;
	private final int ra;
	private final int rb;

	public Lfdx(long pc, InstructionFormat insn) {
		super(pc, insn);
		frt = insn.FRT.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra) + state.getGPR(rb);
		long val = state.getMemory().getI64(ea);
		state.setFPR(frt, val);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lfdx", "f" + frt, r0(ra), "r" + rb };
	}
}
