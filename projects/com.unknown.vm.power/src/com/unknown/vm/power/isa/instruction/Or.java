package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 84
public class Or extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int rb;
	private final boolean rc;

	public Or(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		long s = state.getGPR(rs);
		long b = state.getGPR(rb);
		long a = s | b;
		state.setGPR(ra, a);
		if(rc) {
			state.setCR0(a);
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		if(rs == rb) {
			return new String[] { "mr" + dot, "r" + ra, "r" + rs };
		} else {
			return new String[] { "or" + dot, "r" + ra, "r" + rs, "r" + rb };
		}
	}
}
