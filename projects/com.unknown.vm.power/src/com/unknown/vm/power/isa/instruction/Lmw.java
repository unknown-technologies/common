package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Lmw extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int d;

	public Lmw(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		d = insn.D.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra) + d;
		for(int r = rt; r <= 31; r++) {
			state.setGPR(r, state.getMemory().getI32(ea));
			ea += 4;
		}
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lmw", "r" + rt, d + "(" + r0(ra) + ")" };
	}
}
