package com.unknown.util;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class URLParser {
	private static final Pattern QUERY_PATTERN = Pattern.compile("&");

	public static Map<String, List<String>> getParameters(URI uri) {
		String qs = uri.getRawQuery();
		if(qs == null) {
			return Collections.emptyMap();
		} else {
			return decodeQueryString(qs);
		}
	}

	public static Map<String, String> getFlatParameters(URI uri) {
		String qs = uri.getRawQuery();
		if(qs == null) {
			return Collections.emptyMap();
		} else {
			Map<String, List<String>> parameters = decodeQueryString(qs);
			Map<String, String> result = new HashMap<>();
			for(Entry<String, List<String>> entry : parameters.entrySet()) {
				String value = entry.getValue().get(0);
				result.put(entry.getKey(), value);
			}
			return result;
		}
	}

	public static Map<String, List<String>> decodeQueryString(String qs) {
		if(qs == null) {
			return Collections.emptyMap();
		}
		String s = qs.trim();
		if(s.length() == 0) {
			return Collections.emptyMap();
		}
		return QUERY_PATTERN.splitAsStream(s).map(URLParser::splitQuery)
				.collect(Collectors.groupingBy(x -> decode(x[0]),
						Collectors.mapping(x -> decode(x[1]), Collectors.toList())));
	}

	private static String[] splitQuery(String s) {
		int idx = s.indexOf('=');
		if(idx == -1) {
			return new String[] { s, null };
		} else if(idx + 1 < s.length()) {
			return new String[] { s.substring(0, idx), s.substring(idx + 1) };
		} else {
			return new String[] { s, null };
		}
	}

	private static String decode(String s) {
		if(s == null) {
			return "";
		} else {
			return URLDecoder.decode(s, StandardCharsets.UTF_8);
		}
	}
}
