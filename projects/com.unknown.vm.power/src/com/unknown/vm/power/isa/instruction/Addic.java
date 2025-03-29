package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 67
public class Addic extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int si;

	public Addic(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		si = insn.SI.get();
	}

	@Override
	protected void execute(PowerState state) {
		long a = state.getGPR(ra);
		long t = a + si;
		state.setGPR(rt, t);
		state.setCA(addCA(a, si, state.ppc64));
	}

	@Override
	protected String[] disassemble() {
		if(si < 0) {
			return new String[] { "subic", "r" + rt, "r" + ra, Integer.toString(-si) };
		} else {
			return new String[] { "addic", "r" + rt, "r" + ra, Integer.toString(si) };
		}
	}
}
