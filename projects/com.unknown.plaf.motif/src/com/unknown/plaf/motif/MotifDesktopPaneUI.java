/*
 * Copyright (c) 1997, 2021, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.unknown.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

/**
 * Motif desktop pane.
 *
 * @author David Kloba
 */
public class MotifDesktopPaneUI extends BasicDesktopPaneUI {
	/// DesktopPaneUI methods
	public static ComponentUI createUI(JComponent d) {
		return new MotifDesktopPaneUI();
	}

	public MotifDesktopPaneUI() {
	}

	@Override
	protected void installDesktopManager() {
		desktopManager = desktop.getDesktopManager();
		if(desktopManager == null) {
			desktopManager = new MotifDesktopManager(desktop);
			desktop.setDesktopManager(desktopManager);
			((MotifDesktopManager) desktopManager).adjustIcons(desktop);
		}
	}

	public Insets getInsets(@SuppressWarnings("unused") JComponent c) {
		return new Insets(0, 0, 0, 0);
	}

	////////////////////////////////////////////////////////////////////////////////////
	/// DragPane class
	////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("serial") // Superclass is not serializable across versions
	private static class DragPane extends JComponent {
		@Override
		public void paint(Graphics g) {
			g.setColor(Color.darkGray);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////
	/// MotifDesktopManager class
	////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("serial") // JDK-implementation class
	private static class MotifDesktopManager extends DefaultDesktopManager implements Serializable, UIResource {
		JComponent dragPane;
		boolean usingDragPane = false;
		private transient JLayeredPane layeredPaneForDragPane;
		int iconWidth, iconHeight;
		JDesktopPane desktop;

		public MotifDesktopManager(JDesktopPane desktop) {
			this.desktop = desktop;
		}

		// PENDING(klobad) this should be optimized
		@Override
		public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
			if(!usingDragPane) {
				boolean didResize;
				didResize = (f.getWidth() != newWidth || f.getHeight() != newHeight);
				Rectangle r = f.getBounds();
				f.setBounds(newX, newY, newWidth, newHeight);
				SwingUtilities.computeUnion(newX, newY, newWidth, newHeight, r);
				f.getParent().repaint(r.x, r.y, r.width, r.height);
				if(didResize) {
					f.validate();
				}
			} else {
				Rectangle r = dragPane.getBounds();
				dragPane.setBounds(newX, newY, newWidth, newHeight);
				SwingUtilities.computeUnion(newX, newY, newWidth, newHeight, r);
				dragPane.getParent().repaint(r.x, r.y, r.width, r.height);
			}
		}

		@Override
		public void beginDraggingFrame(JComponent f) {
			if(desktop.getDragMode() == JDesktopPane.LIVE_DRAG_MODE) {
				super.beginDraggingFrame(f);
				return;
			}
			usingDragPane = false;
			if(f.getParent() instanceof JLayeredPane) {
				if(dragPane == null)
					dragPane = new DragPane();
				layeredPaneForDragPane = (JLayeredPane) f.getParent();
				layeredPaneForDragPane.setLayer(dragPane, Integer.MAX_VALUE);
				dragPane.setBounds(f.getX(), f.getY(), f.getWidth(), f.getHeight());
				layeredPaneForDragPane.add(dragPane);
				usingDragPane = true;
			}
		}

		@Override
		public void dragFrame(JComponent f, int newX, int newY) {
			if(desktop.getDragMode() == JDesktopPane.LIVE_DRAG_MODE) {
				super.dragFrame(f, newX, newY);
				return;
			}
			setBoundsForFrame(f, newX, newY, f.getWidth(), f.getHeight());
		}

		@Override
		public void endDraggingFrame(JComponent f) {
			if(desktop.getDragMode() == JDesktopPane.LIVE_DRAG_MODE) {
				super.endDraggingFrame(f);
				return;
			}
			if(usingDragPane) {
				layeredPaneForDragPane.remove(dragPane);
				usingDragPane = false;
				if(f instanceof JInternalFrame) {
					setBoundsForFrame(f, dragPane.getX(), dragPane.getY(),
							dragPane.getWidth(), dragPane.getHeight());
				} else if(f instanceof JDesktopIcon) {
					adjustBoundsForIcon((JDesktopIcon) f,
							dragPane.getX(), dragPane.getY());
				}
			}
		}

		@Override
		public void beginResizingFrame(JComponent f, int direction) {
			if(desktop.getDragMode() == JDesktopPane.LIVE_DRAG_MODE) {
				super.beginResizingFrame(f, direction);
				return;
			}

			usingDragPane = false;
			if(f.getParent() instanceof JLayeredPane) {
				if(dragPane == null)
					dragPane = new DragPane();
				JLayeredPane p = (JLayeredPane) f.getParent();
				p.setLayer(dragPane, Integer.MAX_VALUE);
				dragPane.setBounds(f.getX(), f.getY(),
						f.getWidth(), f.getHeight());
				p.add(dragPane);
				usingDragPane = true;
			}
		}

		@Override
		public void resizeFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
			if(desktop.getDragMode() == JDesktopPane.LIVE_DRAG_MODE) {
				super.resizeFrame(f, newX, newY, newWidth, newHeight);
				return;
			}

			setBoundsForFrame(f, newX, newY, newWidth, newHeight);
		}

		@Override
		public void endResizingFrame(JComponent f) {
			if(desktop.getDragMode() == JDesktopPane.LIVE_DRAG_MODE) {
				super.endResizingFrame(f);
				return;
			}

			if(usingDragPane) {
				JLayeredPane p = (JLayeredPane) f.getParent();
				p.remove(dragPane);
				usingDragPane = false;
				setBoundsForFrame(f, dragPane.getX(), dragPane.getY(),
						dragPane.getWidth(), dragPane.getHeight());
			}
		}

		@Override
		public void iconifyFrame(JInternalFrame f) {
			JDesktopIcon icon = f.getDesktopIcon();
			Point p = icon.getLocation();
			adjustBoundsForIcon(icon, p.x, p.y);
			super.iconifyFrame(f);
		}

		/**
		 * Change positions of icons in the desktop pane so that they do not overlap
		 */
		protected void adjustIcons(@SuppressWarnings("hiding") JDesktopPane desktop) {
			// We need to know Motif icon size
			JDesktopIcon icon = new JDesktopIcon(new JInternalFrame());
			Dimension iconSize = icon.getPreferredSize();
			iconWidth = iconSize.width;
			iconHeight = iconSize.height;

			JInternalFrame[] frames = desktop.getAllFrames();
			for(int i = 0; i < frames.length; i++) {
				icon = frames[i].getDesktopIcon();
				Point ip = icon.getLocation();
				adjustBoundsForIcon(icon, ip.x, ip.y);
			}
		}

		/**
		 * Change positions of icon so that it doesn't overlap other icons.
		 */
		protected void adjustBoundsForIcon(JDesktopIcon icon, int x, int y) {
			JDesktopPane c = icon.getDesktopPane();

			int maxy = c.getHeight();
			int w = iconWidth;
			int h = iconHeight;
			c.repaint(x, y, w, h);
			int px = x < 0 ? 0 : x;
			int py = y < 0 ? 0 : y;

			/*
			 * Fix for disappearing icons. If the y value is maxy then this algorithm would place the icon
			 * in a non-displayed cell. Never to be ssen again.
			 */
			py = py >= maxy ? (maxy - 1) : py;

			/* Compute the offset for the cell we are trying to go in. */
			int lx = (px / w) * w;
			int ygap = maxy % h;
			int ly = ((py - ygap) / h) * h + ygap;

			/* How far are we into the cell we dropped the icon in. */
			int dx = px - lx;
			int dy = py - ly;

			/* Set coordinates for the icon. */
			px = dx < w / 2 ? lx : lx + w;
			py = dy < h / 2 ? ly : ((ly + h) < maxy ? ly + h : ly);

			while(getIconAt(c, icon, px, py) != null) {
				px += w;
			}

			/* Cancel the move if the x value was moved off screen. */
			if(px > c.getWidth()) {
				return;
			}
			if(icon.getParent() != null) {
				setBoundsForFrame(icon, px, py, w, h);
			} else {
				icon.setLocation(px, py);
			}
		}

		protected JDesktopIcon getIconAt(@SuppressWarnings("hiding") JDesktopPane desktop, JDesktopIcon icon,
				int x, int y) {
			Component[] components = desktop.getComponents();

			for(int i = 0; i < components.length; i++) {
				Component comp = components[i];
				if(comp instanceof JDesktopIcon && comp != icon) {

					Point p = comp.getLocation();
					if(p.x == x && p.y == y) {
						return (JDesktopIcon) comp;
					}
				}
			}
			return null;
		}
	} /// END of MotifDesktopManager
}
