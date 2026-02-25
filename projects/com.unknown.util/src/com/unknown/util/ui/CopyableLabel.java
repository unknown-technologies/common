package com.unknown.util.ui;

import java.awt.Font;

import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class CopyableLabel extends JTextPane {
	private static final Font DEFAULT_FONT;

	static {
		Font font = UIManager.getFont("Label.font");
		DEFAULT_FONT = (font != null) ? font : new Font("Dialog", Font.PLAIN, 11);
	}

	public CopyableLabel() {
		this(DEFAULT_FONT, null);
	}

	public CopyableLabel(String text) {
		this(DEFAULT_FONT, text);
	}

	public CopyableLabel(Font font, String text) {
		setContentType("text/plain");

		setEditable(false);
		setBackground(null);
		setBorder(null);

		putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		setFont(font);

		setOpaque(false);
		setDisabledTextColor(getForeground());

		if(text != null) {
			setText(text);
		}
	}
}
