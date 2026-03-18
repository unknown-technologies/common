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

import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 * Windows rendition of the component.
 */
public final class WindowsSliderUI extends BasicSliderUI {
	private boolean rollover = false;
	private boolean pressed = false;

	public WindowsSliderUI(JSlider b) {
		super(b);
	}

	public static ComponentUI createUI(JComponent b) {
		return new WindowsSliderUI((JSlider) b);
	}

	/**
	 * Overrides to return a private track listener subclass which handles the HOT, PRESSED, and FOCUSED states.
	 *
	 * @since 1.6
	 */
	@Override
	protected TrackListener createTrackListener(JSlider s) {
		return new WindowsTrackListener();
	}

	private final class WindowsTrackListener extends TrackListener {

		@Override
		public void mouseMoved(MouseEvent e) {
			updateRollover(thumbRect.contains(e.getX(), e.getY()));
			super.mouseMoved(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			updateRollover(thumbRect.contains(e.getX(), e.getY()));
			super.mouseEntered(e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			updateRollover(false);
			super.mouseExited(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			updatePressed(thumbRect.contains(e.getX(), e.getY()));
			super.mousePressed(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			updatePressed(false);
			super.mouseReleased(e);
		}

		public void updatePressed(boolean newPressed) {
			// You can't press a disabled slider
			if(!slider.isEnabled()) {
				return;
			}
			if(pressed != newPressed) {
				pressed = newPressed;
				slider.repaint(thumbRect);
			}
		}

		public void updateRollover(boolean newRollover) {
			// You can't have a rollover on a disabled slider
			if(!slider.isEnabled()) {
				return;
			}
			if(rollover != newRollover) {
				rollover = newRollover;
				slider.repaint(thumbRect);
			}
		}

	}
}
