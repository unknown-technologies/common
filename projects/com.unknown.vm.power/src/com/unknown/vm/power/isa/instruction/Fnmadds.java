package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Fnmadds extends PowerInstruction {
	private final int frt;
	private final int fra;
	private final int frb;
	private final int frc;
	private final boolean rc;

	public Fnmadds(long pc, InstructionFormat insn) {
		super(pc, insn);
		frt = insn.FRT.get();
		fra = insn.FRA.get();
		frb = insn.FRB.get();
		frc = insn.FRC.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		double a = state.getFPRf(fra);
		double b = state.getFPRf(frb);
		double c = state.getFPRf(frc);
		float t = (float) -((a * c) + b);
		state.setFPR(frt, t);
		state.updateFPSCR(t);
		if(rc) {
			throw new AssertionError("not implemented");
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "fnmadds" + dot, "f" + frt, "f" + fra, "f" + frc, "f" + frb };
	}
}
