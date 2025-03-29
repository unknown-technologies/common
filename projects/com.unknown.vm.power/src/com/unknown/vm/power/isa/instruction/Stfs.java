package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Stfs extends PowerInstruction {
	private final int frs;
	private final int ra;
	private final int d;

	public Stfs(long pc, InstructionFormat insn) {
		super(pc, insn);
		frs = insn.FRS.get();
		ra = insn.RA.get();
		d = insn.D.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra) + d;
		double val = state.getFPRf(frs);
		int bits = Float.floatToRawIntBits((float) val);
		state.getMemory().setI32(ea, bits);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "stfs", "f" + frs, d + "(" + r0(ra) + ")" };
	}
}
