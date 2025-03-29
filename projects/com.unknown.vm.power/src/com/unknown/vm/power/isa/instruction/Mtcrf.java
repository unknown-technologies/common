package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Mtcrf extends PowerInstruction {
	private final int rs;
	private final int fxm;
	private final boolean one;
	private final int mask;

	public Mtcrf(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		fxm = insn.FXM.get();
		one = insn.BIT_11.getBit();
		int m = 0;
		for(int i = 0; i < 8; i++) {
			if((fxm & (1 << (7 - i))) != 0) {
				m |= 0xf << (28 - (4 * i));
			}
		}
		mask = m;
	}

	@Override
	protected void execute(PowerState state) {
		if(one) {
			throw new AssertionError("mtocrf not implemented");
		} else {
			long vs = state.getGPR(rs);
			int s = (int) vs;
			state.cr = (s & mask) | (state.cr & ~mask);
		}
	}

	@Override
	protected String[] disassemble() {
		if(one) {
			return null;
		} else {
			if(fxm == 0xFF) {
				return new String[] { "mtcr", "r" + rs };
			} else {
				return new String[] { "mtcrf", Integer.toString(fxm), "r" + rs };
			}
		}
	}

}
