package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Stswi extends PowerInstruction {
	private final int rs;
	private final int ra;
	private final int nb;
	private final int n;

	public Stswi(long pc, InstructionFormat insn) {
		super(pc, insn);
		rs = insn.RS.get();
		ra = insn.RA.get();
		nb = insn.NB.get();
		if(nb == 0) {
			n = 32;
		} else {
			n = nb;
		}
	}

	private static byte get(long val, int i) {
		return (byte) (val >> (56 - i));
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra);
		int vn = n;
		int r = rs - 1;
		int i = 32;
		while(vn > 0) {
			if(i == 32) {
				r = (r + 1) & 0x1f;
			}
			state.getMemory().setI8(ea, get(state.getGPR(r), i));
			i = i + 8;
			if(i == 64) {
				i = 32;
			}
			ea++;
			vn--;
		}
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "stswi", "r" + rs, "r" + ra, Integer.toString(nb) };
	}
}
