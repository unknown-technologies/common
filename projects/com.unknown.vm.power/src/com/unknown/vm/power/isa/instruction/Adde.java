package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 67
public class Adde extends PowerInstruction {
	private final int ra;
	private final int rb;
	private final int rt;
	private final boolean oe;
	private final boolean rc;

	public Adde(long pc, InstructionFormat insn) {
		super(pc, insn);
		ra = insn.RA.get();
		rb = insn.RB.get();
		rt = insn.RT.get();
		oe = insn.OE.getBit();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		long a = state.getGPR(ra);
		long b = state.getGPR(rb);
		long t = a + b + (state.getCA() ? 1 : 0);
		state.setCA(addCA(a, b, state.getCA(), state.ppc64));
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
		return new String[] { "adde" + add, "r" + rt, "r" + ra, "r" + rb };
	}
}
