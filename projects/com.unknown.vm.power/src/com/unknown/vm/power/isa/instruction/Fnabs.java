package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Fnabs extends PowerInstruction {
	private final int frt;
	private final int frb;
	private final boolean rc;

	public Fnabs(long pc, InstructionFormat insn) {
		super(pc, insn);
		frt = insn.FRT.get();
		frb = insn.FRB.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		long f = state.getFPR(frb);
		long t = f | 0x8000000000000000L;
		assert t == Double.doubleToRawLongBits(-Math.abs(Double.longBitsToDouble(f)));
		state.setFPR(frt, t);
		if(rc) {
			throw new AssertionError("not implemented");
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "fnabs" + dot, "f" + frt, "f" + frb };
	}
}
