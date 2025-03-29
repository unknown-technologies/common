package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Extsh extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final boolean rc;

	public Extsh(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		short s = (short) state.getGPR(rs);
		state.setGPR(ra, s);
		if(rc) {
			state.setCR0(s);
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "extsh" + dot, "r" + ra, "r" + rs };
	}

}
