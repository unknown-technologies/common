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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonModel;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

/**
 * Windows combo box.
 *
 * @author Tom Santos
 * @author Igor Kushnirskiy
 */
public final class WindowsComboBoxUI extends BasicComboBoxUI {
	private static final MouseListener rolloverListener = new MouseAdapter() {
		private void handleRollover(MouseEvent e, boolean isRollover) {
			JComboBox<?> comboBox = getComboBox(e);
			WindowsComboBoxUI comboBoxUI = getWindowsComboBoxUI(e);
			if(comboBox == null || comboBoxUI == null) {
				return;
			}
			if(!comboBox.isEditable()) {
				// mouse over editable ComboBox does not switch rollover for the arrow button
				ButtonModel m = null;
				if(comboBoxUI.arrowButton != null) {
					m = comboBoxUI.arrowButton.getModel();
				}
				if(m != null) {
					m.setRollover(isRollover);
				}
			}
			comboBox.repaint();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			handleRollover(e, true);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			handleRollover(e, false);
		}

		private JComboBox<?> getComboBox(MouseEvent event) {
			Object source = event.getSource();
			JComboBox<?> rv = null;
			if(source instanceof JComboBox) {
				rv = (JComboBox<?>) source;
			} else if(source instanceof JTextField &&
					((JTextField) source).getParent() instanceof JComboBox) {
				rv = (JComboBox<?>) ((JTextField) source).getParent();
			}
			return rv;
		}

		private WindowsComboBoxUI getWindowsComboBoxUI(MouseEvent event) {
			JComboBox<?> comboBox = getComboBox(event);
			WindowsComboBoxUI rv = null;
			if(comboBox != null && comboBox.getUI() instanceof WindowsComboBoxUI) {
				rv = (WindowsComboBoxUI) comboBox.getUI();
			}
			return rv;
		}

	};

	public static ComponentUI createUI(JComponent c) {
		return new WindowsComboBoxUI();
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		comboBox.setRequestFocusEnabled(true);
	}

	@Override
	public void uninstallUI(JComponent c) {
		comboBox.removeMouseListener(rolloverListener);
		if(arrowButton != null) {
			arrowButton.removeMouseListener(rolloverListener);
		}
		super.uninstallUI(c);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.6
	 */
	@Override
	protected void unconfigureEditor() {
		super.unconfigureEditor();
		editor.removeMouseListener(rolloverListener);
	}

	/**
	 * If necessary paints the currently selected item.
	 *
	 * @param g
	 *                Graphics to paint to
	 * @param bounds
	 *                Region to paint current value to
	 * @param focus
	 *                whether or not the JComboBox has focus
	 * @throws NullPointerException
	 *                 if any of the arguments are null.
	 * @since 1.5
	 */
	@Override
	public void paintCurrentValue(Graphics g, Rectangle bounds, boolean focus) {
		bounds.x += 1;
		bounds.y += 1;
		bounds.width -= 2;
		bounds.height -= 2;
		super.paintCurrentValue(g, bounds, focus);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.6
	 */
	@Override
	public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean focus) {
		// nothing
	}

	@Override
	public Dimension getMinimumSize(JComponent c) {
		Dimension d = super.getMinimumSize(c);
		d.width += 4;
		d.height += 2;
		return d;
	}

	/**
	 * Creates a layout manager for managing the components which make up the combo box.
	 *
	 * @return an instance of a layout manager
	 */
	@Override
	protected LayoutManager createLayoutManager() {
		return new BasicComboBoxUI.ComboBoxLayoutManager() {
			@Override
			public void layoutContainer(Container parent) {
				super.layoutContainer(parent);
			}
		};
	}

	@Override
	protected void installKeyboardActions() {
		super.installKeyboardActions();
	}

	@Override
	protected ComboPopup createPopup() {
		return new WinComboPopUp(comboBox);
	}

	/**
	 * Creates the default editor that will be used in editable combo boxes. A default editor will be used only if
	 * an editor has not been explicitly set with <code>setEditor</code>.
	 *
	 * @return a <code>ComboBoxEditor</code> used for the combo box
	 * @see javax.swing.JComboBox#setEditor
	 */
	@Override
	protected ComboBoxEditor createEditor() {
		return new WindowsComboBoxEditor();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.6
	 */
	@Override
	protected ListCellRenderer<Object> createRenderer() {
		return super.createRenderer();
	}

	/**
	 * Creates an button which will be used as the control to show or hide the popup portion of the combo box.
	 *
	 * @return a button which represents the popup control
	 */
	@Override
	protected JButton createArrowButton() {
		return super.createArrowButton();
	}

	@SuppressWarnings("serial") // Same-version serialization only
	protected final class WinComboPopUp extends BasicComboPopup {
		public WinComboPopUp(JComboBox<Object> combo) {
			super(combo);
		}

		@Override
		protected KeyListener createKeyListener() {
			return new InvocationKeyHandler();
		}

		protected final class InvocationKeyHandler extends BasicComboPopup.InvocationKeyHandler {
			protected InvocationKeyHandler() {
				WinComboPopUp.this.super();
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		}
	}

	/**
	 * Subclassed to highlight selected item in an editable combo box.
	 */
	public static final class WindowsComboBoxEditor
			extends BasicComboBoxEditor.UIResource {

		/**
		 * {@inheritDoc}
		 *
		 * @since 1.6
		 */
		@Override
		protected JTextField createEditorComponent() {
			JTextField editorComponent = super.createEditorComponent();
			Border border = (Border) UIManager.get("ComboBox.editorBorder");

			if(border != null) {
				editorComponent.setBorder(border);
			}
			editorComponent.setOpaque(false);
			return editorComponent;
		}

		@Override
		public void setItem(Object item) {
			super.setItem(item);
			Object focus = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
			if((focus == editor) || (focus == editor.getParent())) {
				editor.selectAll();
			}
		}
	}
}
