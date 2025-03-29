package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;
import com.unknown.vm.power.isa.Rotate;

public class Crorc extends PowerInstruction {
	private final int bt;
	private final int ba;
	private final int bb;

	private final int ma;
	private final int mb;
	private final int mask;
	private final int shift;

	public Crorc(long pc, InstructionFormat insn) {
		super(pc, insn);
		bt = insn.BT.get();
		ba = insn.BA.get();
		bb = insn.BB.get();

		ma = (int) Rotate.bit(32 + ba);
		mb = (int) Rotate.bit(32 + bb);
		mask = (int) ~Rotate.bit(32 + bt);
		shift = 63 - (32 + bt);
	}

	@Override
	protected void execute(PowerState state) {
		int cr = state.cr;
		boolean a = (cr & ma) != 0;
		boolean b = (cr & mb) == 0;
		int bit = (a | b) ? 1 : 0;
		state.cr = (cr & mask) | (bit << shift);
	}

	@Override
	protected String[] disassemble() {
		return new String[] { "crorc", Integer.toString(bt), Integer.toString(ba),
				Integer.toString(bb) };
	}
}
