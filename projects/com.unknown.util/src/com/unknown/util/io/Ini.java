package com.unknown.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ini {
	private Map<String, Map<String, String>> data = new HashMap<>();

	public Ini() {
	}

	public Ini(String data) throws IOException {
		load(data);
	}

	public Ini(Reader in) throws IOException {
		load(in);
	}

	public void load(String ini) throws IOException {
		try(Reader in = new StringReader(ini)) {
			load(in);
		}
	}

	public void load(Reader in) throws IOException {
		int state = 0;
		int linenr = 0;
		StringBuilder buf = new StringBuilder();
		String section = null;
		String key = null;
		String line;
		BufferedReader b = new BufferedReader(in);
		while((line = b.readLine()) != null) {
			// strip UTF-8 BOM
			if(linenr == 0 && line.startsWith("\ufeff")) {
				line = line.substring(1);
			}
			linenr++;
			Reader linein = new StringReader(line);
			int ch;
			int col = 0;
			line: while((ch = linein.read()) != -1) {
				assert ch != '\r' && ch != '\n';
				char c = (char) ch;
				col++;
				switch(state) {
				case 0: // no section yet
					if(ch == '#' || ch == ';') {
						break line;
					} else if(ch == '\t' || ch == ' ') {
						// skip
					} else if(ch == '[') {
						state = 1; // section name
					} else {
						throw new IOException("Syntax error on line " + linenr + ":" +
								col + ": unexpected token before section");
					}
					break;
				case 1: // section name
					if(ch == '\t') {
						throw new IOException("Syntax error on line " + linenr + ":" +
								col + ": section name cannot contain tabs");
					} else if(ch == ']') {
						state = 2; // ']' to EOL
						section = buf.toString();
						buf = new StringBuilder();
					} else {
						buf.append(c);
					}
					break;
				case 2: // ']' to EOL
					if(ch == ' ' || ch == '\t') {
						// skip
					} else if(ch == '#' || ch == ';') {
						break line;
					} else {
						throw new IOException("Syntax error on line " + linenr + ":" +
								col + ": unexpected token after section header");
					}
					break;
				case 3: // section (key name, BOL)
					if(ch == '#' || ch == ';') {
						break line;
					} else if(ch == '\t' || ch == ' ') {
						// skip
					} else if(ch == '[') {
						state = 1; // section name
					} else {
						buf.append(c);
						state = 4; // key name
					}
					break;
				case 4: // key name
					if(ch == ' ' || ch == '\t') {
						key = buf.toString();
						buf = new StringBuilder();
						state = 5; // skip whitespace until '='
					} else if(ch == '=') {
						key = buf.toString();
						buf = new StringBuilder();
						state = 6; // skip whitespace after '='
					} else {
						buf.append(c);
					}
					break;
				case 5: // skip whitespace until '='
					if(ch == ' ' || ch == '\t') {
						// skip
					} else if(ch == '=') {
						state = 6; // skip whitespace after '='
					} else {
						throw new IOException("Syntax error on line " + linenr + ":" +
								col + ": unexpected token after key name");
					}
					break;
				case 6: // skip whitespace after '='
					if(ch == ' ' || ch == '\t') {
						// skip
					} else if(ch == '"') {
						state = 7; // quoted value
					} else {
						state = 8; // value
						buf.append(c);
					}
					break;
				case 7: // quoted value
					if(ch == '\\') {
						state = 9; // escape
					} else if(ch == '"') {
						String value = buf.toString();
						buf = new StringBuilder();
						set(section, key, value);
						state = 10; // after quote
					} else {
						buf.append(c);
					}
					break;
				case 8: // value
					buf.append(c);
					break;
				case 9: // escape
					ch = in.read();
					switch(ch) {
					case 'n':
						buf.append('\n');
						break;
					case 'r':
						buf.append('\r');
						break;
					case 't':
						buf.append('\t');
						break;
					case '\'':
						buf.append('\'');
						break;
					case '"':
						buf.append('"');
						break;
					case '\\':
						buf.append('\\');
						break;
					default:
						throw new IOException("Syntax error on line " +
								linenr + ":" + col +
								": invalid escape sequence \\" + c);
					}
					state = 8;
					break;
				case 10: // after quote
					if(ch == '#' || ch == ';') {
						break line;
					} else if(ch == '\r' || ch == '\n' || ch == ' ' || ch == '\t') {
						// skip
					} else {
						throw new IOException("Syntax error on line " + linenr + ":" + col +
								": unexpected token");
					}
					break;
				default:
					throw new AssertionError();
				}
			}
			if(state == 2) { // ']' to EOL
				state = 3; // section (key name, BOL)
			} else if(state == 6) { // skip whitespace after '='
				set(section, key, "");
				state = 3; // section (key name, BOL)
			} else if(state == 8) { // value
				String value = buf.toString();
				buf = new StringBuilder();
				set(section, key, value.trim()); // TODO: implement using FSM
				state = 3; // section (key name, BOL)
			} else if(state == 10) { // after quote
				state = 3; // section (key name, BOL)
			}
			if(state == 1 || state == 2 || state == 4 || state == 5 || state == 6 || state == 7 ||
					state == 9) {
				throw new IOException("Syntax error on line " + linenr + ":" + col +
						": unterminated token");
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		for(String section : getSections()) {
			buf.append('[').append(section).append("]\n");
			for(String key : getKeys(section)) {
				String value = get(section, key);
				buf.append(key).append("=").append(value).append('\n');
			}
		}
		return buf.toString();
	}

	public void set(String section, String key, String value) {
		Map<String, String> sec = data.get(section);
		if(sec == null) {
			sec = new HashMap<>();
			data.put(section, sec);
		}
		sec.put(key, value);
	}

	public String get(String section, String key) {
		Map<String, String> sec = data.get(section);
		if(sec == null) {
			return null;
		} else {
			return sec.get(key);
		}
	}

	public void remove(String section, String key) {
		Map<String, String> sec = data.get(section);
		if(sec == null) {
			return;
		} else {
			sec.remove(key);
			if(sec.size() == 0) {
				data.remove(section);
			}
		}
	}

	public List<String> getSections() {
		return new ArrayList<>(data.keySet());
	}

	public List<String> getKeys(String section) {
		Map<String, String> sec = data.get(section);
		if(sec == null) {
			return Collections.emptyList();
		} else {
			return new ArrayList<>(sec.keySet());
		}
	}

	public int getSectionCount() {
		return data.size();
	}
}
