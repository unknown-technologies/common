package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Lwzx extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int rb;

	public Lwzx(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long b = state.getGPR0(ra);
		long ea = b + state.getGPR(rb);
		int data = state.getMemory().getI32(ea);
		long t = Integer.toUnsignedLong(data);
		state.setGPR(rt, t);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lwzx", "r" + rt, r0(ra), "r" + rb };
	}
}
