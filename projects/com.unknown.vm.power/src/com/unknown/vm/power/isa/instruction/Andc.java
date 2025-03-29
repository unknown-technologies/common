package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Andc extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int rb;
	private final boolean rc;

	public Andc(long pc, InstructionFormat insn) {
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
		long a = s & b;
		state.setGPR(ra, a);
		if(rc) {
			state.setCR0(a);
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "andc" + dot, "r" + ra, "r" + rs, "r" + rb };
	}

}
