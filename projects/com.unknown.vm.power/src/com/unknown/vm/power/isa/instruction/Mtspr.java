package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 104
public class Mtspr extends PowerInstruction {
	private int rs;
	private int spr;

	public Mtspr(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		spr = (insn.spr.get() & 0x1f) << 5 | (insn.spr.get() >> 5) & 0x1f;
	}

	@Override
	protected void execute(PowerState state) {
		long val = state.getGPR(rs);
		switch(spr) {
		case 1:
			state.xer = val;
			break;
		case 8:
			state.lr = val;
			break;
		case 9:
			state.ctr = val;
			break;
		case 256:
			state.vrsave = (int) val;
			break;
		default:
			throw new RuntimeException("not implemented: " + spr);
		}
	}

	@Override
	protected String[] disassemble() {
		switch(spr) {
		case 1:
			return new String[] { "mtxer", "r" + rs };
		case 8:
			return new String[] { "mtlr", "r" + rs };
		case 9:
			return new String[] { "mtctr", "r" + rs };
		default:
			return new String[] { "mtspr", "r" + rs, Integer.toString(spr) };
		}
	}
}
