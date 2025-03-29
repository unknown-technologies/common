package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Lswi extends PowerInstruction {
	private final int rt;
	private final int ra;
	private final int nb;
	private final int n;

	public Lswi(long pc, InstructionFormat insn) {
		super(pc, insn);
		rt = insn.RT.get();
		ra = insn.RA.get();
		nb = insn.NB.get();
		if(nb == 0) {
			n = 32;
		} else {
			n = nb;
		}
	}

	private static long shift(byte val, int i) {
		return Byte.toUnsignedLong(val) << (56 - i);
	}

	private static long mask(int i) {
		return shift((byte) 0xFF, i);
	}

	@Override
	protected void execute(PowerState state) {
		long ea = state.getGPR0(ra);
		int r = rt - 1;
		int i = 32;
		int vn = n;
		while(vn > 0) {
			if(i == 32) {
				r = (r + 1) & 0x1f;
				state.setGPR(r, 0);
			}
			byte mem = state.getMemory().getI8(ea);
			long val = state.getGPR(r);
			val = (val & ~mask(i)) | shift(mem, i);
			state.setGPR(r, val);
			i += 8;
			if(i == 64) {
				i = 32;
			}
			ea++;
			vn--;
		}
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "lswi", "r" + rt, "r" + ra, Integer.toString(nb) };
	}
}
