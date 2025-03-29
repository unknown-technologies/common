package com.unknown.xml.dom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Element extends Node {
	public final String uri;
	public final String name;
	public final String qName;
	public String value;

	public boolean preventSelfClosing = false;

	private Set<Attribute> attributes;
	private List<Element> children;
	private List<String> attributeOrder = Collections.emptyList();

	public Element(String localName) {
		this("", localName, localName);
	}

	public Element(String localName, String value) {
		this("", localName, localName, value);
	}

	public Element(String uri, String localName, String qName) {
		this(uri, localName, qName, null);
	}

	public Element(String uri, String localName, String qName, String value) {
		super(TAG);
		this.uri = uri;
		this.name = localName;
		this.qName = qName;
		this.value = value;
		attributes = new HashSet<>();
		children = new ArrayList<>();
	}

	public void addAttribute(Attribute attr) {
		attributes.add(attr);
	}

	public void addAttributes(Collection<Attribute> attrs) {
		attributes.addAll(attrs);
	}

	public Attribute[] getAttributes() {
		return attributes.toArray(new Attribute[attributes.size()]);
	}

	public void addChild(Element elem) {
		children.add(elem);
	}

	public Element[] getChildren() {
		return getChildren(false);
	}

	public Element[] getChildren(boolean text) {
		if(text && value != null && children.isEmpty()) {
			return new Element[] { new Text(value) };
		}
		return children.toArray(new Element[children.size()]);
	}

	public Element getFirstChild() {
		if(children.size() == 0) {
			if(value != null) {
				return new Text(value);
			} else {
				return null;
			}
		} else {
			return children.get(0);
		}
	}

	public String getTagName() {
		return name;
	}

	public String getNodeValue() {
		return value;
	}

	public void addAttribute(String localName, String val) {
		addAttribute(new Attribute(localName, val));
	}

	public String getAttribute(String localName) {
		return getAttribute(localName, null);
	}

	public String getAttribute(String localName, String defaultValue) {
		for(Attribute a : attributes) {
			if(a.name.equals(localName)) {
				return a.value;
			}
		}
		return defaultValue;
	}

	public Element[] getElementsByTagName(String localName) {
		return children.stream().filter((x) -> x.name.equals(localName)).toArray(Element[]::new);
	}

	public void setAttributeOrder(List<String> order) {
		attributeOrder = order;
	}

	public void compress() {
		// concatenate adjacent text nodes
		StringBuilder lastText = null;
		List<Element> newChildren = new ArrayList<>();

		for(Element e : children) {
			if(e instanceof Text) {
				if(lastText == null) {
					lastText = new StringBuilder();
				}
				lastText.append(e.value);
			} else {
				if(lastText != null) {
					newChildren.add(new Text(lastText.toString()));
					lastText = null;
				}
				e.compress();
				newChildren.add(e);
			}
		}

		if(lastText != null) {
			newChildren.add(new Text(lastText.toString()));
		}

		if(newChildren.size() != children.size()) {
			children = newChildren;
		}

		if((children.size() == 1) && (children.get(0) instanceof Text)) {
			value = children.get(0).value;
			children.clear();
		}
	}

	private static String rep(String s, int n) {
		StringBuilder buf = new StringBuilder(n * s.length());
		for(int i = 0; i < n; i++) {
			buf.append(s);
		}
		return buf.toString();
	}

	protected String serialize(int level) {
		String indent = rep("\t", level == -1 ? 0 : level);
		StringBuffer buf = new StringBuffer(indent).append("<").append(name);
		if(!attributes.isEmpty()) {
			if(attributeOrder != null) {
				Map<String, Attribute> attrs = new HashMap<>();
				for(Attribute attr : attributes) {
					attrs.put(attr.name, attr);
				}

				Set<String> used = new HashSet<>();
				List<String> result = new ArrayList<>();
				for(String attrname : attributeOrder) {
					Attribute attr = attrs.get(attrname);
					if(attr != null) {
						used.add(attrname);
						result.add(attr.toString());
					}
				}
				result.addAll(attributes.stream().filter(x -> !used.contains(x.name))
						.sorted((x, y) -> x.name.compareTo(y.name)).map(Object::toString)
						.toList());
				buf.append(" ").append(result.stream().collect(Collectors.joining(" ")));
			} else {
				buf.append(" ").append(attributes.stream().sorted((x, y) -> x.name.compareTo(y.name))
						.map(Object::toString).collect(Collectors.joining(" ")));
			}
		}

		boolean hasText = level == -1;
		for(Element n : children) {
			if(n instanceof Text) {
				hasText = true;
			}
		}

		if(!children.isEmpty()) {
			if(hasText) {
				buf.append(">");
				for(Element e : children) {
					if(e instanceof Text) {
						buf.append(escape(e.value));
					} else {
						buf.append(e.serialize(-1));
					}
				}
			} else {
				buf.append(">\n");
				for(Element e : children) {
					buf.append(e.serialize(level == -1 ? -1 : level + 1));
				}
			}
		}

		if(value != null) {
			buf.append(">").append(escape(value)).append("</").append(name).append(">");
		} else if(children.isEmpty()) {
			if(preventSelfClosing) {
				buf.append("></").append(name).append(">");
			} else {
				buf.append("/>");
			}
		} else {
			if(!hasText) {
				buf.append(indent);
			}
			buf.append("</").append(name).append(">");
		}

		if(level != -1) {
			buf.append("\n");
		}

		return buf.toString();
	}

	@Override
	public String toString() {
		compress();
		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + serialize(0);
	}

	public String toRawString() {
		compress();
		return serialize(0);
	}
}
