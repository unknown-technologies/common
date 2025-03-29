package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Fdiv extends PowerInstruction {
	private final int frt;
	private final int fra;
	private final int frb;
	private boolean rc;

	public Fdiv(long pc, InstructionFormat insn) {
		super(pc, insn);
		frt = insn.FRT.get();
		fra = insn.FRA.get();
		frb = insn.FRB.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		double a = state.getFPRf(fra);
		double b = state.getFPRf(frb);
		double q = a / b;
		state.setFPR(frt, q);
		state.updateFPSCR(q);
		if(rc) {
			throw new AssertionError("not implemented");
		}
		// TODO: implement float flags
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "fdiv" + dot, "f" + frt, "f" + fra, "f" + frb };
	}
}
