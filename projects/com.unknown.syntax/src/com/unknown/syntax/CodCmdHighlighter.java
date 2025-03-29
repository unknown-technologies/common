package com.unknown.syntax;

public class CodCmdHighlighter extends Highlighter {
	public final static String[] keywords = { "set", "seta", "sets" };
	public final static String delimiters = "\t \"/";

	public CodCmdHighlighter() {
		super(delimiters, keywords);
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
			} else if(isKeyword(tmp) && !inString) {
				formatted.append("<span class=\"" + CSS_PREFIX
						+ "keyword\">"
						+ htmlspecialchars(tmp)
						+ "</span>");
			} else if(isNumeric(tmp) && !inString) {
				formatted.append("<span class=\"" + CSS_PREFIX
						+ "number\">" + tmp + "</span>");
			} else {
				formatted.append(htmlspecialchars(tmp));
			}
			// because the last character read in the while-loop is
			// not part of tmp
			i++;

			boolean do_append = true;

			if((i < length) && (ch == '/')
					&& (line.charAt(i) == '/') && !inString) {
				formatted.append("<span class=\"" + CSS_PREFIX
						+ "comment\">" + ch
						+ line.substring(i) + "</span>");
				break;
			} else if(ch == '"') {
				do_append = false;
				if(i > 1) {
					if(line.charAt(i - 2) == '\\') {
						if((i > 2) && (line.charAt(i - 3) == '\\')) {
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
			}
			// append last character (not contained in tmp) if it
			// was not processed elsewhere
			if(do_append && ((startAt + tmp.length()) < length)) {
				formatted.append(htmlspecialchars(ch));
			}
		}

		formatted.append("\n");
		return formatted.toString();
	}
}
