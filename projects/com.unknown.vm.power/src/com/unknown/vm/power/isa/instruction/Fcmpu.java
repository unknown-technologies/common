package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.Cr;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Fcmpu extends PowerInstruction {
	private final int bf;
	private final int fra;
	private final int frb;
	private final Cr cr;

	public Fcmpu(long pc, InstructionFormat insn) {
		super(pc, insn);
		bf = insn.X_BF.get();
		fra = insn.FRA.get();
		frb = insn.FRB.get();
		cr = new Cr(bf);
	}

	@Override
	protected void execute(PowerState state) {
		int c;
		double a = state.getFPRf(fra);
		double b = state.getFPRf(frb);
		if(Double.isNaN(a) || Double.isNaN(b)) {
			c = 0b0001;
		} else if(a < b) {
			c = 0b1000;
		} else if(a > b) {
			c = 0b0100;
		} else {
			c = 0b0010;
		}
		state.setFPCC(c);
		state.cr = cr.set(state.cr, c);
		// if(isSNaN(a) || isSNaN(b)) VXSNAN = 1;
		if(Double.isNaN(a) || Double.isNaN(b)) {
			state.setVXSNAN();
		}
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "fcmpu", "cr" + bf, "f" + fra, "f" + frb };
	}
}
