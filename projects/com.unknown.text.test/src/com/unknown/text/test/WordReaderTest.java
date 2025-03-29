package com.unknown.text.test;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.unknown.text.WordReader;

public class WordReaderTest {
	private static final String INPUT = "as-so-ciate as-so-ciates dec-li-na-tion oblig-a-tory phil-an-thropic present\n" +
			"presents project projects reci-procity re-cog-ni-zance ref-or-ma-tion\n" +
			"ret-ri-bu-tion ta-ble";
	private static final String[] OUTPUT = { "as-so-ciate", "as-so-ciates", "dec-li-na-tion", "oblig-a-tory",
			"phil-an-thropic", "present", "presents", "project", "projects", "reci-procity",
			"re-cog-ni-zance", "ref-or-ma-tion", "ret-ri-bu-tion", "ta-ble"
	};

	@Test
	public void test001() throws IOException {
		List<String> words = new ArrayList<>();
		try(Reader in = new StringReader(INPUT)) {
			WordReader r = new WordReader(in);
			String word;
			while((word = r.read()) != null) {
				words.add(word);
			}
		}
		String[] wordList = words.toArray(new String[words.size()]);
		assertArrayEquals(OUTPUT, wordList);
	}
}
