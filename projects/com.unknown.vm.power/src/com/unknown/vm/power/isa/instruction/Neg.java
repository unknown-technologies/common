package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 67
public class Neg extends PowerInstruction {
	private final int ra;
	private final int rt;
	private final boolean oe;
	private final boolean rc;

	public Neg(long pc, InstructionFormat insn) {
		super(pc, insn);
		ra = insn.RA.get();
		rt = insn.RT.get();
		oe = insn.OE.getBit();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		long a = state.getGPR(ra);
		long t = -a;
		state.setGPR(rt, t);
		if(oe) {
			throw new RuntimeException("OE not implemented");
		}
		if(rc) {
			state.setCR0(t);
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
		return new String[] { "neg" + add, "r" + rt, "r" + ra };
	}
}
