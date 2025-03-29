package com.unknown.util.json;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonObject extends JsonValue {
	private Map<String, JsonValue> values;

	public JsonObject() {
		values = new HashMap<>();
	}

	public void put(String key, JsonValue value) {
		values.put(key, value);
	}

	public void put(String key, String value) {
		values.put(key, new JsonString(value));
	}

	public void put(String key, long value) {
		values.put(key, new JsonNumber(value));
	}

	public void put(String key, double value) {
		values.put(key, new JsonNumber(value));
	}

	public void put(String key, boolean value) {
		values.put(key, new JsonBoolean(value));
	}

	public Map<String, JsonValue> get() {
		return Collections.unmodifiableMap(values);
	}

	@Override
	public JsonValue get(String key) {
		return values.get(key);
	}

	public boolean hasKey(String key) {
		return values.containsKey(key);
	}

	@Override
	public String toString() {
		return "{" + values.entrySet().stream().map(x -> JsonString.encode(x.getKey()) + ":" + x.getValue())
				.collect(Collectors.joining(",")) + "}";
	}
}
