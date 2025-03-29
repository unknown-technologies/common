package com.unknown.posix.api.net;

import com.unknown.posix.api.PosixPointer;
import com.unknown.posix.api.Struct;

public class Sockaddr implements Struct {
	public short sa_family;
	public byte[] sa_data = new byte[14];

	@Override
	public PosixPointer read(PosixPointer ptr) {
		PosixPointer p = ptr;
		sa_family = p.getI16();
		p.add(2);
		for(int i = 0; i < sa_data.length; i++) {
			sa_data[i] = p.getI8();
			p = p.add(1);
		}
		return p;
	}

	@Override
	public PosixPointer write(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI16(sa_family);
		p = p.add(2);
		for(int i = 0; i < sa_data.length; i++) {
			p.setI8(sa_data[i]);
			p = p.add(1);
		}
		return p;
	}

	public static Sockaddr get(PosixPointer ptr, int len) {
		if(ptr == null) {
			return null;
		}
		short family = ptr.getI16();
		switch(family) {
		case Socket.AF_INET: {
			assert len == 16;
			SockaddrIn sin = new SockaddrIn();
			sin.read(ptr);
			return sin;
		}
		case Socket.AF_INET6: {
			assert len == 28;
			SockaddrIn6 sin6 = new SockaddrIn6();
			sin6.read(ptr);
			return sin6;
		}
		default: {
			Sockaddr sa = new Sockaddr();
			sa.read(ptr);
			return sa;
		}
		}
	}

	public int getSize() {
		return 16;
	}

	@Override
	public String toString() {
		return "{sa_family=" + Socket.addressFamily(sa_family) + "}";
	}
}
