package com.unknown.vm.power.isa;

public class InvalidInstructionException extends RuntimeException {
	private static final long serialVersionUID = 2638793222301964888L;

	private PowerInstruction insn;

	public InvalidInstructionException(PowerInstruction insn) {
		this.insn = insn;
	}

	public PowerInstruction getInstruction() {
		return insn;
	}
}
