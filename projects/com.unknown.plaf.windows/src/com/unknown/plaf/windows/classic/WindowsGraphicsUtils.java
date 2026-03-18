/*
 * Copyright (c) 2000, 2025, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import com.unknown.util.ui.plaf.MnemonicHandler;
import com.unknown.util.ui.plaf.SwingUtilities2;

/**
 * A collection of static utility methods used for rendering the Windows look and feel.
 *
 * @author Mark Davidson
 * @since 1.4
 */
public final class WindowsGraphicsUtils {

	/**
	 * Renders a text String in Windows without the mnemonic. This is here because the WindowsUI hierarchy doesn't
	 * match the Component hierarchy. All the overridden paintText methods of the ButtonUI delegates will call this
	 * static method.
	 *
	 * @param g
	 *                Graphics context
	 * @param b
	 *                Current button to render
	 * @param textRect
	 *                Bounding rectangle to render the text.
	 * @param text
	 *                String to render
	 */
	public static void paintText(Graphics g, AbstractButton b,
			Rectangle textRect, String text,
			int textShiftOffset) {
		FontMetrics fm = SwingUtilities2.getFontMetrics(b, g);

		int mnemIndex = b.getDisplayedMnemonicIndex();
		// W2K Feature: Check to see if the Underscore should be rendered.
		if(MnemonicHandler.isMnemonicHidden()) {
			mnemIndex = -1;
		}

		paintClassicText(b, g, textRect.x + textShiftOffset,
				textRect.y + fm.getAscent() + textShiftOffset,
				text, mnemIndex);
	}

	static void paintClassicText(AbstractButton b, Graphics g, int x, int y,
			String text, int mnemIndex) {
		ButtonModel model = b.getModel();

		/* Draw the Text */
		Color color = b.getForeground();
		if(model.isEnabled()) {
			/*** paint the text normally */
			if(!(b instanceof JMenuItem && model.isArmed()) &&
					!(b instanceof JMenu && (model.isSelected() || model.isRollover()))) {
				/*
				 * We shall not set foreground color for selected menu or armed menuitem. Foreground
				 * must be set in appropriate Windows* class because these colors passes from
				 * BasicMenuItemUI as protected fields and we can't reach them from this class
				 */
				g.setColor(b.getForeground());
			}
			SwingUtilities2.drawStringUnderlineCharAt(b, g, text, mnemIndex, x, y);
		} else { /*** paint the text disabled ***/
			color = getDisabledTextColor(b);
			if(color == null) {
				color = UIManager.getColor("Button.shadow");
			}
			Color shadow = UIManager.getColor("Button.disabledShadow");
			if(model.isArmed()) {
				color = UIManager.getColor("Button.disabledForeground");
			} else {
				if(shadow == null) {
					shadow = b.getBackground().darker();
				}
				g.setColor(shadow);
				SwingUtilities2.drawStringUnderlineCharAt(b, g, text, mnemIndex,
						x + 1, y + 1);
			}
			if(color == null) {
				color = b.getBackground().brighter();
			}
			g.setColor(color);
			SwingUtilities2.drawStringUnderlineCharAt(b, g, text, mnemIndex, x, y);
		}
	}

	private static Color getDisabledTextColor(AbstractButton b) {
		if(b instanceof JCheckBox) {
			return UIManager.getColor("CheckBox.disabledText");
		} else if(b instanceof JRadioButton) {
			return UIManager.getColor("RadioButton.disabledText");
		} else if(b instanceof JToggleButton) {
			return UIManager.getColor("ToggleButton.disabledText");
		} else if(b instanceof JButton) {
			return UIManager.getColor("Button.disabledText");
		}
		return null;
	}

	static boolean isLeftToRight(Component c) {
		return c.getComponentOrientation().isLeftToRight();
	}

}
