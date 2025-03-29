package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Fneg extends PowerInstruction {
	private final int frt;
	private final int frb;
	private final boolean rc;

	public Fneg(long pc, InstructionFormat insn) {
		super(pc, insn);
		frt = insn.FRT.get();
		frb = insn.FRB.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		long f = state.getFPR(frb);
		long neg = f ^ (1L << 63);
		state.setFPR(frt, neg);
		if(rc) {
			throw new AssertionError("not implemented");
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "fneg" + dot, "f" + frt, "f" + frb };
	}
}
