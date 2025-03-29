package com.unknown.vm.power.isa.instruction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Fadd extends PowerInstruction {
	private final int frt;
	private final int fra;
	private final int frb;
	private final boolean rc;

	public Fadd(long pc, InstructionFormat insn) {
		super(pc, insn);
		frt = insn.FRT.get();
		fra = insn.FRA.get();
		frb = insn.FRB.get();
		rc = insn.Rc.getBit();
	}

	@Override
	protected void execute(PowerState state) {
		double a = state.getFPRf(fra);
		double b = state.getFPRf(frb);
		double t;
		RoundingMode rmode = state.getRoundingMode();
		if(rmode == RoundingMode.HALF_EVEN) {
			t = a + b;
		} else {
			MathContext ctx = new MathContext(MathContext.DECIMAL64.getPrecision(), rmode);
			BigDecimal ba = new BigDecimal(a, ctx);
			BigDecimal bb = new BigDecimal(b, ctx);
			BigDecimal bt = ba.add(bb, ctx);
			t = bt.doubleValue();
		}
		state.setFPR(frt, t);
		state.updateFPSCR(t);
		if(rc) {
			throw new AssertionError("not implemented");
		}
		// TODO: implement float flags
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		return new String[] { "fadd" + dot, "f" + frt, "f" + fra, "f" + frb };
	}

}
