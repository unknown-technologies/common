package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Sthbrx extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int rb;

	public Sthbrx(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra) + state.getGPR(rb);
		short s = (short) state.getGPR(rs);
		short r = Short.reverseBytes(s);
		state.getMemory().setI16(ea, r);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "sthbrx", "r" + rs, r0(ra), "r" + rb };
	}
}
