package com.unknown.syntax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.unknown.util.ResourceLoader;

public class JavaHighlighter extends Highlighter {
	private List<String> classes;
	private boolean inComment = false;

	public static String[] keywords = { "abstract", "boolean", "break",
			"byte", "case", "catch", "char", "class", "continue",
			"default", "do", "double", "else", "extends", "false",
			"final", "finally", "float", "for", "if", "implements",
			"import", "instanceof", "int", "interface", "length",
			"long", "native", "new", "null", "package", "private",
			"protected", "public", "return", "short", "static",
			"super", "switch", "synchronized", "this",
			"threadsafe", "throw", "throws", "transient", "true",
			"try", "void", "while" };

	public static String operators = "~!%^&*-+=|/:<>?";

	public JavaHighlighter() throws IOException {
		super("~!@%^&*()-+=|\\/{}[]:;\"\'<> ,	.?", keywords);
		classes = new ArrayList<>();
		InputStream classdefs = ResourceLoader.loadResource(
				this.getClass(), "java.classes");
		if(classdefs != null) {
			loadClasses(new InputStreamReader(classdefs));
		}
	}

	public void loadClasses(Reader classfile) throws IOException {
		if(classfile != null) {
			BufferedReader in = new BufferedReader(classfile);
			String line;
			while((line = in.readLine()) != null) {
				line = line.trim();
				if(line.length() == 0) {
					continue;
				}
				String[] tokens = line.split(" ");
				for(int i = 0; i < tokens.length; i++) {
					if(tokens[i].length() != 0) {
						classes.add(tokens[i]);
					}
				}
			}
			in.close();
		}
	}

	public boolean isClass(String name) {
		return classes.contains(name);
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
			if(ch == '@') { // annotation
				i++;
				ch = line.charAt(i);
				while((i < length) && !isDelimiter(ch)) {
					temp.append(ch);
					i++;
					if(i < length) {
						ch = line.charAt(i);
					}
				}
				tmp = temp.toString();
				if(tmp.length() != 0) {
					if(!inString && !inCharacter && !inComment) {
						formatted.append("<span class=\"" + CSS_PREFIX + "preproc\">@" +
								htmlspecialchars(tmp) + "</span>");
					} else {
						formatted.append('@')
								.append(htmlspecialchars(tmp));
					}
				} else {
					formatted.append('@');
				}
				continue;
			}
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
			} else if(isKeyword(tmp) && !inString && !inCharacter && !inComment) {
				formatted.append("<span class=\"" + CSS_PREFIX + "keyword\">" + htmlspecialchars(tmp) +
						"</span>");
			} else if(isClass(tmp) && !inString && !inCharacter && !inComment) {
				formatted.append("<span class=\"" + CSS_PREFIX + "class\">" + htmlspecialchars(tmp) +
						"</span>");
			} else if(isCNumber(tmp) && !inString && !inCharacter && !inComment) {
				formatted.append("<span class=\"" + CSS_PREFIX + "number\">" + tmp + "</span>");
			} else {
				formatted.append(htmlspecialchars(tmp));
			}
			// because the last character read in the while-loop is
			// not part of tmp
			i++;

			boolean do_append = true;

			if((i < length) && (ch == '/') && (line.charAt(i) == '/') && !inString && !inCharacter &&
					!inComment) {
				formatted.append("<span class=\"" + CSS_PREFIX + "comment\">" + ch + line.substring(i) +
						"</span>");
				break;
			} else if(!inComment && !inCharacter && (ch == '"')) {
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
						formatted.append("<span class=\"" + CSS_PREFIX + "string\">" +
								htmlspecialchars(ch));
					} else {
						formatted.append(htmlspecialchars(ch) + "</span>");
					}
					inString = !inString;
				}
			} else if(!inComment && !inString && (ch == '\'')) {
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
					if(!inCharacter) {
						formatted.append("<span class=\"" + CSS_PREFIX + "string\">" +
								htmlspecialchars(ch));
					} else {
						formatted.append(htmlspecialchars(ch) + "</span>");
					}
					inCharacter = !inCharacter;
				}
			} else if(!inString && !inCharacter && (i < length) && (ch == '/') && (line.charAt(i) == '*')) {
				do_append = false;
				formatted.append("<span class=\"" + CSS_PREFIX + "comment\">" + htmlspecialchars(ch));
				inComment = true;
			} else if(!inString && !inCharacter && (i < length) && (ch == '*') && (line.charAt(i) == '/')) {
				do_append = false;
				formatted.append(htmlspecialchars(Character
						.toString(ch) +
						Character.toString(line
								.charAt(i))) +
						"</span>");
				inComment = false;
				i++;
			}

			// append last character (not contained in tmp) if it
			// was not processed elsewhere
			if(do_append && ((startAt + tmp.length()) < length)) {
				if(isOperator(ch) && !inString && !inComment && !inCharacter) {
					formatted.append("<span class=\"" + CSS_PREFIX + "operator\">" +
							htmlspecialchars(ch) + "</span>");
				} else {
					formatted.append(htmlspecialchars(ch));
				}
			}
		}

		formatted.append("\n");
		return formatted.toString();
	}
}
