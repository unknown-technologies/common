package com.unknown.plaf.motif;

public class FontUtilities {

	/**
	 * Referenced by code in the JDK which wants to test for the minimum char code for which layout may be required.
	 * Note that even basic latin text can benefit from ligatures, eg "ffi" but we presently apply those only if
	 * explicitly requested with TextAttribute.LIGATURES_ON. The value here indicates the lowest char code for which
	 * failing to invoke layout would prevent acceptable rendering.
	 */
	public static final int MIN_LAYOUT_CHARCODE = 0x0300;

	/**
	 * Referenced by code in the JDK which wants to test for the maximum char code for which layout may be required.
	 * Note this does not account for supplementary characters where the caller interprets 'layout' to mean any case
	 * where one 'char' (ie the java type char) does not map to one glyph
	 */
	public static final int MAX_LAYOUT_CHARCODE = 0x206F;

	/**
	 * If there is anything in the text which triggers a case where char->glyph does not map 1:1 in straightforward
	 * left->right ordering, then this method returns true. Scripts which might require it but are not treated as
	 * such due to JDK implementations will not return true. ie a 'true' return is an indication of the treatment by
	 * the implementation. Whether supplementary characters should be considered is dependent on the needs of the
	 * caller. Since this method accepts the 'char' type then such chars are always represented by a pair. From a
	 * rendering perspective these will all (in the cases I know of) still be one unicode character -> one glyph.
	 * But if a caller is using this to discover any case where it cannot make naive assumptions about the number of
	 * chars, and how to index through them, then it may need the option to have a 'true' return in such a case.
	 */
	public static boolean isComplexText(char[] chs, int start, int limit) {

		for(int i = start; i < limit; i++) {
			if(chs[i] < MIN_LAYOUT_CHARCODE) {
				continue;
			} else if(isNonSimpleChar(chs[i])) {
				return true;
			}
		}
		return false;
	}

	/*
	 * This is almost the same as the method above, except it takes a char which means it may include undecoded
	 * surrogate pairs. The distinction is made so that code which needs to identify all cases in which we do not
	 * have a simple mapping from char->unicode character->glyph can be identified. For example measurement cannot
	 * simply sum advances of 'chars', the caret in editable text cannot advance one 'char' at a time, etc. These
	 * callers really are asking for more than whether 'layout' needs to be run, they need to know if they can
	 * assume 1->1 char->glyph mapping.
	 */
	public static boolean isNonSimpleChar(char ch) {
		return isComplexCharCode(ch) ||
				(ch >= CharToGlyphMapper.HI_SURROGATE_START &&
						ch <= CharToGlyphMapper.LO_SURROGATE_END);
	}

	/*
	 * If the character code falls into any of a number of unicode ranges where we know that simple left->right
	 * layout mapping chars to glyphs 1:1 and accumulating advances is going to produce incorrect results, we want
	 * to know this so the caller can use a more intelligent layout approach. A caller who cares about optimum
	 * performance may want to check the first case and skip the method call if its in that range. Although there's
	 * a lot of tests in here, knowing you can skip CTL saves a great deal more. The rest of the checks are ordered
	 * so that rather than checking explicitly if (>= start & <= end) which would mean all ranges would need to be
	 * checked so be sure CTL is not needed, the method returns as soon as it recognises the code point is outside
	 * of a CTL ranges. NOTE: Since this method accepts an 'int' it is asssumed to properly represent a CHARACTER.
	 * ie it assumes the caller has already converted surrogate pairs into supplementary characters, and so can
	 * handle this case and doesn't need to be told such a case is 'complex'.
	 */
	public static boolean isComplexCharCode(int code) {

		if(code < MIN_LAYOUT_CHARCODE || code > MAX_LAYOUT_CHARCODE) {
			return false;
		} else if(code <= 0x036f) {
			// Trigger layout for combining diacriticals 0x0300->0x036f
			return true;
		} else if(code < 0x0590) {
			// No automatic layout for Greek, Cyrillic, Armenian.
			return false;
		} else if(code <= 0x06ff) {
			// Hebrew 0590 - 05ff
			// Arabic 0600 - 06ff
			return true;
		} else if(code < 0x0900) {
			return false; // Syriac and Thaana
		} else if(code <= 0x0e7f) {
			// if Indic, assume shaping for conjuncts, reordering:
			// 0900 - 097F Devanagari
			// 0980 - 09FF Bengali
			// 0A00 - 0A7F Gurmukhi
			// 0A80 - 0AFF Gujarati
			// 0B00 - 0B7F Oriya
			// 0B80 - 0BFF Tamil
			// 0C00 - 0C7F Telugu
			// 0C80 - 0CFF Kannada
			// 0D00 - 0D7F Malayalam
			// 0D80 - 0DFF Sinhala
			// 0E00 - 0E7F if Thai, assume shaping for vowel, tone marks
			return true;
		} else if(code < 0x0f00) {
			return false;
		} else if(code <= 0x0fff) { // U+0F00 - U+0FFF Tibetan
			return true;
		} else if(code < 0x10A0) {  // U+1000 - U+109F Myanmar
			return true;
		} else if(code < 0x1100) {
			return false;
		} else if(code < 0x11ff) { // U+1100 - U+11FF Old Hangul
			return true;
		} else if(code < 0x1780) {
			return false;
		} else if(code <= 0x17ff) { // 1780 - 17FF Khmer
			return true;
		} else if(code < 0x200c) {
			return false;
		} else if(code <= 0x200d) { // zwj or zwnj
			return true;
		} else if(code >= 0x202a && code <= 0x202e) { // directional control
			return true;
		} else if(code >= 0x206a && code <= 0x206f) { // directional control
			return true;
		}
		return false;
	}
}
