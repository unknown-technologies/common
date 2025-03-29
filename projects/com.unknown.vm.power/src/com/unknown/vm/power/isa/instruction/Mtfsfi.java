package com.unknown.vm.power.isa.instruction;

import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

public class Mtfsfi extends PowerInstruction {
	private final int bf;
	private final boolean w;
	private final int u;
	private final boolean rc;
	private final long mask;
	private final long or;

	public Mtfsfi(long pc, InstructionFormat insn) {
		super(pc, insn);
		bf = insn.XL_BF.get();
		w = insn.W.getBit();
		u = insn.U.get();
		rc = insn.Rc.getBit();

		int i = 8 * (1 - (w ? 1 : 0));
		int field = bf + i;
		mask = ~(0xfL << (60 - 4 * field));
		or = (long) u << (60 - 4 * field);
	}

	@Override
	protected void execute(PowerState state) {
		state.setFPSCR((state.fpscr & mask) | or);
		if(rc) {
			throw new AssertionError("not implemented");
		}
	}

	@Override
	protected String[] disassemble() {
		String dot = rc ? "." : "";
		if(!w) {
			return new String[] { "mtfsfi" + dot, Integer.toString(bf), Integer.toString(u) };
		} else {
			return new String[] { "mtfsfi" + dot, Integer.toString(bf), Integer.toString(u),
					w ? "1" : "0" };
		}
	}
}
