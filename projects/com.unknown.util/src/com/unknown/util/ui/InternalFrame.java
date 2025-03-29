package com.unknown.util.ui;

import javax.swing.Icon;
import javax.swing.JInternalFrame;

@SuppressWarnings("serial")
public class InternalFrame extends JInternalFrame {
	private String iconifyTitle = null;
	private Icon iconifyIcon = null;

	public InternalFrame() {
		super();
	}

	public InternalFrame(String title) {
		super(title);
	}

	public InternalFrame(String title, boolean resizable) {
		super(title, resizable);
	}

	public InternalFrame(String title, boolean resizable, boolean closable) {
		super(title, resizable, closable);
	}

	public InternalFrame(String title, boolean resizable, boolean closable, boolean maximizable) {
		super(title, resizable, closable, maximizable);
	}

	public InternalFrame(String title, boolean resizable, boolean closable, boolean maximizable,
			boolean iconifiable) {
		super(title, resizable, closable, maximizable, iconifiable);
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		iconifyTitle = null;
	}

	public void setIconifyTitle(String title) {
		iconifyTitle = title;
	}

	public String getIconifyTitle() {
		return iconifyTitle;
	}

	public void setIconifyIcon(Icon icon) {
		iconifyIcon = icon;
	}

	public Icon getIconifyIcon() {
		return iconifyIcon;
	}
}
