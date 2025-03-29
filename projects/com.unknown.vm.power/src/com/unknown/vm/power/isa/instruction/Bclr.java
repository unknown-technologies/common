package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 39
// Extended: page 710
public class Bclr extends PowerInstruction {
	private final int bo;
	private final int bi;
	private final int bh;
	private final boolean lk;

	public Bclr(long pc, InstructionFormat insn) {
		super(pc, insn);
		bo = insn.BO.get();
		bi = insn.BI.get();
		bh = insn.BH.get() & 3;
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
			nia = state.lr & ~0x3;
			if(lk) {
				state.lr = pc + 4;
			}
		}
		state.pc = nia;
	}

	@Override
	protected String[] disassemble() {
		String l = lk ? "l" : "";
		if(bo == 20 && bi == 0 && bh == 0) {
			return new String[] { "blr" + l };
		} else if(bo == 18 && bi == 0 && bh == 0) {
			return new String[] { "bdzlr" + l };
		} else if(bo == 16 && bi == 0 && bh == 0) {
			return new String[] { "bdnzlr" + l };
		} else if(bo == 12 && bh == 0) {
			if(bi == 0) {
				return new String[] { "btlr" + l };
			} else {
				return new String[] { "btlr" + l, Integer.toString(bi) };
			}
		} else if(bo == 4 && bh == 0) {
			if(bi == 0) {
				return new String[] { "bflr" + l };
			} else {
				return new String[] { "bflr" + l, Integer.toString(bi) };
			}
		} else if(bo == 10 && bh == 0) {
			if(bi == 0) {
				return new String[] { "bdztlr" + l };
			} else {
				return new String[] { "bdztlr" + l, Integer.toString(bi) };
			}
		} else if(bo == 2 && bh == 0) {
			if(bi == 0) {
				return new String[] { "bdzflr" + l };
			} else {
				return new String[] { "bdzflr" + l, Integer.toString(bi) };
			}
		} else if(bo == 8 && bh == 0) {
			if(bi == 0) {
				return new String[] { "bdnztlr" + l };
			} else {
				return new String[] { "bdnztlr" + l, Integer.toString(bi) };
			}
		} else if(bo == 0 && bh == 0) {
			if(bi == 0) {
				return new String[] { "bdnzflr" + l };
			} else {
				return new String[] { "bdnzflr" + l, Integer.toString(bi) };
			}
		}
		if(bh != 0) {
			return new String[] { "bclr" + l, Integer.toString(bo), Integer.toString(bi) };
		} else {
			return new String[] { "bclr" + l, Integer.toString(bo), Integer.toString(bi),
					Integer.toString(bh) };
		}
	}
}
