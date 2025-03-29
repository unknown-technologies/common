package com.unknown.posix.vfs;

import com.unknown.posix.api.PosixPointer;
import com.unknown.util.io.Endianess;

public class TmpfsFileMemory implements PosixPointer {
	private final TmpfsFile file;
	private final long offset;
	private final long length;
	private final byte[] data;

	public TmpfsFileMemory(TmpfsFile file, long offset, long length, boolean nonshared) {
		this.file = file;
		if(nonshared) {
			data = new byte[(int) length];
			byte[] fdata = file.getContent();
			int len = (int) length;
			if(len > fdata.length) {
				len = fdata.length;
			}
			System.arraycopy(fdata, (int) offset, data, 0, len);
			this.offset = 0;
			this.length = data.length;
		} else {
			this.offset = offset;
			data = file.getContent();
			this.length = length;
		}
	}

	private TmpfsFileMemory(TmpfsFile file, long offset, long length, byte[] data) {
		this.file = file;
		this.offset = offset;
		this.data = data;
		this.length = length;
	}

	@Override
	public PosixPointer add(int off) {
		return new TmpfsFileMemory(file, offset + off, length - off, data);
	}

	@Override
	public byte getI8() {
		return data[(int) offset];
	}

	@Override
	public short getI16() {
		return Endianess.get16bitBE(data, (int) offset);
	}

	@Override
	public int getI32() {
		return Endianess.get32bitBE(data, (int) offset);
	}

	@Override
	public long getI64() {
		return Endianess.get64bitBE(data, (int) offset);
	}

	@Override
	public void setI8(byte val) {
		data[(int) offset] = val;
	}

	@Override
	public void setI16(short val) {
		Endianess.set16bitBE(data, (int) offset, val);
	}

	@Override
	public void setI32(int val) {
		Endianess.set32bitBE(data, (int) offset, val);
	}

	@Override
	public void setI64(long val) {
		Endianess.set64bitBE(data, (int) offset, val);
	}

	@Override
	public long size() {
		return length;
	}

	@Override
	public String getName() {
		return file.getName();
	}
}
