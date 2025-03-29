package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 70
public class Subfze extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final boolean oe;
	private final boolean rc;

	public Subfze(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		oe = insn.OE.getBit();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		long a = state.getGPR(ra);
		long t = -a - (state.getCA() ? 0 : 1);
		boolean ca = subCA(0, a, state.getCA(), state.ppc64);
		state.setCA(ca);
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
		return new String[] { "subfze" + add, "r" + rt, "r" + ra };
	}
}
