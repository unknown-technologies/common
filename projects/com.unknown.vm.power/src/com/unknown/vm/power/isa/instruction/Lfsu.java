package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Lfsu extends PowerInstruction {
	private final int frt;
	private final int ra;
	private final int d;

	public Lfsu(long pc, InstructionFormat insn) {
		super(pc, insn);
		frt = insn.FRT.get();
		ra = insn.RA.get();
		d = insn.D.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra) + d;
		int bits = state.getMemory().getI32(ea);
		double val = Float.intBitsToFloat(bits);
		state.setFPR(frt, val);
		state.setGPR(ra, ea);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lfsu", "f" + frt, d + "(" + r0(ra) + ")" };
	}

}
