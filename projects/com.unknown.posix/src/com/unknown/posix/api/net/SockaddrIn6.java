package com.unknown.posix.api.net;

import com.unknown.posix.api.PosixPointer;
import com.unknown.util.io.Endianess;

public class SockaddrIn6 extends Sockaddr {
	public short sin6_port;
	public int sin6_flowinfo;
	public byte[] sin6_addr;
	public int sin6_scope_id;

	@Override
	public PosixPointer read(PosixPointer ptr) {
		PosixPointer p = ptr;
		sa_family = p.getI16();
		p = p.add(2);
		sin6_port = p.getI16();
		p = p.add(2);
		sin6_flowinfo = p.getI32();
		p = p.add(4);
		sin6_addr = new byte[16];
		for(int i = 0; i < sin6_addr.length; i++) {
			sin6_addr[i] = p.add(i).getI8();
		}
		p = p.add(16);
		sin6_scope_id = p.getI32();
		return p.add(4);
	}

	@Override
	public PosixPointer write(PosixPointer ptr) {
		PosixPointer p = ptr;
		p.setI16(sa_family);
		p = p.add(2);
		p.setI16(sin6_port);
		p = p.add(2);
		p.setI32(sin6_flowinfo);
		p = p.add(4);
		for(int i = 0; i < sin6_addr.length; i++) {
			p.add(i).setI8(sin6_addr[i]);
		}
		p = p.add(16);
		p.setI32(sin6_scope_id);
		return p.add(4);
	}

	public String getAddressString() {
		short[] parts = new short[8];
		for(int i = 0; i < parts.length; i++) {
			parts[i] = Endianess.get16bitBE(sin6_addr, i * 2);
		}
		StringBuilder buf = new StringBuilder();
		for(int i = 0; i < parts.length; i++) {
			if(i > 0) {
				buf.append(':');
			}
			buf.append(Integer.toHexString(Short.toUnsignedInt(parts[i])));
		}
		return buf.toString();
	}

	@Override
	public String toString() {
		return "{sa_family=" + Socket.addressFamily(sa_family) + ",sin_port=" + sin6_port + ",sin_addr=\"" +
				getAddressString() + "\"}";
	}
}
