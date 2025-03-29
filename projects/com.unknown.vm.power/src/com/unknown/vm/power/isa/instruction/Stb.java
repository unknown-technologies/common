package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 54
public class Stb extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int d;

	public Stb(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		d = insn.D.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra) + d;
		byte s = (byte) state.getGPR(rs);
		state.getMemory().setI8(ea, s);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "stb", "r" + rs, d + "(" + r0(ra) + ")" };
	}
}
