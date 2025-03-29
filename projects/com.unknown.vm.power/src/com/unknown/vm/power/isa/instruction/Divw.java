package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Divw extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int rb;
	private final boolean oe;
	private final boolean rc;

	public Divw(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
		oe = insn.OE.getBit();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		int dividend = (int) state.getGPR(ra);
		int divisor = (int) state.getGPR(rb);
		int result = dividend / divisor;
		state.setGPR(rt, result);
		if(oe) {
			throw new AssertionError("not implemented");
		}
		if(rc) {
			state.setCR0(result);
		}
	}

	@Override
	protected String[] disassemble() {
		StringBuilder add = new StringBuilder(2);
		if(oe) {
			add.append('o');
		}
		if(rc) {
			add.append('.');
		}
		return new String[] { "divw" + add, "r" + rt, "r" + ra, "r" + rb };
	}
}
