package com.unknown.vm;

public abstract class CodeBlock {
	public final long base;
	public final long end;
	public final long size;
	protected final Code code;

	private final Instruction[] instructions;

	public CodeBlock(long base, long size, Code code) {
		assert size > 0;
		this.base = base;
		this.size = size;
		this.end = base + size;
		this.code = code;
		instructions = compile();
		assert instructions.length > 0;
	}

	public boolean contains(long address) {
		return Long.compareUnsigned(address, base) >= 0 && Long.compareUnsigned(address, end) < 0;
	}

	protected abstract int getInstructionCount();

	protected abstract int getIndex(long id);

	protected int getInstructionSize(long pc) {
		return code.getInstructionSize(pc);
	}

	protected Instruction[] compile() {
		Instruction[] result = new Instruction[getInstructionCount()];
		int i = 0;
		for(long pc = base; Long.compareUnsigned(pc, end) < 0; pc += getInstructionSize(pc)) {
			result[i++] = code.getInstruction(pc);
		}
		return result;
	}

	public long execute(ArchitecturalState state) {
		return execute(base, state);
	}

	public long execute(long pc, ArchitecturalState state) {
		long result = pc;
		for(int i = getIndex(pc - base); i < instructions.length; i++) {
			Instruction insn = instructions[i];
			result = insn.execute(state);
		}
		return result;
	}
}
