package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Stfsu extends PowerInstruction {
	private final int frs;
	private final int ra;
	private final int d;

	public Stfsu(long pc, InstructionFormat insn) {
		super(pc, insn);
		frs = insn.FRS.get();
		ra = insn.RA.get();
		d = insn.D.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR(ra) + d;
		double val = state.getFPRf(frs);
		int bits = Float.floatToRawIntBits((float) val);
		state.getMemory().setI32(ea, bits);
		state.setGPR(ra, ea);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "stfsu", "f" + frs, d + "(r" + ra + ")" };
	}
}
