package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.Cr;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Cmpi extends PowerInstruction {
	private final int bf;
	private final boolean l;
	private final int ra;
	private final int si;
	private final Cr cr;

	public Cmpi(long pc, InstructionFormat insn) {
		super(pc, insn);
		bf = insn.BF.get() >>> 2;
		l = insn.L.getBit();
		ra = insn.RA.get();
		si = insn.SI.get();
		cr = new Cr(bf);
	}

	@Override
	protected void execute(PowerState state) {
		long a;
		int c;
		if(!l) {
			a = (int) state.getGPR(ra);
		} else {
			a = state.getGPR(ra);
		}
		if(a < si) {
			c = 0b100;
		} else if(a > si) {
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
			if(bf != 0) {
				return new String[] { "cmpwi", "cr" + bf, "r" + ra, Integer.toString(si) };
			} else {
				return new String[] { "cmpwi", "r" + ra, Integer.toString(si) };
			}
		} else {
			if(bf != 0) {
				return new String[] { "cmpdi", "cr" + bf, "r" + ra, Integer.toString(si) };
			} else {
				return new String[] { "cmpdi", "r" + ra, Integer.toString(si) };
			}
		}
	}
}
