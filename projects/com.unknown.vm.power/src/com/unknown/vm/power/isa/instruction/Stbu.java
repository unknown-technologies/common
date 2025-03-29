package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 54
public class Stbu extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int d;

	public Stbu(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		d = insn.D.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR(ra) + d;
		byte s = (byte) state.getGPR(rs);
		state.getMemory().setI8(ea, s);
		state.setGPR(ra, ea);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "stbu", "r" + rs, d + "(r" + ra + ")" };
	}
}
