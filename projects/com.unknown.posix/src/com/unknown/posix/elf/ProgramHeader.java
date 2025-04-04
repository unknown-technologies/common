package com.unknown.posix.elf;

import java.util.HashMap;
import java.util.Map;

import com.unknown.util.io.Endianess;

public class ProgramHeader {
	private static final Map<Integer, String> TYPE_NAMES;

	public int p_type;
	public int p_flags;
	public long p_offset;
	public long p_vaddr;
	public long p_paddr;
	public long p_filesz;
	public long p_memsz;
	public long p_align;

	private Elf elf;
	private int offset;

	static {
		TYPE_NAMES = new HashMap<>();
		TYPE_NAMES.put(Elf.PT_NULL, "NULL");
		TYPE_NAMES.put(Elf.PT_LOAD, "LOAD");
		TYPE_NAMES.put(Elf.PT_DYNAMIC, "DYNAMIC");
		TYPE_NAMES.put(Elf.PT_INTERP, "INTERP");
		TYPE_NAMES.put(Elf.PT_NOTE, "NOTE");
		TYPE_NAMES.put(Elf.PT_SHLIB, "SHLIB");
		TYPE_NAMES.put(Elf.PT_PHDR, "PHDR");
		TYPE_NAMES.put(Elf.PT_TLS, "TLS");
		TYPE_NAMES.put(Elf.PT_GNU_EH_FRAME, "GNU_EH_FRAME");
		TYPE_NAMES.put(Elf.PT_GNU_STACK, "GNU_STACK");
		TYPE_NAMES.put(Elf.PT_GNU_RELRO, "GNU_RELRO");
		TYPE_NAMES.put(Elf.PT_SUNWBSS, "SUNWBSS");
		TYPE_NAMES.put(Elf.PT_SUNWSTACK, "SUNWSTACK");
	}

	public ProgramHeader(Elf elf, int offset) {
		this.elf = elf;
		this.offset = offset;
		byte[] data = elf.getData();
		if(elf.ei_class == Elf.ELFCLASS32) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
				p_type = Endianess.get32bitLE(data, offset);
				p_offset = Endianess.get32bitLE(data, offset + 0x04);
				p_vaddr = Endianess.get32bitLE(data, offset + 0x08);
				p_paddr = Endianess.get32bitLE(data, offset + 0x0C);
				p_filesz = Endianess.get32bitLE(data, offset + 0x10);
				p_memsz = Endianess.get32bitLE(data, offset + 0x14);
				p_flags = Endianess.get32bitLE(data, offset + 0x18);
				p_align = Endianess.get32bitLE(data, offset + 0x1C);
			} else if(elf.ei_data == Elf.ELFDATA2MSB) {
				p_type = Endianess.get32bitBE(data, offset);
				p_offset = Endianess.get32bitBE(data, offset + 0x04);
				p_vaddr = Endianess.get32bitBE(data, offset + 0x08);
				p_paddr = Endianess.get32bitBE(data, offset + 0x0C);
				p_filesz = Endianess.get32bitBE(data, offset + 0x10);
				p_memsz = Endianess.get32bitBE(data, offset + 0x14);
				p_flags = Endianess.get32bitBE(data, offset + 0x18);
				p_align = Endianess.get32bitBE(data, offset + 0x1C);
			} else {
				throw new IllegalArgumentException();
			}
		} else if(elf.ei_class == Elf.ELFCLASS64) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
				p_type = Endianess.get32bitLE(data, offset);
				p_flags = Endianess.get32bitLE(data, offset + 0x04);
				p_offset = Endianess.get64bitLE(data, offset + 0x08);
				p_vaddr = Endianess.get64bitLE(data, offset + 0x10);
				p_paddr = Endianess.get64bitLE(data, offset + 0x18);
				p_filesz = Endianess.get64bitLE(data, offset + 0x20);
				p_memsz = Endianess.get64bitLE(data, offset + 0x28);
				p_align = Endianess.get64bitLE(data, offset + 0x30);
			} else if(elf.ei_data == Elf.ELFDATA2MSB) {
				p_type = Endianess.get32bitBE(data, offset);
				p_flags = Endianess.get32bitBE(data, offset + 0x04);
				p_offset = Endianess.get64bitBE(data, offset + 0x08);
				p_vaddr = Endianess.get64bitBE(data, offset + 0x10);
				p_paddr = Endianess.get64bitBE(data, offset + 0x18);
				p_filesz = Endianess.get64bitBE(data, offset + 0x20);
				p_memsz = Endianess.get64bitBE(data, offset + 0x28);
				p_align = Endianess.get64bitBE(data, offset + 0x30);
			} else {
				throw new IllegalArgumentException();
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	public int getType() {
		return p_type;
	}

	public void setType(int type) {
		this.p_type = type;
		byte[] data = elf.getData();
		if(elf.ei_data == Elf.ELFDATA2LSB) {
			Endianess.set32bitLE(data, offset);
		} else if(elf.ei_data == Elf.ELFDATA2MSB) {
			Endianess.set32bitBE(data, offset);
		}
	}

	public String getTypeName() {
		String name = TYPE_NAMES.get(getType());
		if(name != null) {
			return name;
		} else {
			return String.format("0x%x", getType());
		}
	}

	public long getOffset() {
		return p_offset;
	}

	public void setOffset(long offset) {
		this.p_offset = offset;
		byte[] data = elf.getData();
		if(elf.ei_class == Elf.ELFCLASS32) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
				Endianess.set32bitLE(data, this.offset + 0x04, (int) offset);
			} else {
				Endianess.set32bitBE(data, this.offset + 0x04, (int) offset);
			}
		} else if(elf.ei_class == Elf.ELFCLASS64) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
				Endianess.set64bitLE(data, this.offset + 0x08, offset);
			} else {
				Endianess.set64bitBE(data, this.offset + 0x08, offset);
			}
		}
	}

	public long getVirtualAddress() {
		return p_vaddr;
	}

	public void setVirtualAddress(long address) {
		this.p_vaddr = address;
		byte[] data = elf.getData();
		if(elf.ei_class == Elf.ELFCLASS32) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
				Endianess.set32bitLE(data, offset + 0x08, (int) address);
			} else {
				Endianess.set32bitBE(data, offset + 0x08, (int) address);
			}
		} else if(elf.ei_class == Elf.ELFCLASS64) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
				Endianess.set64bitLE(data, offset + 0x10, address);
			} else {
				Endianess.set64bitBE(data, offset + 0x10, address);
			}
		}
	}

	public long getPhysicalAddress() {
		return p_vaddr;
	}

	public void setPhysicalAddress(long address) {
		this.p_paddr = address;
		byte[] data = elf.getData();
		if(elf.ei_class == Elf.ELFCLASS32) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
				Endianess.set32bitLE(data, offset + 0x0C, (int) address);
			} else {
				Endianess.set32bitBE(data, offset + 0x0C, (int) address);
			}
		} else if(elf.ei_class == Elf.ELFCLASS64) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
				Endianess.set64bitLE(data, offset + 0x18, address);
			} else {
				Endianess.set64bitBE(data, offset + 0x18, address);
			}
		}
	}

	public long getFileSize() {
		return p_filesz;
	}

	public void setFileSize(long size) {
		this.p_filesz = size;
		byte[] data = elf.getData();
		if(elf.ei_class == Elf.ELFCLASS32) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
				Endianess.set32bitLE(data, offset + 0x10, (int) size);
			} else {
				Endianess.set32bitBE(data, offset + 0x10, (int) size);
			}
		} else if(elf.ei_class == Elf.ELFCLASS64) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
				Endianess.set64bitLE(data, offset + 0x20, size);
			} else {
				Endianess.set64bitBE(data, offset + 0x20, size);
			}
		}
	}

	public long getMemorySize() {
		return p_memsz;
	}

	public void setMemorySize(long size) {
		this.p_memsz = size;
		byte[] data = elf.getData();
		if(elf.ei_class == Elf.ELFCLASS32) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
				Endianess.set32bitLE(data, offset + 0x14, (int) size);
			} else {
				Endianess.set32bitBE(data, offset + 0x14, (int) size);
			}
		} else if(elf.ei_class == Elf.ELFCLASS64) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
				Endianess.set64bitLE(data, offset + 0x28, size);
			} else {
				Endianess.set64bitBE(data, offset + 0x28, size);
			}
		}
	}

	public boolean getFlag(int flag) {
		return (p_flags & flag) != 0;
	}

	public void setFlag(int flag) {
		setFlags(getFlags() | flag);
	}

	public int getFlags() {
		return p_flags;
	}

	public void setFlags(int flags) {
		this.p_flags = flags;
		byte[] data = elf.getData();
		if(elf.ei_class == Elf.ELFCLASS32) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
				Endianess.set32bitLE(data, offset + 0x18, flags);
			} else {
				Endianess.set32bitBE(data, offset + 0x18, flags);
			}
		} else if(elf.ei_class == Elf.ELFCLASS64) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
				Endianess.set32bitLE(data, offset + 0x04, flags);
			} else {
				Endianess.set32bitBE(data, offset + 0x04, flags);
			}
		}
	}

	public long getAlignment() {
		return p_align;
	}

	public void setAlignment(long alignment) {
		this.p_align = alignment;
		byte[] data = elf.getData();
		if(elf.ei_class == Elf.ELFCLASS32) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
				Endianess.set32bitLE(data, offset + 0x1C, (int) alignment);
			} else {
				Endianess.set32bitBE(data, offset + 0x1C, (int) alignment);
			}
		} else if(elf.ei_class == Elf.ELFCLASS64) {
			if(elf.ei_data == Elf.ELFDATA2LSB) {
				Endianess.set64bitLE(data, offset + 0x30, alignment);
			} else {
				Endianess.set64bitBE(data, offset + 0x30, alignment);
			}
		}
	}

	public Elf getElf() {
		return elf;
	}

	public void load(byte[] target) {
		if(target.length < p_memsz) {
			throw new IllegalArgumentException();
		}
		if(p_filesz > 0) {
			System.arraycopy(elf.getData(), (int) p_offset, target, 0, (int) p_filesz);
		}
	}
}
