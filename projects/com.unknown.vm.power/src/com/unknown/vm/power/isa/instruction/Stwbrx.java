package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Stwbrx extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int rb;

	public Stwbrx(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra) + state.getGPR(rb);
		int s = (int) state.getGPR(rs);
		int r = Integer.reverseBytes(s);
		state.getMemory().setI32(ea, r);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "stwbrx", "r" + rs, r0(ra), "r" + rb };
	}
}
