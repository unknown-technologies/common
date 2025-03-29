package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class B extends PowerInstruction {
	private final int li;
	private final boolean aa;
	private final boolean lk;

	public B(long pc, InstructionFormat insn) {
		super(pc, insn);
		li = insn.LI.get() << 2;
		aa = insn.AA.getBit();
		lk = insn.LK.getBit();
	}

	@Override
	public boolean isControlFlow() {
		return true;
	}

	@Override
	protected void execute(PowerState state) {
		long nia;
		if(aa) {
			nia = li;
		} else {
			nia = pc + li;
		}
		if(lk) {
			state.lr = pc + 4;
		}
		state.pc = nia;
	}

	@Override
	protected String[] disassemble() {
		StringBuilder name = new StringBuilder(3);
		long bta = li;
		name.append('b');
		if(lk) {
			name.append('l');
		}
		if(aa) {
			name.append('a');
		} else {
			bta += pc;
		}
		return new String[] { name.toString(), Long.toHexString(bta) };
	}

}
