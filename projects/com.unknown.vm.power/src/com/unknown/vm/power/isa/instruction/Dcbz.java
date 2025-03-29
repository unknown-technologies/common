package com.unknown.vm.power.isa.instruction;

import com.unknown.math.Logarithm;
import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Dcbz extends PowerInstruction {
	private final int ra;
	private final int rb;

	public Dcbz(long pc, InstructionFormat insn) {
		super(pc, insn);
		ra = insn.RA.get();
		rb = insn.RB.get();
	}

	@Override
	protected void execute(PowerState state) {
		long b = state.getGPR0(ra);
		long ea = b + state.getGPR(rb);
		int n = state.dcache_line_size;
		int m = Logarithm.log2(n);
		long mask = 0;
		for(int i = 0; i < m; i++) {
			mask <<= 1;
			mask |= 1;
		}
		long off = ea & mask;
		long ptr = ea - off;
		for(int i = 0; i < (n / 4); i++) {
			state.getMemory().setI32(ptr, 0);
			ptr += 4;
		}
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "dcbz", r0(ra), "r" + rb };
	}
}
