package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Dcbst extends PowerInstruction {
	private final int ra;
	private final int rb;

	public Dcbst(long pc, InstructionFormat insn) {
		super(pc, insn);
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		// TODO: implement?
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "dcbst", r0(ra), "r" + rb };
	}
}
