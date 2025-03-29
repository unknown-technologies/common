package com.unknown.util.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;

@SuppressWarnings("serial")
public class Desktop extends JDesktopPane {
	private BufferedImage wallpaper;
	private Image resizedWallpaper;

	private JPopupMenu menu;
	private boolean mouseDragged;

	public Desktop() {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				resizedWallpaper = null;
				repaint();
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3) {
					mouseDragged = false;
					if(menu == null) {
						return;
					}

					if(menu.isVisible()) {
						menu.setVisible(false);
					} else {
						menu.show(Desktop.this, e.getX(), e.getY());
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if(menu != null && menu.isVisible() && mouseDragged) {
					menu.setVisible(false);
				}
			}
		});

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				mouseDragged = true;
			}
		});
	}

	public void setMenu(JPopupMenu menu) {
		this.menu = menu;
	}

	@Override
	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
		// See if we have a local binding.
		boolean retValue = super.processKeyBinding(ks, e, condition, pressed);
		if(!retValue) {
			MenuElement[] subElements = menu.getSubElements();
			for(MenuElement subElement : subElements) {
				if(processBindingForKeyStrokeRecursive(subElement.getComponent(), ks, e, condition,
						pressed)) {
					return true;
				}
			}
		}
		return retValue;
	}

	private static boolean processBindingForKeyStrokeRecursive(Component elem, KeyStroke ks, KeyEvent e,
			int condition, boolean pressed) {
		if(elem == null) {
			return false;
		}

		if(elem instanceof JMenu) {
			// recurse
			JMenu m = (JMenu) elem;

			for(Component subElem : m.getMenuComponents()) {
				if(processBindingForKeyStrokeRecursive(subElem, ks, e, condition, pressed)) {
					return true;
				}
			}
			return false;
		} else if(elem instanceof JMenuItem) {
			return processKeyBindings((JMenuItem) elem, ks);
		} else {
			return false;
		}
	}

	private static boolean processKeyBindings(JMenuItem c, KeyStroke ks) {
		KeyStroke k = c.getAccelerator();
		if(ks.equals(k)) {
			c.doClick();
			return true;
		} else {
			return false;
		}
	}

	public void addFrame(JInternalFrame frame) {
		int x = (getWidth() - frame.getWidth()) / 2;
		int y = (getHeight() - frame.getHeight()) / 2;
		frame.setLocation(x, y);
		add(frame);
	}

	public void setWallpaper(BufferedImage wallpaper) {
		this.wallpaper = wallpaper;
		resizedWallpaper = null;
		repaint();
	}

	private void prepareWallpaper() {
		if(wallpaper == null || resizedWallpaper != null) {
			return;
		}

		int width = getWidth();
		int height = getHeight();

		if(width == wallpaper.getWidth() && height == wallpaper.getHeight()) {
			resizedWallpaper = wallpaper;
		} else {
			int swidth = width;
			int sheight = height;

			double ratio = wallpaper.getWidth() / (double) wallpaper.getHeight();
			int rwidth = (int) Math.ceil(height * ratio);
			if(rwidth < width) {
				int rheight = (int) Math.ceil(width / ratio);
				sheight = rheight;
			} else {
				swidth = rwidth;
			}
			resizedWallpaper = wallpaper.getScaledInstance(swidth, sheight, Image.SCALE_SMOOTH);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(wallpaper != null) {
			prepareWallpaper();
			int width = getWidth();
			int height = getHeight();
			int x = (width - resizedWallpaper.getWidth(this)) / 2;
			int y = (height - resizedWallpaper.getHeight(this)) / 2;
			g.drawImage(resizedWallpaper, x, y, this);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		if(wallpaper != null) {
			return new Dimension(wallpaper.getWidth(), wallpaper.getHeight());
		} else {
			return super.getPreferredSize();
		}
	}
}
