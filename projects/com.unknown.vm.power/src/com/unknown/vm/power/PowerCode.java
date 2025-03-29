package com.unknown.vm.power;

import com.unknown.vm.Code;
import com.unknown.vm.CodeBlock;
import com.unknown.vm.memory.MemoryPage;
import com.unknown.vm.power.isa.PowerInstruction;

public class PowerCode extends Code {
	public PowerCode(MemoryPage memory) {
		super(memory);
	}

	@Override
	protected PowerInstruction[] createInstructionCache() {
		return new PowerInstruction[(int) (memory.size / 4)];
	}

	@Override
	protected CodeBlock createCodeBlock(long start, long size) {
		return new PowerCodeBlock(start, size, this);
	}

	@Override
	protected long getIndex(long id) {
		return id / 4;
	}

	@Override
	protected int getInstructionSize(long pc) {
		return 4;
	}

	@Override
	protected PowerInstruction decode(long pc) {
		int insn = memory.getI32(pc);
		return PowerInstruction.decode(pc, insn);
	}

	@Override
	public String disassemble() {
		StringBuilder buf = new StringBuilder();
		for(long pc = memory.base; pc < memory.end; pc += 4) {
			int insn = memory.getI32(pc);
			PowerInstruction decoded = PowerInstruction.decode(pc, insn);
			buf.append(String.format("%016x:\t%02x %02x %02x %02x\t%s\n", pc,
					(insn >> 24) & 0xFF, (insn >> 16) & 0xFF,
					(insn >> 8) & 0xFF, insn & 0xFF, decoded.toString()));
		}
		return buf.toString().trim();
	}

	public String disassemble(long disasmbase, long disasmend) {
		StringBuilder buf = new StringBuilder();
		for(long pc = disasmbase; pc < disasmend; pc += 4) {
			int insn = memory.getI32(pc);
			PowerInstruction decoded = PowerInstruction.decode(pc, insn);
			buf.append(String.format("%016x:\t%02x %02x %02x %02x\t%s\n", pc,
					(insn >> 24) & 0xFF, (insn >> 16) & 0xFF,
					(insn >> 8) & 0xFF, insn & 0xFF, decoded.toString()));
		}
		return buf.toString().trim();
	}
}
