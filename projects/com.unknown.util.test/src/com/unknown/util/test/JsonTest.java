package com.unknown.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.unknown.util.json.JsonNumber;
import com.unknown.util.json.JsonObject;
import com.unknown.util.json.JsonParser;
import com.unknown.util.json.JsonValue;
import com.unknown.util.json.Scanner;
import com.unknown.util.json.Token;
import com.unknown.util.json.Token.Type;

public class JsonTest {
	private List<Token> expected;

	@Before
	public void setup() {
		expected = new ArrayList<>();
	}

	private void scan(String input) throws ParseException {
		Scanner s = new Scanner(new StringReader(input));
		for(Token t : expected) {
			Token act = s.scan();
			assertEquals(t, act);
		}
	}

	private void token(Token t) {
		expected.add(t);
	}

	@Test
	public void testTokens() throws ParseException {
		token(new Token(1.23));
		token(new Token(0.9375));
		token(new Token("hello world"));
		token(new Token(Type.TRUE));
		token(new Token(Type.COMMA));
		token(new Token(Type.FALSE));
		token(new Token(Type.NONE));
		scan("1.23 0x0.F \"hello world\" true, false");
	}

	@Test
	public void testParser() throws ParseException {
		JsonValue value = JsonParser.parse(
				"{\"power\":371.34,\"is_valid\":true,\"timestamp\":1707775762,\"counters\":[377.684, 371.441, 369.976],\"total\":267711}");
		assertTrue(value instanceof JsonObject);
		JsonObject obj = (JsonObject) value;
		assertTrue(obj.get("power") instanceof JsonNumber);
		assertEquals(((JsonNumber) obj.get("power")).floatValue(), 371.34, 0.0000001);
	}
}
