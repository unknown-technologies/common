package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 778
public class Lwarx extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int rb;
	private final boolean eh;

	public Lwarx(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
		eh = insn.EH.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		long b = state.getGPR0(ra);
		long ea = b + state.getGPR(rb);
		int data = state.getMemory().getI32(ea);
		long t = Integer.toUnsignedLong(data);
		state.setGPR(rt, t);
		// FIXME: implement reservation
	}

	@Override
	protected String[] disassemble() {
		if(eh) {
			return new String[] { "lwarx", "r" + rt, r0(ra), "r" + rb, "1" };
		} else {
			return new String[] { "lwarx", "r" + rt, r0(ra), "r" + rb };
		}
	}
}
