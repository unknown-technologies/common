package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Fmul extends PowerInstruction {
	private final int frt;
	private final int fra;
	private final int frc;
	private boolean rc;

	public Fmul(long pc, InstructionFormat insn) {
		super(pc, insn);
		frt = insn.FRT.get();
		fra = insn.FRA.get();
		frc = insn.FRC.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		double a = state.getFPRf(fra);
		double c = state.getFPRf(frc);
		double p = a * c;
		state.setFPR(frt, p);
		state.updateFPSCR(p);
		if(rc) {
			throw new AssertionError("not implemented");
		}
		// TODO: implement float flags
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "fmul" + dot, "f" + frt, "f" + fra, "f" + frc };
	}
}
