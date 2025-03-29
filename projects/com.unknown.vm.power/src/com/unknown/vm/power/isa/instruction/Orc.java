package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 85
public class Orc extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int rb;
	private final boolean rc;

	public Orc(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		long s = state.getGPR(rs);
		long b = ~state.getGPR(rb);
		long a = s | b;
		state.setGPR(ra, a);
		if(rc) {
			state.setCR0(a);
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "orc" + dot, "r" + ra, "r" + rs, "r" + rb };
	}
}
