package com.unknown.vm;

public abstract class Instruction {
	public abstract boolean isControlFlow();

	public abstract long execute(ArchitecturalState state);
}
