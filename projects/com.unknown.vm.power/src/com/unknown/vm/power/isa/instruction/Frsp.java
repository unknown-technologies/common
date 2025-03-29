package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Frsp extends PowerInstruction {
	private final int frt;
	private final int frb;
	private final boolean rc;

	public Frsp(long pc, InstructionFormat insn) {
		super(pc, insn);
		frt = insn.FRT.get();
		frb = insn.FRB.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		double b = state.getFPRf(frb);
		state.setFPR(frt, (float) b);
		state.updateFPSCR((float) b);
		// TODO: implement float flags
		if(rc) {
			throw new AssertionError("not implemented");
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "frsp" + dot, "f" + frt, "f" + frb };
	}
}
