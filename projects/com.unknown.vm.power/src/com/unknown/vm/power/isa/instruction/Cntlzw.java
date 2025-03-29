package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Cntlzw extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final boolean rc;

	public Cntlzw(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		int s = (int) state.getGPR(rs);
		int n = Integer.numberOfLeadingZeros(s);
		state.setGPR(ra, n);
		if(rc) {
			state.setCR0(n);
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "cntlzw" + dot, "r" + ra, "r" + rs };
	}
}
