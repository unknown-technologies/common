package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Xoris extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int ui;

	public Xoris(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		ui = insn.UI.get() << 16;
	}

	@Override
	protected void execute(PowerState state) {
		long s = state.getGPR(rs);
		long result = s ^ ui;
		state.setGPR(ra, result);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "xoris", "r" + ra, "r" + rs, Integer.toString(ui >>> 16) };
	}
}
