package com.unknown.vm.power;

import java.math.RoundingMode;

import com.unknown.util.BitTest;
import com.unknown.vm.ArchitecturalState;
import com.unknown.vm.memory.MemoryPage;
import com.unknown.vm.memory.VirtualMemory;
import com.unknown.vm.posix.PosixEnvironment;
import com.unknown.vm.posix.SyscallException;
import com.unknown.vm.power.isa.Cr;
import com.unknown.vm.power.isa.Fpscr;
import com.unknown.vm.register.Vector128;

public class PowerState extends ArchitecturalState {
	public long[] gpr;
	public Vector128[] vsr;
	public long pc;
	public long lr;
	public long ctr;
	public int cr;
	public long fpscr;
	public long xer;
	public int vrsave;

	public int dcache_line_size;
	public int icache_line_size;

	public boolean ppc64;

	private VirtualMemory memory;

	private Syscall system;

	public PowerState(VirtualMemory memory, PosixEnvironment posix) {
		this.memory = memory;
		this.system = new Syscall(this, posix);

		gpr = new long[32];
		vsr = new Vector128[64];
		for(int i = 0; i < gpr.length; i++) {
			gpr[i] = 0;
			vsr[i] = new Vector128();
		}
		pc = 0;

		ppc64 = false;
	}

	private PowerState(PowerState state) {
		this.memory = state.memory;
		this.system = state.system;

		this.gpr = new long[32];
		this.vsr = new Vector128[64];
		for(int i = 0; i < gpr.length; i++) {
			gpr[i] = state.gpr[i];
			vsr[i] = new Vector128(state.vsr[i]);
		}
		this.pc = state.pc;
		this.lr = state.lr;
		this.ctr = state.ctr;
		this.cr = state.cr;
		this.fpscr = state.fpscr;
		this.xer = state.xer;
		this.vrsave = state.vrsave;
		this.ppc64 = state.ppc64;
	}

	public long getGPR(int i) {
		return gpr[i];
	}

	public long getGPR0(int i) {
		if(i == 0) {
			return 0;
		} else {
			return getGPR(i);
		}
	}

	public void setGPR(int i, long val) {
		gpr[i] = val;
	}

	public long getFPR(int i) {
		return vsr[i].getI64(1);
	}

	public void setFPR(int i, long val) {
		vsr[i].setI64(1, val);
	}

	public void setFPR(int i, double val) {
		vsr[i].setF64(1, val);
	}

	public double getFPRf(int i) {
		return vsr[i].getF64(1);
	}

	public Vector128 getVSR(int i) {
		return vsr[i];
	}

	public void setVSR(int i, Vector128 val) {
		vsr[i] = val;
	}

	public void setCR0SV() {
		int value = 1 << (3 - Cr.SO);
		int shift = 7 * 4;
		cr |= value << shift;
	}

	public void clearCR0SV() {
		int value = 1 << (3 - Cr.SO);
		int shift = 7 * 4;
		cr &= ~(value << shift);
	}

	public void setCR(int n, boolean lt, boolean gt, boolean eq, boolean so) {
		int value = 0;
		if(lt) {
			value |= 1 << (3 - Cr.LT);
		}
		if(gt) {
			value |= 1 << (3 - Cr.GT);
		}
		if(eq) {
			value |= 1 << (3 - Cr.EQ);
		}
		if(so) {
			value |= 1 << (3 - Cr.SO);
		}
		int shift = (7 - n) * 4;
		int mask = 0xf << shift;
		cr = (cr & ~mask) | (value << shift);
	}

	public void setCR0(long val) {
		long v = ppc64 ? val : (int) val;
		setCR(0, v < 0, v > 0, v == 0, getSO());
	}

	public boolean getSO() {
		return (xer & (1 << 31)) != 0;
	}

	public void setSO() {
		xer |= (1 << 31);
	}

	public void setOV() {
		xer |= (1 << 30);
		setSO();
	}

	public void setCA(boolean value) {
		if(value) {
			xer |= 1 << 29;
		} else {
			xer &= ~(1 << 29);
		}
	}

	public boolean getCA() {
		return (xer & (1 << 29)) != 0;
	}

	public void setFPCC(int c) {
		int mask = ~0xf000;
		int cc = c << 12;
		fpscr = (fpscr & mask) | cc;
	}

	public void setC(boolean c) {
		int mask = ~0x10000;
		int cc = (c ? 1 : 0) << 16;
		fpscr = (fpscr & mask) | cc;
	}

	public void setFPSCR(long newFPSCR) {
		boolean fex = Fpscr.isEnabledException(newFPSCR);
		long scr = newFPSCR;
		if(fex) {
			scr |= Fpscr.bit(Fpscr.FEX);
		} else {
			scr &= ~Fpscr.bit(Fpscr.FEX);
		}
		boolean vx = Fpscr.isInvalidOperationException(newFPSCR);
		if(vx) {
			scr |= Fpscr.bit(Fpscr.VX);
		} else {
			scr &= ~Fpscr.bit(Fpscr.VX);
		}
		fpscr = scr;
	}

	public void setFR() {
		fpscr |= Fpscr.bit(Fpscr.FR);
		fpscr |= Fpscr.bit(Fpscr.FX);
	}

	public void setFI() {
		fpscr |= Fpscr.bit(Fpscr.FI);
		fpscr |= Fpscr.bit(Fpscr.XX);
		if(BitTest.test(fpscr, Fpscr.XE)) {
			fpscr |= Fpscr.bit(Fpscr.FEX);
		}
		fpscr |= Fpscr.bit(Fpscr.FX);
	}

	public void setVXSNAN() {
		fpscr |= Fpscr.bit(Fpscr.VXSNAN);
		fpscr |= Fpscr.bit(Fpscr.FX);
		fpscr |= Fpscr.bit(Fpscr.VX);
	}

	public void updateFPSCR(long val) {
		updateFPSCR(Double.longBitsToDouble(val));
	}

	public void updateFPSCR(double val) {
		boolean c;
		int cc;
		if(Double.isNaN(val)) {
			c = true;
			cc = 0b0001;
			fpscr |= Fpscr.bit(Fpscr.FX) | Fpscr.bit(Fpscr.VX) | Fpscr.bit(Fpscr.VXSNAN);
		} else if(Double.isFinite(val)) {
			if(Double.valueOf(-0.0).equals(val)) {
				assert val == 0;
				c = true;
				cc = 0b0010;
			} else if(val == 0.0) {
				assert val == 0;
				c = false;
				cc = 0b0010;
			} else if(val < 0) {
				c = false;
				cc = 0b1000;
			} else if(val > 0) {
				c = false;
				cc = 0b0100;
			} else {
				throw new AssertionError();
			}
		} else if(val < 0) {
			assert Double.isInfinite(val);
			c = false;
			cc = 0b1001;
		} else if(val > 0) {
			assert Double.isInfinite(val);
			c = false;
			cc = 0b0101;
		} else {
			throw new AssertionError();
		}
		setC(c);
		setFPCC(cc);
	}

	public RoundingMode getRoundingMode() {
		switch((int) (fpscr & 3)) {
		case 0:
			return RoundingMode.HALF_EVEN;
		case 1:
			return RoundingMode.DOWN;
		case 2:
			return RoundingMode.CEILING;
		case 3:
			return RoundingMode.FLOOR;
		default:
			throw new AssertionError();
		}
	}

	public VirtualMemory getMemory() {
		return memory;
	}

	public void icbi(long address) {
		MemoryPage codePage = memory.get(address);
		if(codePage == null || !(codePage.code instanceof PowerCode)) {
			throw new RuntimeException(String.format("not executable: 0x%016X", address));
		}
		PowerCode code = (PowerCode) codePage.code;
		code.invalidate();
	}

	public long syscall(int nr, long a1, long a2, long a3, long a4, long a5, long a6, long a7)
			throws SyscallException {
		return system.execute(nr, a1, a2, a3, a4, a5, a6, a7);
	}

	private static boolean test(int x, int i) {
		return (x & (1 << (3 - i))) != 0;
	}

	private String strcr(int i) {
		StringBuilder buf = new StringBuilder(3);
		Cr field = new Cr(i);
		int val = field.get(cr);
		if(test(val, Cr.EQ)) {
			buf.append('E');
		}
		if(test(val, Cr.SO)) {
			buf.append('O');
		}
		if(test(val, Cr.GT)) {
			buf.append('G');
		}
		if(test(val, Cr.LT)) {
			buf.append('L');
		}
		if(buf.length() == 0) {
			return " - ";
		} else if(buf.length() == 1) {
			return " " + buf + " ";
		} else if(buf.length() == 2) {
			return " " + buf;
		} else {
			return buf.toString();
		}
	}

	public void dump() {
		if(!ppc64) {
			System.out.printf("NIP %08x   LR %08x CTR %08x XER %08x CPU#0\n", pc, lr, ctr, xer);
			System.out.printf("MSR %08x HID0 %08x  HF %08x iidx %d didx %d\n", 0, 0, 0, 0, 0);
			System.out.printf("GPR00 %016x %016x %016x %016x\n", getGPR(0), getGPR(1), getGPR(2),
					getGPR(3));
			System.out.printf("GPR04 %016x %016x %016x %016x\n", getGPR(4), getGPR(5), getGPR(6),
					getGPR(7));
			System.out.printf("GPR08 %016x %016x %016x %016x\n", getGPR(8), getGPR(9), getGPR(10),
					getGPR(11));
			System.out.printf("GPR12 %016x %016x %016x %016x\n", getGPR(12), getGPR(13), getGPR(14),
					getGPR(15));
			System.out.printf("GPR16 %016x %016x %016x %016x\n", getGPR(16), getGPR(17), getGPR(18),
					getGPR(19));
			System.out.printf("GPR20 %016x %016x %016x %016x\n", getGPR(20), getGPR(21), getGPR(22),
					getGPR(23));
			System.out.printf("GPR24 %016x %016x %016x %016x\n", getGPR(24), getGPR(25), getGPR(26),
					getGPR(27));
			System.out.printf("GPR28 %016x %016x %016x %016x\n", getGPR(28), getGPR(29), getGPR(30),
					getGPR(31));
			System.out.printf("CR %08X  [%s%s%s%s%s%s%s%s ]             RES %08x\n", cr, strcr(0), strcr(1),
					strcr(2), strcr(3), strcr(4), strcr(5), strcr(6), strcr(7), 0);
			System.out.printf("FPR00 %016x %016x %016x %016x\n", getFPR(0), getFPR(1), getFPR(2),
					getFPR(3));
			System.out.printf("FPR04 %016x %016x %016x %016x\n", getFPR(4), getFPR(5), getFPR(6),
					getFPR(7));
			System.out.printf("FPR08 %016x %016x %016x %016x\n", getFPR(8), getFPR(9), getFPR(10),
					getFPR(11));
			System.out.printf("FPR12 %016x %016x %016x %016x\n", getFPR(12), getFPR(13), getFPR(14),
					getFPR(15));
			System.out.printf("FPR16 %016x %016x %016x %016x\n", getFPR(16), getFPR(17), getFPR(18),
					getFPR(19));
			System.out.printf("FPR20 %016x %016x %016x %016x\n", getFPR(20), getFPR(21), getFPR(22),
					getFPR(23));
			System.out.printf("FPR24 %016x %016x %016x %016x\n", getFPR(24), getFPR(25), getFPR(26),
					getFPR(27));
			System.out.printf("FPR28 %016x %016x %016x %016x\n", getFPR(28), getFPR(29), getFPR(30),
					getFPR(31));
			System.out.printf("FPSCR %08x\n", fpscr);
		}
	}

	@Override
	public PowerState clone() {
		return new PowerState(this);
	}
}
