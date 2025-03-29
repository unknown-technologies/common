package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Mffs extends PowerInstruction {
	private final int frt;
	private final boolean rc;

	public Mffs(long pc, InstructionFormat insn) {
		super(pc, insn);
		frt = insn.FRT.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		state.setFPR(frt, state.fpscr);
		if(rc) {
			throw new AssertionError("not implemented");
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "mffs" + dot, "f" + frt };
	}
}
