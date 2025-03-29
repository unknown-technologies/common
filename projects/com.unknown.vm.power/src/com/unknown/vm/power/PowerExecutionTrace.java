package com.unknown.vm.power;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.unknown.posix.elf.Elf;
import com.unknown.util.BitTest;
import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;
import com.unknown.vm.ArchitecturalState;
import com.unknown.vm.ExecutionTrace;
import com.unknown.vm.Instruction;
import com.unknown.vm.power.isa.PowerInstruction;

public class PowerExecutionTrace extends ExecutionTrace {
	private static final Logger log = Trace.create(PowerExecutionTrace.class);

	private static final byte TYPE_STEP = MAX_TYPE + 1;
	// private static final byte TYPE_TRAP = MAX_TYPE + 2;

	private static final int MASK_LR = bit(0);
	private static final int MASK_CTR = bit(1);
	private static final int MASK_CR = bit(2);
	private static final int MASK_XER = bit(3);
	private static final int MASK_FPSCR = bit(4);
	// private static final int MASK_SRR0 = bit(5);
	// private static final int MASK_SRR1 = bit(6);

	private long step = 0;

	PowerState lastState = null;

	public PowerExecutionTrace(File file) throws IOException {
		super(file, Elf.EM_PPC);
	}

	private static int bit(int x) {
		return 1 << x;
	}

	@Override
	public void insn(ArchitecturalState st, Instruction in) {
		if(out == null) {
			return;
		}

		PowerState state = (PowerState) st;
		PowerInstruction insn = (PowerInstruction) in;

		int word = insn.getInstruction();

		byte regmask = 0;
		int gprmask = 0;
		int fprmask = 0;

		if(lastState == null) {
			regmask = (byte) (MASK_LR | MASK_CTR | MASK_CR | MASK_XER | MASK_FPSCR);
			gprmask = 0xFFFFFFFF;
			fprmask = 0xFFFFFFFF;
		} else {
			if(state.lr != lastState.lr) {
				regmask |= MASK_LR;
			}
			if(state.lr != lastState.ctr) {
				regmask |= MASK_CTR;
			}
			if(state.lr != lastState.cr) {
				regmask |= MASK_CR;
			}
			if(state.lr != lastState.xer) {
				regmask |= MASK_XER;
			}
			if(state.lr != lastState.fpscr) {
				regmask |= MASK_FPSCR;
			}

			for(int i = 0; i < 32; i++) {
				if(state.getGPR(i) != lastState.getGPR(i)) {
					gprmask |= 1 << i;
				}
				if(state.getFPR(i) != lastState.getFPR(i)) {
					fprmask |= 1 << i;
				}
			}
		}

		try {
			out.write8bit(TYPE_STEP);
			out.write8bit(regmask);
			out.write32bit(word);
			out.write64bit(step++);
			out.write32bit((int) insn.getPC());
			out.write32bit(gprmask);
			out.write32bit(fprmask);

			if(BitTest.test(regmask, MASK_LR)) {
				out.write32bit((int) state.lr);
			}
			if(BitTest.test(regmask, MASK_CTR)) {
				out.write32bit((int) state.ctr);
			}
			if(BitTest.test(regmask, MASK_CR)) {
				out.write32bit(state.cr);
			}
			if(BitTest.test(regmask, MASK_XER)) {
				out.write32bit((int) state.xer);
			}
			if(BitTest.test(regmask, MASK_FPSCR)) {
				out.write32bit((int) state.fpscr);
			}

			for(int i = 0; i < 32; i++) {
				if(BitTest.test(gprmask, 1 << i)) {
					out.write32bit((int) state.getGPR(i));
				}
			}
			for(int i = 0; i < 32; i++) {
				if(BitTest.test(fprmask, 1 << i)) {
					out.write64bit(state.getFPR(i));
				}
			}
		} catch(IOException e) {
			log.log(Levels.ERROR, "Failed to write step event: " + e.getMessage(), e);
			try {
				out.close();
			} catch(IOException ex) {
				log.log(Levels.ERROR, "Failed to close trace file: " + e.getMessage(), e);
			}
		}

		lastState = state.clone();
	}
}
