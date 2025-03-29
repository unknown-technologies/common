package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Lbzu extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int d;

	public Lbzu(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		d = insn.D.get();
	}

	@Override
	protected void execute(PowerState state) {
		long a = state.getGPR(ra);
		long ea = a + d;
		byte data = state.getMemory().getI8(ea);
		long t = Byte.toUnsignedLong(data);
		state.setGPR(rt, t);
		state.setGPR(ra, ea);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lbzu", "r" + rt, d + "(r" + ra + ")" };
	}
}
