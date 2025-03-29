package com.unknown.syntax;

public class MipsAsmHighlighter extends Highlighter {
	private enum State {
		NORMAL, REGISTER, NUMBER, MINUS, STRING, SSTRING, DIRECTIVE, COMMENT
	}

	public final static String[] instructions = { "abs.s", "abs.d",
			"abs.ps", "add", "add.s", "add.d", "add.ps", "addi",
			"addiu", "addiupc", "addu", "align", "alnv.ps",
			"aluipc", "and", "andi", "aui", "auipc", "b", "bal",
			"balc", "bc", "bc1eqz", "bc1nez", "bc1f", "bc1fl",
			"bc1t", "bc1tl", "bc2eqz", "bc2nez", "bc2f", "bc2fl",
			"bc2t", "bc2tl", "beq", "beql", "bgez", "bgezal",
			"bzalc", "blezalc", "bgezalc", "bgtzalc", "bltzalc",
			"beqzalc", "bnezalc", "bgezall", "beqc", "bnec",
			"bltc", "bgec", "bltuc", "bgeuc", "bgtc", "blec",
			"bgtuc", "bleuc", "bltzc", "blezc", "bgezc", "bgtzc",
			"beqzc", "bnezc", "bgezl", "bgtz", "bgtzl", "bitswap",
			"blez", "blezl", "bltz", "bltzal", "bltzall", "bltzl",
			"bne", "bnel", "bovc", "bnvc", "break", "c.f.s",
			"c.f.d", "c.f.ps", "c.t.s", "c.t.d", "c.t.ps",
			"c.un.s", "c.un.d", "c.un.ps", "c.or.s", "c.or.d",
			"c.or.ps", "c.eq.s", "c.eq.d", "c.eq.ps", "c.neq.s",
			"c.neq.d", "c.neq.ps", "c.ueq.s", "c.ueq.d",
			"c.ueq.ps", "c.ogl.s", "c.ogl.d", "c.ogl.ps",
			"c.olt.s", "c.olt.d", "c.olt.ps", "c.uge.s", "c.uge.d",
			"c.uge.ps", "c.ult.s", "c.ult.d", "c.ult.ps",
			"c.oge.s", "c.oge.d", "c.oge.ps", "c.ole.s", "c.ole.d",
			"c.ole.ps", "c.ugt.s", "c.ugt.d", "c.ugt.ps",
			"c.ule.s", "c.ule.d", "c.ule.ps", "c.ogt.s", "c.ogt.d",
			"c.ogt.ps", "c.sf.s", "c.sf.d", "c.sf.ps", "c.st.s",
			"c.st.d", "c.st.ps", "c.ngle.s", "c.ngle.d",
			"c.ngle.ps", "c.gle.s", "c.gle.d", "c.gle.ps",
			"c.seq.s", "c.seq.d", "c.seq.ps", "c.sne.s", "c.sne.d",
			"c.sne.ps", "c.ngl.s", "c.ngl.d", "c.ngl.ps", "c.gl.s",
			"c.gl.d", "c.gl.ps", "c.lt.s", "c.lt.d", "c.lt.ps",
			"c.nge.s", "c.nge.d", "c.nge.ps", "c.ge.s", "c.ge.d",
			"c.ge.ps", "c.le.s", "c.le.d", "c.le.ps", "c.nle.s",
			"c.nle.d", "c.nle.ps", "c.ngt.s", "c.ngt.d",
			"c.ngt.ps", "c.gt.s", "c.gt.d", "c.gt.ps", "cache",
			"cachee", "ceil.l.s", "ceil.l.d", "ceil.w.s",
			"ceil.w.d", "cfc1", "cfc2", "class.s", "class.d",
			"clo", "clz", "cmp.af.s", "cmp.af.d", "cmp.at.s",
			"cmp.at.d", "cmp.un.s", "cmp.un.d", "cmp.or.s",
			"cmp.or.d", "cmp.eq.s", "cmp.eq.d", "cmp.une.s",
			"cmp.une.d", "cmp.ueq.s", "cmp.ueq.d", "cmp.ne.s",
			"cmp.ne.d", "cmp.lt.s", "cmp.lt.d", "cmp.uge.s",
			"cmp.uge.d", "cmp.ult.s", "cmp.ult.d", "cmp.oge.s",
			"cmp.oge.d", "cmp.le.s", "cmp.le.d", "cmp.ugt.s",
			"cmp.ugt.d", "cmp.ule.s", "cmp.ule.d", "cmp.ogt.s",
			"cmp.ogt.d", "cop2", "ctc1", "ctc2", "cvt.d.s",
			"cvt.d.w", "cvt.d.l", "cvt.l.s", "cvt.l.d", "cvt.ps.s",
			"cvt.s.pl", "cvt.s.pu", "cvt.s.d", "cvt.s.w",
			"cvt.s.l", "cvt.w.s", "cvt.w.d", "ddiv", "ddivu",
			"deret", "di", "div", "mod", "divu", "modu", "div.s",
			"div.d", "dvp", "ehb", "ei", "eret", "eretnc", "evp",
			"ext", "floor.l.s", "floor.l.d", "floor.w.s",
			"floor.w.d", "ins", "j", "jal", "jalr", "jalr.hb",
			"jalx", "jialc", "jic", "jr", "jr.hb", "lb", "lbe",
			"lbu", "lbue", "ldc1", "ldc2", "ldxc1", "lh", "lhe",
			"lhu", "lhue", "ll", "lle", "lsa", "lui", "luxc1",
			"lw", "lwc1", "lwc2", "lwe", "lwl", "lwle", "lwpc",
			"lwr", "lwre", "lwxc1", "madd", "madd.s", "madd.d",
			"madd.ps", "maddf.s", "maddf.d", "msubf.s", "msubf.d",
			"maddu", "max.s", "max.d", "maxa.s", "maxa.d", "min.s",
			"min.d", "mina.s", "mina.d", "mfc0", "mfc1", "mfc2",
			"mfhc0", "mfhc1", "mfhc2", "mfhi", "mflo", "mov.s",
			"mov.d", "mov.ps", "movf", "movf.s", "movf.d",
			"movf.ps", "movn", "movn.s", "movn.d", "movn.ps",
			"movt", "movt.s", "movt.d", "movt.ps", "movz",
			"movz.s", "movz.d", "movz.ps", "msub", "msub.s",
			"msub.d", "msub.ps", "msubu", "mtc0", "mtc1", "mtc2",
			"mthc0", "mthc1", "mthc2", "mthi", "mtlo", "mul",
			"muh", "mulu", "muhu", "mul.s", "mul.d", "mul.ps",
			"mult", "multu", "nal", "neg.s", "neg.d", "neg.ps",
			"nmadd.s", "nmadd.d", "nmadd.ps", "nmsub.s", "nmsub.d",
			"nmsub.ps", "nop", "nor", "or", "ori", "pause",
			"pll.ps", "plu.ps", "pref", "prefe", "prefx", "pul.ps",
			"puu.ps", "rdhwr", "rdpgpr", "recip.s", "recip.d",
			"rint.s", "rint.d", "rotr", "rotrv", "round.l.s",
			"round.l.d", "round.w.s", "round.w.d", "rsqrt.s",
			"rsqrt.d", "sb", "sbe", "sc", "sce", "sdbbp", "sdc1",
			"sdc2", "sdxc1", "seb", "seh", "sel.s", "sel.d",
			"seleqz", "selnez", "seleqz.s", "seleqz.d", "selnez.s",
			"selnez.d", "sh", "she", "sigrie", "sll", "sllv",
			"slt", "slti", "sltiu", "sltu", "sqrt.s", "sqrt.d",
			"sra", "srav", "srl", "srlv", "ssnop", "sub", "sub.s",
			"sub.d", "sub.ps", "subu", "suxc1", "sw", "swc1",
			"swc2", "swe", "swl", "swle", "swr", "swre", "swxc1",
			"sync", "synci", "syscall", "teq", "teqi", "tge",
			"tgei", "tgeiu", "tgeu", "tlbinv", "tlbinvf", "tlbp",
			"tlbr", "tlbwi", "tlbwr", "tlt", "tlti", "tltiu",
			"tltu", "tne", "tnei", "trunc.l.s", "trunc.l.d",
			"trunc.w.s", "trunc.w.l", "wait", "wrpgpr", "wshb",
			"xor", "xori", /* pseudo instructions */"la", "li",
			"move", "negu", "nop", "not", "beqz", "bnez", "ulw",
			"usw", "rem", "clear", "bgt", "ble", "blt", "bge",
			"sge", "sgt" };
	public final static String[] registers = { "zero", "at", "v0", "v1",
			"a0", "a1", "a2", "a3", "t0", "t1", "t2", "t3", "t4",
			"t5", "t6", "t7", "s0", "s1", "s2", "s3", "s4", "s5",
			"s6", "s7", "t8", "t9", "k0", "k1", "gp", "sp", "fp",
			"ra" };
	public final static String[] fpuregisters = { "fv0", "fv1", "ft0",
			"ft1", "ft2", "ft3", "fa0", "fa1", "ft4", "ft5", "fs0",
			"fs1", "fs2", "fs3", "fs4", "fs5" };

	public MipsAsmHighlighter() {
		super("\r\n\t ,:#", instructions);
	}

	@Override
	public String formatLine(String line) {
		StringBuffer formatted = new StringBuffer(line.length());
		StringBuffer buf = new StringBuffer();
		State state = State.NORMAL;
		for(int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			switch(state) {
			case NORMAL:
				switch(c) {
				case '$':
					state = State.REGISTER;
					formatted.append(htmlspecialchars(buf
							.toString()));
					buf = new StringBuffer();
					buf.append('$');
					break;
				case '.':
					if(buf.toString().trim().length() == 0) {
						state = State.DIRECTIVE;
						formatted.append(htmlspecialchars(buf
								.toString()));
						buf = new StringBuffer();
					}
					buf.append('.');
					break;
				case ':':
					formatted.append("<span class=\""
							+ CSS_PREFIX
							+ "identifier\">"
							+ htmlspecialchars(buf
									.toString())
							+ "</span>:");
					buf = new StringBuffer();
					break;
				case ' ':
				case '\t': // end of token
					if(isKeyword(buf.toString())) {
						formatted.append("<span class=\""
								+ CSS_PREFIX
								+ "keyword\">"
								+ htmlspecialchars(buf
										.toString())
								+ "</span>");
					} else {
						formatted.append(htmlspecialchars(buf
								.toString()));
					}
					formatted.append(c);
					buf = new StringBuffer();
					break;
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					if(buf.toString().trim().length() == 0) {
						state = State.NUMBER;
						formatted.append(htmlspecialchars(buf
								.toString()));
						buf = new StringBuffer();
					}
					buf.append(c);
					break;
				case '-':
					state = State.MINUS;
					formatted.append(htmlspecialchars(buf
							.toString()));
					buf = new StringBuffer();
					buf.append(c);
					break;
				case '#':
					state = State.COMMENT;
					formatted.append(htmlspecialchars(buf
							.toString()));
					buf = new StringBuffer();
					buf.append(c);
					break;
				case '"':
					state = State.STRING;
					formatted.append(htmlspecialchars(buf
							.toString()));
					buf = new StringBuffer();
					buf.append(c);
					break;
				case '\'':
					state = State.SSTRING;
					formatted.append(htmlspecialchars(buf
							.toString()));
					buf = new StringBuffer();
					buf.append(c);
					break;
				default:
					buf.append(c);
				}
				break;
			case REGISTER:
				switch(c) {
				case '\t':
				case ' ':
				case ')':
				case ',':
					state = State.NORMAL;
					formatted.append("<span class=\""
							+ CSS_PREFIX
							+ "operator\">"
							+ htmlspecialchars(buf
									.toString())
							+ "</span>"
							+ htmlspecialchars(c));
					buf = new StringBuffer();
					break;
				case '#':
					state = State.COMMENT;
					formatted.append("<span class=\""
							+ CSS_PREFIX
							+ "number\">"
							+ htmlspecialchars(buf
									.toString())
							+ "</span>");
					buf = new StringBuffer();
					buf.append(c);
					break;
				default:
					buf.append(c);
				}
				break;
			case COMMENT:
				switch(c) {
				case '\r':
				case '\n':
					state = State.NORMAL;
					formatted.append("<span class=\""
							+ CSS_PREFIX
							+ "comment\">"
							+ htmlspecialchars(buf.toString())
							+ "</span>");
					buf = new StringBuffer();
					buf.append(c);
					break;
				default:
					buf.append(c);
				}
				break;
			case DIRECTIVE:
				switch(c) {
				case '\r':
				case '\n':
				case '\t':
				case ' ':
					state = State.NORMAL;
					formatted.append("<span class=\""
							+ CSS_PREFIX
							+ "preproc\">"
							+ htmlspecialchars(buf
									.toString())
							+ "</span>");
					buf = new StringBuffer();
					buf.append(c);
					break;
				case '#':
					state = State.COMMENT;
					formatted.append("<span class=\""
							+ CSS_PREFIX
							+ "preproc\">"
							+ htmlspecialchars(buf
									.toString())
							+ "</span>");
					buf = new StringBuffer();
					buf.append(c);
					break;
				default:
					buf.append(c);
				}
				break;
			case NUMBER:
				char z = Character.toLowerCase(c);
				if(isNumeric(c) || (z >= 'a' && z <= 'f')
						|| c == '.' || c == 'x') {
					buf.append(c);
				} else if(c == '#') {
					state = State.COMMENT;
					formatted.append("<span class=\""
							+ CSS_PREFIX
							+ "number\">"
							+ htmlspecialchars(buf
									.toString())
							+ "</span>");
					buf = new StringBuffer();
					buf.append(c);
				} else {
					state = State.NORMAL;
					formatted.append("<span class=\""
							+ CSS_PREFIX
							+ "number\">"
							+ htmlspecialchars(buf
									.toString())
							+ "</span>");
					buf = new StringBuffer();
					buf.append(c);
				}
				break;
			case MINUS:
				z = Character.toLowerCase(c);
				if(isNumeric(c) || (z >= 'a' && z <= 'f')
						|| c == '.' || c == 'x') {
					state = State.NUMBER;
					buf.append(c);
				} else if(c == '#') {
					state = State.COMMENT;
					formatted.append(htmlspecialchars(buf
							.toString()));
					buf = new StringBuffer();
					buf.append(c);
				} else {
					state = State.NORMAL;
					formatted.append(htmlspecialchars(buf
							.toString()));
					buf = new StringBuffer();
					buf.append(c);
				}
				break;
			case STRING:
				if(c == '"') {
					state = State.NORMAL;
					buf.append(c);
					formatted.append("<span class=\""
							+ CSS_PREFIX
							+ "string\">"
							+ htmlspecialchars(buf
									.toString())
							+ "</span>");
					buf = new StringBuffer();
				} else {
					buf.append(c);
				}
				break;
			case SSTRING:
				if(c == '\'') {
					state = State.NORMAL;
					buf.append(c);
					formatted.append("<span class=\""
							+ CSS_PREFIX
							+ "string\">"
							+ htmlspecialchars(buf
									.toString())
							+ "</span>");
					buf = new StringBuffer();
				} else {
					buf.append(c);
				}
				break;
			default:
				break;
			}
		}
		if(buf.length() != 0) {
			switch(state) {
			case NORMAL:
				if(isKeyword(buf.toString())) {
					formatted.append("<span class=\""
							+ CSS_PREFIX
							+ "keyword\">"
							+ htmlspecialchars(buf
									.toString())
							+ "</span>");
				} else {
					formatted.append(htmlspecialchars(buf
							.toString()));
				}
				break;
			case COMMENT:
				formatted.append("<span class=\""
						+ CSS_PREFIX
						+ "comment\">"
						+ htmlspecialchars(buf
								.toString())
						+ "</span>");
				break;
			case DIRECTIVE:
				formatted.append("<span class=\""
						+ CSS_PREFIX
						+ "preproc\">"
						+ htmlspecialchars(buf
								.toString())
						+ "</span>");
				break;
			case MINUS:
				formatted.append('-');
				break;
			case NUMBER:
				formatted.append("<span class=\""
						+ CSS_PREFIX
						+ "number\">"
						+ htmlspecialchars(buf
								.toString())
						+ "</span>");
				break;
			case REGISTER:
				formatted.append("<span class=\""
						+ CSS_PREFIX
						+ "operator\">"
						+ htmlspecialchars(buf
								.toString())
						+ "</span>");
				break;
			case STRING:
			case SSTRING:
				formatted.append("<span class=\""
						+ CSS_PREFIX
						+ "string\">"
						+ htmlspecialchars(buf
								.toString())
						+ "</span>");
				break;
			default:
				break;
			}
		}
		return formatted.append("\n").toString();
	}
}
