package com.unknown.text;

import java.util.ArrayList;

import com.unknown.text.hyph.Hyphenator;

public class TextWrap {
	private static enum State {
		NEWLINE, WORD, SPACE
	}

	private Hyphenator hyphenator;

	private int cols;
	private State state;

	private boolean space;

	private ArrayList<String> lines = new ArrayList<>();
	private StringBuilder buf = new StringBuilder();
	private StringBuilder currentLine = new StringBuilder();

	public TextWrap(int cols) {
		this.cols = cols;
	}

	public TextWrap(int cols, Hyphenator hyphenator) {
		this.cols = cols;
		this.hyphenator = hyphenator;
	}

	public void setHyphenator(Hyphenator hyphenator) {
		this.hyphenator = hyphenator;
	}

	public static String[] wrap(String text, int cols) {
		return new TextWrap(cols).wrap(text);
	}

	public static String[] wrap(String text, int cols, Hyphenator hyphenator) {
		return new TextWrap(cols, hyphenator).wrap(text);
	}

	private static boolean isSpace(char c) {
		return c == ' ' || c == '\t';
	}

	protected boolean tooLong(String s) {
		return s.length() > cols;
	}

	protected int getMaxFragments(String line, String[] fragments) {
		StringBuilder s = new StringBuilder(line);
		for(int i = 0; i < fragments.length; i++) {
			s.append(fragments[i]);
			s.append('-');
			if(tooLong(s.toString())) {
				return i;
			}
			s.deleteCharAt(s.length() - 1);
		}
		return fragments.length;
	}

	private String getCurrentLine() {
		String word = buf.toString();
		if(word.length() > 0) {
			if(currentLine.length() == 0) {
				return word;
			} else {
				return currentLine + (space ? " " : "") + word;
			}
		} else {
			return currentLine.toString();
		}
	}

	private void finishWord() {
		String line = getCurrentLine();
		if(tooLong(line)) {
			if(hyphenator != null) {
				StringBuilder word = new StringBuilder();
				char[] chars = buf.toString().toCharArray();
				for(int i = 0; i < chars.length; i++) {
					char c = chars[i];
					if(Character.isLetter(c)) {
						word.append(c);
					} else if(i != chars.length - 1) {
						// if there are special chars, don't wrap it
						word = null;
						break;
					}
				}
				if(word != null) {
					String[] fragments = hyphenator.hyphenate(buf.toString());
					String sp = (space ? " " : "");
					int fragcnt = getMaxFragments(currentLine.toString() + sp, fragments);
					if(fragcnt == 0) {
						lines.add(currentLine.toString());
						currentLine = buf;
						buf = new StringBuilder();
					} else {
						if(space) {
							currentLine.append(' ');
						}
						for(int i = 0; i < fragcnt; i++) {
							currentLine.append(fragments[i]);
						}
						if(fragcnt < fragments.length) {
							currentLine.append('-');
						}
						lines.add(currentLine.toString());
						currentLine = new StringBuilder();
						for(int i = fragcnt; i < fragments.length; i++) {
							currentLine.append(fragments[i]);
						}
						buf = new StringBuilder();
					}
				} else {
					lines.add(currentLine.toString());
					currentLine = buf;
					buf = new StringBuilder();
				}
			} else {
				lines.add(currentLine.toString());
				currentLine = buf;
				buf = new StringBuilder();
			}
		} else {
			if(currentLine.length() > 0 && space) {
				currentLine.append(' ');
			}
			currentLine.append(buf);
			buf = new StringBuilder();
		}
	}

	private void newline() {
		String line = getCurrentLine();
		lines.add(line);
		buf = new StringBuilder();
		currentLine = new StringBuilder();
	}

	protected void paragraph() {
		newline();
	}

	public String[] wrap(String text) {
		state = State.NEWLINE;
		space = false;
		for(char c : text.toCharArray()) {
			switch(state) {
			case NEWLINE: // start of a line
				space = false;
				if(isSpace(c)) {
					state = State.SPACE;
				} else if(c == '\n') {
					paragraph();
				} else {
					buf.append(c);
					state = State.WORD;
				}
				break;
			case WORD:
				if(c == '\n') {
					space = true;
					finishWord();
					state = State.NEWLINE;
				} else if(isSpace(c)) {
					state = State.SPACE;
					space = true;
					finishWord();
				} else {
					buf.append(c);
				}
				break;
			case SPACE:
				if(c == '\n') {
					// ignore
				} else if(isSpace(c)) {
					// ignore
				} else {
					state = State.WORD;
					buf.append(c);
				}
				break;
			default:
				throw new AssertionError();
			}
		}
		if(buf.length() > 0) {
			finishWord();
		}
		if(currentLine.length() > 0) {
			newline();
		}
		return lines.toArray(new String[lines.size()]);
	}
}
