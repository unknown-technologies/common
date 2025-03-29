package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 69
public class Addme extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final boolean oe;
	private final boolean rc;

	public Addme(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		oe = insn.OE.getBit();
		rc = insn.Rc.getBit();
	}

	@Override
	public void execute(PowerState state) {
		long a = state.getGPR(ra);
		long t = a - 1 + (state.getCA() ? 1 : 0);
		state.setGPR(rt, t);
		state.setCA(addCA(a, -1, state.getCA(), state.ppc64));
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
		return new String[] { "addme" + add, "r" + rt, "r" + ra };
	}
}
