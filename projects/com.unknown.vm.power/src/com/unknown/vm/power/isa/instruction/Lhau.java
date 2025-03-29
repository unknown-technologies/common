package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Lhau extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int d;

	public Lhau(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		d = insn.D.get();
	}

	@Override
	protected void execute(PowerState state) {
		long b = state.getGPR(ra);
		long ea = b + d;
		long t = state.getMemory().getI16(ea);
		state.setGPR(rt, t);
		state.setGPR(ra, ea);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lhau", "r" + rt, d + "(r" + ra + ")" };
	}
}
