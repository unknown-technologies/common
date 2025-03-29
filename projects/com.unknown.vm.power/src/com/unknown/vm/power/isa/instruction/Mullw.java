package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Mullw extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int rb;
	private final boolean oe;
	private final boolean rc;

	public Mullw(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
		oe = insn.OE.getBit();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		int a = (int) state.getGPR(ra);
		int b = (int) state.getGPR(rb);
		long t = (long) a * (long) b;
		state.setGPR(rt, t);
		if(oe && (int) t != t) {
			state.setOV();
		}
		if(rc) {
			state.setCR0(t);
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		String o = oe ? "o" : "";
		String add = o + dot;
		return new String[] { "mullw" + add, "r" + rt, "r" + ra, "r" + rb };
	}
}
