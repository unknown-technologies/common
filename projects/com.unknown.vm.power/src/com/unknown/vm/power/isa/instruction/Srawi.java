package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;
import com.unknown.vm.power.isa.Rotate;

// Manual: page 98
public class Srawi extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int sh;
	private final boolean rc;
	private final int mask;

	public Srawi(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		sh = insn.SH.get();
		rc = insn.Rc.getBit();
		mask = (int) Rotate.mask(32 + (32 - sh), 63);
	}

	@Override
	protected void execute(PowerState state) {
		int s = (int) state.getGPR(rs);
		int r = s >> sh;
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
		return new String[] { "srawi" + dot, "r" + ra, "r" + rs, Integer.toString(sh) };
	}
}
