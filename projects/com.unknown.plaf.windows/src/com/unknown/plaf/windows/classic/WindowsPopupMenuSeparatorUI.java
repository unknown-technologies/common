/*
 * Copyright (c) 2004, 2025, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;

/**
 * Windows {@literal L&F} implementation of PopupMenuSeparatorUI.
 *
 * @author Leif Samuelsson
 * @author Igor Kushnirskiy
 */

public final class WindowsPopupMenuSeparatorUI extends BasicPopupMenuSeparatorUI {

	public static ComponentUI createUI(JComponent c) {
		return new WindowsPopupMenuSeparatorUI();
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		Dimension s = c.getSize();
		int y = s.height / 2;
		g.setColor(c.getForeground());
		g.drawLine(1, y - 1, s.width - 2, y - 1);

		g.setColor(c.getBackground());
		g.drawLine(1, y, s.width - 2, y);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		int fontHeight = 0;
		Font font = c.getFont();
		if(font != null) {
			fontHeight = c.getFontMetrics(font).getHeight();
		}

		return new Dimension(0, fontHeight / 2 + 2);
	}

}
