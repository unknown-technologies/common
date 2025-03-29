package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Lbzux extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int rb;

	public Lbzux(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR(ra) + state.getGPR(rb);
		byte data = state.getMemory().getI8(ea);
		long t = Byte.toUnsignedLong(data);
		state.setGPR(rt, t);
		state.setGPR(ra, ea);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lbzux", "r" + rt, "r" + ra, "r" + rb };
	}
}
