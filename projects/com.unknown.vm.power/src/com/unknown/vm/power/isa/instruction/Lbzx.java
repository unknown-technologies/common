package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Lbzx extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int rb;

	public Lbzx(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long b = state.getGPR0(ra);
		long ea = b + state.getGPR(rb);
		byte data = state.getMemory().getI8(ea);
		long t = Byte.toUnsignedLong(data);
		state.setGPR(rt, t);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lbzx", "r" + rt, r0(ra), "r" + rb };
	}
}
