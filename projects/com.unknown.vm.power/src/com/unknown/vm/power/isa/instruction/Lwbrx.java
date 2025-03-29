package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Lwbrx extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int rb;

	public Lwbrx(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long b = state.getGPR0(ra);
		long ea = b + state.getGPR(rb);
		int load_data = state.getMemory().getI32(ea);
		int result = Integer.reverseBytes(load_data);
		state.setGPR(rt, result);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lwbrx", "r" + rt, r0(ra), "r" + rb };
	}
}
