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

import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

/**
 * A Motif {@literal L&F} implementation of MenuUI.
 *
 * @author Georges Saab
 * @author Rich Schiavi
 */
public class MotifMenuUI extends BasicMenuUI {

	public static ComponentUI createUI(JComponent x) {
		return new MotifMenuUI();
	}

	// These should not be necessary because BasicMenuUI does this,
	// and this class overrides createChangeListener.
	// protected void installListeners() {
	// super.installListeners();
	// changeListener = createChangeListener(menuItem);
	// menuItem.addChangeListener(changeListener);
	// }
	//
	// protected void uninstallListeners() {
	// super.uninstallListeners();
	// menuItem.removeChangeListener(changeListener);
	// }

	@Override
	protected ChangeListener createChangeListener(JComponent c) {
		return new MotifChangeHandler((JMenu) c, this);
	}

	@Override
	protected MouseInputListener createMouseInputListener(JComponent c) {
		return new MouseInputHandler();
	}

	public class MotifChangeHandler extends ChangeHandler {
		public MotifChangeHandler(JMenu m, MotifMenuUI ui) {
			super(m, ui);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			JMenuItem c = (JMenuItem) e.getSource();
			if(c.isArmed() || c.isSelected()) {
				c.setBorderPainted(true);
				// c.repaint();
			} else {
				c.setBorderPainted(false);
			}

			super.stateChanged(e);
		}
	}

	protected class MouseInputHandler implements MouseInputListener {
		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			MenuSelectionManager manager = MenuSelectionManager.defaultManager();
			JMenu menu = (JMenu) e.getComponent();
			if(menu.isEnabled()) {
				if(menu.isTopLevelMenu()) {
					if(menu.isSelected()) {
						manager.clearSelectedPath();
					} else {
						Container cnt = menu.getParent();
						if(cnt instanceof JMenuBar) {
							JMenuBar menuBar = (JMenuBar) cnt;
							MenuElement[] me = new MenuElement[2];
							me[0] = menuBar;
							me[1] = menu;
							manager.setSelectedPath(me);
						}
					}
				}

				MenuElement[] path = getPath();
				if(path.length > 0) {
					MenuElement[] newPath = new MenuElement[path.length + 1];
					System.arraycopy(path, 0, newPath, 0, path.length);
					newPath[path.length] = menu.getPopupMenu();
					manager.setSelectedPath(newPath);
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			MenuSelectionManager manager = MenuSelectionManager.defaultManager();
			JMenuItem item = (JMenuItem) e.getComponent();
			Point p = e.getPoint();
			if(!(p.x >= 0 && p.x < item.getWidth() &&
					p.y >= 0 && p.y < item.getHeight())) {
				manager.processMouseEvent(e);
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseDragged(MouseEvent e) {
			MenuSelectionManager.defaultManager().processMouseEvent(e);
		}

		public void mouseMoved(MouseEvent e) {
		}
	}
}
