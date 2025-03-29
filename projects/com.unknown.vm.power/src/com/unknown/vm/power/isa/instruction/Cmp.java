package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.Cr;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Cmp extends PowerInstruction {
	private final int bf;
	private final boolean l;
	private final int ra;
	private final int rb;
	private final Cr cr;

	public Cmp(long pc, InstructionFormat insn) {
		super(pc, insn);
		bf = insn.BF.get() >>> 2;
		l = insn.L.getBit();
		ra = insn.RA.get();
		rb = insn.RB.get();
		cr = new Cr(bf);
	}

	@Override
	protected void execute(PowerState state) {
		long a;
		long b;
		int c;
		if(!l) {
			a = (int) state.getGPR(ra);
			b = (int) state.getGPR(rb);
		} else {
			a = state.getGPR(ra);
			b = state.getGPR(rb);
		}
		if(a < b) {
			c = 0b100;
		} else if(a > b) {
			c = 0b010;
		} else {
			c = 0b001;
		}
		int so = state.getSO() ? 1 : 0;
		state.cr = cr.set(state.cr, c << 1 | so);
	}

	@Override
	protected String[] disassemble() {
		if(!l) {
			if(bf == 0) {
				return new String[] { "cmpw", "r" + ra, "r" + rb };
			} else {
				return new String[] { "cmpw", "cr" + Integer.toString(bf), "r" + ra, "r" + rb };
			}
		} else {
			if(bf == 0) {
				return new String[] { "cmpd", "r" + ra, "r" + rb };
			} else {
				return new String[] { "cmpd", "cr" + Integer.toString(bf), "r" + ra, "r" + rb };
			}
		}
	}
}
