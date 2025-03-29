package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Lwzux extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int rb;

	public Lwzux(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long b = state.getGPR(ra);
		long ea = b + state.getGPR(rb);
		int data = state.getMemory().getI32(ea);
		long t = Integer.toUnsignedLong(data);
		state.setGPR(rt, t);
		state.setGPR(ra, ea);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lwzux", "r" + rt, "r" + ra, "r" + rb };
	}
}
