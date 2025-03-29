package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;
import com.unknown.vm.power.isa.Rotate;

// Manual: page 98
public class Sraw extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int rb;
	private final boolean rc;

	public Sraw(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		int s = (int) state.getGPR(rs);
		int b = (int) state.getGPR(rb);
		int r = s >> b;
		int mask = (int) Rotate.mask(32 + (32 - b), 63);
		state.setGPR(ra, r);
		if(rc) {
			state.setCR0(r);
		}
		boolean ca = (s < 0) && ((s & mask) != 0);
		state.setCA(ca);
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "sraw" + dot, "r" + ra, "r" + rs, "r" + rb };
	}
}
