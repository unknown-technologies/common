package com.unknown.syntax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

public abstract class Highlighter {
	private final Set<String> keywords;
	private final String delimiters;

	public static final String CSS_PREFIX = "syntax-";

	public static final String DEFAULT_CSS = "pre{font-family:Consolas,\"Courier New\",\"Lucida Console\",monospace;}" +
			".syntax-keyword{color:#00f;}.syntax-class{color:#f00;}.syntax-comment{color:#008080;}.syntax-string{color:#808080;}" +
			".syntax-number{color:#f00;}.syntax-operator{color:#ff8000;}.syntax-cpp{color:#f00;}";

	public Highlighter(String delimiters, String[] keywords) {
		this.delimiters = delimiters;
		this.keywords = new HashSet<>();
		if(keywords != null) {
			for(int i = 0; i < keywords.length; i++) {
				this.keywords.add(keywords[i]);
			}
		}
	}

	public boolean isDelimiter(char c) {
		if((c == '\r') || (c == '\n')) {
			return true;
		} else {
			return delimiters.indexOf(c) != -1;
		}
	}

	public static String htmlspecialchars(char c) {
		return htmlspecialchars(c, false);
	}

	public static String htmlspecialchars(char c, boolean quote) {
		return htmlspecialchars(Character.toString(c), quote);
	}

	public static String htmlspecialchars(String s) {
		return htmlspecialchars(s, false);
	}

	public static String htmlspecialchars(String s, boolean quote) {
		String r = s.replace("&", "&amp;").replace("<", "&lt;")
				.replace(">", "&gt;");
		return quote ? r.replace("\"", "&quot;") : r;
	}

	public boolean isKeyword(String keyword) {
		return keywords.contains(keyword);
	}

	public boolean isInsideString(String line, int position) {
		if(line.indexOf("\"") == -1) {
			return false;
		}
		boolean inString = false;
		for(int i = 0; i < position; i++) {
			char ch = line.charAt(i);
			if((i > 0) && (ch == '"') && (line.charAt(i - 1) == '\\')) {
				continue;
			} else if(ch == '"') {
				inString = !inString;
			}
		}
		return inString;
	}

	public static boolean isNumeric(char c) {
		if((c < '0') || (c > '9')) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean isNumeric(String line) {
		int length = line.length();
		for(int i = 0; i < length; i++) {
			char c = line.charAt(i);
			if((c < '0') || (c > '9')) {
				return false;
			}
		}
		return true;
	}

	public static boolean isHexNumber(String line) {
		String s = line.toLowerCase();
		if(s.length() < 3) {
			return isNumeric(line);
		}
		if(s.charAt(0) != '0') {
			return false;
		}
		if(s.charAt(1) != 'x') {
			return false;
		}
		for(int i = 2; i < s.length(); i++) {
			char c = s.charAt(i);
			if(((c < '0') || (c > '9')) && ((c < 'a') || (c > 'f'))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNumber(String line, String suffixes) {
		if(line.length() > 1) {
			if(isNumeric(line.substring(0, line.length() - 2)) && (suffixes.indexOf(line.charAt(line
					.length() - 1)) != -1)) {
				return true;
			}
		}
		return isNumeric(line);
	}

	public static boolean isCNumber(String line) {
		return isHexNumber(line) || isNumber(line, "fFlLUu");
	}

	public abstract String formatLine(String line);

	public void format(Reader in, Writer out) throws IOException {
		BufferedReader linein = new BufferedReader(in);
		String line = null;
		while((line = linein.readLine()) != null) {
			out.write(formatLine(line));
		}
	}
}
