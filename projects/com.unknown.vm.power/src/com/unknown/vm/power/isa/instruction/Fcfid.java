package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Fcfid extends PowerInstruction {
	private final int frt;
	private final int frb;
	private boolean rc;

	public Fcfid(long pc, InstructionFormat insn) {
		super(pc, insn);
		frt = insn.FRT.get();
		frb = insn.FRB.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		long val = state.getFPR(frb);
		state.setFPR(frt, (double) val);
		state.updateFPSCR((double) val);
		if(rc) {
			throw new AssertionError("not implemented");
		}
		// TODO: implement FPSCR_{FPRF} and other flags/registers
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "fcfid" + dot, "f" + frt, "f" + frb };
	}
}
