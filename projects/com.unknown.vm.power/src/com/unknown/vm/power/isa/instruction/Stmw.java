package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Stmw extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int d;

	public Stmw(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		d = insn.D.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra) + d;
		for(int r = rs; r <= 31; r++) {
			state.getMemory().setI32(ea, (int) state.getGPR(r));
			ea += 4;
		}
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "stmw", "r" + rs, d + "(" + r0(ra) + ")" };
	}
}
