package com.unknown.vm.posix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.unknown.posix.api.Dirent;
import com.unknown.posix.api.Errno;
import com.unknown.posix.api.Posix;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.PosixPointer;
import com.unknown.posix.api.Rlimit;
import com.unknown.posix.api.Sigaction;
import com.unknown.posix.api.Sigset;
import com.unknown.posix.api.Stack;
import com.unknown.posix.api.Timespec;
import com.unknown.posix.api.Timeval;
import com.unknown.posix.api.Tms;
import com.unknown.posix.api.Utimbuf;
import com.unknown.posix.api.Utsname;
import com.unknown.posix.api.io.Fcntl;
import com.unknown.posix.api.io.FileDescriptorManager;
import com.unknown.posix.api.io.Iovec;
import com.unknown.posix.api.io.Pollfd;
import com.unknown.posix.api.io.Stat;
import com.unknown.posix.api.io.Statx;
import com.unknown.posix.api.io.Stream;
import com.unknown.posix.api.linux.Net;
import com.unknown.posix.api.linux.Sysinfo;
import com.unknown.posix.api.mem.Mman;
import com.unknown.posix.api.net.Mmsghdr;
import com.unknown.posix.api.net.Msghdr;
import com.unknown.posix.api.net.RecvResult;
import com.unknown.posix.api.net.Sockaddr;
import com.unknown.posix.elf.Elf;
import com.unknown.posix.elf.ProgramHeader;
import com.unknown.posix.elf.Section;
import com.unknown.posix.elf.Symbol;
import com.unknown.posix.elf.SymbolResolver;
import com.unknown.posix.elf.SymbolTable;
import com.unknown.posix.vfs.ProcessVFS;
import com.unknown.posix.vfs.VFSFileSystem;
import com.unknown.util.BitTest;
import com.unknown.util.HexFormatter;
import com.unknown.util.io.Endianess;
import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;
import com.unknown.vm.Code;
import com.unknown.vm.ExecutionTrace;
import com.unknown.vm.exceptions.SegmentationViolation;
import com.unknown.vm.memory.ByteMemory;
import com.unknown.vm.memory.Memory;
import com.unknown.vm.memory.MemoryPage;
import com.unknown.vm.memory.PosixMemory;
import com.unknown.vm.memory.PosixVirtualMemoryPointer;
import com.unknown.vm.memory.VirtualMemory;

public class PosixEnvironment {
	private static final Logger log = Trace.create(PosixEnvironment.class);

	private static final boolean SYMBOLS = System.getProperty("vm.power.trace") != null;

	private VirtualMemory mem;
	private Posix posix;
	private boolean strace;
	private Function<MemoryPage, Code> gencode;
	private String arch;

	private NavigableMap<Long, Symbol> symbols;
	private NavigableSet<Long> libraries;
	private SymbolResolver symbolResolver;

	private ExecutionTrace trace;

	public PosixEnvironment(VirtualMemory mem, Function<MemoryPage, Code> gencode, String arch) {
		this(mem, gencode, arch, new Posix(), SYMBOLS);
	}

	public PosixEnvironment(VirtualMemory mem, Function<MemoryPage, Code> gencode, String arch, Posix posix,
			boolean loadSymbols) {
		this.mem = mem;
		this.gencode = gencode;
		this.arch = arch;
		this.posix = posix;
		strace = System.getProperty("posix.strace") != null;
		if(loadSymbols) {
			symbols = new TreeMap<>();
			symbolResolver = new SymbolResolver(symbols);
			libraries = new TreeSet<>();
		}

		posix.setMemoryMapProvider(() -> {
			try(ByteArrayOutputStream buf = new ByteArrayOutputStream();
					PrintStream ps = new PrintStream(buf)) {
				mem.printMaps(ps);
				ps.flush();
				return buf.toByteArray();
			} catch(IOException e) {
				return null;
			}
		});
	}

	public void setStrace(boolean value) {
		strace = value;
		posix.setStrace(value);
	}

	public boolean isStrace() {
		return strace;
	}

	public void setTrace(ExecutionTrace trace) {
		this.trace = trace;
	}

	public ExecutionTrace getTrace() {
		return trace;
	}

	public Symbol getSymbol(long pc) {
		if(symbolResolver != null) {
			return symbolResolver.getSymbol(pc);
		} else {
			return null;
		}
	}

	public long getBase(long pc) {
		Long result = libraries.floor(pc);
		if(result == null) {
			return -1;
		} else {
			return result;
		}
	}

	private void loadSymbols(int fildes, long offset, long ptr, long length) {
		log.log(Levels.DEBUG,
				"Loading symbols for file " + fildes + " (pointer: " + HexFormatter.tohex(ptr, 16) +
						", file offset: " + HexFormatter.tohex(offset, 16) + "-" +
						HexFormatter.tohex(offset + length, 16) + ")");
		try {
			// check if this is an ELF file
			byte[] magic = new byte[4];
			Stat stat = new Stat();
			Stream stream = posix.getStream(fildes);
			stream.stat(stat);
			if((stat.st_mode & Stat.S_IFMT) == Stat.S_IFREG && stat.st_size > 4) {
				log.log(Levels.DEBUG, "File " + fildes + " is a regular file of size " + stat.st_size);
				int read = stream.pread(magic, 0, 4, 0);
				if(read == 4 && Endianess.get32bitBE(magic) == Elf.MAGIC && (int) stat.st_size > 0) {
					log.log(Levels.DEBUG, "File " + fildes + " is an ELF file");
					// load elf file
					byte[] buf = new byte[(int) stat.st_size];
					stream.pread(buf, 0, buf.length, 0);
					Elf elf = new Elf(buf);
					log.log(Levels.DEBUG, "Segments: " +
							elf.getProgramHeaders().stream()
									.map(phdr -> String.format("0x%08x-0x%08x",
											phdr.p_vaddr,
											phdr.p_vaddr + phdr.p_memsz))
									.collect(Collectors.joining(", ")));
					log.log(Levels.DEBUG, "Sections: " + elf.sections.stream().map(Section::getName)
							.collect(Collectors.joining(", ")));

					// find program header of this segment
					long loadBias = ptr - offset; // strange assumption
					for(ProgramHeader phdr : elf.getProgramHeaders()) {
						if(phdr.p_offset == offset) { // this is it, probably
							loadBias = ptr - phdr.p_vaddr;
							log.log(Levels.DEBUG,
									"Program header found: " + String.format(
											"0x%08x-0x%08x", phdr.p_vaddr,
											phdr.p_vaddr + phdr.p_memsz));
							log.log(Levels.DEBUG, "Computed load bias is " +
									HexFormatter.tohex(loadBias, 16));
							libraries.add(loadBias);
							break;
						}
					}

					SymbolTable symtab = elf.getSymbolTable();
					if(symtab == null) {
						symtab = elf.getDynamicSymbolTable();
					}
					if(symtab != null) {
						log.log(Levels.DEBUG, "Loading symbols in range " +
								HexFormatter.tohex(ptr, 16) + "-" +
								HexFormatter.tohex(ptr + length, 16) + "...");
						for(Symbol sym : symtab.getSymbols()) {
							if(sym.getSectionIndex() != Symbol.SHN_UNDEF &&
									sym.getValue() >= offset &&
									sym.getValue() < offset + length) {
								symbols.put(sym.getValue() + loadBias,
										sym.offset(loadBias));
								log.log(Levels.DEBUG, "Adding symbol " + sym +
										" for address 0x" +
										HexFormatter.tohex(sym.getValue() +
												loadBias, 16));
							}
						}
					}
				}
			}
			symbolResolver = new SymbolResolver(symbols);
		} catch(PosixException | IOException e) {
			log.log(Level.WARNING, "Error while reading symbols: " + e.getMessage(), e);
		}
	}

	private String cstr(long buf) {
		long addr = mem.addr(buf);
		if(addr == 0) {
			return null;
		}
		StringBuilder str = new StringBuilder();
		long ptr = buf;
		while(true) {
			byte b = mem.getI8(ptr);
			if(b == 0) {
				break;
			} else {
				str.append((char) (b & 0xff));
				ptr++;
			}
		}
		return str.toString();
	}

	private PosixPointer posixPointer(long ptr) {
		if(ptr == 0) {
			return null;
		}
		return mem.getPosixPointer(ptr & 0xFFFFFFFFL);
	}

	private long getPointer(PosixPointer ptr, boolean r, boolean w, boolean x, long offset, boolean priv) {
		PosixMemory pmem = new PosixMemory(ptr, true, priv);
		MemoryPage page = mem.allocate(pmem, mem.roundToPageSize(ptr.size()), ptr.getName(), offset);
		page.r = r;
		page.w = w;
		page.x = x;
		if(x) {
			page.code = gencode.apply(page);
		}
		return page.getBase();
	}

	private long getPointer(PosixPointer ptr, long address, long size, boolean r, boolean w, boolean x, long offset,
			boolean priv) {
		long addr = mem.addr(address);
		Memory memory = new PosixMemory(ptr, true, priv);
		MemoryPage page = new MemoryPage(memory, addr, size, ptr.getName(), offset);
		page.r = r;
		page.w = w;
		page.x = x;
		if(x) {
			page.code = gencode.apply(page);
		}
		mem.add(page);
		return address;
	}

	public void setStandardIn(InputStream in) {
		posix.setStream(FileDescriptorManager.STDIN, in);
	}

	public void setStandardOut(OutputStream out) {
		posix.setStream(FileDescriptorManager.STDOUT, out);
	}

	public void setStandardErr(OutputStream out) {
		posix.setStream(FileDescriptorManager.STDERR, out);
	}

	public void setStandardInTTY(InputStream in) {
		posix.setTTY(FileDescriptorManager.STDIN, in);
	}

	public void setStandardOutTTY(OutputStream out) {
		posix.setTTY(FileDescriptorManager.STDOUT, out);
	}

	public void setStandardErrTTY(OutputStream out) {
		posix.setTTY(FileDescriptorManager.STDERR, out);
	}

	public int open(long pathname, int flags, int mode) throws SyscallException {
		try {
			return posix.open(cstr(pathname), flags, mode);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "open failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int openat(int fd, long pathname, int flags, int mode) throws SyscallException {
		try {
			return posix.openat(fd, cstr(pathname), flags, mode);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "openat failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int close(int fd) throws SyscallException {
		try {
			return posix.close(fd);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "close failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long read(int fd, long buf, long nbyte) throws SyscallException {
		try {
			return posix.read(fd, posixPointer(buf), nbyte);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "read failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long write(int fd, long buf, long nbyte) throws SyscallException {
		try {
			return posix.write(fd, posixPointer(buf), nbyte);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "write failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	private Iovec[] getIov32(long iov, int iovcnt) {
		Iovec[] iovs = new Iovec[iovcnt];
		long ptr = iov;
		for(int i = 0; i < iovcnt; i++) {
			long base = mem.getI32(ptr);
			ptr += 4;
			int size = mem.getI32(ptr);
			ptr += 4;
			PosixPointer baseptr = posixPointer(base);
			iovs[i] = new Iovec(baseptr, size);
		}
		return iovs;
	}

	public long readv32(int fd, long iov, int iovcnt) throws SyscallException {
		if(iovcnt < 0) {
			throw new SyscallException(Errno.EINVAL);
		}
		Iovec[] iovs = getIov32(iov, iovcnt);
		try {
			return posix.readv(fd, iovs);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "readv failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long writev32(int fd, long iov, int iovcnt) throws SyscallException {
		if(iovcnt < 0) {
			throw new SyscallException(Errno.EINVAL);
		}
		Iovec[] iovs = getIov32(iov, iovcnt);
		try {
			return posix.writev(fd, iovs);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "writev failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long sendfile32(int out_fd, int in_fd, long offset, long count) throws SyscallException {
		PosixPointer ptr = posixPointer(offset);
		try {
			return posix.sendfile32(out_fd, in_fd, ptr, count);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "sendfile failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long sendfile64(int out_fd, int in_fd, long offset, long count) throws SyscallException {
		PosixPointer ptr = posixPointer(offset);
		try {
			return posix.sendfile64(out_fd, in_fd, ptr, count);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "sendfile failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int _llseek(int fd, int offset_high, int offset_low, long result, int whence) throws SyscallException {
		long offset = (Integer.toUnsignedLong(offset_high) << 32) | Integer.toUnsignedLong(offset_low);
		try {
			long pos = posix.lseek(fd, offset, whence);
			mem.setI64(result, pos);
			return 0;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "_llseek failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int uname(long buf) throws SyscallException {
		PosixPointer ptr = posixPointer(buf);
		Utsname uname = new Utsname();
		try {
			int result = posix.uname(uname);
			uname.machine = arch;
			uname.write(ptr);
			return result;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "uname failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int symlink(long target, long linkpath) throws SyscallException {
		String tgt = cstr(target);
		String lnk = cstr(linkpath);
		try {
			return posix.symlink(tgt, lnk);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "symlink failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int symlinkat(long target, int fd, long linkpath) throws SyscallException {
		String tgt = cstr(target);
		String lnk = cstr(linkpath);
		try {
			return posix.symlinkat(tgt, fd, lnk);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "symlinkat failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long readlink(long pathname, long buf, long bufsiz) throws SyscallException {
		PosixPointer ptr = posixPointer(buf);
		String path = cstr(pathname);
		try {
			return posix.readlink(path, ptr, bufsiz);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "readlink failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int unlink(long pathname) throws SyscallException {
		String path = cstr(pathname);
		try {
			return posix.unlink(path);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "unlink failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int unlinkat(int fd, long pathname, int flag) throws SyscallException {
		String path = cstr(pathname);
		try {
			return posix.unlinkat(fd, path, flag);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "unlinkat failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int chown(long pathname, long owner, long group) throws SyscallException {
		String path = cstr(pathname);
		try {
			return posix.chown(path, owner, group);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "chown failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int chmod(long pathname, int mode) throws SyscallException {
		String path = cstr(pathname);
		try {
			return posix.chmod(path, mode);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "chmod failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int utime(long filename, long buf) throws SyscallException {
		String path = cstr(filename);
		try {
			PosixPointer ptr = posixPointer(buf);
			Utimbuf times = null;
			if(ptr != null) {
				times = new Utimbuf();
				times.read32(ptr);
			}
			return posix.utime(path, times);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "utime failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int mkdir(long pathname, int mode) throws SyscallException {
		String path = cstr(pathname);
		try {
			return posix.mkdir(path, mode);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "mkdir failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int chdir(long pathname) throws SyscallException {
		String path = cstr(pathname);
		try {
			return posix.chdir(path);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "chdir failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long getcwd(long buf, long size) throws SyscallException {
		try {
			posix.getcwd(posixPointer(buf), size);
			return buf;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "getcwd failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int access(long pathname, int mode) throws SyscallException {
		String path = cstr(pathname);
		try {
			return posix.access(path, mode);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "access failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long stat64(long pathname, long statbuf) throws SyscallException {
		PosixPointer ptr = posixPointer(statbuf);
		Stat stat = new Stat();
		try {
			int result = posix.stat(cstr(pathname), stat);
			stat.write3264(ptr);
			return result;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "stat failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long lstat64(long pathname, long statbuf) throws SyscallException {
		PosixPointer ptr = posixPointer(statbuf);
		Stat stat = new Stat();
		try {
			int result = posix.lstat(cstr(pathname), stat);
			stat.write3264(ptr);
			return result;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "stat failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long fstat64(int fd, long statbuf) throws SyscallException {
		PosixPointer ptr = posixPointer(statbuf);
		Stat stat = new Stat();
		try {
			int result = posix.fstat(fd, stat);
			stat.write3264(ptr);
			return result;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "fstat failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long fstatat64(int fd, long path, long buf, int flags) throws SyscallException {
		PosixPointer ptr = posixPointer(buf);
		Stat stat = new Stat();
		try {
			int result = posix.fstatat(fd, cstr(path), stat, flags);
			stat.write3264(ptr);
			return result;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "fstatat64 failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long statx(int dirfd, long pathname, int flags, int mask, long statxbuf) throws SyscallException {
		PosixPointer ptr = posixPointer(statxbuf);
		Statx statx = new Statx();
		try {
			// TODO: bug report to coreutils/ls; STATX_MODE and STATX_TYPE *should* be independent
			int reqmask = mask;
			if(BitTest.test(mask, Stat.STATX_MODE)) {
				reqmask |= Stat.STATX_TYPE;
			} else if(BitTest.test(mask, Stat.STATX_TYPE)) {
				reqmask |= Stat.STATX_MODE;
			}
			int result = posix.statx(dirfd, cstr(pathname), flags, reqmask, statx);
			statx.write32(ptr);
			return result;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "statx failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long fcntl64(int fd, int cmd, long arg) throws SyscallException {
		try {
			switch(cmd) {
			case Fcntl.F_GETFD:
			case Fcntl.F_SETFD:
			case Fcntl.F_GETFL:
			case Fcntl.F_SETFL:
			case Fcntl.F_DUPFD:
			case Fcntl.F_DUPFD_CLOEXEC:
				return posix.fcntl(fd, cmd, (int) arg);
			default:
				log.log(Level.INFO, "fcntl command not implemented: " + cmd);
				throw new PosixException(Errno.EINVAL);
			}
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "fcntl failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int ftruncate(int fd, long length) throws SyscallException {
		try {
			return posix.ftruncate(fd, length);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "ftruncate failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long ioctl(int fd, long request, long arg) throws SyscallException {
		try {
			return posix.ioctl(fd, request, posixPointer(arg));
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "ioctl failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	private void logMmap(long addr, long length, int prot, int flags, int fildes, long offset, long result) {
		if(trace != null) {
			try {
				try {
					String filename = posix.getFileDescriptor(fildes).name;
					trace.mmap(addr, length, prot, flags, fildes, offset, result, filename, null);
				} catch(PosixException e) {
					trace.mmap(addr, length, prot, flags, fildes, offset, result, null, null);
				}
			} catch(IOException e) {
				log.log(Levels.ERROR, "Failed to write mmap event: " + e.getMessage(), e);
				trace = null;
			}
		}
	}

	private void logMmap(long addr, long length, int prot, int flags, int fildes, long offset, long result,
			PosixPointer ptr) {
		if(trace != null) {
			byte[] data = new byte[(int) length];
			try {
				for(int i = 0; i < data.length; i++) {
					data[i] = ptr.add(i).getI8();
				}
			} catch(Throwable t) {
				// swallow
			}

			try {
				try {
					String filename = posix.getFileDescriptor(fildes).name;
					trace.mmap(addr, length, prot, flags, fildes, offset, result, filename, data);
				} catch(PosixException e) {
					trace.mmap(addr, length, prot, flags, fildes, offset, result, null, data);
				}
			} catch(IOException e) {
				log.log(Levels.ERROR, "Failed to write mmap event: " + e.getMessage(), e);
				trace = null;
			}
		}
	}

	public long mmap(long addr, long length, int pr, int fl, int fildes, long offset)
			throws SyscallException {
		int flags = fl | Mman.MAP_PRIVATE;
		int prot = pr | Mman.PROT_WRITE;
		try {
			if(mem.pageStart(addr) != mem.addr(addr)) {
				throw new PosixException(Errno.EINVAL);
			}
			if(length == 0) {
				throw new PosixException(Errno.EINVAL);
			}
			if(BitTest.test(flags, Mman.MAP_ANONYMOUS) && BitTest.test(flags, Mman.MAP_PRIVATE)) {
				if(strace) {
					log.log(Levels.INFO,
							() -> String.format("mmap(0x%016x, %d, %s, %s, %d, %d)", addr,
									length, Mman.prot(prot), Mman.flags(flags),
									fildes, offset));
				}
				MemoryPage page;
				try {
					if(BitTest.test(flags, Mman.MAP_FIXED) || addr != 0) {
						long aligned = addr;
						if(!BitTest.test(flags, Mman.MAP_FIXED)) {
							aligned = mem.pageStart(addr);
						}
						Memory bytes = new ByteMemory(mem.roundToPageSize(length), true);
						page = new MemoryPage(bytes, mem.addr(aligned),
								mem.roundToPageSize(length));
						mem.add(page);
					} else {
						page = mem.allocate(mem.roundToPageSize(length));
					}
				} catch(OutOfMemoryError e) {
					throw new PosixException(Errno.ENOMEM);
				}
				page.x = BitTest.test(prot, Mman.PROT_EXEC);
				logMmap(addr, length, pr, fl, fildes, offset, page.base);
				return page.base;
			}
			PosixPointer p = new PosixVirtualMemoryPointer(mem, addr);
			PosixPointer ptr = posix.mmap(p, length, prot, flags, fildes, offset);
			boolean r = BitTest.test(prot, Mman.PROT_READ);
			boolean w = BitTest.test(prot, Mman.PROT_WRITE);
			boolean x = BitTest.test(prot, Mman.PROT_EXEC);
			boolean priv = BitTest.test(flags, Mman.MAP_PRIVATE);
			long result;
			if(BitTest.test(flags, Mman.MAP_FIXED)) {
				result = getPointer(ptr, addr, mem.roundToPageSize(length), r, w, x, offset, priv);
			} else {
				assert mem.roundToPageSize(ptr.size()) == mem.roundToPageSize(length);
				result = getPointer(ptr, r, w, x, offset, priv);
			}
			if(trace != null) {
				loadSymbols(fildes, offset, result, length);
			}
			logMmap(addr, length, pr, fl, fildes, offset, result, ptr);
			return result;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "mmap failed: " + Errno.toString(e.getErrno()));
			}
			logMmap(addr, length, pr, fl, fildes, offset, -e.getErrno());
			throw new SyscallException(e.getErrno());
		}
	}

	public int munmap(long addr, long length) throws SyscallException {
		if(strace) {
			log.log(Level.INFO, () -> String.format("munmap(0x%016x, %s)", addr, length));
		}
		try {
			// return posix.munmap(posixPointer(addr), length);
			mem.remove(addr, mem.roundToPageSize(length));
			if(trace != null) {
				try {
					trace.munmap(addr, length, 0);
				} catch(IOException e) {
					log.log(Levels.ERROR, "Failed to write munmap event: " + e.getMessage(), e);
					trace = null;
				}
			}
			return 0;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "munmap failed: " + Errno.toString(e.getErrno()));
			}
			if(trace != null) {
				try {
					trace.munmap(addr, length, -e.getErrno());
				} catch(IOException ex) {
					log.log(Levels.ERROR, "Failed to write munmap event: " + ex.getMessage(), ex);
					trace = null;
				}
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long mmap2(long addr, long length, int pr, int fl, int fildes, long offset)
			throws SyscallException {
		return mmap(addr, length, pr, fl, fildes, offset << 12);
	}

	public int mprotect(long addr, long size, int prot) throws SyscallException {
		if(strace) {
			log.log(Level.INFO,
					() -> String.format("mprotect(0x%016x, %d, %s)", addr, size, Mman.prot(prot)));
		}
		try {
			boolean r = BitTest.test(prot, Mman.PROT_READ);
			boolean w = BitTest.test(prot, Mman.PROT_WRITE);
			boolean x = BitTest.test(prot, Mman.PROT_EXEC);
			mem.mprotect(addr, size, r, w, x);
			if(trace != null) {
				try {
					trace.mprotect(addr, size, prot, 0);
				} catch(IOException e) {
					log.log(Levels.ERROR, "Failed to write mprotect event: " + e.getMessage(), e);
					trace = null;
				}
			}
		} catch(PosixException e) {
			if(trace != null) {
				try {
					trace.mprotect(addr, size, prot, -e.getErrno());
				} catch(IOException ex) {
					log.log(Levels.ERROR, "Failed to write mprotect event: " + ex.getMessage(), ex);
					trace = null;
				}
			}
			throw new SyscallException(e.getErrno());
		} catch(SegmentationViolation e) {
			if(trace != null) {
				try {
					trace.mprotect(addr, size, prot, -Errno.ENOMEM);
				} catch(IOException ex) {
					log.log(Levels.ERROR, "Failed to write mprotect event: " + ex.getMessage(), ex);
					trace = null;
				}
			}
			throw new SyscallException(Errno.ENOMEM);
		}
		return 0;
	}

	public long dup(int fildes) throws SyscallException {
		try {
			return posix.dup(fildes);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "dup failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long dup2(int fildes, int fildes2) throws SyscallException {
		try {
			return posix.dup2(fildes, fildes2);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "dup2 failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long dup3(int oldfd, int newfd, int flags) throws SyscallException {
		try {
			return posix.dup3(oldfd, newfd, flags);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "dup3 failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int clock_getres32(int clk_id, long tp) throws SyscallException {
		try {
			Timespec t = new Timespec();
			int val = posix.clock_getres(clk_id, t);
			if(tp != 0) {
				PosixPointer ptr = posixPointer(tp);
				t.write32(ptr);
			}
			return val;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "clock_getres failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int clock_gettime32(int clk_id, long tp) throws SyscallException {
		try {
			Timespec t = new Timespec();
			int val = posix.clock_gettime(clk_id, t);
			PosixPointer ptr = posixPointer(tp);
			t.write32(ptr);
			return val;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "clock_gettime failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int gettimeofday32(long tp, @SuppressWarnings("unused") long tzp) {
		Timeval t = new Timeval();
		int val = posix.gettimeofday(t, null);
		PosixPointer ptr = posixPointer(tp);
		t.write32(ptr);
		return val;
	}

	public long times32(long buffer) throws SyscallException {
		try {
			Tms buf = new Tms();
			long val = posix.times(buf);
			PosixPointer ptr = posixPointer(buffer);
			buf.write32(ptr);
			return val;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "times failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long setuid(long uid) throws SyscallException {
		try {
			return posix.setuid(uid);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "setuid failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long getuid() {
		return posix.getuid();
	}

	public long geteuid() {
		// TODO: implement euid
		return posix.getuid();
	}

	public long getegid() {
		// TODO: implement egid
		return posix.getgid();
	}

	public long setgid(long uid) throws SyscallException {
		try {
			return posix.setgid(uid);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "setgid failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long getgid() {
		return posix.getgid();
	}

	public long getpid() {
		return posix.getpid();
	}

	public int getrlimit32(int resource, long rlp) throws SyscallException {
		try {
			Rlimit rlim = new Rlimit();
			int result = posix.getrlimit(resource, rlim);
			rlim.write32(posixPointer(rlp));
			return result;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "getrlimit failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int sigaction32(int sig, long act, long oact) throws SyscallException {
		try {
			PosixPointer pact = posixPointer(act);
			PosixPointer poact = posixPointer(oact);
			Sigaction newact = null;
			if(pact != null) {
				newact = new Sigaction();
				newact.read32(pact);
			}
			Sigaction oldact = poact != null ? new Sigaction() : null;
			int result = posix.sigaction(sig, newact, oldact);
			if(poact != null) {
				oldact.write32(poact);
			}
			return result;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "sigaction failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int sigprocmask32(int how, long set, long oldset, int sigsetsize) throws SyscallException {
		try {
			if(sigsetsize != 8) {
				throw new PosixException(Errno.EINVAL);
			}
			PosixPointer pset = posixPointer(set);
			PosixPointer poldset = posixPointer(oldset);
			Sigset sset = null;
			Sigset soldset = null;
			if(pset != null) {
				sset = new Sigset();
				sset.read32(pset);
			}
			if(poldset != null) {
				soldset = new Sigset();
			}
			int result = posix.sigprocmask(how, sset, soldset);
			if(poldset != null) {
				soldset.write32(poldset);
			}
			return result;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "sigprocmask failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int sigaltstack32(long ss, long oldss) throws SyscallException {
		try {
			PosixPointer pss = posixPointer(ss);
			PosixPointer poldss = posixPointer(oldss);
			Stack sigstk = null;
			if(pss != null) {
				sigstk = new Stack();
				sigstk.read32(pss);
			}
			Stack oldsigstk = poldss == null ? null : new Stack();
			int result = posix.sigaltstack(sigstk, oldsigstk);
			if(poldss != null) {
				oldsigstk.write32(poldss);
			}
			return result;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "sigaltstack failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int poll(long fds, int nfds, int timeout) throws SyscallException {
		try {
			PosixPointer pfds = posixPointer(fds);
			Pollfd[] parsed = new Pollfd[nfds];
			PosixPointer ptr = pfds;
			for(int i = 0; i < nfds; i++) {
				parsed[i] = new Pollfd();
				ptr = parsed[i].read(ptr);
			}

			int result = posix.poll(parsed, nfds, timeout);

			if(result > 0) {
				ptr = pfds;
				for(int i = 0; i < nfds; i++) {
					ptr = parsed[i].write(ptr);
				}
			}

			return result;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "poll failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int setsockopt(int sock, int level, int option_name, long option_value, int option_len)
			throws SyscallException {
		if(option_len != 4) {
			log.log(Level.WARNING, "Invalid option_len " + option_len + " in setsockopt");
			throw new SyscallException(Errno.EINVAL);
		}
		try {
			int val = posixPointer(option_value).getI32();
			return posix.setsockopt(sock, level, option_name, val);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "getsockname failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int getsockname32(int socket, long address, long address_len) throws SyscallException {
		try {
			Sockaddr sa = posix.getsockname(socket);
			sa.write32(posixPointer(address));
			posixPointer(address_len).setI32(sa.getSize());
			return 0;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "getsockname failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int getpeername32(int socket, long address, long address_len) throws SyscallException {
		try {
			Sockaddr sa = posix.getpeername(socket);
			sa.write32(posixPointer(address));
			posixPointer(address_len).setI32(sa.getSize());
			return 0;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "getpeername failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int shutdown(int socket, int how) throws SyscallException {
		try {
			return posix.shutdown(socket, how);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "shutdown failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long send(int socket, long buffer, long length, int flags) throws SyscallException {
		try {
			return posix.send(socket, posixPointer(buffer), length, flags);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "send failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long recv(int socket, long buffer, long length, int flags) throws SyscallException {
		try {
			return posix.recv(socket, posixPointer(buffer), length, flags);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "recv failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	private Msghdr parseMsghdr32(PosixPointer p) {
		PosixPointer ptr = p;
		Msghdr msghdr = new Msghdr();

		int msg_name = ptr.getI32();
		int msg_namelen = ptr.add(4).getI32();
		ptr = ptr.add(8);
		if(msg_name != 0) {
			msghdr.msg_name = Sockaddr.get(posixPointer(msg_name), msg_namelen);
		}
		int iov = ptr.getI32();
		msghdr.msg_iovlen = ptr.add(4).getI32();
		msghdr.msg_iov = getIov32(iov, msghdr.msg_iovlen);
		ptr = ptr.add(8);
		// TODO: parse msg_control
		msghdr.msg_control = null;
		ptr = ptr.add(8);
		msghdr.msg_flags = ptr.getI32();
		return msghdr;
	}

	public long recvmsg32(int socket, long message, int flags) throws SyscallException {
		try {
			PosixPointer ptr = posixPointer(message);
			Msghdr msg = parseMsghdr32(ptr);
			long result = posix.recvmsg(socket, msg, flags);
			PosixPointer msg_name = posixPointer(ptr.getI32());
			if(msg_name != null && msg.msg_name != null) {
				msg.msg_name.write32(msg_name);
				ptr.add(4).setI32(msg.msg_name.getSize());
			}
			PosixPointer msg_control = posixPointer(ptr.add(16).getI32());
			int msg_controllen = ptr.add(20).getI32();
			if(msg_control != null && msg_controllen > 0) {
				if(msg.msg_control == null) {
					ptr.add(20).setI32(0);
				} else {
					ptr.add(20).setI32(12 + msg.msg_control.cmsg_data.length);
				}
			} else {
				ptr.add(20).setI32(0);
			}
			ptr.add(24).setI32(msg.msg_flags);
			return result;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "recvmsg failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int sendmmsg32(int sockfd, long msgvec, int vlen, int flags) throws SyscallException {
		try {
			PosixPointer ptr = posixPointer(msgvec);
			Mmsghdr[] vec = new Mmsghdr[vlen];
			for(int i = 0; i < vec.length; i++) {
				vec[i] = new Mmsghdr();
				vec[i].msg_hdr = parseMsghdr32(ptr);
				ptr = ptr.add(7 * 4);
				vec[i].msg_len = ptr.getI32();
				ptr = ptr.add(4);
			}
			int result = posix.sendmmsg(sockfd, vec, vlen, flags);
			ptr = posixPointer(msgvec);
			for(int i = 0; i < vec.length; i++) {
				ptr = ptr.add(7 * 4);
				ptr.setI32(vec[i].msg_len);
				ptr = ptr.add(4);
			}
			return result;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "sendmmsg failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long recvfrom(int sock, long buffer, long length, int flags, long address, long address_len)
			throws SyscallException {
		try {
			RecvResult result = posix.recvfrom(sock, posixPointer(buffer), length, flags);
			if(address != 0) {
				result.sa.write(posixPointer(address));
			}
			if(address_len != 0) {
				posixPointer(address_len).setI32(16);
			}
			return result.length;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "recvfrom failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int socket(int domain, int type, int protocol) throws SyscallException {
		try {
			return posix.socket(domain, type, protocol);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "socket failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int connect(int socket, long address, int addressLen) throws SyscallException {
		try {
			return posix.connect(socket, posixPointer(address), addressLen);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "connect failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int bind(int socket, long address, int addressLen) throws SyscallException {
		try {
			return posix.bind(socket, posixPointer(address), addressLen);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "bind failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long socketcall(int call, long args) throws SyscallException {
		PosixPointer ptr = posixPointer(args);
		switch(call) {
		case Net.SYS_SOCKET:
			return socket(ptr.getI32(), ptr.getI32(4), ptr.getI32(8));
		case Net.SYS_BIND:
			return bind(ptr.getI32(), ptr.getI32(4), ptr.getI32(8));
		case Net.SYS_CONNECT:
			return connect(ptr.getI32(), ptr.getI32(4), ptr.getI32(8));
		// case Net.SYS_LISTEN:
		// return listen();
		// case Net.SYS_ACCEPT:
		// return accept();
		case Net.SYS_GETSOCKNAME:
			return getsockname32(ptr.getI32(), ptr.getI32(4), ptr.getI32(8));
		case Net.SYS_GETPEERNAME:
			return getpeername32(ptr.getI32(), ptr.getI32(4), ptr.getI32(8));
		// case Net.SYS_SOCKETPAIR:
		// return socketpair();
		case Net.SYS_SEND:
			return send(ptr.getI32(), ptr.getI32(4), ptr.getI32(8), ptr.getI32(12));
		case Net.SYS_RECV:
			return recv(ptr.getI32(), ptr.getI32(4), ptr.getI32(8), ptr.getI32(12));
		// case Net.SYS_SENDTO:
		// return sendto();
		case Net.SYS_RECVFROM:
			return recvfrom(ptr.getI32(), ptr.getI32(4), ptr.getI32(8), ptr.getI32(12), ptr.getI32(16),
					ptr.getI32(20));
		case Net.SYS_SHUTDOWN:
			return shutdown(ptr.getI32(), ptr.getI32(4));
		case Net.SYS_SETSOCKOPT:
			return setsockopt(ptr.getI32(), ptr.getI32(4), ptr.getI32(8), ptr.getI32(12), ptr.getI32(16));
		// case Net.SYS_GETSOCKOPT:
		// return getsockopt();
		// case Net.SYS_SENDMSG:
		// return sendmsg();
		case Net.SYS_RECVMSG:
			return recvmsg32(ptr.getI32(), ptr.getI32(4), ptr.getI32(8));
		// case Net.SYS_ACCEPT4:
		// return accept4();
		// case Net.SYS_RECVMMSG:
		// return recvmmsg();
		// case Net.SYS_SENDMMSG:
		// return sendmmsg();
		default:
			throw new SyscallException(Errno.ENOSYS);
		}
	}

	public int sysinfo32(long info) throws SyscallException {
		try {
			Sysinfo sysinfo = new Sysinfo();
			int result = posix.sysinfo(sysinfo);
			sysinfo.write32(posixPointer(info));
			return result;
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "sysinfo failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long getdents_32(int fd, long dirp, int count) throws SyscallException {
		long cnt = Integer.toUnsignedLong(count);
		try {
			return posix.getdents(fd, posixPointer(dirp), cnt, Dirent.DIRENT_32);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "getdents failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public long getdents64(int fd, long dirp, int count) throws SyscallException {
		long cnt = Integer.toUnsignedLong(count);
		try {
			return posix.getdents(fd, posixPointer(dirp), cnt, Dirent.DIRENT64);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "getdents failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int mount(long source, long target, long filesystemtype, long mountflags, long data)
			throws SyscallException {
		try {
			return posix.mount(cstr(source), cstr(target), cstr(filesystemtype), mountflags,
					posixPointer(data));
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "mount failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int umount(long target) throws SyscallException {
		try {
			return posix.umount(cstr(target));
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "umount failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public int umount2(long target, int flags) throws SyscallException {
		try {
			return posix.umount2(cstr(target), flags);
		} catch(PosixException e) {
			if(strace) {
				log.log(Level.INFO, "umount2 failed: " + Errno.toString(e.getErrno()));
			}
			throw new SyscallException(e.getErrno());
		}
	}

	public void exit(int code) {
		posix.exit(code);
	}

	public void exit_group(int code) {
		posix.exit_group(code);
	}

	public Posix getPosix() {
		return posix;
	}

	public ProcessVFS getVFS() {
		return posix.getVFS();
	}

	public void mount(String path, VFSFileSystem fs) throws PosixException {
		posix.getVFS().mount(path, fs);
	}
}
