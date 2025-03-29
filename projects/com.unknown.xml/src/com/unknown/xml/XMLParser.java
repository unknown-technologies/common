package com.unknown.xml;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.unknown.util.log.Trace;
import com.unknown.xml.sax.AttributesImpl;

public class XMLParser {
	private final static Logger trace = Trace.create(XMLParser.class);

	private boolean notrim = false;

	private XMLScanner scanner;
	private State state;
	private boolean error;
	private StringBuffer buf;
	private String name;
	private String attrname;
	private boolean squote;
	private boolean outside;

	private Map<String, String> entities;

	private AttributesImpl attributes;

	private ContentHandler contentHandler;

	private Stack<Namespace> namespaces;
	private int level;

	private boolean skipErrors = false;

	private static enum State {
		INIT, PI, PISPACE, PIDATA, T, TNAME, TEQ, TSTR, TSPACE, TC, TCN, TEXT;
	}

	public XMLParser() {
		scanner = new XMLScanner();
		initEntities();
	}

	public XMLParser(ContentHandler handler) {
		this();
		setContentHandler(handler);
	}

	public void setTrim(boolean trim) {
		notrim = !trim;
	}

	private void initEntities() {
		entities = new HashMap<>();
		entities.put("lt", "<");
		entities.put("gt", ">");
		entities.put("quot", "\"");
		entities.put("amp", "&");
	}

	public void process(byte[] data, int off, int len) throws ParseException {
		scanner.process(data, off, len);
		while(scanner.available()) {
			parse();
		}
	}

	public void process(byte[] data) throws ParseException {
		scanner.process(data);
		while(scanner.available()) {
			parse();
		}
	}

	public void process(char[] data, int off, int len) throws ParseException {
		scanner.process(data, off, len);
		while(scanner.available()) {
			parse();
		}
	}

	public void process(char[] data) throws ParseException {
		scanner.process(data);
		while(scanner.available()) {
			parse();
		}
	}

	public void process(String data) throws ParseException {
		scanner.process(data);
		while(scanner.available()) {
			parse();
		}
	}

	private void error(XMLToken tok, String msg) throws ParseException {
		error = true;
		if(!skipErrors) {
			throw new ParseException(msg, tok.getPosition());
		}
	}

	public boolean hasError() {
		return error;
	}

	public void setContentHandler(ContentHandler handler) {
		this.contentHandler = handler;
	}

	public void start() {
		state = State.INIT;
		error = false;
		outside = true;
		buf = new StringBuffer();
		namespaces = new Stack<>();
		level = 0;
		try {
			contentHandler.startDocument();
		} catch(SAXException e) {
			trace.log(Level.WARNING, Messages.START_DOCUMENT.format(e.getMessage()), e);
		}
	}

	public void end() {
		try {
			contentHandler.endDocument();
		} catch(SAXException e) {
			trace.log(Level.WARNING, Messages.END_DOCUMENT.format(e.getMessage()), e);
		}
	}

	// //////////////////////////////////////////////////////////////////////
	private void tagStart(@SuppressWarnings("unused") String tagName) {
		level++;
		attributes = new AttributesImpl();
	}

	private void tagOpen(String tagName) {
		String ns = "";
		String uri = "";
		String localName = tagName;
		String qName = tagName;
		int pos = tagName.indexOf(':');
		if(pos != -1) {
			ns = tagName.substring(0, pos);
			localName = tagName.substring(pos + 1);
		}
		for(int i = namespaces.size() - 1; i >= 0; i--) {
			Namespace n = namespaces.elementAt(i);
			if(n.name.equals(ns)) {
				uri = n.uri;
				break;
			}
		}
		try {
			contentHandler.startElement(uri, localName, qName, attributes);
		} catch(SAXException e) {
			trace.log(Level.WARNING, Messages.START_ELEMENT.format(e.getMessage()), e);
		}
	}

	private void tagClose(String tagName) {
		String ns = "";
		String uri = "";
		String localName = tagName;
		String qName = tagName;
		int pos = tagName.indexOf(':');
		if(pos != -1) {
			ns = tagName.substring(0, pos);
			localName = tagName.substring(pos + 1);
		}
		for(int i = namespaces.size() - 1; i >= 0; i--) {
			Namespace n = namespaces.elementAt(i);
			if(n.name.equals(ns)) {
				uri = n.uri;
				break;
			}
		}
		try {
			contentHandler.endElement(uri, localName, qName);
		} catch(SAXException e) {
			trace.log(Level.WARNING, Messages.END_ELEMENT.format(e.getMessage()), e);
		}
		while(!namespaces.isEmpty() && namespaces.peek().level == level) {
			Namespace n = namespaces.pop();
			try {
				contentHandler.endPrefixMapping(n.name);
			} catch(SAXException e) {
				trace.log(Level.WARNING, Messages.END_PREFIXMAP.format(e.getMessage()), e);
			}
		}
		level--;
	}

	private void attrib(String attribName, String value) {
		if(attribName.equals("xmlns")) {
			namespaces.add(new Namespace("", value, level));
			try {
				contentHandler.startPrefixMapping("", value);
			} catch(SAXException e) {
				trace.log(Level.WARNING, Messages.START_PREFIXMAP.format(e.getMessage()), e);
			}
			return;
		}
		if(attribName.startsWith("xmlns:")) {
			String ns = attribName.substring(6);
			namespaces.add(new Namespace(ns, value, level));
			try {
				contentHandler.startPrefixMapping(ns, value);
			} catch(SAXException e) {
				trace.log(Level.WARNING, Messages.START_PREFIXMAP.format(e.getMessage()), e);
			}
			return;
		}
		String ns = "";
		String uri = "";
		String localName = attribName;
		String qName = attribName;
		int pos = attribName.indexOf(':');
		if(pos != -1) {
			ns = attribName.substring(0, pos);
			localName = attribName.substring(pos + 1);
		}
		for(int i = namespaces.size() - 1; i >= 0; i--) {
			Namespace n = namespaces.elementAt(i);
			if(n.name.equals(ns)) {
				uri = n.uri;
				break;
			}
		}
		attributes.add(uri, localName, qName, value);
	}

	private void pi(String piName, String content) {
		if(piName.equals("xml")) {
			// System.out.printf("XML PI: '%s'\n", content);
			return;
		}
		try {
			contentHandler.processingInstruction(piName, content);
		} catch(SAXException e) {
			trace.log(Level.WARNING, Messages.PROCESSING_INSN.format(e.getMessage()), e);
		}
	}

	private void text(String text) {
		try {
			contentHandler.characters(text.toCharArray(), 0,
					text.length());
		} catch(SAXException e) {
			trace.log(Level.WARNING, Messages.TEXT.format(e.getMessage()), e);
		}
	}

	private String ent(String val) {
		String e = entities.get(val);
		if(e != null) {
			return e;
		}
		return val;
	}

	private void parse() throws ParseException {
		XMLToken t = scanner.scan();
		s: switch(state) {
		case TEXT:
			switch(t.type) {
			case quote:
				buf.append('"');
				break s;
			case squote:
				buf.append('\'');
				break s;
			case ent:
				buf.append(ent(t.val));
				break s;
			case equal:
				buf.append('=');
				break s;
			case space:
			case text:
				buf.append(t.val);
				break s;
			case tsopen:
				state = State.TC;
				if(buf.length() > 0) {
					if(!outside) {
						String s = buf.toString();
						if(s.trim().length() > 0) {
							text(s);
						} else if(notrim && s.length() > 0) {
							text(s);
						}
					} else if(buf.toString().trim().length() > 0) {
						trace.log(Level.WARNING,
								Messages.UNEXPECTED_TEXT.format(buf.toString()));
					}
					buf = new StringBuffer();
				}
				break s;
			default:
				if(buf.length() > 0) {
					if(!outside) {
						String s = buf.toString();
						if(s.trim().length() > 0) {
							text(s);
						} else if(notrim && s.length() > 0) {
							text(s);
						}
					} else if(buf.toString().trim().length() > 0) {
						trace.log(Level.WARNING,
								Messages.UNEXPECTED_TEXT.format(buf.toString()));
					}
					buf = new StringBuffer();
				}
				break;
			}
		case INIT:
			switch(t.type) {
			case piopen:
				state = State.PI;
				break;
			case topen:
				state = State.T;
				break;
			default:
				error(t, "unexpected " + t.type);
				break;
			}
			break;
		case PI:
			if(t.type != XMLTokenType.text) {
				error(t, "expected text, got " + t.type);
			} else {
				state = State.PISPACE;
				name = t.val;
			}
			break;
		case PISPACE:
			state = State.PIDATA;
			if(t.type == XMLTokenType.space) {
				break;
			}
		case PIDATA:
			switch(t.type) {
			case quote:
				buf.append('"');
				break;
			case squote:
				buf.append('\'');
				break;
			case ent:
				buf.append(ent(t.val));
				break;
			case space:
			case text:
				buf.append(t.val);
				break;
			case equal:
				buf.append('=');
				break;
			case piclose:
				state = State.TEXT;
				pi(name, buf.toString());
				buf = new StringBuffer();
				break;
			default:
				error(t, "unexpected " + t.type + " in PIDATA");
			}
			break;
		case T:
			if(t.type != XMLTokenType.text) {
				error(t, "expected text, got " + t.type);
			} else {
				state = State.TSPACE;
				name = t.val;
				tagStart(name);
			}
			break;
		case TSPACE:
			switch(t.type) {
			case tclose:
				state = State.TEXT;
				outside = false;
				tagOpen(name);
				break;
			case tcloses:
				state = State.TEXT;
				tagOpen(name);
				tagClose(name);
				break;
			case text:
				state = State.TNAME;
				attrname = t.val;
				break;
			case space:
				break;
			default:
				error(t, "unexpected " + t.type + " in TSPACE");
			}
			break;
		case TNAME:
			switch(t.type) {
			case equal:
				state = State.TEQ;
				break;
			default:
				error(t, "unexpected " + t.type + " in TNAME");
			}
			break;
		case TEQ:
			switch(t.type) {
			case quote:
				state = State.TSTR;
				squote = false;
				break;
			case squote:
				state = State.TSTR;
				squote = true;
				break;
			default:
				error(t, "unexpected " + t.type + " in TEQ");
			}
			break;
		case TSTR:
			switch(t.type) {
			case quote:
				if(!squote) {
					state = State.TSPACE;
					attrib(attrname, buf.toString());
					buf = new StringBuffer();
				} else {
					buf.append('"');
				}
				break;
			case squote:
				if(squote) {
					state = State.TSPACE;
					attrib(attrname, buf.toString());
					buf = new StringBuffer();
				} else {
					buf.append('\'');
				}
				break;
			case ent:
				buf.append(ent(t.val));
				break;
			case space:
			case text:
				buf.append(t.val);
				break;
			case equal:
				buf.append('=');
				break;
			default:
				error(t, "unexpected " + t.type + " in TSTR");
			}
			break;
		case TC:
			if(t.type != XMLTokenType.text) {
				error(t, "expected text, got " + t.type);
			} else {
				state = State.TCN;
				name = t.val;
			}
			break;
		case TCN:
			if(t.type != XMLTokenType.tclose) {
				error(t, "expected tclose, got " + t.type);
			} else {
				state = State.TEXT;
				tagClose(name);
			}
			break;
		}
	}
}
