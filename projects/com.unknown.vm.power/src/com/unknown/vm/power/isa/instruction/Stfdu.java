package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Stfdu extends PowerInstruction {
	private final int frs;
	private final int ra;
	private final int d;

	public Stfdu(long pc, InstructionFormat insn) {
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
		state.setGPR(ra, ea);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "stfdu", "f" + frs, d + "(" + r0(ra) + ")" };
	}
}
