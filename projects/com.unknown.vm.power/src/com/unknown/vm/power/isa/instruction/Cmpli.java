package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.Cr;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Cmpli extends PowerInstruction {
	private final int bf;
	private final boolean l;
	private final int ra;
	private final int ui;
	private final Cr cr;

	public Cmpli(long pc, InstructionFormat insn) {
		super(pc, insn);
		bf = insn.BF.get() >>> 2;
		l = insn.L.getBit();
		ra = insn.RA.get();
		ui = insn.UI.get();
		cr = new Cr(bf);
	}

	@Override
	protected void execute(PowerState state) {
		long a;
		int c;
		if(!l) {
			a = Integer.toUnsignedLong((int) state.getGPR(ra));
		} else {
			a = state.getGPR(ra);
		}
		if(Long.compareUnsigned(a, ui) < 0) {
			c = 0b100;
		} else if(Long.compareUnsigned(a, ui) > 0) {
			c = 0b010;
		} else {
			c = 0b001;
		}
		int so = state.getSO() ? 1 : 0;
		state.cr = cr.set(state.cr, c << 1 | so);
	}

	@Override
	protected String[] disassemble() {
		String uis = Integer.toString(ui);
		if(!l) {
			if(bf == 0) {
				return new String[] { "cmplwi", "r" + ra, uis };
			} else {
				return new String[] { "cmplwi", "cr" + bf, "r" + ra, uis };
			}
		} else {
			if(bf == 0) {
				return new String[] { "cmpldi", "cr" + bf, "r" + ra, uis };
			} else {
				return new String[] { "cmpldi", "r" + ra, uis };
			}
		}
	}
}
