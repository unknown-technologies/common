package com.unknown.util.json;

public class JsonBoolean extends JsonValue {
	private boolean value;

	public JsonBoolean(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public boolean bool() {
		return value;
	}

	@Override
	public String toString() {
		return value ? "true" : "false";
	}
}
