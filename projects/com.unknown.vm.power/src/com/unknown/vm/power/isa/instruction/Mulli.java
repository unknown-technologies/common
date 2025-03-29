package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Mulli extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int si;

	public Mulli(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		si = insn.SI.get();
	}

	@Override
	protected void execute(PowerState state) {
		int a = (int) state.getGPR(ra);
		int prod = a * si;
		state.setGPR(rt, prod);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "mulli", "r" + rt, "r" + ra, Integer.toString(si) };
	}

}
