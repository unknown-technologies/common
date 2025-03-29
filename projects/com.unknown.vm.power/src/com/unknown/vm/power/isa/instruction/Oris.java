package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Oris extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int ui;
	private final int or;

	public Oris(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		ui = insn.UI.get();
		or = ui << 16;
	}

	@Override
	protected void execute(PowerState state) {
		long s = state.getGPR(rs);
		state.setGPR(ra, s | or);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "oris", "r" + ra, "r" + rs, Integer.toString(ui) };
	}
}
