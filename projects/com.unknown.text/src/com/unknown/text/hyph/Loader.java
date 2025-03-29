package com.unknown.text.hyph;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.unknown.text.WordReader;

public class Loader {
	public static String[] load(String name) throws IOException {
		List<String> words = new ArrayList<>();
		try(Reader in = new InputStreamReader(Loader.class.getResourceAsStream("rules/" + name))) {
			WordReader win = new WordReader(in);
			String word;
			while((word = win.read()) != null) {
				words.add(word);
			}
		}
		return words.toArray(new String[words.size()]);
	}
}
