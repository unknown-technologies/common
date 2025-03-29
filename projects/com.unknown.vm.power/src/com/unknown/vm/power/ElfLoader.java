package com.unknown.vm.power;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.unknown.posix.api.BytePosixPointer;
import com.unknown.posix.api.Errno;
import com.unknown.posix.api.Posix;
import com.unknown.posix.api.PosixException;
import com.unknown.posix.api.io.Fcntl;
import com.unknown.posix.api.io.Stat;
import com.unknown.posix.api.mem.Mman;
import com.unknown.posix.elf.Elf;
import com.unknown.posix.elf.ProgramHeader;
import com.unknown.posix.elf.Symbol;
import com.unknown.posix.elf.SymbolTable;
import com.unknown.posix.libc.CString;
import com.unknown.posix.vfs.NativeFileSystem;
import com.unknown.posix.vfs.ProcessVFS;
import com.unknown.posix.vfs.VFSFileSystem;
import com.unknown.posix.vfs.proc.Procfs;
import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;
import com.unknown.vm.ExecutionTrace;
import com.unknown.vm.memory.ByteMemory;
import com.unknown.vm.memory.MemoryPage;
import com.unknown.vm.memory.VirtualMemory;
import com.unknown.vm.posix.PosixEnvironment;

// @formatter:off
/*
     ------------------------------------------------------------- 0x7fff6c845000
     0x7fff6c844ff8: 0x0000000000000000
            _  4fec: './stackdump\0'                      <------+
      env  /   4fe2: 'ENVVAR2=2\0'                               |    <----+
           \_  4fd8: 'ENVVAR1=1\0'                               |   <---+ |
           /   4fd4: 'two\0'                                     |       | |     <----+
     args |    4fd0: 'one\0'                                     |       | |    <---+ |
           \_  4fcb: 'zero\0'                                    |       | |   <--+ | |
               3020: random gap padded to 16B boundary           |       | |      | | |
    - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -|       | |      | | |
               3019: 'x86_64\0'                        <-+       |       | |      | | |
     auxv      3009: random data: ed99b6...2adcc7        | <-+   |       | |      | | |
     data      3000: zero padding to align stack         |   |   |       | |      | | |
    . . . . . . . . . . . . . . . . . . . . . . . . . . .|. .|. .|       | |      | | |
               2ff0: AT_NULL(0)=0                        |   |   |       | |      | | |
               2fe0: AT_PLATFORM(15)=0x7fff6c843019    --+   |   |       | |      | | |
               2fd0: AT_EXECFN(31)=0x7fff6c844fec      ------|---+       | |      | | |
               2fc0: AT_RANDOM(25)=0x7fff6c843009      ------+           | |      | | |
      ELF      2fb0: AT_SECURE(23)=0                                     | |      | | |
    auxiliary  2fa0: AT_EGID(14)=1000                                    | |      | | |
     vector:   2f90: AT_GID(13)=1000                                     | |      | | |
    (id,val)   2f80: AT_EUID(12)=1000                                    | |      | | |
      pairs    2f70: AT_UID(11)=1000                                     | |      | | |
               2f60: AT_ENTRY(9)=0x4010c0                                | |      | | |
               2f50: AT_FLAGS(8)=0                                       | |      | | |
               2f40: AT_BASE(7)=0x7ff6c1122000                           | |      | | |
               2f30: AT_PHNUM(5)=9                                       | |      | | |
               2f20: AT_PHENT(4)=56                                      | |      | | |
               2f10: AT_PHDR(3)=0x400040                                 | |      | | |
               2f00: AT_CLKTCK(17)=100                                   | |      | | |
               2ef0: AT_PAGESZ(6)=4096                                   | |      | | |
               2ee0: AT_HWCAP(16)=0xbfebfbff                             | |      | | |
               2ed0: AT_SYSINFO_EHDR(33)=0x7fff6c86b000                  | |      | | |
    . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .        | |      | | |
               2ec8: environ[2]=(nil)                                    | |      | | |
               2ec0: environ[1]=0x7fff6c844fe2         ------------------|-+      | | |
               2eb8: environ[0]=0x7fff6c844fd8         ------------------+        | | |
               2eb0: argv[3]=(nil)                                                | | |
               2ea8: argv[2]=0x7fff6c844fd4            ---------------------------|-|-+
               2ea0: argv[1]=0x7fff6c844fd0            ---------------------------|-+
               2e98: argv[0]=0x7fff6c844fcb            ---------------------------+
     0x7fff6c842e90: argc=3
 */
// @formatter:on
public class ElfLoader {
	private static final Logger log = Trace.create(ElfLoader.class);

	public static final long DEFAULT_LOAD_BIAS = 0x40000000L;

	public static final String PLATFORM = "power8";
	public static final int RANDOM_SIZE = 16;
	public static final int PAGE_SIZE = 4096;
	public static final int HWCAP = Hwcap.PPC_FEATURE_32 | Hwcap.PPC_FEATURE_HAS_ALTIVEC |
			Hwcap.PPC_FEATURE_HAS_FPU | Hwcap.PPC_FEATURE_HAS_VSX;
	public static final int HWCAP2 = Hwcap.PPC_FEATURE2_ARCH_2_07;

	public static final int AT_NULL = 0;
	public static final int AT_PHDR = 3;
	public static final int AT_PHENT = 4;
	public static final int AT_PHNUM = 5;
	public static final int AT_PAGESZ = 6;
	public static final int AT_BASE = 7;
	public static final int AT_FLAGS = 8;
	public static final int AT_ENTRY = 9;
	public static final int AT_UID = 11;
	public static final int AT_EUID = 12;
	public static final int AT_GID = 13;
	public static final int AT_EGID = 14;
	public static final int AT_PLATFORM = 15;
	public static final int AT_HWCAP = 16;
	public static final int AT_DCACHEBSIZE = 19;
	public static final int AT_ICACHEBSIZE = 20;
	public static final int AT_UCACHEBSIZE = 21;
	public static final int AT_IGNOREPPC = 22;
	public static final int AT_SECURE = 23;
	public static final int AT_BASE_PLATFORM = 24;
	public static final int AT_RANDOM = 25;
	public static final int AT_HWCAP2 = 26;
	public static final int AT_EXECFN = 31;

	private Power power;
	private Elf elf;
	private long load_bias;
	private long base;
	private long entry;
	private long phoff = -1;

	private String progname;
	private String[] args;
	private String[] env;

	private int ptrsz;
	private long brk;

	private boolean execstack = true;

	private ExecutionTrace trace;

	public ElfLoader(Power power) {
		this(power, null);
	}

	public ElfLoader(Power power, ExecutionTrace trace) {
		this.power = power;
		this.trace = trace;
		progname = "";
		args = new String[0];
		env = new String[0];
		brk = 0;
	}

	private static long pad(long addr) {
		long offset = addr % PAGE_SIZE;
		return PAGE_SIZE - offset;
	}

	public void load(byte[] data) throws IOException {
		load(data, "[program]");
	}

	private static long getLowAddress(Elf elf) {
		long lo = -1;
		for(ProgramHeader hdr : elf.getProgramHeaders()) {
			if(hdr.getType() != Elf.PT_LOAD) {
				continue;
			}
			long a = hdr.getVirtualAddress() - hdr.getOffset();
			if(Long.compareUnsigned(a, lo) < 0) {
				lo = a;
			}
		}
		return lo;
	}

	public void load(byte[] data, String filename) throws IOException {
		elf = new Elf(data);
		if(elf.e_machine != Elf.EM_PPC) {
			throw new IOException("not a ppc64abi32 executable");
		}
		power.getState().ppc64 = elf.ei_class == Elf.ELFCLASS64;
		ptrsz = elf.ei_class == Elf.ELFCLASS64 ? 8 : 4;
		base = 0;

		if(elf.e_type == Elf.ET_DYN) {
			load_bias = DEFAULT_LOAD_BIAS; // avoid mapping things to zero page
		} else {
			load_bias = 0;
		}

		// check if execstack/noexecstack
		for(ProgramHeader hdr : elf.getProgramHeaders()) {
			if(hdr.getType() == Elf.PT_GNU_STACK) {
				execstack = hdr.getFlag(Elf.PF_X);
			}
		}

		// initialize phoff with data from Ehdr
		phoff = load_bias + elf.e_phoff;

		NavigableMap<Long, Symbol> symbols = new TreeMap<>();

		VirtualMemory memory = power.getMemory();
		for(ProgramHeader hdr : elf.getProgramHeaders()) {
			if(hdr.getType() == Elf.PT_LOAD || hdr.getType() == Elf.PT_PHDR) {
				long size = hdr.getMemorySize();
				long offset = load_bias + hdr.getVirtualAddress();
				long fileOffset = hdr.getOffset();
				long segmentEnd = offset + size;
				long pageEnd = memory.roundToPageSize(segmentEnd);
				size += pageEnd - segmentEnd;

				long start = memory.pageStart(offset);
				long off = fileOffset - (offset - start);

				byte[] segment = new byte[(int) size];
				hdr.load(segment);

				// fill start of page with zero if necessary
				size += offset - start;
				byte[] load = new byte[(int) size];
				System.arraycopy(segment, 0, load, (int) (offset - start), segment.length);

				MemoryPage p;
				if(hdr.getFlag(Elf.PF_X)) {
					p = power.loadCode(start, load);
					p.name = filename;
				} else {
					p = power.loadData(start, load);
					p.name = filename;
				}
				p.r = hdr.getFlag(Elf.PF_R);
				p.w = hdr.getFlag(Elf.PF_W);
				p.x = hdr.getFlag(Elf.PF_X) | execstack;

				if(hdr.getType() == Elf.PT_PHDR) {
					phoff = offset;
				} else if(fileOffset == 0) {
					phoff = offset + elf.e_phoff;
				}

				if(trace != null) {
					int prot = 0;
					if(p.r) {
						prot |= Mman.PROT_READ;
					}
					if(p.w) {
						prot |= Mman.PROT_WRITE;
					}
					if(p.x) {
						prot |= Mman.PROT_EXEC;
					}
					try {
						trace.mmap(load_bias + hdr.getVirtualAddress(), p.size, prot,
								Mman.MAP_PRIVATE | Mman.MAP_FIXED, -1, off, p.base,
								filename, load);
					} catch(IOException e) {
						log.log(Levels.ERROR, "Failed to write mmap event: " + e.getMessage(),
								e);
						trace = null;
					}
				}

				long end = load_bias + hdr.getVirtualAddress() + segment.length;
				if(brk < load_bias + hdr.getVirtualAddress() + hdr.getMemorySize()) {
					brk = end;
				}
			}
		}

		SymbolTable symtab = elf.getSymbolTable();
		if(symtab != null) {
			for(Symbol sym : symtab.getSymbols()) {
				if(sym.getSectionIndex() != Symbol.SHN_UNDEF) {
					symbols.put(sym.getValue() + load_bias, sym.offset(load_bias));
				}
			}

			if(trace != null) {
				trace.symtab32(symtab.getSymbols(), load_bias, load_bias, brk - load_bias, filename);
			}
		}

		entry = load_bias + elf.getEntryPoint();
		power.getState().pc = entry;

		Optional<ProgramHeader> interp = elf.getProgramHeaders().stream()
				.filter((x) -> x.getType() == Elf.PT_INTERP).findAny();

		if(interp.isPresent()) {
			base = 0xf8000000L;
			ProgramHeader phinterp = interp.get();
			byte[] segment = new byte[(int) phinterp.getFileSize()];
			phinterp.load(segment);
			String interpreter = CString.str(segment, 0);
			byte[] interpbin;
			try {
				interpbin = loadFile(interpreter);
			} catch(PosixException e) {
				throw new IOException(Errno.toString(e.getErrno()));
			}
			Elf interpelf = new Elf(interpbin);
			if(elf.ei_class != interpelf.ei_class) {
				throw new IOException("invalid interpreter ELFCLASS");
			}

			if(interpelf.e_type == Elf.ET_DYN) {
				long low = getLowAddress(interpelf);
				base -= low;
			} else {
				base = 0;
			}

			long interpend = 0;
			for(ProgramHeader hdr : interpelf.getProgramHeaders()) {
				if(hdr.getType() == Elf.PT_LOAD) {
					// round size to page size
					long size = hdr.getMemorySize();
					long offset = base + hdr.getVirtualAddress();
					long fileOffset = hdr.getOffset();
					long end = offset + size;
					long pageEnd = power.getMemory().roundToPageSize(end);
					size += pageEnd - end;
					interpend = end;

					segment = new byte[(int) size];
					hdr.load(segment);
					MemoryPage p;
					if(hdr.getFlag(Elf.PF_X)) {
						p = power.loadCode(base + hdr.getVirtualAddress(), segment);
						p.name = interpreter;
					} else {
						p = power.loadData(base + hdr.getVirtualAddress(), segment);
						p.name = interpreter;
					}

					p.r = hdr.getFlag(Elf.PF_R);
					p.w = hdr.getFlag(Elf.PF_W);
					p.x = hdr.getFlag(Elf.PF_X);
					memory.add(p);

					if(trace != null) {
						int prot = 0;
						if(p.r) {
							prot |= Mman.PROT_READ;
						}
						if(p.w) {
							prot |= Mman.PROT_WRITE;
						}
						if(p.x) {
							prot |= Mman.PROT_EXEC;
						}
						try {
							trace.mmap(base + hdr.getVirtualAddress(), p.size, prot,
									Mman.MAP_PRIVATE | Mman.MAP_FIXED, -1,
									fileOffset, p.base, interpreter, segment);
						} catch(IOException e) {
							log.log(Levels.ERROR,
									"Failed to write mmap event: " + e.getMessage(),
									e);
							trace = null;
						}
					}
				}
			}

			power.getState().pc = base + interpelf.e_entry;

			symtab = interpelf.getSymbolTable();
			if(symtab != null) {
				for(Symbol sym : symtab.getSymbols()) {
					if(sym.getSectionIndex() != Symbol.SHN_UNDEF) {
						symbols.put(sym.getValue() + base, sym.offset(base));
					}
				}

				if(trace != null) {
					trace.symtab32(symtab.getSymbols(), base, base, interpend - base, interpreter);
				}
			}
		}

		long pad = pad(brk);
		if(pad > 0) {
			MemoryPage padding = new MemoryPage(new ByteMemory(pad, true), brk, pad, "[heap]");
			memory.add(padding);
			brk += pad;
		}
		assert brk % PAGE_SIZE == 0 : String.format("unaligned: 0x%016X", brk);

		power.getMemory().setBrk(brk);
		if(trace != null) {
			try {
				trace.brk(0, brk);
			} catch(IOException e) {
				log.log(Levels.ERROR, "Failed to write brk event: " + e.getMessage(), e);
				trace = null;
			}
		}

		power.setSymbols(symbols);

		if(elf.ei_class == Elf.ELFCLASS32) {
			buildArgs32();
		}
	}

	private static int align16B(int x) {
		if((x & 0xf) != 0) {
			return x + 0x10 - (x & 0xf);
		} else {
			return x;
		}
	}

	private static long str(VirtualMemory mem, long addr, String s) {
		long ptr = addr;
		for(byte b : s.getBytes()) {
			mem.setI8(ptr, b);
			ptr++;
		}
		mem.setI8(ptr, (byte) 0);
		return ptr + 1;
	}

	private static long setPair(VirtualMemory mem, long address, int type, int value) {
		long ptr = address;
		mem.setI32(ptr, type);
		ptr += 4;
		mem.setI32(ptr, value);
		return ptr += 4;
	}

	private void buildArgs32() {
		assert ptrsz == 4;

		int stringSize = progname.length() + 1;
		for(String arg : args) {
			stringSize += arg.length() + 1;
		}
		for(String var : env) {
			stringSize += var.length() + 1;
		}

		int auxvcnt = 24;
		int auxvDataSize = PLATFORM.length() + 1 + RANDOM_SIZE;
		int pointersSize = (args.length + env.length + 3 + (auxvcnt * 2)) * ptrsz;

		stringSize = align16B(stringSize);
		auxvDataSize = align16B(auxvDataSize);
		pointersSize = align16B(pointersSize);

		int size = stringSize + auxvDataSize + pointersSize;

		VirtualMemory mem = power.getMemory();
		long r1 = power.getState().getGPR(1);

		r1 -= size;
		power.getState().setGPR(1, r1);

		long ptr = pointersSize + auxvDataSize + stringSize;

		// strings
		ptr = r1 + pointersSize + auxvDataSize;
		int[] ptrArgs = new int[args.length];
		int[] ptrEnv = new int[env.length];

		for(int i = 0; i < args.length; i++) {
			ptrArgs[i] = (int) ptr;
			ptr = str(mem, ptr, args[i]);
		}

		for(int i = 0; i < env.length; i++) {
			ptrEnv[i] = (int) ptr;
			ptr = str(mem, ptr, env[i]);
		}

		long ptrExecfn = ptr;
		ptr = str(mem, ptr, progname);
		assert ptr - (r1 + pointersSize + auxvDataSize) <= stringSize;

		// auxv data
		ptr = r1 + pointersSize;
		long ptrRandom = ptr;
		Random random = new Random();
		for(int i = 0; i < RANDOM_SIZE / 4; i++) {
			mem.setI32(ptr, random.nextInt());
			ptr += 4;
		}
		long ptrPlatform = ptr;
		for(byte b : PLATFORM.getBytes()) {
			mem.setI8(ptr, b);
			ptr++;
		}

		assert ptr - (r1 + pointersSize) < auxvDataSize;

		// pointers
		ptr = r1;

		// argc
		mem.setI32(ptr, args.length);
		ptr += ptrsz;

		// argv
		for(int i = 0; i < args.length; i++) {
			mem.setI32(ptr, ptrArgs[i]);
			ptr += ptrsz;
		}
		// (nil)
		mem.setI32(ptr, 0);
		ptr += ptrsz;

		// env
		for(int i = 0; i < env.length; i++) {
			mem.setI32(ptr, ptrEnv[i]);
			ptr += ptrsz;
		}
		// (nil)
		mem.setI32(ptr, 0);
		ptr += ptrsz;

		// auxv
		ptr = setPair(mem, ptr, AT_IGNOREPPC, AT_IGNOREPPC);
		ptr = setPair(mem, ptr, AT_IGNOREPPC, AT_IGNOREPPC);
		ptr = setPair(mem, ptr, AT_DCACHEBSIZE, power.getState().dcache_line_size);
		ptr = setPair(mem, ptr, AT_ICACHEBSIZE, power.getState().icache_line_size);
		ptr = setPair(mem, ptr, AT_UCACHEBSIZE, 0);
		if(phoff != -1) {
			ptr = setPair(mem, ptr, AT_PHDR, (int) phoff);
		}
		ptr = setPair(mem, ptr, AT_PHENT, elf.e_phentsize);
		ptr = setPair(mem, ptr, AT_PHNUM, elf.e_phnum);
		ptr = setPair(mem, ptr, AT_PAGESZ, PAGE_SIZE);
		ptr = setPair(mem, ptr, AT_BASE, (int) base);
		ptr = setPair(mem, ptr, AT_FLAGS, 0);
		ptr = setPair(mem, ptr, AT_ENTRY, (int) entry);
		ptr = setPair(mem, ptr, AT_UID, (int) power.getPosixEnvironment().getuid());
		ptr = setPair(mem, ptr, AT_EUID, (int) power.getPosixEnvironment().geteuid());
		ptr = setPair(mem, ptr, AT_GID, (int) power.getPosixEnvironment().getgid());
		ptr = setPair(mem, ptr, AT_EGID, (int) power.getPosixEnvironment().getegid());
		ptr = setPair(mem, ptr, AT_PLATFORM, (int) ptrPlatform);
		ptr = setPair(mem, ptr, AT_HWCAP, HWCAP);
		ptr = setPair(mem, ptr, AT_SECURE, 0);
		ptr = setPair(mem, ptr, AT_BASE_PLATFORM, (int) ptrPlatform);
		ptr = setPair(mem, ptr, AT_RANDOM, (int) ptrRandom);
		ptr = setPair(mem, ptr, AT_HWCAP2, HWCAP2);
		ptr = setPair(mem, ptr, AT_EXECFN, (int) ptrExecfn);
		ptr = setPair(mem, ptr, AT_NULL, 0);

		assert ptr - r1 <= pointersSize;
	}

	public void setProgramName(String progname) {
		this.progname = progname;
	}

	public void setArguments(String... args) {
		this.args = args;
	}

	public void setEnvironment(Map<String, String> environ) {
		env = environ.entrySet().stream().map((e) -> e.getKey() + "=" + e.getValue())
				.toArray(String[]::new);
	}

	private byte[] loadFile(String path) throws PosixException {
		PosixEnvironment posixEnv = power.getPosixEnvironment();
		Posix posix = posixEnv.getPosix();
		int fd = posix.open(path, Fcntl.O_RDONLY, 0);
		Stat stat = new Stat();
		byte[] data = null;
		try {
			posix.fstat(fd, stat);
			data = new byte[(int) stat.st_size];
			int read = posix.read(fd, new BytePosixPointer(data), data.length);
			if(read != data.length) {
				throw new PosixException(Errno.EIO);
			}
		} finally {
			posix.close(fd);
		}
		return data;
	}

	public void load(String filename) throws IOException {
		try {
			byte[] file = loadFile(filename);
			load(file, filename);
		} catch(PosixException e) {
			throw new IOException(filename + ": " + Errno.toString(e.getErrno()));
		}
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		Trace.setupConsoleApplication(Levels.INFO);

		if(args.length < 1) {
			System.out.printf("Usage: %s program [args]\n", ElfLoader.class.getSimpleName());
			System.exit(1);
		}

		String tracefile = System.getProperty("vm.power.trace");
		ExecutionTrace trace = null;
		if(tracefile != null) {
			trace = new PowerExecutionTrace(new File(tracefile));
		}
		Power power = new Power(trace);

		ElfLoader loader = new ElfLoader(power, trace);
		loader.setProgramName(args[0]);
		loader.setArguments(args);
		loader.setEnvironment(System.getenv());

		PosixEnvironment posix = power.getPosixEnvironment();
		ProcessVFS vfs = posix.getVFS();
		Path cwd = Paths.get(".").toAbsolutePath().normalize();
		VFSFileSystem fs;

		String fsroot = System.getProperty("vm.power.fsroot");
		if(fsroot != null) {
			fs = new NativeFileSystem(vfs.getVFS(), fsroot);
			String cwdprop = System.getProperty("vm.power.cwd");
			if(cwdprop != null) {
				cwd = Paths.get(cwdprop);
			} else {
				cwd = Paths.get("/");
			}
		} else {
			fs = new NativeFileSystem(vfs.getVFS(), cwd.getRoot().toString());

			// never touch the host's /etc/ld.so.cache, this would only lead to crashes
			posix.getPosix().blacklistFile("/etc/ld.so.cache");
		}

		try {
			posix.mount("/", fs);
			StringBuilder posixPath = new StringBuilder();
			if(cwd.getNameCount() == 0) {
				posixPath.append('/');
			}
			for(int i = 0; i < cwd.getNameCount(); i++) {
				posixPath.append('/').append(cwd.getName(i));
			}
			posix.getPosix().chdir(posixPath.toString());
		} catch(PosixException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// mount proc filesystem if /proc exists
		try {
			if(vfs.get("/proc") != null) {
				// mount fake procfs
				Procfs proc = new Procfs(vfs.getVFS());
				vfs.mount("/proc", proc);
			}
		} catch(PosixException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// set up executable paths
		String execpath = vfs.resolve(args[0]);
		posix.getPosix().setExecfn(execpath);
		posix.getPosix().setExecpath(execpath);

		// ensure a TID is allocated for the main thread
		int tid = Posix.getTid();
		posix.getPosix().addThread(tid, Thread.currentThread());

		String map = System.getProperty("vm.power.printmapsonexit");
		boolean printmaps = false;
		if(map != null) {
			printmaps = map.equals("1") || map.equals("true");
		}

		int status;
		try {
			String path = vfs.resolve(args[0]);
			loader.load(path);
			status = power.run();
		} finally {
			if(printmaps) {
				power.getMemory().printLayout(System.out);
			}

			try {
				if(trace != null) {
					trace.close();
				}
			} catch(IOException e) {
				log.log(Levels.WARNING, "Failed to close trace file: " + e.getMessage());
			}
		}

		String vmstats = System.getProperty("vm.power.printvmstats");
		boolean printvmstats = false;
		if(vmstats != null) {
			printvmstats = vmstats.equals("1") || vmstats.equals("true");
		}
		if(printvmstats) {
			power.getMemory().printStats(System.out);
		}

		System.exit(status);
	}
}
