package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;
import com.unknown.vm.register.Vector128;

public class Stvx extends PowerInstruction {
	private final int vrs;
	private final int ra;
	private final int rb;

	public Stvx(long pc, InstructionFormat insn) {
		super(pc, insn);
		vrs = insn.VRS.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long b = state.getGPR0(ra);
		long ea = b + state.getGPR(rb);
		Vector128 vec = state.getVSR(vrs);
		long ea1 = ea & 0xFFFF_FFFF_FFFF_FFF0L;
		long ea2 = ea1 | 8;
		state.getMemory().setI64(ea1, vec.getI64(0));
		state.getMemory().setI64(ea2, vec.getI64(1));
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "stvx", "v" + vrs, r0(ra), "r" + rb };
	}

}
