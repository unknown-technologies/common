package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Xori extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int ui;

	public Xori(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		ui = insn.UI.get();
	}

	@Override
	protected void execute(PowerState state) {
		long s = state.getGPR(rs);
		long result = s ^ ui;
		state.setGPR(ra, result);
	}

	@Override
	protected String[] disassemble() {
		if(rs == 0 && ra == 0 && ui == 0) {
			return new String[] { "xnop" };
		} else {
			return new String[] { "xori", "r" + ra, "r" + rs, Integer.toString(ui) };
		}
	}
}
