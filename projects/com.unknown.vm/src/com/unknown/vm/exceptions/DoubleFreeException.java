package com.unknown.vm.exceptions;

import com.unknown.vm.memory.Memory;

public class DoubleFreeException extends RuntimeException {
	private static final long serialVersionUID = -4027903412205546876L;

	private Memory mem;

	public DoubleFreeException(Memory mem) {
		this.mem = mem;
	}

	public Memory getMemory() {
		return mem;
	}
}
