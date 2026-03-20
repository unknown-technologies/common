package com.unknown.util.ui;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

@SuppressWarnings("serial")
public class ExtendedHTMLEditorKit extends HTMLEditorKit {
	private StyleSheet style;

	@Override
	public StyleSheet getStyleSheet() {
		return style == null ? super.getStyleSheet() : style;
	}

	@Override
	public void setStyleSheet(StyleSheet s) {
		this.style = s;
	}

	public StyleSheet getDefaultStyleSheet() {
		return super.getStyleSheet();
	}

	public void setDefaultStyleSheet(StyleSheet s) {
		super.setStyleSheet(s);
	}
}
