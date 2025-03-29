package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 66
public class Addis extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int si;

	public Addis(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		si = insn.SI.get() << 16;
	}

	@Override
	public void execute(PowerState state) {
		long a = state.getGPR0(ra);
		long t = a + si;
		state.setGPR(rt, t);
	}

	@Override
	protected String[] disassemble() {
		String arg = Integer.toString(si >> 16);
		if(ra == 0) {
			return new String[] { "lis", "r" + rt, arg };
		} else if(si < 0) {
			return new String[] { "subis", "r" + rt, r0(ra), Integer.toString(-(si >> 16)) };
		} else {
			return new String[] { "addis", "r" + rt, r0(ra), arg };
		}
	}
}
