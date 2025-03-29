package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;
import com.unknown.vm.power.isa.Rotate;

// Manual: page 93
public class Rlwimi extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int sh;
	private final int mb;
	private final int me;
	private final boolean rc;

	private final long mask;

	public Rlwimi(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		sh = insn.SH.get();
		mb = insn.MB.get();
		me = insn.ME.get();
		rc = insn.Rc.getBit();

		mask = Rotate.mask(32 + mb, 32 + me);
	}

	@Override
	protected void execute(PowerState state) {
		int s = (int) state.getGPR(rs);
		long a = (int) state.getGPR(ra);
		int r = Rotate.rotl(s, sh);
		long result = (r & mask) | (a & ~mask);
		state.setGPR(ra, result);
		if(rc) {
			state.setCR0(result);
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		if(sh == (32 - mb)) {
			int b = mb;
			int n = me - b + 1;
			return new String[] { "inslwi", "r" + ra, "r" + rs, Integer.toString(n), Integer.toString(b) };
		} else {
			return new String[] { "rlwimi" + dot, "r" + ra, "r" + rs, Integer.toString(sh),
					Integer.toString(mb), Integer.toString(me) };
		}
	}
}
