package com.unknown.util.json;

import static com.unknown.util.json.Token.Type.COLON;
import static com.unknown.util.json.Token.Type.COMMA;
import static com.unknown.util.json.Token.Type.LBRAC;
import static com.unknown.util.json.Token.Type.LBRACE;
import static com.unknown.util.json.Token.Type.NONE;
import static com.unknown.util.json.Token.Type.RBRAC;
import static com.unknown.util.json.Token.Type.RBRACE;
import static com.unknown.util.json.Token.Type.STR;

import java.io.StringReader;
import java.text.ParseException;

import com.unknown.util.json.Token.Type;

public class JsonParser {
	private final Scanner scanner;

	private Token t;
	private Token la;
	private Type sym;

	public static JsonValue parse(String json) throws ParseException {
		JsonParser parser = new JsonParser(new Scanner(new StringReader(json)));
		return parser.parse();
	}

	private JsonParser(Scanner scanner) {
		this.scanner = scanner;
	}

	private void scan() throws ParseException {
		t = la;
		la = scanner.scan();
		sym = la.type;
	}

	private void check(Type type) throws ParseException {
		if(sym != type) {
			error("invalid token: expected " + type + ", got " + sym);
		}
		scan();
	}

	private void error(String msg) throws ParseException {
		throw new ParseException(msg, scanner.getPosition());
	}

	private JsonValue parse() throws ParseException {
		scan();
		JsonValue value = parseValue();
		check(NONE);
		return value;
	}

	private JsonValue parseValue() throws ParseException {
		switch(sym) {
		case STR:
			scan();
			return new JsonString(t.sval);
		case INT:
			scan();
			return new JsonNumber(t.ival);
		case FLOAT:
			scan();
			return new JsonNumber(t.fval);
		case ADD:
			scan();
			switch(sym) {
			case INT:
				scan();
				return new JsonNumber(t.ival);
			case FLOAT:
				scan();
				return new JsonNumber(t.fval);
			default:
				error("expected INT or FLOAT");
				throw new AssertionError("unreachable");
			}
		case SUB:
			scan();
			switch(sym) {
			case INT:
				scan();
				return new JsonNumber(-t.ival);
			case FLOAT:
				scan();
				return new JsonNumber(-t.fval);
			default:
				error("expected INT or FLOAT");
				throw new AssertionError("unreachable");
			}
		case TRUE:
			scan();
			return new JsonBoolean(true);
		case FALSE:
			scan();
			return new JsonBoolean(false);
		case NULL:
			return null;
		case LBRAC:
			return parseArray();
		case LBRACE:
			return parseObject();
		default:
			error("unexpected token " + sym);
			throw new AssertionError("unreachable");
		}
	}

	private JsonArray parseArray() throws ParseException {
		check(LBRAC);
		JsonArray array = new JsonArray();
		if(sym != RBRAC) {
			array.add(parseValue());
			while(sym == COMMA) {
				scan();
				array.add(parseValue());
			}
		}
		check(RBRAC);
		return array;
	}

	private JsonObject parseObject() throws ParseException {
		check(LBRACE);
		JsonObject object = new JsonObject();
		if(sym == STR) {
			scan();
			String key = t.sval;
			check(COLON);
			JsonValue value = parseValue();
			object.put(key, value);

			while(sym == COMMA) {
				scan();
				check(STR);
				key = t.sval;
				check(COLON);
				value = parseValue();
				object.put(key, value);
			}
		}
		check(RBRACE);
		return object;
	}
}
