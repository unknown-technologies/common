package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 69
public class Subfe extends PowerInstruction {
	private final int ra;
	private final int rb;
	private final int rt;
	private final boolean oe;
	private final boolean rc;

	public Subfe(long pc, InstructionFormat insn) {
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
		long c = state.getCA() ? 0 : 1;
		long t = b - a - c;
		boolean ca = subCA(b, a, state.getCA(), state.ppc64);
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
		return new String[] { "subfe" + add, "r" + rt, "r" + ra, "r" + rb };
	}
}
