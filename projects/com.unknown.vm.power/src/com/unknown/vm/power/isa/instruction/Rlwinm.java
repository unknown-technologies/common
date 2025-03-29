package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;
import com.unknown.vm.power.isa.Rotate;

// Manual: page 91
public class Rlwinm extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int sh;
	private final int mb;
	private final int me;
	private final boolean rc;

	private final long mask;

	public Rlwinm(long pc, InstructionFormat insn) {
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
		long vs = state.getGPR(rs);
		if(sh == 0 && mb == 0) {
			long r = (int) vs & mask;
			state.setGPR(ra, r);
			if(rc) {
				state.setCR0(r);
			}
		} else {
			int s = (int) vs;
			int r = Rotate.rotl(s, sh);
			state.setGPR(ra, r & mask);
			if(rc) {
				state.setCR0(r & mask);
			}
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		if(sh == 0 && mb == 0) {
			int n = 31 - me;
			return new String[] { "clrrwi" + dot, "r" + ra, "r" + rs, Integer.toString(n) };
		} else if(sh == (32 - mb) && me == 31) {
			return new String[] { "srwi" + dot, "r" + ra, "r" + rs, Integer.toString(mb) };
		} else if(mb == 0) {
			int n = me + 1;
			return new String[] { "extlwi" + dot, "r" + ra, "r" + rs, Integer.toString(n),
					Integer.toString(sh) };
		} else {
			return new String[] { "rlwinm" + dot, "r" + ra, "r" + rs, Integer.toString(sh),
					Integer.toString(mb), Integer.toString(me) };
		}
	}
}
