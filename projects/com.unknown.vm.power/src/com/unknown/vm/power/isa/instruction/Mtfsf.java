package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Mtfsf extends PowerInstruction {
	private final boolean l;
	private final int flm;
	private final boolean w;
	private final int frb;
	private final boolean rc;
	private final long mask;

	public Mtfsf(long pc, InstructionFormat insn) {
		super(pc, insn);
		l = insn.L.getBit();
		flm = insn.XL_BF.get();
		w = insn.W.getBit();
		frb = insn.FRB.get();
		rc = insn.Rc.getBit();

		long m = 0;
		for(int i = 0; i < 8; i++) {
			if((flm & (1 << (7 - i))) != 0) {
				m |= 0xfL << (60 - 4 * i);
			}
		}
		mask = m;
	}

	@Override
	protected void execute(PowerState state) {
		if(l) {
			state.setFPSCR(state.getFPR(frb));
		} else {
			state.setFPSCR(((state.fpscr & mask) | (state.getFPR(frb) & ~mask)));
		}
		if(rc) {
			throw new AssertionError("not implemented");
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		if(!w && !l) {
			return new String[] { "mtfsf" + dot, Integer.toString(flm), "f" + Integer.toString(frb) };
		} else {
			return new String[] { "mtfsf" + dot, Integer.toString(flm), "f" + Integer.toString(frb),
					l ? "1" : "0", w ? "1" : "0" };
		}
	}
}
