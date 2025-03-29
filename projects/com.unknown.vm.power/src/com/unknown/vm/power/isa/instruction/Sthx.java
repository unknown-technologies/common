package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 55
public class Sthx extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int rb;

	public Sthx(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra) + state.getGPR(rb);
		short s = (short) state.getGPR(rs);
		state.getMemory().setI16(ea, s);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "sthx", "r" + rs, r0(ra), "r" + rb };
	}
}
