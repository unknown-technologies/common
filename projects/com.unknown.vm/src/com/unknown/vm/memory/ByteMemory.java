package com.unknown.vm.memory;

import java.util.Arrays;

import com.unknown.posix.api.BytePosixPointer;
import com.unknown.posix.api.PosixPointer;
import com.unknown.util.io.Endianess;
import com.unknown.vm.exceptions.SegmentationViolation;

public class ByteMemory extends Memory {
	private byte[] data;

	private static byte[] newArray(long size) {
		if((int) size != size) {
			throw new OutOfMemoryError();
		} else {
			return new byte[(int) size];
		}
	}

	public ByteMemory(long size) {
		this(size, true);
	}

	public ByteMemory(long size, boolean isBE) {
		this(newArray(size), isBE);
	}

	public ByteMemory(byte[] data) {
		this(data, true);
	}

	public ByteMemory(byte[] data, boolean isBE) {
		super(isBE);
		this.data = data;
	}

	@Override
	public byte[] getBytes() {
		return data;
	}

	@Override
	protected byte i8(long pos) {
		assert pos == (int) pos;
		check(pos);

		try {
			return data[(int) pos];
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected short i16L(long pos) {
		assert pos == (int) pos;
		check(pos);

		try {
			return Endianess.get16bitLE(data, (int) pos);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected short i16B(long pos) {
		assert pos == (int) pos;
		check(pos);

		try {
			return Endianess.get16bitBE(data, (int) pos);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected int i32L(long pos) {
		assert pos == (int) pos;
		check(pos);

		try {
			return Endianess.get32bitLE(data, (int) pos);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected int i32B(long pos) {
		assert pos == (int) pos;
		check(pos);

		try {
			return Endianess.get32bitBE(data, (int) pos);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected long i64L(long pos) {
		assert pos == (int) pos;
		check(pos);

		try {
			return Endianess.get64bitLE(data, (int) pos);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected long i64B(long pos) {
		assert pos == (int) pos;
		check(pos);

		try {
			return Endianess.get64bitBE(data, (int) pos);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected void i8(long pos, byte val) {
		assert pos == (int) pos;
		check(pos);

		try {
			data[(int) pos] = val;
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected void i16L(long pos, short val) {
		assert pos == (int) pos;
		check(pos);

		try {
			Endianess.set16bitLE(data, (int) pos, val);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected void i16B(long pos, short val) {
		assert pos == (int) pos;
		check(pos);

		try {
			Endianess.set16bitBE(data, (int) pos, val);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected void i32L(long pos, int val) {
		assert pos == (int) pos;
		check(pos);

		try {
			Endianess.set32bitLE(data, (int) pos, val);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected void i32B(long pos, int val) {
		assert pos == (int) pos;
		check(pos);

		try {
			Endianess.set32bitBE(data, (int) pos, val);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected void i64L(long pos, long val) {
		assert pos == (int) pos;
		check(pos);

		try {
			Endianess.set64bitLE(data, (int) pos, val);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	protected void i64B(long pos, long val) {
		assert pos == (int) pos;
		check(pos);

		try {
			Endianess.set64bitBE(data, (int) pos, val);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new SegmentationViolation(this, pos);
		}
	}

	@Override
	public void free() {
		super.free();
		data = null;
	}

	@Override
	public void memcpy(byte[] dst, long off) {
		assert off == (int) off : String.format("Invalid offset 0x%016X", off);
		System.arraycopy(data, (int) off, dst, 0, dst.length);
	}

	@Override
	public PosixPointer getPosixPointer(long off) {
		check(off);
		assert off == (int) off : String.format("Invalid offset 0x%016X", off);
		return new BytePosixPointer(data, (int) off);
	}

	@Override
	public long size() {
		return data.length;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(!(o instanceof ByteMemory)) {
			return false;
		}
		ByteMemory m = (ByteMemory) o;
		return m.data == data && m.isFree() == isFree();
	}
}
