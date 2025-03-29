package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Dcbt extends PowerInstruction {
	private final int th;
	private final int ra;
	private final int rb;

	public Dcbt(long pc, InstructionFormat insn) {
		super(pc, insn);
		th = insn.TH.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		// TODO: implement?
	}

	@Override
	protected String[] disassemble() {
		if(th == 0) {
			return new String[] { "dcbt", r0(ra), "r" + rb };
		} else {
			return new String[] { "dcbt", r0(ra), "r" + rb, Integer.toString(th) };
		}
	}
}
