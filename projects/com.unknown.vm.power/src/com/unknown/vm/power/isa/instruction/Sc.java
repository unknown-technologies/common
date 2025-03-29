package com.unknown.vm.power.isa.instruction;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.unknown.posix.api.Errno;
import com.unknown.util.log.Trace;
import com.unknown.vm.posix.SyscallException;
import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.SyscallNames;
import com.unknown.vm.power.isa.InstructionFormat;
import com.unknown.vm.power.isa.PowerInstruction;

// Linux: r3 r4 r5 r6 r7 r8 r9
public class Sc extends PowerInstruction {
	private static final Logger log = Trace.create(Sc.class);

	private final int lev;

	public Sc(long pc, InstructionFormat insn) {
		super(pc, insn);
		lev = insn.LEV.get();
	}

	@Override
	public boolean isControlFlow() {
		return true;
	}

	@Override
	protected void execute(PowerState state) {
		long nr = state.getGPR(0);
		long a1 = state.getGPR(3);
		long a2 = state.getGPR(4);
		long a3 = state.getGPR(5);
		long a4 = state.getGPR(6);
		long a5 = state.getGPR(7);
		long a6 = state.getGPR(8);
		long a7 = state.getGPR(9);
		try {
			long result = state.syscall((int) nr, a1, a2, a3, a4, a5, a6, a7);
			state.setGPR(3, result);
			state.clearCR0SV();
		} catch(SyscallException e) {
			if(e.getValue() == Errno.ENOSYS) {
				log(nr);
			}
			state.setGPR(3, e.getValue());
			state.setCR0SV();
		}
	}

	private static void log(long nr) {
		String name = SyscallNames.getName(nr);
		if(name != null) {
			log.log(Level.WARNING, "Unsupported syscall " + name + " (#" + nr + ")");
		} else {
			log.log(Level.WARNING, "Unsupported syscall " + nr);
		}
	}

	@Override
	protected String[] disassemble() {
		if(lev == 0) {
			return new String[] { "sc" };
		} else {
			return new String[] { "sc", Integer.toString(lev) };
		}
	}
}
