package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Lfiwax extends PowerInstruction {
	private final int frt;
	private final int ra;
	private final int rb;

	public Lfiwax(long pc, InstructionFormat insn) {
		super(pc, insn);
		frt = insn.FRT.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra) + state.getGPR(rb);
		int val = state.getMemory().getI32(ea);
		state.setFPR(frt, val);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lfiwax", "f" + frt, r0(ra), "r" + rb };
	}
}
