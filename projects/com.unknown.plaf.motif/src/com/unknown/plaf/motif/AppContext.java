package com.unknown.plaf.motif;

import java.util.HashMap;
import java.util.Map;

public class AppContext {
	private static final AppContext ctx = new AppContext();

	private Map<Object, Object> map = new HashMap<>();

	public static AppContext getAppContext() {
		return ctx;
	}

	public Object get(Object key) {
		return map.get(key);
	}

	public void put(Object key, Object value) {
		map.put(key, value);
	}
}
