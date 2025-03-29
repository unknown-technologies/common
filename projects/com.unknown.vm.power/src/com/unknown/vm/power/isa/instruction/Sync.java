package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Sync extends PowerInstruction {
	private final boolean l;
	private final int e;

	public Sync(long pc, InstructionFormat insn) {
		super(pc, insn);
		l = insn.X_L.getBit();
		e = insn.E.get();
	}

	@Override
	protected void execute(PowerState state) {
		// TODO: implement?
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "sync", l ? "1" : "0", Integer.toString(e) };
	}
}
