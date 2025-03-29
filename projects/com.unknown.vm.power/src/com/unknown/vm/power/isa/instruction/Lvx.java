package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;
import com.unknown.vm.register.Vector128;

public class Lvx extends PowerInstruction {
	private final int vrt;
	private final int ra;
	private final int rb;

	public Lvx(long pc, InstructionFormat insn) {
		super(pc, insn);
		vrt = insn.VRT.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long b = state.getGPR0(ra);
		long ea = b + state.getGPR(rb);
		Vector128 vec = state.getVSR(vrt);
		long ea1 = ea & 0xFFFF_FFFF_FFFF_FFF0L;
		long ea2 = ea1 | 8;
		vec.setI64(0, state.getMemory().getI64(ea1));
		vec.setI64(1, state.getMemory().getI64(ea2));
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lvx", "v" + vrt, r0(ra), "r" + rb };
	}
}
