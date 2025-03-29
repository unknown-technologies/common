package com.unknown.vm.power.isa.instruction;

import com.unknown.math.LongMultiplication;
import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Mulhwu extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int rb;
	private final boolean rc;

	public Mulhwu(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		rb = insn.RB.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		long prodh = LongMultiplication.mulhwu((int) state.getGPR(ra), (int) state.getGPR(rb));
		state.setGPR(rt, prodh);
		if(rc) {
			state.setCR0(prodh);
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "mulhwu" + dot, "r" + rt, "r" + ra, "r" + rb };
	}

}
