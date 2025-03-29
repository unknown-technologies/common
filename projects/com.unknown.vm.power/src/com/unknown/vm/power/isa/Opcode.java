package com.unknown.vm.power.isa;

public class Opcode {
	public static final int TWI = 3;
	public static final int MULLI = 7;
	public static final int SUBFIC = 8;
	public static final int CMPLI = 10;
	public static final int CMPI = 11;
	public static final int ADDIC = 12;
	public static final int ADDIC_ = 13;
	public static final int ADDI = 14;
	public static final int ADDIS = 15;
	public static final int BC = 16;
	public static final int SC = 17;
	public static final int B = 18;
	public static final int CR_OPS = 19;
	public static final int RLWIMI = 20;
	public static final int RLWINM = 21;
	public static final int RLWNM = 23;
	public static final int ORI = 24;
	public static final int ORIS = 25;
	public static final int XORI = 26;
	public static final int XORIS = 27;
	public static final int ANDI = 28;
	public static final int ANDIS = 29;
	public static final int FX_EXTENDED_OPS = 31;
	public static final int LWZ = 32;
	public static final int LWZU = 33;
	public static final int LBZ = 34;
	public static final int LBZU = 35;
	public static final int STW = 36;
	public static final int STWU = 37;
	public static final int STB = 38;
	public static final int STBU = 39;
	public static final int LHZ = 40;
	public static final int LHZU = 41;
	public static final int LHA = 42;
	public static final int LHAU = 43;
	public static final int STH = 44;
	public static final int STHU = 45;
	public static final int LMW = 46;
	public static final int STMW = 47;
	public static final int LFS = 48;
	public static final int LFSU = 49;
	public static final int LFD = 50;
	public static final int LFDU = 51;
	public static final int STFS = 52;
	public static final int STFSU = 53;
	public static final int STFD = 54;
	public static final int STFDU = 55;
	public static final int FP_SINGLE_OPS = 59;
	public static final int VSX_EXTENDED_OPS = 60;
	public static final int FP_DOUBLE_OPS = 63;

	public static final int XO_CMP = 0;
	public static final int XO_MCRF = 0;
	public static final int XO_FCMPU = 0;
	public static final int XO_SUBFC = 8;
	public static final int XO_ADDC = 10;
	public static final int XO_MULHWU = 11;
	public static final int XO_FRSP = 12;
	public static final int XO_FCTIWZ = 15;
	public static final int XO_BCLR = 16;
	public static final int XO_FDIV = 18;
	public static final int XO_FDIVS = 18;
	public static final int XO_MFCR = 19;
	public static final int XO_LWARX = 20;
	public static final int XO_FSUB = 20;
	public static final int XO_FSUBS = 20;
	public static final int XO_FADD = 21;
	public static final int XO_FADDS = 21;
	public static final int XO_LWZX = 23;
	public static final int XO_SLW = 24;
	public static final int XO_FMUL = 25;
	public static final int XO_FMULS = 25;
	public static final int XO_CNTLZW = 26;
	public static final int XO_AND = 28;
	public static final int XO_FMSUB = 28;
	public static final int XO_FMSUBS = 28;
	public static final int XO_FMADD = 29;
	public static final int XO_FMADDS = 29;
	public static final int XO_FNMSUB = 30;
	public static final int XO_FNMSUBS = 30;
	public static final int XO_FNMADD = 31;
	public static final int XO_FNMADDS = 31;
	public static final int XO_CMPL = 32;
	public static final int XO_SUBF = 40;
	public static final int XO_FNEG = 40;
	public static final int XO_DCBST = 54;
	public static final int XO_LWZUX = 55;
	public static final int XO_ANDC = 60;
	public static final int XO_FMR = 72;
	public static final int XO_MULHW = 75;
	public static final int XO_LBZX = 87;
	public static final int XO_LVX = 103;
	public static final int XO_NEG = 104;
	public static final int XO_LBZUX = 119;
	public static final int XO_NOR = 124;
	public static final int XO_MTFSFI = 134;
	public static final int XO_SUBFE = 136;
	public static final int XO_FNABS = 136;
	public static final int XO_ADDE = 138;
	public static final int XO_MTCRF = 144;
	public static final int XO_ISYNC = 150;
	public static final int XO_STWCX_ = 150;
	public static final int XO_STWX = 151;
	public static final int XO_XXLXOR = 154;
	public static final int XO_STWUX = 183;
	public static final int XO_CRXOR = 193;
	public static final int XO_SUBFZE = 200;
	public static final int XO_ADDZE = 202;
	public static final int XO_STBX = 215;
	public static final int XO_STVX = 231;
	public static final int XO_ADDME = 234;
	public static final int XO_MULLW = 235;
	public static final int XO_DCBTST = 246;
	public static final int XO_STBUX = 247;
	public static final int XO_FABS = 264;
	public static final int XO_ADD = 266;
	public static final int XO_DCBT = 278;
	public static final int XO_LHZX = 279;
	public static final int XO_EQV = 284;
	public static final int XO_CREQV = 289;
	public static final int XO_LHZUX = 311;
	public static final int XO_XOR = 316;
	public static final int XO_MFSPR = 339;
	public static final int XO_LHAX = 343;
	public static final int XO_STHX = 407;
	public static final int XO_ORC = 412;
	public static final int XO_CRORC = 417;
	public static final int XO_OR = 444;
	public static final int XO_CROR = 449;
	public static final int XO_DIVWU = 459;
	public static final int XO_MTSPR = 467;
	public static final int XO_NAND = 476;
	public static final int XO_DIVW = 491;
	public static final int XO_BCCTR = 528;
	public static final int XO_LWBRX = 534;
	public static final int XO_LFSX = 535;
	public static final int XO_SRW = 536;
	public static final int XO_MFFS = 583;
	public static final int XO_LSWI = 597;
	public static final int XO_SYNC = 598;
	public static final int XO_LFDX = 599;
	public static final int XO_STWBRX = 662;
	public static final int XO_STFSX = 663;
	public static final int XO_STFSUX = 695;
	public static final int XO_MTFSF = 711;
	public static final int XO_STSWI = 725;
	public static final int XO_STFDX = 727;
	public static final int XO_LHBRX = 790;
	public static final int XO_SRAW = 792;
	public static final int XO_SRAWI = 824;
	public static final int XO_FCFID = 846;
	public static final int XO_LFIWAX = 855;
	public static final int XO_STHBRX = 918;
	public static final int XO_EXTSH = 922;
	public static final int XO_EXTSB = 954;
	public static final int XO_ICBI = 982;
	public static final int XO_DCBZ = 1014;
}
