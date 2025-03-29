package com.unknown.util.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JsonArray extends JsonValue {
	private List<JsonValue> values;

	public JsonArray() {
		values = new ArrayList<>();
	}

	public void add(JsonValue value) {
		values.add(value);
	}

	public List<JsonValue> getValues() {
		return Collections.unmodifiableList(values);
	}

	@Override
	public JsonValue get(int index) {
		return values.get(index);
	}

	@Override
	public String toString() {
		return "[" + values.stream().map(String::valueOf).collect(Collectors.joining(",")) + "]";
	}
}
