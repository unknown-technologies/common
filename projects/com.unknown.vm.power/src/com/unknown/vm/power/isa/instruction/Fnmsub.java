package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Fnmsub extends PowerInstruction {
	private final int frt;
	private final int fra;
	private final int frb;
	private final int frc;
	private final boolean rc;

	public Fnmsub(long pc, InstructionFormat insn) {
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
		double t = -Math.fma(a, c, -b); // -((a * c) - b);
		state.setFPR(frt, t);
		state.updateFPSCR(t);
		if(rc) {
			throw new AssertionError("not implemented");
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "fnmsub" + dot, "f" + frt, "f" + fra, "f" + frc, "f" + frb };
	}
}
