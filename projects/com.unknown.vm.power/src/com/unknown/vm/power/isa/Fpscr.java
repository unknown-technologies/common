package com.unknown.vm.power.isa;

import com.unknown.util.BitTest;

public class Fpscr {
	public static final int FX = 32;
	public static final int FEX = 33;
	public static final int VX = 34;
	public static final int OX = 35;
	public static final int UX = 36;
	public static final int ZX = 37;
	public static final int XX = 38;
	public static final int VXSNAN = 39;
	public static final int VXISI = 40;
	public static final int VXIDI = 41;
	public static final int VXZDZ = 42;
	public static final int VXIMZ = 43;
	public static final int VXVC = 44;
	public static final int FR = 45;
	public static final int FI = 46;
	public static final int C = 47;
	public static final int FL = 48;
	public static final int FG = 49;
	public static final int FE = 50;
	public static final int FU = 51;
	public static final int VXSOFT = 53;
	public static final int VXSQRT = 54;
	public static final int VXCVI = 55;
	public static final int VE = 56;
	public static final int OE = 57;
	public static final int UE = 58;
	public static final int ZE = 59;
	public static final int XE = 60;
	public static final int NI = 61;

	public static long bit(int i) {
		return 1L << (63 - i);
	}

	public static boolean isEnabledException(long fpscr) {
		boolean oe = BitTest.test(fpscr, bit(OE));
		boolean ue = BitTest.test(fpscr, bit(UE));
		boolean ze = BitTest.test(fpscr, bit(ZE));
		boolean xe = BitTest.test(fpscr, bit(XE));
		boolean o = oe && BitTest.test(fpscr, OX);
		boolean u = ue && BitTest.test(fpscr, UX);
		boolean z = ze && BitTest.test(fpscr, ZX);
		boolean x = xe && BitTest.test(fpscr, XX);
		return o || u || z || x;
	}

	public static boolean isInvalidOperationException(long fpscr) {
		boolean snan = BitTest.test(fpscr, bit(VXSNAN));
		boolean isi = BitTest.test(fpscr, bit(VXISI));
		boolean idi = BitTest.test(fpscr, bit(VXIDI));
		boolean zdz = BitTest.test(fpscr, bit(VXZDZ));
		boolean imz = BitTest.test(fpscr, bit(VXIMZ));
		boolean vc = BitTest.test(fpscr, bit(VXVC));
		boolean soft = BitTest.test(fpscr, bit(VXSOFT));
		boolean sqrt = BitTest.test(fpscr, bit(VXSQRT));
		boolean cvi = BitTest.test(fpscr, bit(VXCVI));
		return snan | isi | idi | zdz | imz | vc | soft | sqrt | cvi;
	}
}
