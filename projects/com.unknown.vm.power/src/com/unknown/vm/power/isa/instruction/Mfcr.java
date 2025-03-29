package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Mfcr extends PowerInstruction {
	private final int rt;

	public Mfcr(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
	}

	@Override
	protected void execute(PowerState state) {
		state.setGPR(rt, Integer.toUnsignedLong(state.cr));
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "mfcr", "r" + rt };
	}

}
