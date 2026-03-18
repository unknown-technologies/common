/*
 * Copyright (c) 1997, 2026, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicMenuItemUI;

import com.unknown.plaf.windows.classic.TMSchema.Part;
import com.unknown.plaf.windows.classic.TMSchema.State;
import com.unknown.util.ui.plaf.MenuItemCheckIconFactory;
import com.unknown.util.ui.plaf.MenuItemLayoutHelper;

/**
 * Windows rendition of the component.
 *
 * @author Igor Kushnirskiy
 */
public final class WindowsMenuItemUI extends BasicMenuItemUI {
	/**
	 * The instance of {@code PropertyChangeListener}.
	 */
	private PropertyChangeListener changeListener;

	final WindowsMenuItemUIAccessor accessor = new WindowsMenuItemUIAccessor() {
		@Override
		public JMenuItem getMenuItem() {
			return menuItem;
		}

		@Override
		public State getState(JMenuItem item) {
			return WindowsMenuItemUI.getState(item);
		}

		@Override
		public Part getPart(JMenuItem item) {
			return WindowsMenuItemUI.getPart();
		}
	};

	public static ComponentUI createUI(JComponent c) {
		return new WindowsMenuItemUI();
	}

	private void updateCheckIcon() {
		String prefix = getPropertyPrefix();

		if(checkIcon == null ||
				checkIcon instanceof UIResource) {
			checkIcon = UIManager.getIcon(prefix + ".checkIcon");
			// In case of column layout, .checkIconFactory is defined for this UI,
			// the icon is compatible with it and useCheckAndArrow() is true,
			// then the icon is handled by the checkIcon.
			boolean isColumnLayout = MenuItemLayoutHelper.isColumnLayout(
					menuItem.getComponentOrientation().isLeftToRight(), menuItem);
			if(isColumnLayout) {
				MenuItemCheckIconFactory iconFactory = (MenuItemCheckIconFactory) UIManager
						.get(prefix + ".checkIconFactory");
				if(iconFactory != null && MenuItemLayoutHelper.useCheckAndArrow(menuItem) &&
						iconFactory.isCompatible(checkIcon, prefix)) {
					checkIcon = iconFactory.getIcon(menuItem);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void installListeners() {
		super.installListeners();
		changeListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				String name = e.getPropertyName();
				if(name == "horizontalTextPosition") {
					updateCheckIcon();
				}
			}
		};
		menuItem.addPropertyChangeListener(changeListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();
		if(changeListener != null) {
			menuItem.removePropertyChangeListener(changeListener);
		}
		changeListener = null;
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
	 */
	@Override
	protected void paintText(Graphics g, JMenuItem item, Rectangle textRect, String text) {
		ButtonModel model = item.getModel();
		Color oldColor = g.getColor();

		if(model.isEnabled() && (model.isArmed() || (item instanceof JMenu && model.isSelected()))) {
			g.setColor(selectionForeground); // Uses protected field.
		}

		WindowsGraphicsUtils.paintText(g, item, textRect, text, 0);

		g.setColor(oldColor);
	}

	static State getState(JMenuItem menuItem) {
		State state;
		ButtonModel model = menuItem.getModel();
		if(model.isArmed()) {
			state = (model.isEnabled()) ? State.HOT : State.DISABLEDHOT;
		} else {
			state = (model.isEnabled()) ? State.NORMAL : State.DISABLED;
		}
		return state;
	}

	static Part getPart() {
		return Part.MP_POPUPITEM;
	}
}
