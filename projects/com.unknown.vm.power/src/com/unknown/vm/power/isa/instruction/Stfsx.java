package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Stfsx extends PowerInstruction {
	private final int frs;
	private final int ra;
	private final int rb;

	public Stfsx(long pc, InstructionFormat insn) {
		super(pc, insn);
		frs = insn.FRS.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra) + state.getGPR(rb);
		double val = state.getFPRf(frs);
		int bits = Float.floatToRawIntBits((float) val);
		state.getMemory().setI32(ea, bits);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "stfsx", "f" + frs, r0(ra), "r" + rb };
	}
}
