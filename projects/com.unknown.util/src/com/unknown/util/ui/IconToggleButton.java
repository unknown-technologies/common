package com.unknown.util.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JComponent;

@SuppressWarnings("serial")
public class IconToggleButton extends JComponent {
	private boolean value;
	private Icon enabled;
	private Icon disabled;
	private boolean down;

	public IconToggleButton(Icon enabled, Icon disabled) {
		this(enabled, disabled, false);
	}

	public IconToggleButton(Icon enabled, Icon disabled, boolean value) {
		this.enabled = enabled;
		this.disabled = disabled;
		this.value = value;
		down = false;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				down = true;
				setSelected(!isSelected());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				down = false;
				repaint();
			}
		});
		int width = Math.max(enabled.getIconWidth(), disabled.getIconWidth());
		int height = Math.max(enabled.getIconHeight(), disabled.getIconHeight());
		Dimension size = new Dimension(width, height);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
	}

	public void setSelected(boolean value) {
		this.value = value;
		repaint();
	}

	public boolean isSelected() {
		return value;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		if(value) {
			int px = (getWidth() - enabled.getIconWidth()) / 2;
			int py = (getHeight() - enabled.getIconHeight()) / 2;
			enabled.paintIcon(this, g, px, py);
		} else {
			int px = (getWidth() - disabled.getIconWidth()) / 2;
			int py = (getHeight() - disabled.getIconHeight()) / 2;
			disabled.paintIcon(this, g, px, py);
		}
		if(down) {
			// TODO: implement
		}
	}
}
