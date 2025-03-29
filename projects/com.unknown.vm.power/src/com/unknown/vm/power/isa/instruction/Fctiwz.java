package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.Fpscr;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Fctiwz extends PowerInstruction {
	private final int frt;
	private final int frb;
	private final boolean rc;

	public Fctiwz(long pc, InstructionFormat insn) {
		super(pc, insn);
		frt = insn.FRT.get();
		frb = insn.FRB.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		double src = state.getFPRf(frb);
		int dst;
		if(Double.isNaN(src)) {
			dst = 0x8000_0000;
			state.fpscr |= Fpscr.bit(Fpscr.VXSNAN);
			state.fpscr |= Fpscr.bit(Fpscr.FX);
			state.fpscr |= Fpscr.bit(Fpscr.VX);
		} else if(src > Integer.MAX_VALUE) {
			dst = 0x7FFF_FFFF;
			state.fpscr |= Fpscr.bit(Fpscr.VXCVI);
			state.fpscr |= Fpscr.bit(Fpscr.FX);
		} else if(src < Integer.MIN_VALUE) {
			dst = 0x8000_0000;
			state.fpscr |= Fpscr.bit(Fpscr.VXCVI);
			state.fpscr |= Fpscr.bit(Fpscr.FX);
		} else {
			dst = (int) src;
			if(dst != src) {
				state.setFI();
			}
		}
		state.setFPR(frt, dst);
		if(rc) {
			throw new AssertionError("not implemented");
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "fctiwz" + dot, "f" + frt, "f" + frb };
	}
}
