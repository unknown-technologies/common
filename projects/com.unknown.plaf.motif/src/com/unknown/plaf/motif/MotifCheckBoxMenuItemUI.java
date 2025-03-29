/*
 * Copyright (c) 1997, 2006, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.MenuSelectionManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;

/**
 * MotifCheckboxMenuItem implementation
 *
 * @author Georges Saab
 * @author Rich Schiavi
 */
public class MotifCheckBoxMenuItemUI extends BasicCheckBoxMenuItemUI {
	protected ChangeListener changeListener;

	public static ComponentUI createUI(JComponent b) {
		return new MotifCheckBoxMenuItemUI();
	}

	@Override
	protected void installListeners() {
		super.installListeners();
		changeListener = createChangeListener(menuItem);
		menuItem.addChangeListener(changeListener);
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();
		menuItem.removeChangeListener(changeListener);
	}

	@SuppressWarnings("unused")
	protected ChangeListener createChangeListener(JComponent c) {
		return new ChangeHandler();
	}

	protected class ChangeHandler implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JMenuItem c = (JMenuItem) e.getSource();
			LookAndFeel.installProperty(c, "borderPainted", c.isArmed());
		}
	}

	@Override
	protected MouseInputListener createMouseInputListener(JComponent c) {
		return new MouseInputHandler();
	}

	protected class MouseInputHandler implements MouseInputListener {
		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			MenuSelectionManager manager = MenuSelectionManager.defaultManager();
			manager.setSelectedPath(getPath());
		}

		public void mouseReleased(MouseEvent e) {
			MenuSelectionManager manager = MenuSelectionManager.defaultManager();
			JMenuItem item = (JMenuItem) e.getComponent();
			Point p = e.getPoint();
			if(p.x >= 0 && p.x < item.getWidth() &&
					p.y >= 0 && p.y < item.getHeight()) {
				String property = "CheckBoxMenuItem.doNotCloseOnMouseClick";
				if(!SwingUtilities2.getBoolean(item, property)) {
					manager.clearSelectedPath();
				}
				item.doClick(0);
			} else {
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
