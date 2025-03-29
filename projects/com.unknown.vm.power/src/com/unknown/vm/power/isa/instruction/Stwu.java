package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 56
public class Stwu extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int d;

	public Stwu(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		d = insn.D.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR(ra) + d;
		int s = (int) state.getGPR(rs);
		state.getMemory().setI32(ea, s);
		state.setGPR(ra, ea);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "stwu", "r" + rs, d + "(r" + ra + ")" };
	}
}
