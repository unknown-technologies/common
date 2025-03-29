package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Stfsux extends PowerInstruction {
	private final int frs;
	private final int ra;
	private final int rb;

	public Stfsux(long pc, InstructionFormat insn) {
		super(pc, insn);
		frs = insn.FRS.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR(ra) + state.getGPR(rb);
		double val = state.getFPRf(frs);
		int bits = Float.floatToRawIntBits((float) val);
		state.getMemory().setI32(ea, bits);
		state.setGPR(ra, ea);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "stfsux", "f" + frs, "r" + ra, "r" + rb };
	}
}
