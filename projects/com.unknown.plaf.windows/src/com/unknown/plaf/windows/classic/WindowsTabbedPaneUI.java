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

import java.awt.KeyboardFocusManager;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * Windows rendition of the component.
 */
public final class WindowsTabbedPaneUI extends BasicTabbedPaneUI {
	/**
	 * Keys to use for forward focus traversal when the JComponent is managing focus.
	 */
	private static Set<KeyStroke> managingFocusForwardTraversalKeys;

	/**
	 * Keys to use for backward focus traversal when the JComponent is managing focus.
	 */
	private static Set<KeyStroke> managingFocusBackwardTraversalKeys;

	@Override
	@SuppressWarnings("deprecation")
	protected void installDefaults() {
		super.installDefaults();

		// focus forward traversal key
		if(managingFocusForwardTraversalKeys == null) {
			managingFocusForwardTraversalKeys = new HashSet<>();
			managingFocusForwardTraversalKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
		}
		tabPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				managingFocusForwardTraversalKeys);
		// focus backward traversal key
		if(managingFocusBackwardTraversalKeys == null) {
			managingFocusBackwardTraversalKeys = new HashSet<>();
			managingFocusBackwardTraversalKeys
					.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK));
		}
		tabPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
				managingFocusBackwardTraversalKeys);
	}

	@Override
	protected void uninstallDefaults() {
		// sets the focus forward and backward traversal keys to null
		// to restore the defaults
		tabPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
		tabPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
		super.uninstallDefaults();
	}

	public static ComponentUI createUI(JComponent c) {
		return new WindowsTabbedPaneUI();
	}

	@Override
	protected void setRolloverTab(int index) {
		// Rollover is only supported on XP
	}
}
