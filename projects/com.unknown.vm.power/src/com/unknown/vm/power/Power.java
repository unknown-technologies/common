package com.unknown.vm.power;

import java.io.IOException;
import java.util.Collections;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.logging.Logger;

import com.unknown.posix.api.Posix;
import com.unknown.posix.api.ProcessExitException;
import com.unknown.posix.api.mem.Mman;
import com.unknown.posix.elf.Symbol;
import com.unknown.posix.elf.SymbolResolver;
import com.unknown.util.log.Levels;
import com.unknown.util.log.Trace;
import com.unknown.vm.ExecutionTrace;
import com.unknown.vm.exceptions.SegmentationViolation;
import com.unknown.vm.memory.ByteMemory;
import com.unknown.vm.memory.Memory;
import com.unknown.vm.memory.MemoryPage;
import com.unknown.vm.memory.VirtualMemory;
import com.unknown.vm.posix.PosixEnvironment;

public class Power {
	private static final Logger log = Trace.create(Power.class);

	public static final long STACK_SIZE = 1024 * 1024; // 1M
	// public static final long STACK_ADDRESS = 0x7fff6c845000L;
	public static final long STACK_ADDRESS = 0xf6fff000L;
	public static final long STACK_BASE = STACK_ADDRESS - STACK_SIZE;

	public static final int DCACHE_LINE_SIZE = 0x20;
	public static final int ICACHE_LINE_SIZE = 0x20;

	private PowerState state;
	private VirtualMemory memory;
	private MemoryPage stack;
	private PosixEnvironment posix;

	private PowerCode code;
	private MemoryPage codePage;

	private NavigableMap<Long, Symbol> symbols;
	private SymbolResolver symbolResolver;

	private boolean debug = false;
	private String debugStart = null;

	private long breakpoint = -1;

	private ExecutionTrace trace;

	public Power() {
		this(new Posix(), null);
	}

	public Power(Posix posix) {
		this(posix, null);
	}

	public Power(ExecutionTrace trc) {
		this(new Posix(), trc);
	}

	public Power(Posix posix, ExecutionTrace trc) {
		memory = new VirtualMemory();
		this.posix = new PosixEnvironment(memory, PowerCode::new, "ppc", posix, trc != null);
		setTrace(trc);
		state = new PowerState(memory, this.posix);
		state.dcache_line_size = DCACHE_LINE_SIZE;
		state.icache_line_size = ICACHE_LINE_SIZE;
		symbols = Collections.emptyNavigableMap();
		symbolResolver = new SymbolResolver(symbols);
		long stackbase = memory.pageStart(STACK_BASE);
		long stacksize = memory.roundToPageSize(STACK_SIZE);
		Memory stackMemory = new ByteMemory(stacksize);
		stack = new MemoryPage(stackMemory, stackbase, stacksize, "[stack]");
		memory.add(stack);
		// Value sp = new Value(new Pointer(stackMemory, STACK_ADDRESS - STACK_BASE));
		long sp = STACK_ADDRESS - 16;
		state.setGPR(1, sp);
		assert (sp & 0xf) == 0;
		// assert (sp.getPointer().getOffset() & 0xf) == 0 : String.format("%X", sp.getPointer().getOffset());
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setDebugStart(String symbol) {
		debugStart = symbol;
	}

	public void setBreakpoint(long pc) {
		breakpoint = pc;
	}

	public void setBreakpoint(String name) {
		Optional<Long> symbol = symbols.entrySet().stream().filter((e) -> e.getValue().getName().equals(name))
				.map((x) -> x.getKey()).findAny();
		if(!symbol.isPresent()) {
			throw new IllegalArgumentException("Symbol '" + name + "' does not exist");
		}
		setBreakpoint(symbol.get());
	}

	public void setTrace(ExecutionTrace trace) {
		this.trace = trace;
		posix.setTrace(trace);
		memory.setTrace(trace);
		if(trace == null) {
			return;
		}
		try {
			long stackbase = memory.pageStart(STACK_BASE);
			long stacksize = memory.roundToPageSize(STACK_SIZE);
			trace.mmap(stackbase, stacksize, Mman.PROT_READ | Mman.PROT_WRITE,
					Mman.MAP_ANONYMOUS | Mman.MAP_PRIVATE, -1, 0, stackbase, "[stack]", null);
		} catch(IOException e) {
			log.log(Levels.ERROR, "Failed to write mmap event: " + e.getMessage(), e);
			this.trace = null;
		}
	}

	public PosixEnvironment getPosixEnvironment() {
		return posix;
	}

	public void setSymbols(NavigableMap<Long, Symbol> symbols) {
		this.symbols = symbols;
		symbolResolver = new SymbolResolver(symbols);
	}

	public MemoryPage loadCode(long address, byte[] binary) {
		MemoryPage mem = new MemoryPage(new ByteMemory(binary), address, memory.roundToPageSize(binary.length),
				"[code]");
		memory.add(mem);
		code = new PowerCode(mem);
		mem.code = code;
		return mem;
	}

	public MemoryPage loadData(long address, byte[] binary) {
		MemoryPage page = new MemoryPage(new ByteMemory(binary), address, memory.roundToPageSize(binary.length),
				"[data]");
		memory.add(page);
		return page;
	}

	public PowerState getState() {
		return state;
	}

	public MemoryPage getStack() {
		return stack;
	}

	public VirtualMemory getMemory() {
		return memory;
	}

	public String disassemble(long pc, long end) {
		StringBuilder buf = new StringBuilder();
		for(long ip = pc; ip < end; ip += 4) {
			MemoryPage page = memory.get(ip);
			if(page.isExecutable()) {
				PowerCode c = (PowerCode) page.code;
				buf.append(c.disassemble(ip, ip + 4));
				buf.append('\n');
			}
		}
		return buf.toString().trim();
	}

	public Symbol getSymbol(long pc) {
		Symbol sym = symbolResolver.getSymbol(pc);
		if(sym == null) {
			return posix.getSymbol(pc);
		} else {
			return sym;
		}
	}

	private void getCode() {
		codePage = memory.get(state.pc);
		if(codePage == null || !(codePage.code instanceof PowerCode)) {
			throw new RuntimeException(String.format("not executable: 0x%016X", state.pc));
		}
		code = (PowerCode) codePage.code;
	}

	public void run(long steps) {
		getCode();
		for(long i = 0; i < steps; i++) {
			state.pc = code.executeSlow(state.pc, state);
		}
	}

	public int run() {
		try {
			getCode();
			while(true) {
				state.pc = memory.addr(state.pc);
				long oldpc = state.pc;
				if(breakpoint == state.pc) {
					setDebug(true);
				}
				if(debugStart != null) {
					Symbol sym = symbols.get(state.pc);
					if(sym != null && sym.getName().equals(debugStart)) {
						debugStart = null;
						setDebug(true);
					}
				}
				if(trace != null) {
					code.setTrace(trace);
				}
				try {
					state.pc = code.execute(state.pc, state);
				} catch(SegmentationViolation e) {
					if(debug) {
						System.err.printf("SIGSEGV: %s\n", e);
					}
					throw e;
				}
				if(state.pc != oldpc + 4 && !codePage.contains(state.pc)) {
					getCode();
				}
			}
		} catch(ProcessExitException e) {
			return e.getCode();
		} catch(SegmentationViolation e) {
			Symbol sym = getSymbol(state.pc);
			if(sym != null) {
				System.err.printf("In function '%s':\n", sym.getName());
			}
			System.err.printf("Instruction at 0x%016X (0x%08X): %s\n", state.pc - 4,
					memory.getI32(state.pc - 4), e.toString());
			memory.printLayout(System.err);
			throw e;
		} catch(Throwable t) {
			Symbol sym = getSymbol(state.pc);
			if(sym != null) {
				System.err.printf("In function '%s':\n", sym.getName());
			}
			memory.printLayout(System.err);
			throw t;
		}
	}
}
