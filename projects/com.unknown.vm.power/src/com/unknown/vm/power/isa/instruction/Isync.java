package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Isync extends PowerInstruction {
	public Isync(long pc, InstructionFormat insn) {
		super(pc, insn);
	}

	@Override
	protected void execute(PowerState state) {
		// TODO: implement?
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "isync" };
	}
}
