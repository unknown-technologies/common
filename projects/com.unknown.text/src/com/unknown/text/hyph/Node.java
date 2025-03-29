package com.unknown.text.hyph;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Node {
	public final char letter;
	public Node next;
	public Node children;
	public int[] points;

	public Node(char letter) {
		this.letter = letter;
		next = null;
		children = null;
		points = null;
	}

	public void print() {
		print(0);
	}

	private static void indent(int level) {
		for(int i = 0; i < level; i++) {
			System.out.print('\t');
		}
	}

	public void print(int level) {
		indent(level);
		if(points != null) {
			String value = IntStream.of(points).mapToObj(Integer::toString)
					.collect(Collectors.joining(","));
			System.out.println("['" + letter + "'] = " + value);
		} else {
			System.out.println("['" + letter + "']");
		}
		if(children != null) {
			children.print(level + 1);
		}
		if(next != null) {
			next.print(level);
		}
	}

	@Override
	public String toString() {
		return "[Node '" + letter + "']";
	}
}
