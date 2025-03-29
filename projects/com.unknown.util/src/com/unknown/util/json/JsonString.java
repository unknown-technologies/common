package com.unknown.util.json;

public class JsonString extends JsonValue {
	private String value;

	public JsonString(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String str() {
		return value;
	}

	static String encode(String s) {
		StringBuilder buf = new StringBuilder(s.length() + 2);
		buf.append("\"");
		for(char c : s.toCharArray()) {
			switch(c) {
			case '"':
				buf.append("\\\"");
				break;
			case '\\':
				buf.append("\\\\");
				break;
			case '\t':
				buf.append("\\\t");
				break;
			case '\f':
				buf.append("\\\f");
				break;
			case '\r':
				buf.append("\\\r");
				break;
			case '\n':
				buf.append("\\\n");
				break;
			case '\b':
				buf.append("\\\b");
				break;
			default:
				buf.append(c);
				break;
			}
		}
		buf.append("\"");
		return buf.toString();
	}

	@Override
	public String toString() {
		return encode(value);
	}
}
