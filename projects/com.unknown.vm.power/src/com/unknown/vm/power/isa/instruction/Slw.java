package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 97
public class Slw extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int rb;
	private final boolean rc;

	public Slw(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		int n = (int) state.getGPR(rb) & 0x1f;
		int r = (int) state.getGPR(rs) << n;
		state.setGPR(ra, r);
		if(rc) {
			state.setCR0(r);
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "slw" + dot, "r" + ra, "r" + rs, "r" + rb };
	}
}
