package com.unknown.vm;

import com.unknown.vm.exceptions.SegmentationViolation;
import com.unknown.vm.memory.MemoryPage;

public abstract class Code {
	protected ExecutionTrace trace = null;

	protected MemoryPage memory;
	protected Instruction[] instructions;
	private CodeBlock[] cache;

	public Code(MemoryPage memory) {
		this.memory = memory;
		this.instructions = createInstructionCache();
		this.cache = new CodeBlock[instructions.length];
	}

	public void setTrace(ExecutionTrace trace) {
		this.trace = trace;
	}

	public void set(long pc, Instruction insn) {
		long id = pc - memory.base;
		instructions[(int) id] = insn;
	}

	protected CodeBlock compile(long pc) {
		long start = pc;
		long end = memory.end;
		for(long i = start; memory.contains(i); i += getInstructionSize(i)) {
			Instruction insn = getInstruction(i);
			if(insn.isControlFlow()) {
				end = i + getInstructionSize(i);
				break;
			}
		}
		return createCodeBlock(start, end - start);
	}

	public long execute(long pc, ArchitecturalState state) {
		if(trace != null) {
			return executeSlow(pc, state);
		} else {
			return executeFast(pc, state);
		}
	}

	public long executeFast(long pc, ArchitecturalState state) {
		CodeBlock block = cache[(int) getIndex(pc - memory.base)];
		if(block == null) {
			block = compile(pc);
			for(long i = getIndex(block.base - memory.base); i < getIndex(
					block.end - memory.base); i += getInstructionSize(i)) {
				cache[(int) i] = block;
			}
		}
		return block.execute(pc, state);
	}

	public long executeSlow(long pc, ArchitecturalState state) {
		if(!memory.contains(pc)) {
			throw new SegmentationViolation(memory, pc);
		}
		long id = pc - memory.base;
		long index = getIndex(id);
		if(instructions[(int) index] == null) {
			instructions[(int) index] = decode(pc);
		}
		if(trace != null) {
			trace.insn(state, instructions[(int) index]);
		}
		return instructions[(int) index].execute(state);
	}

	public Instruction getInstruction(long pc) {
		long id = pc - memory.base;
		long index = getIndex(id);
		if(instructions[(int) index] == null) {
			instructions[(int) index] = decode(pc);
		}
		return instructions[(int) index];
	}

	protected MemoryPage getMemory() {
		return memory;
	}

	protected abstract long getIndex(long id);

	protected abstract int getInstructionSize(long pc);

	protected abstract Instruction decode(long pc);

	public String disassemble() {
		return null;
	}

	protected abstract Instruction[] createInstructionCache();

	protected abstract CodeBlock createCodeBlock(long start, long size);

	public void invalidate() {
		instructions = createInstructionCache();
		cache = new CodeBlock[instructions.length];
	}
}
