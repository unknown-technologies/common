package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Icbi extends PowerInstruction {
	private final int ra;
	private final int rb;

	public Icbi(long pc, InstructionFormat insn) {
		super(pc, insn);
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra) + state.getGPR(rb);
		state.icbi(ea);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "icbi", r0(ra), "r" + rb };
	}
}
