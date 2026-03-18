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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;

import com.unknown.plaf.windows.mixed.TMSchema.Part;
import com.unknown.plaf.windows.mixed.TMSchema.State;
import com.unknown.plaf.windows.mixed.XPStyle.Skin;

public final class WindowsSpinnerUI extends BasicSpinnerUI {
	public static ComponentUI createUI(JComponent c) {
		return new WindowsSpinnerUI();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.6
	 */
	@Override
	public void paint(Graphics g, JComponent c) {
		if(XPStyle.getXP() != null) {
			paintXPBackground(g, c);
		}
		super.paint(g, c);
	}

	private static State getXPState(JComponent c) {
		State state = State.NORMAL;
		if(!c.isEnabled()) {
			state = State.DISABLED;
		}
		return state;
	}

	private static void paintXPBackground(Graphics g, JComponent c) {
		XPStyle xp = XPStyle.getXP();
		if(xp == null) {
			return;
		}
		Skin skin = xp.getSkin(c, Part.EP_EDIT);
		State state = getXPState(c);
		skin.paintSkin(g, 0, 0, c.getWidth(), c.getHeight(), state);
	}

	@Override
	protected Component createPreviousButton() {
		if(XPStyle.getXP() != null) {
			JButton xpButton = new XPStyle.GlyphButton(spinner, Part.SPNP_DOWN);
			Dimension size = UIManager.getDimension("Spinner.arrowButtonSize");
			xpButton.setPreferredSize(size);
			xpButton.setRequestFocusEnabled(false);
			installPreviousButtonListeners(xpButton);
			return xpButton;
		}
		return super.createPreviousButton();
	}

	@Override
	protected Component createNextButton() {
		if(XPStyle.getXP() != null) {
			JButton xpButton = new XPStyle.GlyphButton(spinner, Part.SPNP_UP);
			Dimension size = UIManager.getDimension("Spinner.arrowButtonSize");
			xpButton.setPreferredSize(size);
			xpButton.setRequestFocusEnabled(false);
			installNextButtonListeners(xpButton);
			return xpButton;
		}
		return super.createNextButton();
	}
}
