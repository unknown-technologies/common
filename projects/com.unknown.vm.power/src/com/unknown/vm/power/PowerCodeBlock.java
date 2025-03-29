package com.unknown.vm.power;

import com.unknown.vm.Code;
import com.unknown.vm.CodeBlock;

public class PowerCodeBlock extends CodeBlock {
	public PowerCodeBlock(long base, long size, Code code) {
		super(base, size, code);
	}

	@Override
	protected int getInstructionCount() {
		return (int) (size / 4);
	}

	@Override
	protected int getIndex(long id) {
		return (int) (id / 4);
	}
}
