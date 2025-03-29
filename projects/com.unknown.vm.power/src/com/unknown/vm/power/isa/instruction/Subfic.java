package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 68
public class Subfic extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int si;

	public Subfic(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		si = insn.SI.get();
	}

	@Override
	public void execute(PowerState state) {
		long a = state.getGPR(ra);
		long t = si - a;
		state.setCA(subCA(si, a, state.ppc64));
		state.setGPR(rt, t);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "subfic", "r" + rt, "r" + ra, Integer.toString(si) };
	}
}
