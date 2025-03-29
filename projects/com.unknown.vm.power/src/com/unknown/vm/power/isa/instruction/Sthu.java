package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 55
public class Sthu extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int d;

	public Sthu(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		d = insn.D.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra) + d;
		short s = (short) state.getGPR(rs);
		state.getMemory().setI16(ea, s);
		state.setGPR(ra, ea);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "sthu", "r" + rs, d + "(" + r0(ra) + ")" };
	}
}
