package com.unknown.syntax;

import java.util.ArrayList;
import java.util.List;

public class VimHighlighter extends Highlighter {
	public static String[] keywords = expand(new String[] { "a",
			"arga[dd]", "ar[gs]", "bd[elete]", "bN[ext]",
			"breakd[el]", "bufdo", "cabc[lear]", "cat[ch]",
			"cex[pr]", "c[hange]", "cla[st]", "cnew[er]",
			"cNf[ile]", "con", "cp[revious]", "cuna[bbrev]", "del",
			"deletep", "delm[arks]", "diffp[atch]", "dig[raphs]",
			"do", "e", "echon", "endf", "endw[hile]", "f[ile]",
			"fin[d]", "folddoc[losed]", "fu[nction]", "gvim",
			"helpt[ags]", "iabc[lear]", "intro", "k", "l", "lan",
			"lc[d]", "lefta[bove]", "lg[etfile]", "lla[st]",
			"lnew[er]", "lNf[ile]", "lockv[ar]", "ls",
			"lvimgrepa[dd]", "mat[ch]", "mk[exrc]", "mo", "n",
			"n[ext]", "o", "ownsyntax", "perld[o]", "pre[serve]",
			"promptf[ind]", "ptl[ast]", "ptr[ewind]", "py3do",
			"qa[ll]", "r[ead]", "redr[aw]", "retu[rn]", "rub[y]",
			"rv[iminfo]", "sba[ll]", "sbN[ext]",
			"scripte[ncoding]", "setf[iletype]", "sh[ell]",
			"sim[alt]", "sm[ap]", "sni[ff]", "sor[t]",
			"spelli[nfo]", "spr[evious]", "start", "st[op]",
			"sunmenu", "syn", "ta", "tabf[ind]", "tabnew",
			"tabr[ewind]", "tcld[o]", "tj[ump]", "tN", "tr",
			"tu[nmenu]", "undoj[oin]", "uns[ilent]", "ve[rsion]",
			"vimgrepa[dd]", "vs[plit]", "winc[md]", "wN[ext]",
			"ws[verb]", "x[it]", "xnoremenu", "ab", "argd[elete]",
			"argu[ment]", "bel[owright]", "bo[tright]",
			"breakl[ist]", "b[uffer]", "cad", "cb[uffer]",
			"cf[ile]", "changes", "cl[ist]", "cn[ext]", "col[der]",
			"conf[irm]", "cq[uit]", "cw[indow]", "delc[ommand]",
			"deletl", "delp", "diffpu[t]", "dir", "doau", "ea",
			"e[dit]", "endfo[r]", "ene[w]", "files", "fini[sh]",
			"foldd[oopen]", "g", "h", "hi", "if", "is[earch]",
			"keepa", "la", "lan[guage]", "lch[dir]", "lex[pr]",
			"lgr[ep]", "lli[st]", "lne[xt]", "lo", "lol[der]",
			"lt[ag]", "lw[indow]", "menut", "mks[ession]",
			"mod[e]", "nbc[lose]", "nmapc[lear]", "ol[dfiles]",
			"p", "po[p]", "prev[ious]", "promptr[epl]", "ptn",
			"pts[elect]", "pydo", "q[uit]", "rec[over]",
			"redraws[tatus]", "rew[ind]", "rubyd[o]", "sal[l]",
			"sbf[irst]", "sbp[revious]", "scrip[tnames]",
			"setg[lobal]", "si", "sl", "sme", "sno[magic]",
			"so[urce]", "spellr[epall]", "sre[wind]",
			"startg[replace]", "stopi[nsert]", "sus[pend]", "sync",
			"tab", "tabfir[st]", "tabn[ext]", "tabs", "tclf[ile]",
			"tl[ast]", "tn[ext]", "tr[ewind]", "u", "undol[ist]",
			"up[date]", "vert[ical]", "vi[sual]", "w", "windo",
			"wp[revious]", "wundo", "xmapc[lear]", "xunme",
			"abc[lear]", "argdo", "as[cii]", "bf[irst]",
			"bp[revious]", "br[ewind]", "buffers", "caddb[uffer]",
			"cc", "cfir[st]", "chd[ir]", "clo[se]", "cN[ext]",
			"colo[rscheme]", "con[tinue]", "cr[ewind]", "d",
			"delel", "deletp", "dep", "diffs[plit]", "di[splay]",
			"dp", "earlier", "el[se]", "endfun", "ex", "filet",
			"fir[st]", "foldo[pen]", "go[to]", "ha[rdcopy]",
			"hid[e]", "ij[ump]", "isp[lit]", "keepalt", "lad",
			"la[st]", "lcl[ose]", "lf[ile]", "lgrepa[dd]",
			"lmak[e]", "lN[ext]", "loadk", "lop[en]", "lua", "ma",
			"menut[ranslate]", "mksp[ell]", "m[ove]", "nb[key]",
			"noa", "omapc[lear]", "pc[lose]", "popu", "p[rint]",
			"ps[earch]", "ptN", "pu[t]", "pyf[ile]", "quita[ll]",
			"red", "reg[isters]", "ri[ght]", "rubyf[ile]",
			"san[dbox]", "sbl[ast]", "sbr[ewind]", "scs",
			"setl[ocal]", "sig", "sla[st]", "smenu", "snoreme",
			"spe", "spellu[ndo]", "st", "star[tinsert]",
			"sts[elect]", "sv[iew]", "syncbind", "tabc[lose]",
			"tabl[ast]", "tabN[ext]", "ta[g]", "te[aroff]", "tm",
			"tN[ext]", "try", "un", "unh[ide]", "v", "vi",
			"viu[sage]", "wa[ll]", "winp[os]", "wq", "wv[iminfo]",
			"xme", "xunmenu", "abo[veleft]", "arge[dit]", "au",
			"bl[ast]", "br", "bro[wse]", "bun[load]", "cad[dexpr]",
			"ccl[ose]", "cgetb[uffer]", "che[ckpath]",
			"cmapc[lear]", "cnf", "com", "cope[n]", "cs", "de",
			"delep", "delf", "di", "difft[his]", "dj[ump]",
			"dr[op]", "ec", "elsei[f]", "endf[unction]", "exi[t]",
			"filetype", "fix[del]", "for", "gr[ep]", "h[elp]",
			"his[tory]", "il[ist]", "iuna[bbrev]", "keepj[umps]",
			"laddb[uffer]", "lat", "lcs", "lfir[st]",
			"lh[elpgrep]", "lmapc[lear]", "lnf", "loadkeymap",
			"lpf[ile]", "luado", "mak[e]", "mes", "mkv", "mz",
			"nbs[tart]", "noautocmd", "on[ly]", "pe", "popu[p]",
			"pro", "pta[g]", "ptn[ext]", "pw[d]", "py[thon]", "r",
			"redi[r]", "res[ize]", "rightb[elow]", "rundo",
			"sa[rgument]", "sbm[odified]", "sb[uffer]", "scscope",
			"sf[ind]", "sign", "sl[eep]", "sn[ext]", "snoremenu",
			"spelld[ump]", "spellw[rong]", "sta[g]",
			"startr[eplace]", "sun[hide]", "sw[apname]", "syntime",
			"tabd[o]", "tabm[ove]", "tabo[nly]", "tags",
			"tf[irst]", "tm[enu]", "to[pleft]", "ts[elect]",
			"una[bbreviate]", "unl", "ve", "vie[w]", "vmapc[lear]",
			"wh[ile]", "win[size]", "wqa[ll]", "x", "xmenu",
			"xwininfo", "al[l]", "argg[lobal]", "bad[d]",
			"bm[odified]", "brea[k]", "bu", "bw[ipeout]",
			"caddf[ile]", "cd", "cgete[xpr]", "checkt[ime]", "cn",
			"cNf", "comc[lear]", "co[py]", "cscope", "debug",
			"d[elete]", "delf[unction]", "diffg[et]",
			"diffu[pdate]", "dl", "ds[earch]", "echoe[rr]",
			"em[enu]", "en[dif]", "exu[sage]", "fin", "fo[ld]",
			"fu", "grepa[dd]", "helpf[ind]", "i", "imapc[lear]",
			"j[oin]", "kee[pmarks]", "lad[dexpr]", "later",
			"lcscope", "lgetb[uffer]", "l[ist]", "lN", "lNf",
			"lo[adview]", "lp[revious]", "luafile", "ma[rk]",
			"messages", "mkvie[w]", "mzf[ile]", "ne",
			"noh[lsearch]", "o[pen]", "ped[it]", "pp[op]",
			"profd[el]", "ptf[irst]", "ptN[ext]", "py", "python3",
			"re", "red[o]", "ret[ab]", "ru", "ru[ntime]",
			"sav[eas]", "sbn[ext]", "scrip", "se[t]", "sfir[st]",
			"sil[ent]", "sm[agic]", "sN[ext]", "so", "spe[llgood]",
			"sp[lit]", "star", "stj[ump]", "sunme", "sy", "t",
			"tabe[dit]", "tabN", "tabp[revious]", "tc[l]",
			"th[row]", "tn", "tp[revious]", "tu", "u[ndo]",
			"unlo[ckvar]", "verb[ose]", "vim[grep]", "vne[w]",
			"win", "wn[ext]", "w[rite]", "xa[ll]", "xnoreme",
			"y[ank]", "let", "unl[et]" });
	public static String operators = "~!%^&*-+=|/:<>?";

	public static String[] expand(String[] rawdata) {
		List<String> all = new ArrayList<>();
		for(String keyword : rawdata) {
			int pos = keyword.indexOf('[');
			if(pos != -1) {
				String required = keyword.substring(0, pos);
				String optional = keyword.substring(pos + 1,
						keyword.length() - 1);
				all.add(required);
				for(int i = 1; i <= optional.length(); i++) {
					all.add(required
							+ optional.substring(0,
									i));
				}
			} else {
				all.add(keyword);
			}
		}
		return all.toArray(new String[all.size()]);
	}

	public VimHighlighter() {
		super("~!@%^&*()-+=|\\/{}[]:;\"\'<> ,	.?", keywords);
	}

	public boolean isClass(@SuppressWarnings("unused") String s) {
		return false;
	}

	public static boolean isOperator(char c) {
		return operators.indexOf(c) != -1;
	}

	@Override
	public String formatLine(String line) {
		StringBuffer formatted = new StringBuffer();
		int i = 0;
		int startAt = 0;
		char ch;
		StringBuffer temp;
		String tmp;
		boolean inString = false;
		boolean inCharacter = false;

		int length = line.length();
		while(i < length) {
			temp = new StringBuffer();
			ch = line.charAt(i);
			startAt = i;
			while((i < length) && !isDelimiter(ch)) {
				temp.append(ch);
				i++;
				if(i < length) {
					ch = line.charAt(i);
				}
			}

			tmp = temp.toString();
			if(tmp.length() == 0) {
				// nothing
			} else if(isKeyword(tmp) && !inString && !inCharacter) {
				formatted.append("<span class=\"" + CSS_PREFIX
						+ "keyword\">"
						+ htmlspecialchars(tmp)
						+ "</span>");
			} else if(isClass(tmp) && !inString && !inCharacter) {
				formatted.append("<span class=\"" + CSS_PREFIX
						+ "class\">"
						+ htmlspecialchars(tmp)
						+ "</span>");
			} else if(isCNumber(tmp) && !inString && !inCharacter) {
				formatted.append("<span class=\"" + CSS_PREFIX
						+ "number\">" + tmp + "</span>");
			} else {
				formatted.append(htmlspecialchars(tmp));
			}
			// because the last character read in the while-loop is
			// not part of tmp
			i++;

			boolean do_append = true;

			boolean hasMoreQuotes = false;
			if(ch == '"') {
				hasMoreQuotes = line.indexOf('"', i) != -1;
				if(line.trim().charAt(0) == '"') {
					hasMoreQuotes = false;
				}
			}
			if((i < length) && (ch == '"') && !inString
					&& !hasMoreQuotes) {
				formatted.append("<span class=\"" + CSS_PREFIX
						+ "comment\">" + ch
						+ line.substring(i) + "</span>");
				break;
			} else if((ch == '"') && !inString && !hasMoreQuotes) {
				formatted.append("<span class=\"" + CSS_PREFIX
						+ "comment\">" + ch + "</span>");
				break;
			} else if(!inCharacter && (ch == '"')) {
				do_append = false;
				if(i > 1) {
					if(line.charAt(i - 2) == '\\') {
						if((i > 2)
								&& (line.charAt(i - 3) == '\\')) {
							do_append = false;
						} else {
							do_append = true;
						}
					}
				}
				if(!do_append) {
					if(!inString) {
						formatted.append("<span class=\""
								+ CSS_PREFIX
								+ "string\">"
								+ htmlspecialchars(ch));
					} else {
						formatted.append(htmlspecialchars(ch)
								+ "</span>");
					}
					inString = !inString;
				}
			} else if(!inString && (ch == '\'')) {
				do_append = false;
				if(i > 1) {
					if(line.charAt(i - 2) == '\\') {
						if((i > 2)
								&& (line.charAt(i - 3) == '\\')) {
							do_append = false;
						} else {
							do_append = true;
						}
					}
				}
				if(!do_append) {
					if(!inCharacter) {
						formatted.append("<span class=\""
								+ CSS_PREFIX
								+ "string\">"
								+ htmlspecialchars(ch));
					} else {
						formatted.append(htmlspecialchars(ch)
								+ "</span>");
					}
					inCharacter = !inCharacter;
				}
			}

			// append last character (not contained in tmp) if it
			// was not processed elsewhere
			if(do_append && ((startAt + tmp.length()) < length)) {
				if(isOperator(ch) && !inString && !inCharacter) {
					formatted.append("<span class=\""
							+ CSS_PREFIX
							+ "operator\">"
							+ htmlspecialchars(ch)
							+ "</span>");
				} else {
					formatted.append(htmlspecialchars(ch));
				}
			}
		}

		formatted.append("\n");
		return formatted.toString();
	}
}
