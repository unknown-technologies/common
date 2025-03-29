package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Fabs extends PowerInstruction {
	private final int frt;
	private final int frb;
	private final boolean rc;

	public Fabs(long pc, InstructionFormat insn) {
		super(pc, insn);
		frt = insn.FRT.get();
		frb = insn.FRB.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		long b = state.getFPR(frb);
		long t = b & 0x7FFFFFFFFFFFFFFFL;
		assert t == Double.doubleToRawLongBits(Math.abs(Double.longBitsToDouble(b)));
		state.setFPR(frt, t);
		if(rc) {
			throw new AssertionError("not implemented");
		}
		// TODO: implement float flags
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "fabs" + dot, "f" + frt, "f" + frb };
	}

}
