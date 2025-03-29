package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Andi extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int ui;

	public Andi(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		ui = insn.UI.get();
	}

	@Override
	protected void execute(PowerState state) {
		long s = state.getGPR(rs);
		long result = s & ui;
		state.setGPR(ra, result);
		state.setCR0(result);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "andi.", "r" + ra, "r" + rs, Integer.toString(ui) };
	}
}
