/*
 * Copyright (c) 1997, 2025, Oracle and/or its affiliates. All rights reserved.
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

package com.unknown.plaf.windows.classic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

import com.unknown.plaf.windows.classic.TMSchema.Part;
import com.unknown.plaf.windows.classic.TMSchema.State;

/**
 * Windows rendition of the component.
 */
public final class WindowsMenuUI extends BasicMenuUI {
	private Integer menuBarHeight;
	private boolean hotTrackingOn;

	final WindowsMenuItemUIAccessor accessor = new WindowsMenuItemUIAccessor() {

		@Override
		public JMenuItem getMenuItem() {
			return menuItem;
		}

		@Override
		public State getState(JMenuItem menu) {
			State state = menu.isEnabled() ? State.NORMAL
					: State.DISABLED;
			ButtonModel model = menu.getModel();
			if(model.isArmed() || model.isSelected()) {
				state = (menu.isEnabled()) ? State.PUSHED
						: State.DISABLEDPUSHED;
			} else if(model.isRollover() && ((JMenu) menu).isTopLevelMenu()) {
				/*
				 * Only paint rollover if no other menu on menubar is selected
				 */
				State stateTmp = state;
				state = (menu.isEnabled()) ? State.HOT
						: State.DISABLEDHOT;
				for(MenuElement menuElement : ((JMenuBar) menu.getParent()).getSubElements()) {
					if(((JMenuItem) menuElement).isSelected()) {
						state = stateTmp;
						break;
					}
				}
			}

			// non top level menus have HOT state instead of PUSHED
			if(!((JMenu) menu).isTopLevelMenu()) {
				if(state == State.PUSHED) {
					state = State.HOT;
				} else if(state == State.DISABLEDPUSHED) {
					state = State.DISABLEDHOT;
				}
			}

			return state;
		}

		@Override
		public Part getPart(JMenuItem item) {
			return ((JMenu) item).isTopLevelMenu() ? Part.MP_BARITEM : Part.MP_POPUPITEM;
		}
	};

	public static ComponentUI createUI(JComponent x) {
		return new WindowsMenuUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		menuBarHeight = UIManager.getInt("MenuBar.height");

		Object obj = UIManager.get("MenuBar.rolloverEnabled");
		hotTrackingOn = (obj instanceof Boolean) ? (Boolean) obj : true;
	}

	@SuppressWarnings("hiding")
	@Override
	protected void paintMenuItem(Graphics g, JComponent c, Icon checkIcon, Icon arrowIcon, Color background,
			Color foreground, int defaultTextIconGap) {
		assert c == menuItem : "menuItem passed as 'c' must be the same";
		super.paintMenuItem(g, c, checkIcon, arrowIcon, background, foreground, defaultTextIconGap);
	}

	/**
	 * Method which renders the text of the current menu item.
	 *
	 * @param g
	 *                Graphics context
	 * @param item
	 *                Current menu item to render
	 * @param textRect
	 *                Bounding rectangle to render the text.
	 * @param text
	 *                String to render
	 * @since 1.4
	 */
	@Override
	protected void paintText(Graphics g, JMenuItem item, Rectangle textRect, String text) {
		JMenu menu = (JMenu) item;
		ButtonModel model = item.getModel();
		Color oldColor = g.getColor();

		// Only paint rollover if no other menu on menubar is selected
		boolean paintRollover = model.isRollover();
		if(paintRollover && menu.isTopLevelMenu()) {
			MenuElement[] menus = ((JMenuBar) menu.getParent()).getSubElements();
			for(int i = 0; i < menus.length; i++) {
				if(((JMenuItem) menus[i]).isSelected()) {
					paintRollover = false;
					break;
				}
			}
		}

		if(model.isSelected()) {
			g.setColor(selectionForeground); // Uses protected field.
		}

		WindowsGraphicsUtils.paintText(g, item, textRect, text, 0);

		g.setColor(oldColor);
	}

	@Override
	protected MouseInputListener createMouseInputListener(JComponent c) {
		return new WindowsMouseInputHandler();
	}

	/**
	 * This class implements a mouse handler that sets the rollover flag to true when the mouse enters the menu and
	 * false when it exits.
	 *
	 * @since 1.4
	 */
	protected final class WindowsMouseInputHandler extends BasicMenuUI.MouseInputHandler {
		@Override
		public void mouseEntered(MouseEvent evt) {
			super.mouseEntered(evt);

			JMenu menu = (JMenu) evt.getSource();
			if(hotTrackingOn && menu.isTopLevelMenu() && menu.isRolloverEnabled()) {
				menu.getModel().setRollover(true);
				menuItem.repaint();
			}
		}

		@Override
		public void mouseExited(MouseEvent evt) {
			super.mouseExited(evt);

			JMenu menu = (JMenu) evt.getSource();
			ButtonModel model = menu.getModel();
			if(menu.isRolloverEnabled() && menu.isTopLevelMenu()) {
				model.setRollover(false);
				menuItem.repaint();
			}
		}
	}

	@Override
	protected Dimension getPreferredMenuItemSize(JComponent c, Icon check, Icon arrow, int textIconGap) {
		Dimension d = super.getPreferredMenuItemSize(c, check, arrow, textIconGap);

		// Note: When toolbar containers (rebars) are implemented, only do
		// this if the JMenuBar is not in a rebar (i.e. ignore the desktop
		// property win.menu.height if in a rebar.)
		if(c instanceof JMenu && ((JMenu) c).isTopLevelMenu() && menuBarHeight != null &&
				d.height < menuBarHeight) {
			d.height = menuBarHeight;
		}

		return d;
	}
}
