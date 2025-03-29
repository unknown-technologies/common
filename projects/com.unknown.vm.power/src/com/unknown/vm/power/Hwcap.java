package com.unknown.vm.power;

public class Hwcap {
	// @formatter:off
	/* Feature definitions in AT_HWCAP. */
	public static final int PPC_FEATURE_32              = 0x80000000; /* 32-bit mode. */
	public static final int PPC_FEATURE_64              = 0x40000000; /* 64-bit mode. */
	public static final int PPC_FEATURE_601_INSTR       = 0x20000000; /* 601 chip, Old POWER ISA. */
	public static final int PPC_FEATURE_HAS_ALTIVEC     = 0x10000000; /* SIMD/Vector Unit. */
	public static final int PPC_FEATURE_HAS_FPU         = 0x08000000; /* Floating Point Unit. */
	public static final int PPC_FEATURE_HAS_MMU         = 0x04000000; /* Memory Management Unit. */
	public static final int PPC_FEATURE_HAS_4xxMAC      = 0x02000000; /* 4xx Multiply Accumulator. */
	public static final int PPC_FEATURE_UNIFIED_CACHE   = 0x01000000; /* Unified I/D cache. */
	public static final int PPC_FEATURE_HAS_SPE         = 0x00800000; /* Signal Processing ext. */
	public static final int PPC_FEATURE_HAS_EFP_SINGLE  = 0x00400000; /* SPE Float. */
	public static final int PPC_FEATURE_HAS_EFP_DOUBLE  = 0x00200000; /* SPE Double. */
	public static final int PPC_FEATURE_NO_TB           = 0x00100000; /* 601/403gx have no timebase */
	public static final int PPC_FEATURE_POWER4          = 0x00080000; /* POWER4 ISA 2.00 */
	public static final int PPC_FEATURE_POWER5          = 0x00040000; /* POWER5 ISA 2.02 */
	public static final int PPC_FEATURE_POWER5_PLUS     = 0x00020000; /* POWER5+ ISA 2.03 */
	public static final int PPC_FEATURE_CELL_BE         = 0x00010000; /* CELL Broadband Engine */
	public static final int PPC_FEATURE_BOOKE           = 0x00008000 ;/* ISA Category Embedded */
	public static final int PPC_FEATURE_SMT             = 0x00004000; /* Simultaneous Multi-Threading */
	public static final int PPC_FEATURE_ICACHE_SNOOP    = 0x00002000;
	public static final int PPC_FEATURE_ARCH_2_05       = 0x00001000; /* ISA 2.05 */
	public static final int PPC_FEATURE_PA6T            = 0x00000800; /* PA Semi 6T Core */
	public static final int PPC_FEATURE_HAS_DFP         = 0x00000400; /* Decimal FP Unit */
	public static final int PPC_FEATURE_POWER6_EXT      = 0x00000200; /* P6 + mffgpr/mftgpr */
	public static final int PPC_FEATURE_ARCH_2_06       = 0x00000100; /* ISA 2.06 */
	public static final int PPC_FEATURE_HAS_VSX         = 0x00000080; /* P7 Vector Extension. */
	public static final int PPC_FEATURE_PSERIES_PERFMON_COMPAT  = 0x00000040;
	public static final int PPC_FEATURE_TRUE_LE         = 0x00000002;
	public static final int PPC_FEATURE_PPC_LE          = 0x00000001;

	/* Feature definitions in AT_HWCAP2. */
	public static final int PPC_FEATURE2_ARCH_2_07     = 0x80000000; /* ISA 2.07 */
	public static final int PPC_FEATURE2_HAS_HTM       = 0x40000000; /* Hardware Transactional Memory */
	public static final int PPC_FEATURE2_HAS_DSCR      = 0x20000000; /* Data Stream Control Register */
	public static final int PPC_FEATURE2_HAS_EBB       = 0x10000000; /* Event Base Branching */
	public static final int PPC_FEATURE2_HAS_ISEL      = 0x08000000; /* Integer Select */
	public static final int PPC_FEATURE2_HAS_TAR       = 0x04000000; /* Target Address Register */
	public static final int PPC_FEATURE2_HAS_VEC_CRYPTO  = 0x02000000;  /* Target supports vector instruction. */
	public static final int PPC_FEATURE2_HTM_NOSC      = 0x01000000; /* Kernel aborts transaction when a syscall is made. */
	public static final int PPC_FEATURE2_ARCH_3_00     = 0x00800000; /* ISA 3.0 */
	public static final int PPC_FEATURE2_HAS_IEEE128   = 0x00400000; /* VSX IEEE Binary Float 128-bit */
	// @formatter:on
}
