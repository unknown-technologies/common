package com.unknown.syntax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class McabberLogHighlighter extends Highlighter {
	private static Pattern url = Pattern
			.compile("((?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!10(?:\\.\\d{1,3}){3})(?!127(?:\\.\\d{1,3}){3})(?!169\\.254(?:\\.\\d{1,3}){2})(?!192\\.168(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)*(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}]{2,})))(?::\\d{2,5})?(?:/[^\\s]*)?)");

	private static Pattern alternate = Pattern
			.compile("(\\(\\d{1,2}:\\d{2}:\\d{2}(?: (?:AM|PM))?\\)) ((\\*\\*\\*\\w+|.*?:) (.*)|.*)");

	public McabberLogHighlighter() {
		super(null, null);
	}

	@Override
	public void format(Reader in, Writer out) throws IOException {
		BufferedReader r = new BufferedReader(in);
		int lines = 0;
		String line = null;
		try {
			while((line = r.readLine()) != null) {
				if(lines <= 0 && line.length() > 25) {
					if(Integer.parseInt(line.substring(22, 25), 10) > 0) {
						lines = Integer.parseInt(line.substring(22, 25), 10);
					}
					out.write(formatLine(line));
				} else {
					out.write(htmlspecialchars(line) + "\n");
					lines--;
				}
			}
		} catch(NumberFormatException e) {
			// not a mcabber log
			if(alternate.matcher(line).matches()) {
				out.write(formatAlternate(line));
				while((line = r.readLine()) != null) {
					out.write(formatAlternate(line));
				}
			} else {
				out.write(htmlspecialchars(line) + "\n");
				while((line = r.readLine()) != null) {
					out.write(htmlspecialchars(line) + "\n");
				}
			}
		}
		out.flush();
	}

	@Override
	public String formatLine(String line) {
		if(line.length() < 26) {
			return htmlspecialchars(line) + "\n";
		}
		String type = line.substring(0, 2);
		StringBuffer result = new StringBuffer(
				"<span class=\"syntax-commentplain\">")
				.append(type)
				.append(" <span class=\"syntax-type\">").append(line.substring(3, 11))
				.append("</span>T<span class=\"syntax-preproc\">")
				.append(line.substring(12, 20))
				.append("</span>Z ")
				.append(line.substring(22, 25));
		if(type.equals("MI") || type.charAt(0) == 'S') {
			result.append(" ").append(escapeAndConvertLinks(line.substring(26))).append("</span>");
		} else {
			result.append("</span> ").append(escapeAndConvertLinks(line.substring(26)));
		}
		return result.append("\n").toString();
	}

	private static String formatAlternate(String line) {
		Matcher m = alternate.matcher(line);
		if(!m.matches()) {
			return new StringBuffer(escapeAndConvertLinks(line, false)).append("\n").toString();
		}
		String time = m.group(1);
		String msg = m.group(2);
		String nick = m.group(3);
		StringBuffer result = new StringBuffer("<span class=\"syntax-preproc\">")
				.append(htmlspecialchars(time)).append("</span> ");
		if(nick != null) {
			msg = m.group(4);
			result.append("<span class=\"syntax-statement\">")
					.append(htmlspecialchars(nick))
					.append("</span> ")
					.append(escapeAndConvertLinks(msg, false));
		} else {
			result.append("<span class=\"syntax-comment\">")
					.append(escapeAndConvertLinks(msg, false))
					.append("</span>");
		}
		return result.append("\n").toString();
	}

	private static String escapeAndConvertLinks(String line) {
		return escapeAndConvertLinks(line, true);
	}

	private static String escapeAndConvertLinks(String in, boolean containsNick) {
		String line = in;
		StringBuffer result = new StringBuffer();
		if(containsNick && line.charAt(0) == '<') {
			int until = line.indexOf('>');
			if(line.indexOf("> /me ", until) == until) {
				until += 5;
			} else {
				until++;
			}
			result.append("<span class=\"syntax-statement\">")
					.append(htmlspecialchars(line.substring(0, until)))
					.append("</span>");
			line = line.substring(until);
		}
		line = htmlspecialchars(line, true);
		Matcher murl = url.matcher(line);
		result.append(murl.replaceAll("<span class=\"syntax-string\"><a href=\"$1\">$1</a></span>"));
		return result.toString();
	}
}
