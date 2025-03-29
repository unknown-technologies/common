package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;
import com.unknown.vm.power.isa.Rotate;

// Manual: page 92
public class Rlwnm extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int rb;
	private final int mb;
	private final int me;
	private final boolean rc;

	private final long mask;

	public Rlwnm(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
		mb = insn.MB.get();
		me = insn.ME.get();
		rc = insn.Rc.getBit();

		mask = Rotate.mask(32 + mb, 32 + me);
	}

	@Override
	protected void execute(PowerState state) {
		int s = (int) state.getGPR(rs);
		int b = (int) state.getGPR(rb) & 0x1f;
		int r = Rotate.rotl(s, b);
		state.setGPR(ra, r & mask);
		if(rc) {
			state.setCR0(r & mask);
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		if(mb == 0 && me == 31) {
			return new String[] { "rotlw" + dot, "r" + ra, "r" + rs, "r" + rb };
		} else {
			return new String[] { "rlwnm" + dot, "r" + ra, "r" + rs, "r" + rb, Integer.toString(mb),
					Integer.toString(me) };
		}
	}
}
