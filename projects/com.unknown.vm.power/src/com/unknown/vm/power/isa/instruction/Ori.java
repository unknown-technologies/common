package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Ori extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int ui;

	public Ori(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		ui = insn.UI.get();
	}

	@Override
	protected void execute(PowerState state) {
		long s = state.getGPR(rs);
		state.setGPR(ra, s | ui);
	}

	@Override
	protected String[] disassemble() {
		if(rs == 0 && ra == 0 && ui == 0) {
			return new String[] { "nop" };
		} else {
			return new String[] { "ori", "r" + ra, "r" + rs, Integer.toString(ui) };
		}
	}
}
