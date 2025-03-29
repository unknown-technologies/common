package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Stfd extends PowerInstruction {
	private final int frs;
	private final int ra;
	private final int d;

	public Stfd(long pc, InstructionFormat insn) {
		super(pc, insn);
		frs = insn.FRS.get();
		ra = insn.RA.get();
		d = insn.D.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra) + d;
		long val = state.getFPR(frs);
		state.getMemory().setI64(ea, val);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "stfd", "f" + frs, d + "(" + r0(ra) + ")" };
	}
}
