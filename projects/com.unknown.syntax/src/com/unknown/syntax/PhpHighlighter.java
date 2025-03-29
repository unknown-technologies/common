package com.unknown.syntax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.unknown.util.ResourceLoader;

public class PhpHighlighter extends Highlighter {
	public final static String[] KEYWORDS = { "break", "case", "continue",
			"default", "do", "else", "elseif", "endfor",
			"endforeach", "endif", "endswitch", "endwhile", "for",
			"foreach", "function", "if", "include", "include_once",
			"require", "require_once", "return", "switch", "while",
			"$GLOBALS", "$HTTP_COOKIE_VARS", "$HTTP_ENV_VARS",
			"$HTTP_GET_VARS", "$HTTP_POST_FILES",
			"$HTTP_POST_VARS", "$HTTP_SERVER_VARS",
			"$HTTP_SESSION_VARS", "$PHP_SELF", "$_COOKIE", "$_ENV",
			"$_FILES", "$_GET", "$_POST", "$_REQUEST", "$_SERVER",
			"$_SESSION", "$argc", "$argv", "$this", "NULL",
			"__autoload", "__call", "__clone", "__construct",
			"__destruct", "__get", "__set", "__sleep", "__wakeup",
			"abstract", "as", "catch", "cfunction", "class",
			"declare", "enddeclare", "extends", "false", "final",
			"global", "implements", "interface", "namespace",
			"old_function", "parent", "private", "protected",
			"public", "static", "stdClass", "throw", "true", "try",
			"var" };

	public final static String DELIMITERS = "~!@%^&*()-+=|\\/{}[]:;\"'<> ,	.?";

	private List<String> functions;
	private List<String> constants;

	private boolean inComment = false;
	private boolean inPHP = false;
	private boolean inTag = false;
	private boolean inHTMLComment = false;
	private boolean inHTMLString = false;

	public List<String> loadList(Reader file) throws IOException {
		return loadList(file, false);
	}

	public List<String> loadList(Reader file, boolean ignoreCase)
			throws IOException {
		List<String> data = new ArrayList<>();
		if(file != null) {
			BufferedReader in = new BufferedReader(file);
			String line;
			while((line = in.readLine()) != null) {
				line = line.trim();
				if(line.length() == 0) {
					continue;
				}
				if(ignoreCase) {
					line = line.toUpperCase();
				}
				String[] tokens = line.split(" ");
				for(int i = 0; i < tokens.length; i++) {
					if(tokens[i].length() != 0) {
						data.add(tokens[i]);
					}
				}
			}
			in.close();
		}
		return data;
	}

	private static String[] toupper(String[] s) {
		String[] r = new String[s.length];
		for(int i = 0; i < s.length; i++) {
			r[i] = s[i].toUpperCase();
		}
		return r;
	}

	public PhpHighlighter() throws IOException {
		super(DELIMITERS, toupper(KEYWORDS));
		InputStream functiondefs = ResourceLoader.loadResource(this.getClass(), "php.functions");
		functions = loadList(new InputStreamReader(functiondefs), true);
		InputStream constantdefs = ResourceLoader.loadResource(this.getClass(), "php.constants");
		constants = loadList(new InputStreamReader(constantdefs), true);
	}

	@Override
	public String formatLine(String line) {
		if(line == null || line.length() == 0) {
			return "";
		}

		StringBuffer formatted = new StringBuffer();

		int i = 0;
		int startAt = 0;
		char ch;
		StringBuffer temp = new StringBuffer();

		boolean inString = false;
		boolean inCharacter = false;

		// parse line
		while(i < line.length()) {
			temp = new StringBuffer();
			ch = line.charAt(i);
			startAt = i;
			while(i < line.length() && !isDelimiter(ch)) {
				temp.append(ch);
				i++;
				if(i < line.length()) {
					ch = line.charAt(i);
				}
			}
			i++; // because the last character read in the
				 // while-loop is not part of tempString
			if((i > 0) && (line.length() >= (i + 4)) &&
					line.substring(i - 1, i + 4).toUpperCase().equals("<?PHP") && !inPHP) {
				formatted.append("<span class=\"" + CSS_PREFIX + "keyword\">")
						.append(htmlspecialchars(line.substring(i - 1, i + 4)))
						.append("</span>");
				inPHP = true;
				i += 4;
			} else if((i > 0) && (line.length() >= (i + 1)) && line.substring(i - 1, i + 1).equals("?>") &&
					inPHP && !inString && !inCharacter) {
				formatted.append("<span class=\"" + CSS_PREFIX + "keyword\">")
						.append(htmlspecialchars(line.substring(i - 1, i + 1)))
						.append("</span>");
				inPHP = false;
				i++;
			} else if(inPHP) { // PHP-Mode
				if(temp.length() == 0) {
					// nothing
				} else if(isKeyword(temp.toString().toUpperCase()) && !inString && !inCharacter &&
						!inComment) {
					formatted.append("<span class=\"" + CSS_PREFIX + "keyword\">")
							.append(htmlspecialchars(temp.toString())).append("</span>");
				} else if(functions.contains(temp.toString().toUpperCase()) && !inString &&
						!inCharacter && !inComment) {
					formatted.append("<span class=\"" + CSS_PREFIX + "class\">")
							.append(htmlspecialchars(temp.toString())).append("</span>");
				} else if(constants.contains(temp.toString().toUpperCase()) && !inString &&
						!inCharacter && !inComment) {
					formatted.append("<span class=\"" + CSS_PREFIX + "constant\">")
							.append(htmlspecialchars(temp.toString())).append("</span>");
				} else if((temp.charAt(0) == '$') && !inString && !inCharacter && !inComment) {
					formatted.append("<span class=\"" + CSS_PREFIX + "identifier\">")
							.append(htmlspecialchars(temp.toString())).append("</span>");
				} else if(isNumeric(temp.toString()) && !inString && !inCharacter && !inComment) {
					formatted.append("<span class=\"" + CSS_PREFIX + "number\">")
							.append(htmlspecialchars(temp.toString())).append("</span>");
				} else {
					formatted.append(htmlspecialchars(temp.toString()));
				}

				boolean do_append = true;

				if((i < line.length()) && (ch == '/') && (line.charAt(i) == '/') && !inString &&
						!inCharacter && !inComment) {
					formatted.append("<span class=\"" + CSS_PREFIX + "comment\">")
							.append(htmlspecialchars(ch + line.substring(i)))
							.append("</span>");
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
							formatted.append("<span class=\"" + CSS_PREFIX + "string\">")
									.append(htmlspecialchars(ch));
						} else {
							formatted.append(htmlspecialchars(ch)).append("</span>");
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
							formatted.append("<span class=\"" + CSS_PREFIX + "string\">")
									.append(htmlspecialchars(ch));
						} else {
							formatted.append(htmlspecialchars(ch)).append("</span>");
						}
						inCharacter = !inCharacter;
					}
				} else if(!inString && !inCharacter && (i < line.length()) && (ch == '/') &&
						(line.charAt(i) == '*')) {
					do_append = false;
					formatted.append("<span class=\"" + CSS_PREFIX + "comment\">").append(
							htmlspecialchars(ch));
					inComment = true;
				} else if(!inString && !inCharacter && (i < line.length()) && (ch == '*') &&
						(line.charAt(i) == '/')) {
					do_append = false;
					formatted.append(
							htmlspecialchars(new StringBuffer().append(ch)
									.append(line.charAt(i)).toString()))
							.append(
									"</span>");
					inComment = false;
					i++;
				}

				// append last character (not contained in tempString) if it was not processed elsewhere
				// replace html-specific chars
				if(do_append && ((startAt + temp.length()) < line.length())) {
					formatted.append(htmlspecialchars(ch));
				}
			} else { // HTML-Mode
				boolean done = false;
				if((startAt + temp.length()) < line.length() && !inHTMLString) {
					int end = startAt + temp.length() + 1;
					done = true;
					if(((end + 2) < line.length()) && (ch == '<') &&
							(line.charAt(end + 1) == '-') &&
							(line.charAt(end + 2) == '-') && !inTag && !inHTMLComment) {
						inHTMLComment = true;
						formatted.append(htmlspecialchars(temp.toString()))
								.append("<span class=\"" + CSS_PREFIX + "comment\">")
								.append(htmlspecialchars(ch));
					} else if((ch == '<') && !inTag) {
						inTag = true;
						formatted.append(htmlspecialchars(temp.toString()))
								.append("<span class=\"" + CSS_PREFIX + "keyword\">")
								.append(htmlspecialchars(ch));
					} else if((ch == '>') && inTag) {
						inTag = false;
						formatted.append(htmlspecialchars(temp.toString()))
								.append(htmlspecialchars(ch)).append("</span>");
					} else if((ch == '>') && inHTMLComment) {
						inHTMLComment = false;
						formatted.append(htmlspecialchars(temp.toString()))
								.append(htmlspecialchars(ch)).append("</span>");
					} else
						done = false;
				}
				if(!done) {
					if(inTag && !inHTMLComment && !inHTMLString && (ch == '=')) {
						formatted.append("<span class=\"" + CSS_PREFIX + "class\">")
								.append(htmlspecialchars(temp.toString()))
								.append(htmlspecialchars(ch)).append("</span>");
					} else if(inTag && !inHTMLComment && !inHTMLString && (ch == '"')) {
						inHTMLString = true;
						formatted.append(htmlspecialchars(temp.toString()))
								.append("<span class=\"" + CSS_PREFIX + "string\">")
								.append(htmlspecialchars(ch));
					} else if(inTag && !inHTMLComment && inHTMLString && (ch == '"')) {
						inHTMLString = false;
						formatted.append(htmlspecialchars(temp.toString()))
								.append(htmlspecialchars(ch)).append("</span>");
					} else {
						formatted.append(htmlspecialchars(temp.toString())).append(
								htmlspecialchars(ch));
					}
				}

			}
		}

		formatted.append("\n");
		return formatted.toString();
	}
}
