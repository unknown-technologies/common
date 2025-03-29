package com.unknown.vm.memory;

import com.unknown.posix.api.PosixPointer;
import com.unknown.vm.exceptions.SegmentationViolation;

public class PosixMemory extends Memory {
	private final PosixPointer ptr;
	private final boolean readonly;

	public PosixMemory(PosixPointer ptr, boolean isBE, boolean readonly) {
		super(isBE);
		this.ptr = ptr;
		this.readonly = readonly;
	}

	public boolean isReadOnly() {
		return readonly;
	}

	private PosixPointer ptr(long pos) {
		assert pos == (int) pos : String.format("0x%016X vs 0x%016X", pos, (int) pos);
		return ptr.add((int) pos);
	}

	@Override
	protected byte i8(long pos) {
		try {
			return ptr(pos).getI8();
		} catch(RuntimeException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected short i16L(long pos) {
		try {
			return Short.reverseBytes(ptr(pos).getI16());
		} catch(RuntimeException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected short i16B(long pos) {
		try {
			return ptr(pos).getI16();
		} catch(RuntimeException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected int i32L(long pos) {
		try {
			return Integer.reverseBytes(ptr(pos).getI32());
		} catch(RuntimeException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected int i32B(long pos) {
		try {
			return ptr(pos).getI32();
		} catch(RuntimeException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected long i64L(long pos) {
		try {
			return Long.reverseBytes(ptr(pos).getI64());
		} catch(RuntimeException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected long i64B(long pos) {
		try {
			return ptr(pos).getI64();
		} catch(RuntimeException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected void i8(long pos, byte val) {
		try {
			ptr(pos).setI8(val);
		} catch(RuntimeException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected void i16L(long pos, short val) {
		try {
			ptr(pos).setI16(Short.reverseBytes(val));
		} catch(RuntimeException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected void i16B(long pos, short val) {
		try {
			ptr(pos).setI16(val);
		} catch(RuntimeException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected void i32L(long pos, int val) {
		try {
			ptr(pos).setI32(Integer.reverseBytes(val));
		} catch(RuntimeException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected void i32B(long pos, int val) {
		try {
			ptr(pos).setI32(val);
		} catch(RuntimeException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected void i64L(long pos, long val) {
		try {
			ptr(pos).setI64(Long.reverseBytes(val));
		} catch(RuntimeException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected void i64B(long pos, long val) {
		try {
			ptr(pos).setI64(val);
		} catch(RuntimeException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	public long size() {
		return ptr.size();
	}

	@Override
	public String toString() {
		return "[" + (readonly ? "ro" : "rw") + ":" + ptr.toString() + "]";
	}
}
