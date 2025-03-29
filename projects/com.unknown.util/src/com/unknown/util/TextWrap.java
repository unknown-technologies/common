package com.unknown.util;

import java.util.ArrayList;
import java.util.List;

public class TextWrap {
	public static String wrap(final String str, final int wrapLength,
			final String newLineStr, int indent,
			final boolean wrapLongWords) {
		if(str == null) {
			return null;
		}
		String newLineString = newLineStr == null ? "\n" : newLineStr;
		int wrap = wrapLength < 1 ? 1 : wrapLength;
		final int inputLineLength = str.length();
		int offset = 0;
		final StringBuilder wrappedLine = new StringBuilder(inputLineLength + 32);

		boolean first = true;
		while(offset < inputLineLength) {
			if(str.charAt(offset) == ' ') {
				offset++;
				continue;
			}
			// only last line without leading spaces is left
			if(inputLineLength - offset <= wrap) {
				break;
			}
			int spaceToWrapAt = str.lastIndexOf(' ', wrap + offset);

			if(spaceToWrapAt >= offset) {
				// normal case
				wrappedLine.append(str.substring(offset, spaceToWrapAt));
				wrappedLine.append(newLineString);
				offset = spaceToWrapAt + 1;

			} else {
				// really long word or URL
				if(wrapLongWords) {
					// wrap really long word one line at a time
					wrappedLine.append(str.substring(offset, wrap + offset));
					wrappedLine.append(newLineString);
					offset += wrap;
				} else {
					// do not wrap really long word, just extend beyond limit
					spaceToWrapAt = str.indexOf(' ', wrap + offset);
					if(spaceToWrapAt >= 0) {
						wrappedLine.append(str.substring(offset, spaceToWrapAt));
						wrappedLine.append(newLineString);
						offset = spaceToWrapAt + 1;
					} else {
						wrappedLine.append(str.substring(offset));
						offset = inputLineLength;
					}
				}
			}
			if(first) {
				first = false;
				wrap -= indent;
			}
		}

		// Whatever is left in line is short enough to just pass through
		wrappedLine.append(str.substring(offset));

		return wrappedLine.toString();
	}

	public static String[] getLines(String s, int width, int indent) {
		List<String> result = new ArrayList<>();
		StringBuffer wrapbuf = new StringBuffer("\n");
		for(int i = 0; i < indent; i++)
			wrapbuf.append(' ');
		String wrap = wrapbuf.toString();
		int last = 0;
		int eol = 1;
		while(eol != -1) {
			eol = s.indexOf('\n', last);
			String l = wrap(s.substring(last, eol == -1 ? s.length() : eol), width, wrap, indent, true);
			if(eol == -1 && s.endsWith("\n"))
				break;
			last = eol + 1;
			if(l.indexOf('\n') != -1) {
				String[] lines = l.split("\n");
				for(String t : lines)
					result.add(t);
			} else {
				result.add(l);
			}
			// TODO: optimize!
		}
		return result.toArray(new String[result.size()]);
	}
}
