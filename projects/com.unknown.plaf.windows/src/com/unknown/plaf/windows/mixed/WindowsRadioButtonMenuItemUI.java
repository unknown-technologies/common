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

package com.unknown.plaf.windows.mixed;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;

import com.unknown.plaf.windows.mixed.TMSchema.Part;
import com.unknown.plaf.windows.mixed.TMSchema.State;

/**
 * Windows rendition of the component.
 */
public final class WindowsRadioButtonMenuItemUI extends BasicRadioButtonMenuItemUI {

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

	public static ComponentUI createUI(JComponent b) {
		return new WindowsRadioButtonMenuItemUI();
	}

	@Override
	protected void paintBackground(Graphics g, JMenuItem item, Color bgColor) {
		if(WindowsMenuItemUI.isVistaPainting()) {
			WindowsMenuItemUI.paintBackground(accessor, g, item);
			return;
		}
		super.paintBackground(g, item, bgColor);
	}

	@SuppressWarnings("hiding")
	@Override
	protected void paintMenuItem(Graphics g, JComponent c, Icon checkIcon, Icon arrowIcon, Color background,
			Color foreground, int defaultTextIconGap) {
		if(WindowsMenuItemUI.isVistaPainting()) {
			WindowsMenuItemUI.paintMenuItem(accessor, g, c, checkIcon, arrowIcon, background, foreground,
					disabledForeground, acceleratorSelectionForeground, acceleratorForeground,
					defaultTextIconGap, menuItem, getPropertyPrefix());
			return;
		}
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
		if(WindowsMenuItemUI.isVistaPainting()) {
			WindowsMenuItemUI.paintText(accessor, g, item, textRect, text);
			return;
		}
		ButtonModel model = item.getModel();
		Color oldColor = g.getColor();

		if(model.isEnabled() && model.isArmed()) {
			g.setColor(selectionForeground); // Uses protected field.
		}

		WindowsGraphicsUtils.paintText(g, item, textRect, text, 0);

		g.setColor(oldColor);
	}
}
