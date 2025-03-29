package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Manual: page 39
// Extended: page 710
public class Bcctr extends PowerInstruction {
	private final int bo;
	private final int bi;
	private final int bh;
	private final boolean lk;

	public Bcctr(long pc, InstructionFormat insn) {
		super(pc, insn);
		bo = insn.BO.get();
		bi = insn.BI.get();
		bh = insn.BH.get() & 1;
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
		boolean cond_ok;
		long nia = pc + 4;
		cond_ok = bo(0) || (cr(state.cr, bi + 32) == bo(1));
		if(cond_ok) {
			if(state.ppc64) {
				nia = state.ctr & ~0x3;
			} else {
				nia = Integer.toUnsignedLong((int) (state.ctr & ~0x3));
			}
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
			return new String[] { "bctr" + l };
		} else if(bo == 18 && bi == 0 && bh == 0) {
			return new String[] { "bdzctr" + l };
		} else if(bo == 16 && bi == 0 && bh == 0) {
			return new String[] { "bdnzctr" + l };
		} else if(bo == 12 && bh == 0) {
			if(bi == 0) {
				return new String[] { "btctr" + l };
			} else {
				return new String[] { "btctr" + l, Integer.toString(bi) };
			}
		} else if(bo == 4 && bh == 0) {
			if(bi == 0) {
				return new String[] { "bfctr" + l };
			} else {
				return new String[] { "bfctr" + l, Integer.toString(bi) };
			}
		} else if(bo == 10 && bh == 0) {
			if(bi == 0) {
				return new String[] { "bdztctr" + l };
			} else {
				return new String[] { "bdztctr" + l, Integer.toString(bi) };
			}
		} else if(bo == 2 && bh == 0) {
			if(bi == 0) {
				return new String[] { "bdzfctr" + l };
			} else {
				return new String[] { "bdzfctr" + l, Integer.toString(bi) };
			}
		} else if(bo == 8 && bh == 0) {
			if(bi == 0) {
				return new String[] { "bdnztctr" + l };
			} else {
				return new String[] { "bdnztctr" + l, Integer.toString(bi) };
			}
		} else if(bo == 0 && bh == 0) {
			if(bi == 0) {
				return new String[] { "bdnzfctr" + l };
			} else {
				return new String[] { "bdnzfctr" + l, Integer.toString(bi) };
			}
		}
		if(bh != 0) {
			return new String[] { "bcctr" + l, Integer.toString(bo), Integer.toString(bi) };
		} else {
			return new String[] { "bcctr" + l, Integer.toString(bo), Integer.toString(bi),
					Integer.toString(bh) };
		}
	}
}
