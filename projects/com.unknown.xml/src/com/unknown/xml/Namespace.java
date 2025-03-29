package com.unknown.xml;

public class Namespace {
	public final String name;
	public final String uri;
	public final int level;

	public Namespace(String name, String uri, int level) {
		this.name = name;
		this.uri = uri;
		this.level = level;
	}
}
