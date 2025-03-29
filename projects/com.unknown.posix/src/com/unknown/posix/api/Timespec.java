package com.unknown.posix.api;

import java.util.Date;

public class Timespec implements Struct {
	public long tv_sec;
	public long tv_nsec;

	public Timespec() {
	}

	public Timespec(long tv_sec, long tv_nsec) {
		this.tv_sec = tv_sec;
		this.tv_nsec = tv_nsec;
	}

	public Timespec(Date date) {
		long msec = date.getTime();
		tv_sec = msec / 1000;
		tv_nsec = (msec % 1000) * 1000000;
	}

	public Timespec(Timespec ts) {
		copyFrom(ts);
	}

	public long toMillis() {
		return tv_sec * 1000 + (tv_nsec / 1000000);
	}

	public void copyFrom(Timespec ts) {
		tv_sec = ts.tv_sec;
		tv_nsec = ts.tv_nsec;
	}

	@Override
	public PosixPointer read32(PosixPointer ptr) {
		PosixPointer p = ptr;
		tv_sec = Integer.toUnsignedLong(p.getI32());
		p = p.add(4);
		tv_nsec = Integer.toUnsignedLong(p.getI32());
		return p.add(4);
	}

	@Override
	public PosixPointer read64(PosixPointer ptr) {
		PosixPointer p = ptr;
		tv_sec = p.getI64();
		p = p.add(8);
		tv_nsec = p.getI64();
		return p.add(8);
	}

	@Override
	public PosixPointer write32(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI32((int) tv_sec);
		p = p.add(4);
		p.setI32((int) tv_nsec);
		return p.add(4);
	}

	@Override
	public PosixPointer write64(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI64(tv_sec);
		p = p.add(8);
		p.setI64(tv_nsec);
		return p.add(8);
	}

	@Override
	public String toString() {
		return String.format("{tv_sec=%d,tv_nsec=%d}", tv_sec, tv_nsec);
	}
}
