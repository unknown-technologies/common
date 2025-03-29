package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;
import com.unknown.vm.power.isa.Trap;
import com.unknown.vm.power.isa.TrapException;

public class Twi extends PowerInstruction {
	private final int to;
	private final int ra;
	private final int si;

	public Twi(long pc, InstructionFormat insn) {
		super(pc, insn);
		to = insn.TO.get();
		ra = insn.RA.get();
		si = insn.SI.get();
	}

	private boolean to(int i) {
		return (to & (1 << (4 - i))) != 0;
	}

	@Override
	protected void execute(PowerState state) {
		int a = (int) state.getGPR(ra);
		boolean trap = false;
		if((a < si) && to(0)) {
			trap = true;
		} else if((a > si) && to(1)) {
			trap = true;
		} else if((a == si) && to(2)) {
			trap = true;
		} else if((Integer.compareUnsigned(a, si) < 0) && to(3)) {
			trap = true;
		} else if((Integer.compareUnsigned(a, si) > 0) && to(4)) {
			trap = true;
		}
		if(trap) {
			throw new TrapException(pc);
		}
	}

	@Override
	protected String[] disassemble() {
		String dec = Trap.decodeTO(to);
		if(dec != null) {
			return new String[] { "tw" + dec + "i", "r" + ra, Integer.toString(si) };
		} else {
			return new String[] { "twi", Integer.toString(to), "r" + ra, Integer.toString(si) };
		}
	}
}
