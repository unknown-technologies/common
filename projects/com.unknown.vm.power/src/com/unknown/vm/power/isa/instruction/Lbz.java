package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Lbz extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int d;

	public Lbz(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		d = insn.D.get();
	}

	@Override
	protected void execute(PowerState state) {
		long b = state.getGPR0(ra);
		long ea = b + d;
		byte data = state.getMemory().getI8(ea);
		long t = Byte.toUnsignedLong(data);
		state.setGPR(rt, t);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lbz", "r" + rt, d + "(" + r0(ra) + ")" };
	}
}
