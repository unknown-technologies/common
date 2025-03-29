package com.unknown.vm.power.isa;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.unknown.math.Addition;
import com.unknown.vm.ArchitecturalState;
import com.unknown.vm.Instruction;
import com.unknown.vm.power.PowerState;
import com.unknown.vm.power.isa.instruction.Add;
import com.unknown.vm.power.isa.instruction.Addc;
import com.unknown.vm.power.isa.instruction.Adde;
import com.unknown.vm.power.isa.instruction.Addi;
import com.unknown.vm.power.isa.instruction.Addic;
import com.unknown.vm.power.isa.instruction.Addic_;
import com.unknown.vm.power.isa.instruction.Addis;
import com.unknown.vm.power.isa.instruction.Addme;
import com.unknown.vm.power.isa.instruction.Addze;
import com.unknown.vm.power.isa.instruction.And;
import com.unknown.vm.power.isa.instruction.Andc;
import com.unknown.vm.power.isa.instruction.Andi;
import com.unknown.vm.power.isa.instruction.Andis;
import com.unknown.vm.power.isa.instruction.B;
import com.unknown.vm.power.isa.instruction.Bc;
import com.unknown.vm.power.isa.instruction.Bcctr;
import com.unknown.vm.power.isa.instruction.Bclr;
import com.unknown.vm.power.isa.instruction.Cmp;
import com.unknown.vm.power.isa.instruction.Cmpi;
import com.unknown.vm.power.isa.instruction.Cmpl;
import com.unknown.vm.power.isa.instruction.Cmpli;
import com.unknown.vm.power.isa.instruction.Cntlzw;
import com.unknown.vm.power.isa.instruction.Creqv;
import com.unknown.vm.power.isa.instruction.Cror;
import com.unknown.vm.power.isa.instruction.Crorc;
import com.unknown.vm.power.isa.instruction.Crxor;
import com.unknown.vm.power.isa.instruction.Dcbst;
import com.unknown.vm.power.isa.instruction.Dcbt;
import com.unknown.vm.power.isa.instruction.Dcbtst;
import com.unknown.vm.power.isa.instruction.Dcbz;
import com.unknown.vm.power.isa.instruction.Divw;
import com.unknown.vm.power.isa.instruction.Divwu;
import com.unknown.vm.power.isa.instruction.Eqv;
import com.unknown.vm.power.isa.instruction.Extsb;
import com.unknown.vm.power.isa.instruction.Extsh;
import com.unknown.vm.power.isa.instruction.Fabs;
import com.unknown.vm.power.isa.instruction.Fadd;
import com.unknown.vm.power.isa.instruction.Fadds;
import com.unknown.vm.power.isa.instruction.Fcfid;
import com.unknown.vm.power.isa.instruction.Fcmpu;
import com.unknown.vm.power.isa.instruction.Fctiwz;
import com.unknown.vm.power.isa.instruction.Fdiv;
import com.unknown.vm.power.isa.instruction.Fdivs;
import com.unknown.vm.power.isa.instruction.Fmadd;
import com.unknown.vm.power.isa.instruction.Fmadds;
import com.unknown.vm.power.isa.instruction.Fmr;
import com.unknown.vm.power.isa.instruction.Fmsub;
import com.unknown.vm.power.isa.instruction.Fmsubs;
import com.unknown.vm.power.isa.instruction.Fmul;
import com.unknown.vm.power.isa.instruction.Fmuls;
import com.unknown.vm.power.isa.instruction.Fnabs;
import com.unknown.vm.power.isa.instruction.Fneg;
import com.unknown.vm.power.isa.instruction.Fnmadd;
import com.unknown.vm.power.isa.instruction.Fnmadds;
import com.unknown.vm.power.isa.instruction.Fnmsub;
import com.unknown.vm.power.isa.instruction.Fnmsubs;
import com.unknown.vm.power.isa.instruction.Frsp;
import com.unknown.vm.power.isa.instruction.Fsub;
import com.unknown.vm.power.isa.instruction.Fsubs;
import com.unknown.vm.power.isa.instruction.Icbi;
import com.unknown.vm.power.isa.instruction.Isync;
import com.unknown.vm.power.isa.instruction.Lbz;
import com.unknown.vm.power.isa.instruction.Lbzu;
import com.unknown.vm.power.isa.instruction.Lbzux;
import com.unknown.vm.power.isa.instruction.Lbzx;
import com.unknown.vm.power.isa.instruction.Lfd;
import com.unknown.vm.power.isa.instruction.Lfdu;
import com.unknown.vm.power.isa.instruction.Lfdx;
import com.unknown.vm.power.isa.instruction.Lfiwax;
import com.unknown.vm.power.isa.instruction.Lfs;
import com.unknown.vm.power.isa.instruction.Lfsu;
import com.unknown.vm.power.isa.instruction.Lfsx;
import com.unknown.vm.power.isa.instruction.Lha;
import com.unknown.vm.power.isa.instruction.Lhau;
import com.unknown.vm.power.isa.instruction.Lhax;
import com.unknown.vm.power.isa.instruction.Lhbrx;
import com.unknown.vm.power.isa.instruction.Lhz;
import com.unknown.vm.power.isa.instruction.Lhzu;
import com.unknown.vm.power.isa.instruction.Lhzux;
import com.unknown.vm.power.isa.instruction.Lhzx;
import com.unknown.vm.power.isa.instruction.Lmw;
import com.unknown.vm.power.isa.instruction.Lswi;
import com.unknown.vm.power.isa.instruction.Lvx;
import com.unknown.vm.power.isa.instruction.Lwarx;
import com.unknown.vm.power.isa.instruction.Lwbrx;
import com.unknown.vm.power.isa.instruction.Lwz;
import com.unknown.vm.power.isa.instruction.Lwzu;
import com.unknown.vm.power.isa.instruction.Lwzux;
import com.unknown.vm.power.isa.instruction.Lwzx;
import com.unknown.vm.power.isa.instruction.Mcrf;
import com.unknown.vm.power.isa.instruction.Mfcr;
import com.unknown.vm.power.isa.instruction.Mffs;
import com.unknown.vm.power.isa.instruction.Mfspr;
import com.unknown.vm.power.isa.instruction.Mtcrf;
import com.unknown.vm.power.isa.instruction.Mtfsf;
import com.unknown.vm.power.isa.instruction.Mtfsfi;
import com.unknown.vm.power.isa.instruction.Mtspr;
import com.unknown.vm.power.isa.instruction.Mulhw;
import com.unknown.vm.power.isa.instruction.Mulhwu;
import com.unknown.vm.power.isa.instruction.Mulli;
import com.unknown.vm.power.isa.instruction.Mullw;
import com.unknown.vm.power.isa.instruction.Nand;
import com.unknown.vm.power.isa.instruction.Neg;
import com.unknown.vm.power.isa.instruction.Nor;
import com.unknown.vm.power.isa.instruction.Or;
import com.unknown.vm.power.isa.instruction.Orc;
import com.unknown.vm.power.isa.instruction.Ori;
import com.unknown.vm.power.isa.instruction.Oris;
import com.unknown.vm.power.isa.instruction.Rlwimi;
import com.unknown.vm.power.isa.instruction.Rlwinm;
import com.unknown.vm.power.isa.instruction.Rlwnm;
import com.unknown.vm.power.isa.instruction.Sc;
import com.unknown.vm.power.isa.instruction.Slw;
import com.unknown.vm.power.isa.instruction.Sraw;
import com.unknown.vm.power.isa.instruction.Srawi;
import com.unknown.vm.power.isa.instruction.Srw;
import com.unknown.vm.power.isa.instruction.Stb;
import com.unknown.vm.power.isa.instruction.Stbu;
import com.unknown.vm.power.isa.instruction.Stbux;
import com.unknown.vm.power.isa.instruction.Stbx;
import com.unknown.vm.power.isa.instruction.Stfd;
import com.unknown.vm.power.isa.instruction.Stfdu;
import com.unknown.vm.power.isa.instruction.Stfdx;
import com.unknown.vm.power.isa.instruction.Stfs;
import com.unknown.vm.power.isa.instruction.Stfsu;
import com.unknown.vm.power.isa.instruction.Stfsux;
import com.unknown.vm.power.isa.instruction.Stfsx;
import com.unknown.vm.power.isa.instruction.Sth;
import com.unknown.vm.power.isa.instruction.Sthbrx;
import com.unknown.vm.power.isa.instruction.Sthu;
import com.unknown.vm.power.isa.instruction.Sthx;
import com.unknown.vm.power.isa.instruction.Stmw;
import com.unknown.vm.power.isa.instruction.Stswi;
import com.unknown.vm.power.isa.instruction.Stvx;
import com.unknown.vm.power.isa.instruction.Stw;
import com.unknown.vm.power.isa.instruction.Stwbrx;
import com.unknown.vm.power.isa.instruction.Stwcx_;
import com.unknown.vm.power.isa.instruction.Stwu;
import com.unknown.vm.power.isa.instruction.Stwux;
import com.unknown.vm.power.isa.instruction.Stwx;
import com.unknown.vm.power.isa.instruction.Subf;
import com.unknown.vm.power.isa.instruction.Subfc;
import com.unknown.vm.power.isa.instruction.Subfe;
import com.unknown.vm.power.isa.instruction.Subfic;
import com.unknown.vm.power.isa.instruction.Subfze;
import com.unknown.vm.power.isa.instruction.Sync;
import com.unknown.vm.power.isa.instruction.Twi;
import com.unknown.vm.power.isa.instruction.Xor;
import com.unknown.vm.power.isa.instruction.Xori;
import com.unknown.vm.power.isa.instruction.Xoris;
import com.unknown.vm.power.isa.instruction.Xxlxor;

public abstract class PowerInstruction extends Instruction {
	protected final long pc;
	private final int insn;

	protected PowerInstruction(long pc, InstructionFormat insn) {
		this(pc, insn.value);
	}

	protected PowerInstruction(long pc, int insn) {
		this.pc = pc;
		this.insn = insn;
	}

	public static PowerInstruction decode(long pc, int word) {
		String PC = String.format("%016X: ", pc);
		InstructionFormat insn = new InstructionFormat(word);
		switch(insn.OPCD.get()) {
		case Opcode.TWI:
			return new Twi(pc, insn);
		case Opcode.MULLI:
			return new Mulli(pc, insn);
		case Opcode.SUBFIC:
			return new Subfic(pc, insn);
		case Opcode.CMPLI:
			return new Cmpli(pc, insn);
		case Opcode.CMPI:
			return new Cmpi(pc, insn);
		case Opcode.ADDIC:
			return new Addic(pc, insn);
		case Opcode.ADDIC_:
			return new Addic_(pc, insn);
		case Opcode.ADDI:
			return new Addi(pc, insn);
		case Opcode.ADDIS:
			return new Addis(pc, insn);
		case Opcode.BC:
			return new Bc(pc, insn);
		case Opcode.SC:
			return new Sc(pc, insn);
		case Opcode.B:
			return new B(pc, insn);
		case Opcode.CR_OPS:
			switch(insn.XO_1.get()) {
			case Opcode.XO_MCRF:
				return new Mcrf(pc, insn);
			case Opcode.XO_BCLR:
				return new Bclr(pc, insn);
			case Opcode.XO_ISYNC:
				return new Isync(pc, insn);
			case Opcode.XO_CRXOR:
				return new Crxor(pc, insn);
			case Opcode.XO_CREQV:
				return new Creqv(pc, insn);
			case Opcode.XO_CRORC:
				return new Crorc(pc, insn);
			case Opcode.XO_CROR:
				return new Cror(pc, insn);
			case Opcode.XO_BCCTR:
				return new Bcctr(pc, insn);
			default:
				throw new RuntimeException(
						PC + "Unknown opcode " + insn.OPCD.get() + ", xo " + insn.XO_1.get());
			}
		case Opcode.RLWIMI:
			return new Rlwimi(pc, insn);
		case Opcode.RLWINM:
			return new Rlwinm(pc, insn);
		case Opcode.RLWNM:
			return new Rlwnm(pc, insn);
		case Opcode.ORI:
			return new Ori(pc, insn);
		case Opcode.ORIS:
			return new Oris(pc, insn);
		case Opcode.XORI:
			return new Xori(pc, insn);
		case Opcode.XORIS:
			return new Xoris(pc, insn);
		case Opcode.ANDI:
			return new Andi(pc, insn);
		case Opcode.ANDIS:
			return new Andis(pc, insn);
		case Opcode.FX_EXTENDED_OPS:
			switch(insn.XO_1.get()) {
			case Opcode.XO_CMP:
				return new Cmp(pc, insn);
			case Opcode.XO_MFCR:
				return new Mfcr(pc, insn);
			case Opcode.XO_LWARX:
				return new Lwarx(pc, insn);
			case Opcode.XO_LWZX:
				return new Lwzx(pc, insn);
			case Opcode.XO_SLW:
				return new Slw(pc, insn);
			case Opcode.XO_CNTLZW:
				return new Cntlzw(pc, insn);
			case Opcode.XO_AND:
				return new And(pc, insn);
			case Opcode.XO_CMPL:
				return new Cmpl(pc, insn);
			case Opcode.XO_DCBST:
				return new Dcbst(pc, insn);
			case Opcode.XO_LWZUX:
				return new Lwzux(pc, insn);
			case Opcode.XO_ANDC:
				return new Andc(pc, insn);
			case Opcode.XO_LBZX:
				return new Lbzx(pc, insn);
			case Opcode.XO_LVX:
				return new Lvx(pc, insn);
			case Opcode.XO_LBZUX:
				return new Lbzux(pc, insn);
			case Opcode.XO_NOR:
				return new Nor(pc, insn);
			case Opcode.XO_MTCRF:
				return new Mtcrf(pc, insn);
			case Opcode.XO_STWCX_:
				assert insn.Rc.getBit();
				return new Stwcx_(pc, insn);
			case Opcode.XO_STWX:
				return new Stwx(pc, insn);
			case Opcode.XO_STWUX:
				return new Stwux(pc, insn);
			case Opcode.XO_STBX:
				return new Stbx(pc, insn);
			case Opcode.XO_STVX:
				return new Stvx(pc, insn);
			case Opcode.XO_DCBTST:
				return new Dcbtst(pc, insn);
			case Opcode.XO_STBUX:
				return new Stbux(pc, insn);
			case Opcode.XO_DCBT:
				return new Dcbt(pc, insn);
			case Opcode.XO_LHZX:
				return new Lhzx(pc, insn);
			case Opcode.XO_EQV:
				return new Eqv(pc, insn);
			case Opcode.XO_LHZUX:
				return new Lhzux(pc, insn);
			case Opcode.XO_XOR:
				return new Xor(pc, insn);
			case Opcode.XO_MFSPR:
				return new Mfspr(pc, insn);
			case Opcode.XO_LHAX:
				return new Lhax(pc, insn);
			case Opcode.XO_STHX:
				return new Sthx(pc, insn);
			case Opcode.XO_ORC:
				return new Orc(pc, insn);
			case Opcode.XO_OR:
				return new Or(pc, insn);
			case Opcode.XO_MTSPR:
				return new Mtspr(pc, insn);
			case Opcode.XO_LWBRX:
				return new Lwbrx(pc, insn);
			case Opcode.XO_LFSX:
				return new Lfsx(pc, insn);
			case Opcode.XO_SRW:
				return new Srw(pc, insn);
			case Opcode.XO_LSWI:
				return new Lswi(pc, insn);
			case Opcode.XO_SYNC:
				return new Sync(pc, insn);
			case Opcode.XO_LFDX:
				return new Lfdx(pc, insn);
			case Opcode.XO_STWBRX:
				return new Stwbrx(pc, insn);
			case Opcode.XO_STFSX:
				return new Stfsx(pc, insn);
			case Opcode.XO_STFSUX:
				return new Stfsux(pc, insn);
			case Opcode.XO_STSWI:
				return new Stswi(pc, insn);
			case Opcode.XO_STFDX:
				return new Stfdx(pc, insn);
			case Opcode.XO_LHBRX:
				return new Lhbrx(pc, insn);
			case Opcode.XO_SRAW:
				return new Sraw(pc, insn);
			case Opcode.XO_SRAWI:
				return new Srawi(pc, insn);
			case Opcode.XO_LFIWAX:
				return new Lfiwax(pc, insn);
			case Opcode.XO_STHBRX:
				return new Sthbrx(pc, insn);
			case Opcode.XO_EXTSH:
				return new Extsh(pc, insn);
			case Opcode.XO_EXTSB:
				return new Extsb(pc, insn);
			case Opcode.XO_ICBI:
				return new Icbi(pc, insn);
			case Opcode.XO_DCBZ:
				return new Dcbz(pc, insn);
			}

			switch(insn.XO_2.get()) {
			case Opcode.XO_SUBFC:
				return new Subfc(pc, insn);
			case Opcode.XO_ADDC:
				return new Addc(pc, insn);
			case Opcode.XO_MULHWU:
				return new Mulhwu(pc, insn);
			case Opcode.XO_SUBF:
				return new Subf(pc, insn);
			case Opcode.XO_MULHW:
				return new Mulhw(pc, insn);
			case Opcode.XO_NEG:
				return new Neg(pc, insn);
			case Opcode.XO_SUBFE:
				return new Subfe(pc, insn);
			case Opcode.XO_ADDE:
				return new Adde(pc, insn);
			case Opcode.XO_SUBFZE:
				return new Subfze(pc, insn);
			case Opcode.XO_ADDZE:
				return new Addze(pc, insn);
			case Opcode.XO_ADDME:
				return new Addme(pc, insn);
			case Opcode.XO_MULLW:
				return new Mullw(pc, insn);
			case Opcode.XO_ADD:
				return new Add(pc, insn);
			case Opcode.XO_DIVWU:
				return new Divwu(pc, insn);
			case Opcode.XO_NAND:
				return new Nand(pc, insn);
			case Opcode.XO_DIVW:
				return new Divw(pc, insn);
			default:
				throw new RuntimeException(
						PC + "Unknown opcode " + insn.OPCD.get() + ", xo " + insn.XO_1.get());
			}
		case Opcode.LWZ:
			return new Lwz(pc, insn);
		case Opcode.LWZU:
			return new Lwzu(pc, insn);
		case Opcode.LBZ:
			return new Lbz(pc, insn);
		case Opcode.LBZU:
			return new Lbzu(pc, insn);
		case Opcode.STW:
			return new Stw(pc, insn);
		case Opcode.STWU:
			return new Stwu(pc, insn);
		case Opcode.STB:
			return new Stb(pc, insn);
		case Opcode.STBU:
			return new Stbu(pc, insn);
		case Opcode.LHZ:
			return new Lhz(pc, insn);
		case Opcode.LHZU:
			return new Lhzu(pc, insn);
		case Opcode.LHA:
			return new Lha(pc, insn);
		case Opcode.LHAU:
			return new Lhau(pc, insn);
		case Opcode.STH:
			return new Sth(pc, insn);
		case Opcode.STHU:
			return new Sthu(pc, insn);
		case Opcode.LMW:
			return new Lmw(pc, insn);
		case Opcode.STMW:
			return new Stmw(pc, insn);
		case Opcode.LFS:
			return new Lfs(pc, insn);
		case Opcode.LFSU:
			return new Lfsu(pc, insn);
		case Opcode.LFD:
			return new Lfd(pc, insn);
		case Opcode.LFDU:
			return new Lfdu(pc, insn);
		case Opcode.STFS:
			return new Stfs(pc, insn);
		case Opcode.STFSU:
			return new Stfsu(pc, insn);
		case Opcode.STFD:
			return new Stfd(pc, insn);
		case Opcode.STFDU:
			return new Stfdu(pc, insn);
		case Opcode.FP_SINGLE_OPS:
			switch(insn.XO_6.get()) {
			case Opcode.XO_FDIVS:
				return new Fdivs(pc, insn);
			case Opcode.XO_FSUBS:
				return new Fsubs(pc, insn);
			case Opcode.XO_FMSUBS:
				return new Fmsubs(pc, insn);
			case Opcode.XO_FADDS:
				return new Fadds(pc, insn);
			case Opcode.XO_FMULS:
				return new Fmuls(pc, insn);
			case Opcode.XO_FMADDS:
				return new Fmadds(pc, insn);
			case Opcode.XO_FNMSUBS:
				return new Fnmsubs(pc, insn);
			case Opcode.XO_FNMADDS:
				return new Fnmadds(pc, insn);
			default:
				throw new RuntimeException(
						PC + "Unknown opcode " + insn.OPCD.get() + ", xo " + insn.XO_6.get());
			}
		case Opcode.VSX_EXTENDED_OPS:
			switch(insn.XXO.get()) {
			case Opcode.XO_XXLXOR:
				return new Xxlxor(pc, insn);
			default:
				throw new RuntimeException(
						PC + "Unknown opcode " + insn.OPCD.get() + ", xo " + insn.XXO.get());
			}
		case Opcode.FP_DOUBLE_OPS:
			switch(insn.XO_6.get()) {
			case Opcode.XO_FRSP:
				return new Frsp(pc, insn);
			case Opcode.XO_FDIV:
				return new Fdiv(pc, insn);
			case Opcode.XO_FSUB:
				return new Fsub(pc, insn);
			case Opcode.XO_FADD:
				return new Fadd(pc, insn);
			case Opcode.XO_FMUL:
				return new Fmul(pc, insn);
			case Opcode.XO_FMSUB:
				return new Fmsub(pc, insn);
			case Opcode.XO_FMADD:
				return new Fmadd(pc, insn);
			case Opcode.XO_FNMSUB:
				return new Fnmsub(pc, insn);
			case Opcode.XO_FNMADD:
				return new Fnmadd(pc, insn);
			}
			switch(insn.XO_1.get()) {
			case Opcode.XO_FCMPU:
				return new Fcmpu(pc, insn);
			case Opcode.XO_FCTIWZ:
				return new Fctiwz(pc, insn);
			case Opcode.XO_FNEG:
				return new Fneg(pc, insn);
			case Opcode.XO_FMR:
				return new Fmr(pc, insn);
			case Opcode.XO_MTFSFI:
				return new Mtfsfi(pc, insn);
			case Opcode.XO_FNABS:
				return new Fnabs(pc, insn);
			case Opcode.XO_FABS:
				return new Fabs(pc, insn);
			case Opcode.XO_MFFS:
				return new Mffs(pc, insn);
			case Opcode.XO_MTFSF:
				return new Mtfsf(pc, insn);
			case Opcode.XO_FCFID:
				return new Fcfid(pc, insn);
			default:
				throw new RuntimeException(
						PC + "Unknown opcode " + insn.OPCD.get() + ", xo " + insn.XO_1.get());
			}
		}
		throw new RuntimeException(PC + "Unknown opcode " + insn.OPCD.get());
	}

	protected abstract void execute(PowerState state);

	protected abstract String[] disassemble();

	@Override
	public boolean isControlFlow() {
		return false;
	}

	@Override
	public long execute(ArchitecturalState state) {
		if(state instanceof PowerState) {
			PowerState ps = (PowerState) state;
			if(ps.pc != pc) {
				throw new RuntimeException();
			}
			ps.pc += 4;
			execute(ps);
			return ps.pc;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public long getPC() {
		return pc;
	}

	public int getInstruction() {
		return insn;
	}

	@Override
	public String toString() {
		String[] parts = disassemble();
		if(parts.length == 1) {
			return parts[0];
		} else {
			return String.format("%s\t%s", parts[0],
					Stream.of(parts).skip(1).collect(Collectors.joining(",")));
		}
	}

	protected static String r0(int r) {
		if(r == 0) {
			return "0";
		} else {
			return "r" + r;
		}
	}

	protected boolean addCA(long a, long b, boolean ppc64) {
		if(ppc64) {
			return Addition.carry(a, b);
		} else {
			return Addition.carry((int) a, (int) b);
		}
	}

	protected boolean addCA(long a, long b, boolean ca, boolean ppc64) {
		if(ppc64) {
			return Addition.carry(a, b, ca);
		} else {
			return Addition.carry((int) a, (int) b, ca);
		}
	}

	protected boolean subCA(long a, long b, boolean ppc64) {
		return addCA(a, ~b, true, ppc64);
	}

	protected boolean subCA(long a, long b, boolean ca, boolean ppc64) {
		return addCA(a, ~b, ca, ppc64);
	}
}
