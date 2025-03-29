package com.unknown.text;

import java.io.IOException;
import java.io.Reader;

public class WordReader {
	private Reader in;

	public WordReader(Reader in) {
		this.in = in;
	}

	public String read() throws IOException {
		int ch;
		StringBuilder buf = new StringBuilder();
		loop: while((ch = in.read()) != -1) {
			char c = (char) ch;
			switch(c) {
			case ' ':
			case '\r':
			case '\n':
			case '\t':
				if(buf.length() == 0) {
					continue loop;
				} else {
					return buf.toString();
				}
			default:
				buf.append(c);
			}
		}
		if(buf.length() > 0) {
			return buf.toString();
		} else {
			return null;
		}
	}
}
