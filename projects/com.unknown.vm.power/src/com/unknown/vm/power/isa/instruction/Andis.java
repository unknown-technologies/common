package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 82
public class Andis extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int ui;

	public Andis(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		ui = insn.UI.get() << 16;
	}

	@Override
	public void execute(PowerState state) {
		long s = state.getGPR(rs);
		long a = s & ui;
		state.setGPR(ra, a);
		state.setCR0(a);
	}

	@Override
	protected String[] disassemble() {
		String arg = Integer.toString(ui >>> 16);
		return new String[] { "andis.", "r" + ra, "r" + rs, arg };
	}
}
