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

import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 * Windows rendition of the component.
 */
public final class WindowsInternalFrameUI extends BasicInternalFrameUI {
	@Override
	public void installDefaults() {
		super.installDefaults();

		frame.setBorder(UIManager.getBorder("InternalFrame.border"));
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		LookAndFeel.installProperty(c, "opaque", Boolean.TRUE);
	}

	@Override
	public void uninstallDefaults() {
		frame.setBorder(null);
		super.uninstallDefaults();
	}

	public static ComponentUI createUI(JComponent b) {
		return new WindowsInternalFrameUI((JInternalFrame) b);
	}

	public WindowsInternalFrameUI(JInternalFrame w) {
		super(w);
	}

	@Override
	protected DesktopManager createDesktopManager() {
		return new WindowsDesktopManager();
	}

	@Override
	protected JComponent createNorthPane(JInternalFrame w) {
		titlePane = new WindowsInternalFrameTitlePane(w);
		return titlePane;
	}
}
