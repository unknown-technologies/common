package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 781
public class Stwcx_ extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int rb;

	public Stwcx_(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra) + state.getGPR(rb);
		int s = (int) state.getGPR(rs);
		// FIXME: implement reservation
		// FIXME: this is just a "hack" to always signal "store performed"
		state.setCR(0, false, false, true, state.getSO());
		state.getMemory().setI32(ea, s);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "stwcx.", "r" + rs, r0(ra), "r" + rb };
	}
}
