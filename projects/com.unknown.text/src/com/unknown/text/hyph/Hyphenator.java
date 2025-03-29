package com.unknown.text.hyph;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Hyphenator {
	private Map<String, int[]> exceptions;

	private Node root = null;

	public Hyphenator() {
		exceptions = new HashMap<>();
	}

	public Hyphenator(String lang) throws IOException {
		String[] exc = Loader.load(lang + ".hyp");
		exceptions = new HashMap<>();
		for(String ex : exc) {
			addException(ex);
		}

		String[] pat = Loader.load(lang + ".pat");
		for(String p : pat) {
			addPattern(p);
		}
	}

	public void addException(String ex) {
		char[] exchars = ex.toCharArray();
		char[] chars = new char[ex.length()];
		int i = 0;
		for(char c : exchars) {
			if(c != '-') {
				chars[i++] = c;
			}
		}
		String name = new String(chars, 0, i);
		int[] points = new int[name.length() + 2];
		points[0] = 0;
		points[points.length - 1] = 0;
		i = 1;
		boolean set = false;
		for(char c : exchars) {
			if(c == '-') {
				set = true;
			} else {
				points[i++] = set ? 1 : 0;
				set = false;
			}
		}
		exceptions.put(name, points);
	}

	public void addPattern(String pat) {
		char[] patchars = pat.toCharArray();
		char[] chars = new char[pat.length()];
		int i = 0;
		for(char c : patchars) {
			if(c < '0' || c > '9') {
				chars[i++] = c;
			}
		}
		String name = new String(chars, 0, i);

		int[] points = new int[name.length() + 1];
		i = 0;
		for(int j = 0; j < name.length(); i++, j++) {
			if(patchars[i] >= '0' && patchars[i] <= '9') {
				points[j] = patchars[i++] - '0';
			} else {
				points[j] = 0;
			}
		}
		if(i < patchars.length && patchars[i] >= '0' && patchars[i] <= '9') {
			points[points.length - 1] = patchars[i] - '0';
		} else {
			points[points.length - 1] = 0;
		}

		// insert into tree
		i = 0;
		if(root == null) {
			root = new Node(name.charAt(0));
		}

		Node node = root;
		loop: for(i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			while(true) {
				if(node.letter == c) {
					// found
					if(node.children != null) {
						node = node.children;
					} else if(i + 1 < name.length()) {
						node.children = new Node(name.charAt(i + 1));
						node = node.children;
					}
					continue loop;
				}
				if(node.next != null) {
					node = node.next;
				} else {
					break;
				}
			}
			// not found
			Node n = new Node(c);
			node.next = n;
			node = n;
			if(i + 1 < name.length()) {
				node.children = new Node(name.charAt(i + 1));
				node = node.children;
			}
		}
		if(node.points != null) {
			throw new AssertionError(pat + ": error at node " + node.letter);
		}
		node.points = points;
	}

	public int[] getException(String exception) {
		return exceptions.get(exception);
	}

	public int[] getPattern(String pat) {
		if(root == null) {
			return null;
		}
		Node node = root;
		loop: for(int i = 0; i < pat.length(); i++) {
			char c = pat.charAt(i);
			while(true) {
				if(node.letter == c) {
					// found
					if(node.children != null) {
						node = node.children;
					} else if(i + 1 < pat.length()) {
						// no children; not found
						return null;
					}
					continue loop;
				}
				if(node.next != null) {
					node = node.next;
				} else {
					break;
				}
			}
			// not found
			return null;
		}
		return node.points;
	}

	public int[] get(String s) {
		int[] exc = getException(s);
		if(exc != null) {
			return exc;
		} else {
			return getPattern(s);
		}
	}

	private void match(char[] work, int start, int[] points) {
		if(root == null) {
			return;
		}
		Node node = root;
		loop: for(int i = start; i < work.length; i++) {
			char c = work[i];
			while(true) {
				if(node.letter == c) {
					if(node.points != null) {
						for(int j = 0; j < node.points.length; j++) {
							points[start + j] = Math.max(points[start + j], node.points[j]);
						}
					}
					// found
					if(node.children != null) {
						node = node.children;
					} else if(i + 1 < work.length) {
						// no children; not found
						return;
					}
					continue loop;
				}
				if(node.next != null) {
					node = node.next;
				} else {
					break;
				}
			}
			// not found
			return;
		}
	}

	public int[] getPoints(String word) {
		String lower = word.toLowerCase();
		int[] points = getException(lower);
		if(points == null) {
			char[] work = ("." + lower + ".").toCharArray();
			points = new int[work.length + 1];

			for(int i = 0; i < work.length; i++) {
				match(work, i, points);
			}
			points[0] = 0;
			points[1] = 0;
			points[2] = 0;
			points[points.length - 3] = 0;
			points[points.length - 2] = 0;
			points[points.length - 1] = 0;
		}
		return points;
	}

	public String[] hyphenate(String word) {
		if(word.length() <= 4) {
			return new String[] { word };
		}
		int[] points = getPoints(word);
		int length = 1;
		for(int i = 2; i < points.length; i++) {
			if((points[i] % 2) != 0) {
				length++;
			}
		}
		String[] pieces = new String[length];
		StringBuilder buf = new StringBuilder();
		int n = 0;
		for(int i = 0; i < word.length(); i++) {
			buf.append(word.charAt(i));
			if(points[i + 2] % 2 != 0) {
				pieces[n++] = buf.toString();
				buf = new StringBuilder();
			}
		}
		if(buf.length() > 0) {
			pieces[n++] = buf.toString();
		}
		return pieces;
	}
}
