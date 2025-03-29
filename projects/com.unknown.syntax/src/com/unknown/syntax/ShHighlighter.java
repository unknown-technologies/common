package com.unknown.syntax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.unknown.util.ResourceLoader;

public class ShHighlighter extends Highlighter {
	public final static String[] keywords = { ".", ":", "alias", "bg",
			"bind", "break", "builtin", "caller", "case", "cd",
			"command", "compgen", "complete", "compopt",
			"continue", "coproc", "declare", "dirs", "disown",
			"do", "done", "echo", "else", "enable", "esac", "eval",
			"exec", "exit", "export", "false", "fc", "fg", "fi",
			"for", "function", "getopts", "hash", "help",
			"history", "if", "in", "jobs", "kill", "let", "local",
			"logout", "mapfile", "popd", "printf", "pushd", "pwd",
			"read", "readarray", "readonly", "return", "select",
			"set", "shift", "shopt", "source", "suspend", "test",
			"then", "time", "times", "trap", "true", "type",
			"typeset", "ulimit", "umask", "unalias", "unset",
			"until", "variables", "wait", "while", "{" };
	public final static String delimiters = "\t {}()[]#\"\'$*?|&<=>;,";
	public final static String operators = "[]{}()<=>|&?*,$";

	public static List<String> commands;

	public ShHighlighter() throws IOException {
		super(delimiters, keywords);
		commands = new ArrayList<>();
		InputStream commanddef = ResourceLoader.loadResource(
				this.getClass(), "sh.commands");
		if(commanddef != null) {
			loadCommands(new InputStreamReader(commanddef));
		}
	}

	public void loadCommands(Reader commandfile) throws IOException {
		if(commandfile != null) {
			BufferedReader in = new BufferedReader(commandfile);
			String line;
			while((line = in.readLine()) != null) {
				line = line.trim();
				if(line.length() == 0) {
					continue;
				}
				String[] tokens = line.split(" ");
				for(int i = 0; i < tokens.length; i++) {
					if(tokens[i].length() != 0) {
						commands.add(tokens[i]);
					}
				}
			}
			in.close();
		}
	}

	public boolean isCommand(String name) {
		return commands.contains(name);
	}

	public static boolean isOperator(char ch) {
		return operators.indexOf(ch) != -1;
	}

	enum STATE {
		BOL, COMMAND, DELIMITER, ARGUMENT, COMMENT, SINGLEQUOTE, DOUBLEQUOTE
	}

	public String formatLineStateMachine(String line) {
		if(line.startsWith("#!")) {
			// Hashbang
			return "<span class=\"" + CSS_PREFIX + "comment\">" + htmlspecialchars(line) + "</span>\n";
		}
		int i;
		char ch;
		STATE state = STATE.BOL;
		StringBuffer result = new StringBuffer();
		StringBuffer temp = new StringBuffer();
		int length = line.length();
		for(i = 0; i < length; i++) {
			ch = line.charAt(i);
			switch(state) {
			case BOL:
				switch(ch) {
				case '#':
					state = STATE.COMMENT;
					result.append("<span class=\"" + CSS_PREFIX + "comment\">" +
							htmlspecialchars(ch));
					break;
				case ' ':
				case '\t':
					result.append(ch);
					break;
				default:
					state = STATE.COMMAND;
					temp = new StringBuffer(
							Character.toString(ch));
				}
				break;
			case COMMAND:
				if("\t ".indexOf(ch) != -1) {
					String cmd = temp.toString();
					if(isKeyword(cmd)) {
						result.append("<span class=\"" + CSS_PREFIX + "keyword\">" +
								htmlspecialchars(cmd) + "</span>");
					} else if(isCommand(cmd)) {
						result.append("<span class=\"" + CSS_PREFIX + "class\">" +
								htmlspecialchars(cmd) + "</span>");
					} else {
						result.append(cmd);
					}
					switch(ch) {
					case '#':
						state = STATE.COMMENT;
						result.append("<span class=\"" + CSS_PREFIX + "comment\">" +
								htmlspecialchars(ch));
						break;
					default:
						state = STATE.DELIMITER;
						temp = new StringBuffer(
								Character.toString(ch));
					}
				} else {
					temp.append(ch);
				}
				break;
			case DELIMITER:
				switch(ch) {
				case '#':
					String delimiter = temp.toString();
					result.append(delimiter);
					state = STATE.COMMENT;
					result.append("<span class=\"" + CSS_PREFIX + "comment\">" +
							htmlspecialchars(ch));
					break;
				case ' ':
				case '\t':
					temp.append(ch);
					break;
				case '"':
					state = STATE.DOUBLEQUOTE;
					result.append(temp.toString());
					temp = new StringBuffer(
							Character.toString(ch));
					break;
				case '\'':
					state = STATE.DOUBLEQUOTE;
					result.append(temp.toString());
					temp = new StringBuffer(
							Character.toString(ch));
					break;
				default:
					result.append(temp.toString());
					temp = new StringBuffer(
							Character.toString(ch));
					state = STATE.ARGUMENT;
				}
				break;
			case DOUBLEQUOTE:
				switch(ch) {
				case '"':
					state = STATE.ARGUMENT;
					temp.append(ch);
					result.append("<span class=\"" + CSS_PREFIX + "string\">" +
							htmlspecialchars(temp
									.toString()) +
							"</span>");
					temp = new StringBuffer();
					break;
				default:
					temp.append(ch);

				}
				break;
			case SINGLEQUOTE:
				switch(ch) {
				case '\'':
					state = STATE.ARGUMENT;
					temp.append(ch);
					result.append("<span class=\"" + CSS_PREFIX + "string\">" +
							htmlspecialchars(temp
									.toString()) +
							"</span>");
					temp = new StringBuffer();
					break;
				default:
					temp.append(ch);

				}
				break;
			case ARGUMENT:
				switch(ch) {
				case ' ':
				case '\t':
					result.append(htmlspecialchars(temp
							.toString()));
					temp = new StringBuffer(
							Character.toString(ch));
					state = STATE.DELIMITER;
					break;
				default:
					temp.append(ch);
					break;
				}
				break;
			case COMMENT:
				result.append(htmlspecialchars(ch));
				break;
			default:
				temp.append(ch);
			}
		}
		switch(state) {
		case COMMENT:
			result.append("</span>");
			break;
		case COMMAND:
			String cmd = temp.toString();
			if(isKeyword(cmd)) {
				result.append("<span class=\"" + CSS_PREFIX + "keyword\">" + htmlspecialchars(cmd) +
						"</span>");
			} else if(isCommand(cmd)) {
				result.append("<span class=\"" + CSS_PREFIX + "class\">" + htmlspecialchars(cmd) +
						"</span>");
			} else {
				result.append(htmlspecialchars(cmd));
			}
			break;
		default:
			if(temp != null) {
				result.append(htmlspecialchars(temp.toString()));
			}
			break;
		}
		result.append("\n");
		return result.toString();
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
				formatted.append("<span class=\"" + CSS_PREFIX + "keyword\">" + htmlspecialchars(tmp) +
						"</span>");
			} else if(isCommand(tmp) && !inString && !inCharacter) {
				formatted.append("<span class=\"" + CSS_PREFIX + "class\">" + htmlspecialchars(tmp) +
						"</span>");
			} else if(isNumeric(tmp) && !inString && !inCharacter) {
				formatted.append("<span class=\"" + CSS_PREFIX + "number\">" + tmp + "</span>");
			} else {
				formatted.append(htmlspecialchars(tmp));
			}
			// because the last character read in the while-loop is
			// not part of tmp
			i++;

			boolean do_append = true;

			if((i < length) && (ch == '#') && !inString && !inCharacter) {
				formatted.append("<span class=\"" + CSS_PREFIX + "comment\">" + ch + line.substring(i) +
						"</span>");
				break;
			} else if((ch == '#') && !inString && !inCharacter) {
				formatted.append("<span class=\"" + CSS_PREFIX + "comment\">" + ch + "</span>");
				break;
			} else if(!inCharacter && (ch == '"')) {
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
			} else if(!inString && (ch == '\'')) {
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
			}

			// append last character (not contained in tmp) if it
			// was not processed elsewhere
			if(do_append && ((startAt + tmp.length()) < length)) {
				if(isOperator(ch) && !inString && !inCharacter) {
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
