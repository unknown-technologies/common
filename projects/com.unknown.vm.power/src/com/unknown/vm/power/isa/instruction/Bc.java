package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 38
public class Bc extends PowerInstruction {
	private final int bo;
	private final int bi;
	private final int bd;
	private final boolean aa;
	private final boolean lk;

	public Bc(long pc, InstructionFormat insn) {
		super(pc, insn);
		bo = insn.BO.get();
		bi = insn.BI.get();
		bd = insn.BD.get() << 2;
		aa = insn.AA.getBit();
		lk = insn.LK.getBit();
	}

	private boolean bo(int bit) {
		return (bo & (1 << (4 - bit))) != 0;
	}

	private static boolean cr(long cr, int bit) {
		return (cr & (1 << (63 - bit))) != 0;
	}

	@Override
	public boolean isControlFlow() {
		return true;
	}

	@Override
	protected void execute(PowerState state) {
		boolean ctr_ok;
		boolean cond_ok;
		long nia = pc + 4;
		if(!bo(2)) {
			state.ctr--;
			if(state.ppc64) {
				ctr_ok = (state.ctr != 0) ^ bo(3);
			} else {
				ctr_ok = ((int) state.ctr != 0) ^ bo(3);
			}
		} else {
			ctr_ok = true;
		}
		cond_ok = bo(0) || (cr(state.cr, bi + 32) == bo(1));
		if(ctr_ok && cond_ok) {
			if(aa) {
				nia = bd;
			} else {
				nia = pc + bd;
			}
			if(lk) {
				state.lr = pc + 4;
			}
		}
		state.pc = nia;
	}

	@Override
	protected String[] disassemble() {
		String a = aa ? "a" : "";
		String l = lk ? "l" : "";
		String add = l + a;
		String bta = Long.toHexString(aa ? bd : pc + bd);
		return new String[] { "bc" + add, Integer.toString(bo), Integer.toString(bi), bta };
	}
}
