package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.Cr;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Mcrf extends PowerInstruction {
	private final int bf;
	private final int bfa;
	private final Cr src;
	private final Cr dst;

	public Mcrf(long pc, InstructionFormat insn) {
		super(pc, insn);
		bf = insn.XL_BF.get();
		bfa = insn.XL_BFA.get();
		src = new Cr(bfa);
		dst = new Cr(bf);
	}

	@Override
	protected void execute(PowerState state) {
		int field = src.get(state.cr);
		state.cr = dst.set(state.cr, field);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "mcrf", "cr" + bf, "cr" + bfa };
	}

}
