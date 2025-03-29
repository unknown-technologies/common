package com.unknown.syntax;

import java.util.Vector;

public class CHighlighter extends Highlighter {
	private boolean inComment = false;

	public static String operators = "!%&*+-/<=>^|~?:";
	public static String[] keywords = { "#define", "#elif", "#else",
			"#endif", "#error", "#if", "#ifdef", "#ifndef",
			"#include", "#include_next", "#line", "#pragma",
			"#undef", "__asm", "__based", "__cdecl", "__declspec",
			"__except", "__far", "__fastcall", "__finally",
			"__fortran", "__huge", "__inline", "__int16",
			"__int32", "__int64", "__int8", "__interrupt",
			"__leave", "__loadds", "__near", "__pascal",
			"__saveregs", "__segment", "__segname", "__self",
			"__stdcall", "__try", "__uuidof", "auto", "bool",
			"break", "case", "char", "const", "continue",
			"default", "defined", "do", "double", "else", "enum",
			"extern", "float", "for", "goto", "if", "int", "long",
			"register", "return", "short", "signed", "sizeof",
			"static", "struct", "switch", "typedef", "union",
			"unsigned", "void", "volatile", "while" };

	public static String[] cpp = { "__multiple_inheritance",
			"__single_inheritance", "__virtual_inheritance",
			"catch", "class", "const_cast", "delete",
			"dynamic_cast", "explicit", "export", "false",
			"friend", "inline", "mutable", "namespace", "new",
			"operator", "private", "protected", "public",
			"reinterpret_cast", "static_cast", "template", "this",
			"throw", "true", "try", "typeid", "typename", "using",
			"virtual", "wchar_t" };
	private static Vector<String> cppkeywords;

	public CHighlighter() {
		super("~!@%^&*()-+=|\\/{}[]:;\"\'<> ,	.?", keywords);
		cppkeywords = new Vector<>();
		for(String keyword : cpp) {
			cppkeywords.addElement(keyword);
		}
	}

	public static boolean isOperator(char c) {
		return operators.indexOf(c) != -1;
	}

	public static boolean isCPPKeyword(String keyword) {
		return cppkeywords.contains(keyword);
	}

	@Override
	public String formatLine(String line) {
		if(line == null) {
			return new String();
		}

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
			} else if(isKeyword(tmp) && !inString && !inCharacter
					&& !inComment) {
				formatted.append("<span class=\"" + CSS_PREFIX
						+ "keyword\">"
						+ htmlspecialchars(tmp)
						+ "</span>");
			} else if(isCPPKeyword(tmp) && !inString
					&& !inCharacter && !inComment) {
				formatted.append("<span class=\"" + CSS_PREFIX
						+ "special\">"
						+ htmlspecialchars(tmp)
						+ "</span>");
			} else if(isCNumber(tmp) && !inString && !inCharacter
					&& !inComment) {
				formatted.append("<span class=\"" + CSS_PREFIX
						+ "number\">"
						+ htmlspecialchars(tmp)
						+ "</span>");
			} else {
				formatted.append(htmlspecialchars(tmp));
			}

			// because the last character read in the while-loop is
			// not part of tmp
			i++;

			boolean doAppend = true;
			if((i < length) && (ch == '/')
					&& (line.charAt(i) == '/') && !inString
					&& !inCharacter && !inComment) {
				formatted.append("<span class=\"" + CSS_PREFIX
						+ "comment\">" + ch
						+ line.substring(i) + "</span>");
				break;
			} else if(!inComment && !inCharacter && (ch == '"')) {
				doAppend = false;
				if(i > 1) {
					if(line.charAt(i - 2) == '\\') {
						if((i > 2) && (line.charAt(i - 3) == '\\')) {
							doAppend = false;
						} else {
							doAppend = true;
						}
					}
				}
				if(!doAppend) {
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
			} else if(!inComment && !inString && (ch == '\'')) {
				doAppend = false;
				if(i > 1) {
					if(line.charAt(i - 2) == '\\') {
						if((i > 2) && (line.charAt(i - 3) == '\\')) {
							doAppend = false;
						} else {
							doAppend = true;
						}
					}
				}
				if(!doAppend) {
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
			} else if(!inString && !inCharacter && (i < length)
					&& (ch == '/')
					&& (line.charAt(i) == '*')) {
				doAppend = false;
				formatted.append("<span class=\"" + CSS_PREFIX
						+ "comment\">"
						+ htmlspecialchars(ch));
				inComment = true;
			} else if(!inString && !inCharacter && (i < length)
					&& (ch == '*')
					&& (line.charAt(i) == '/')) {
				doAppend = false;
				formatted.append(htmlspecialchars(Character
						.toString(ch)
						+ Character.toString(line
								.charAt(i)))
						+ "</span>");
				inComment = false;
				i++;
			} else if(!inString && !inCharacter && !inComment
					&& isOperator(ch)) {
				doAppend = false;
				formatted.append("<span class=\"" + CSS_PREFIX
						+ "operator\">"
						+ htmlspecialchars(ch)
						+ "</span>");
			}

			// append last character (not contained in tmp) if it
			// was not processed elsewhere
			if(doAppend && ((startAt + tmp.length()) < length)) {
				formatted.append(htmlspecialchars(ch));
			}
		}
		formatted.append("\n");
		return formatted.toString();
	}
}
