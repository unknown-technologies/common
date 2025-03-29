package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 106
public class Mfspr extends PowerInstruction {
	private int rt;
	private int spr;

	public Mfspr(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		spr = (insn.spr.get() & 0x1f) << 5 | (insn.spr.get() >> 5) & 0x1f;
	}

	@Override
	protected void execute(PowerState state) {
		long val = 0;
		switch(spr) {
		case 1:
			val = state.xer;
			break;
		case 8:
			val = state.lr;
			break;
		case 9:
			val = state.ctr;
			break;
		case 256:
			val = state.vrsave;
			break;
		default:
			throw new RuntimeException("not implemented: " + spr);
		}
		state.setGPR(rt, val);
	}

	@Override
	protected String[] disassemble() {
		switch(spr) {
		case 1:
			return new String[] { "mfxer", "r" + rt };
		case 8:
			return new String[] { "mflr", "r" + rt };
		case 9:
			return new String[] { "mfctr", "r" + rt };
		default:
			return new String[] { "mfspr", "r" + rt, Integer.toString(spr) };
		}
	}
}
