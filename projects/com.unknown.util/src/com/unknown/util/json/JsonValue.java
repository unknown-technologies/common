package com.unknown.util.json;

public abstract class JsonValue {
	@Override
	public abstract String toString();

	public boolean bool() {
		throw new UnsupportedOperationException();
	}

	public String str() {
		throw new UnsupportedOperationException();
	}

	public long intValue() {
		throw new UnsupportedOperationException();
	}

	public double floatValue() {
		throw new UnsupportedOperationException();
	}

	public JsonValue get(@SuppressWarnings("unused") int index) {
		throw new UnsupportedOperationException();
	}

	public JsonValue get(@SuppressWarnings("unused") String key) {
		throw new UnsupportedOperationException();
	}
}
