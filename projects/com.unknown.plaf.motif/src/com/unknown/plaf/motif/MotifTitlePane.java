package com.unknown.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.UIResource;

@SuppressWarnings("serial")
public class MotifTitlePane extends JComponent {
	// The width and height of a title pane button
	public static final int BUTTON_SIZE = 19;  // 17 + 1 pixel border

	/**
	 * Action used to close the Window.
	 */
	private Action closeAction;

	/**
	 * Action used to iconify the Frame.
	 */
	private Action iconifyAction;

	/**
	 * Action to restore the Frame size.
	 */
	private Action restoreAction;

	/**
	 * Action to restore the Frame size.
	 */
	private Action maximizeAction;

	/**
	 * PropertyChangeListener added to the JRootPane.
	 */
	private PropertyChangeListener propertyChangeListener;

	/**
	 * JPopupMenu, typically renders the system menu items.
	 */
	private JPopupMenu systemMenu;

	/**
	 * Listens for changes in the state of the Window listener to update the state of the widgets.
	 */
	private WindowListener windowListener;

	/**
	 * Window we're currently in.
	 */
	private Window window;

	/**
	 * JRootPane rendering for.
	 */
	private JRootPane rootPane;

	private MotifRootPaneUI rootPaneUI;

	private int state;

	private SystemButton systemButton;
	private MinimizeButton minimizeButton;
	private MaximizeButton maximizeButton;
	private Title title;
	private Color color;
	private Color highlight;
	private Color shadow;

	private long popupTime;
	private Point lastPoint;

	public MotifTitlePane(JRootPane root, MotifRootPaneUI ui) {
		this.rootPane = root;
		rootPaneUI = ui;

		state = -1;

		createActions();
		assembleSystemMenu();
		createButtons();
		addSubComponents();
		installDefaults();
		setColors();

		setLayout(createLayout());
	}

	private void setColors() {
		boolean isSelected = (window == null) ? true : window.isActive();
		setColors(isSelected);
	}

	private void setColors(boolean isSelected) {
		if(isSelected) {
			color = UIManager.getColor("InternalFrame.activeTitleBackground");
		} else {
			color = UIManager.getColor("InternalFrame.inactiveTitleBackground");
		}
		highlight = color.brighter();
		shadow = color.darker().darker();
		setColors(color, highlight, shadow);
	}

	/**
	 * Installs the necessary listeners.
	 */
	private void installListeners() {
		if(window != null) {
			windowListener = createWindowListener();
			window.addWindowListener(windowListener);
			propertyChangeListener = createWindowPropertyChangeListener();
			window.addPropertyChangeListener(propertyChangeListener);
		}
	}

	/**
	 * Uninstalls the necessary listeners.
	 */
	private void uninstallListeners() {
		if(window != null) {
			window.removeWindowListener(windowListener);
			window.removePropertyChangeListener(propertyChangeListener);
		}
	}

	/**
	 * Returns the <code>WindowListener</code> to add to the <code>Window</code>.
	 */
	private WindowListener createWindowListener() {
		return new WindowHandler();
	}

	/**
	 * Returns the <code>PropertyChangeListener</code> to install on the <code>Window</code>.
	 */
	private PropertyChangeListener createWindowPropertyChangeListener() {
		return new PropertyChangeHandler();
	}

	/**
	 * Returns the <code>JRootPane</code> this was created for.
	 */
	@Override
	public JRootPane getRootPane() {
		return rootPane;
	}

	/**
	 * Returns the decoration style of the <code>JRootPane</code>.
	 */
	private int getWindowDecorationStyle() {
		return getRootPane().getWindowDecorationStyle();
	}

	@Override
	public void addNotify() {
		super.addNotify();

		uninstallListeners();

		window = SwingUtilities.getWindowAncestor(this);
		if(window != null) {
			if(window instanceof Frame) {
				setState(((Frame) window).getExtendedState());
			} else {
				setState(0);
			}
			setActive(window.isActive());
			installListeners();
		}
	}

	@Override
	public void removeNotify() {
		super.removeNotify();

		uninstallListeners();
		window = null;
	}

	/**
	 * Installs the fonts and necessary properties on the MetalTitlePane.
	 */
	private void installDefaults() {
		setFont(UIManager.getFont("InternalFrame.titleFont", getLocale()));
		setPreferredSize(new Dimension(100, BUTTON_SIZE));
	}

	/**
	 * Closes the Window.
	 */
	private void close() {
		Window win = getWindow();

		if(win != null) {
			win.dispatchEvent(new WindowEvent(win, WindowEvent.WINDOW_CLOSING));
		}
	}

	/**
	 * Iconifies the Frame.
	 */
	private void iconify() {
		Frame frame = getFrame();
		if(frame != null) {
			frame.setExtendedState(state | Frame.ICONIFIED);
			setState(state | Frame.ICONIFIED);
		}
	}

	/**
	 * Maximizes the Frame.
	 */
	private void maximize() {
		Frame frame = getFrame();
		if(frame != null) {
			frame.setExtendedState(state | Frame.MAXIMIZED_BOTH);
			setState(state | Frame.MAXIMIZED_BOTH);
		}
	}

	/**
	 * Restores the Frame size.
	 */
	private void restore() {
		Frame frame = getFrame();

		if(frame == null) {
			return;
		}

		if((state & Frame.ICONIFIED) != 0) {
			frame.setExtendedState(state & ~Frame.ICONIFIED);
			setState(state & ~Frame.ICONIFIED);
		} else {
			frame.setExtendedState(state & ~Frame.MAXIMIZED_BOTH);
			setState(state & ~Frame.MAXIMIZED_BOTH);
		}
	}

	private void createActions() {
		closeAction = new CloseAction();
		if(getWindowDecorationStyle() == JRootPane.FRAME) {
			iconifyAction = new IconifyAction();
			restoreAction = new RestoreAction();
			maximizeAction = new MaximizeAction();
		}
	}

	/**
	 * Returns the <code>LayoutManager</code> that should be installed on the <code>MetalTitlePane</code>.
	 */
	private LayoutManager createLayout() {
		return new TitlePaneLayout();
	}

	/**
	 * Updates state dependent upon the Window's active state.
	 */
	private void setActive(boolean isActive) {
		// Repaint the whole thing as the Borders that are used have
		// different colors for active vs inactive
		setColors(isActive);
		getRootPane().repaint();
	}

	/**
	 * Sets the state of the Window.
	 */
	private void setState(int state) {
		setState(state, false);
	}

	/**
	 * Sets the state of the window. If <code>updateRegardless</code> is true and the state has not changed, this
	 * will update anyway.
	 */
	private void setState(int state, boolean updateRegardless) {
		Window w = getWindow();

		if(w != null && getWindowDecorationStyle() == JRootPane.FRAME) {
			if(this.state == state && !updateRegardless) {
				return;
			}

			Frame frame = getFrame();

			if(frame != null) {
				JRootPane root = getRootPane();

				if(((state & Frame.MAXIMIZED_BOTH) != 0) &&
						(root.getBorder() == null ||
								(root.getBorder() instanceof UIResource)) &&
						frame.isShowing()) {
					root.setBorder(null);
				} else if((state & Frame.MAXIMIZED_BOTH) == 0) {
					// This is a croak, if state becomes bound, this can be nuked.
					rootPaneUI.installBorder(root);
				}
				if(frame.isResizable()) {
					if((state & Frame.MAXIMIZED_BOTH) != 0) {
						maximizeAction.setEnabled(false);
						restoreAction.setEnabled(true);
					} else {
						maximizeAction.setEnabled(true);
						restoreAction.setEnabled(false);
					}
				} else {
					maximizeAction.setEnabled(false);
					restoreAction.setEnabled(false);
				}
			} else {
				// Not contained in a Frame
				maximizeAction.setEnabled(false);
				restoreAction.setEnabled(false);
				iconifyAction.setEnabled(false);
				revalidate();
				repaint();
			}
			closeAction.setEnabled(true);
			this.state = state;
		}
	}

	/**
	 * Returns the Frame rendering in. This will return null if the <code>JRootPane</code> is not contained in a
	 * <code>Frame</code>.
	 */
	private Frame getFrame() {
		Window w = getWindow();

		if(w instanceof Frame) {
			return (Frame) w;
		}
		return null;
	}

	/**
	 * Returns the <code>Window</code> the <code>JRootPane</code> is contained in. This will return null if there is
	 * no parent ancestor of the <code>JRootPane</code>.
	 */
	private Window getWindow() {
		return window;
	}

	/**
	 * Returns the String to display as the title.
	 */
	private String getTitle() {
		Window w = getWindow();

		if(w instanceof Frame) {
			return ((Frame) w).getTitle();
		} else if(w instanceof Dialog) {
			return ((Dialog) w).getTitle();
		}
		return null;
	}

	protected boolean isResizable() {
		Window win = getWindow();
		if(win == null) {
			return false;
		} else if(win instanceof Frame) {
			Frame frame = (Frame) win;
			return frame.isResizable();
		} else if(win instanceof Dialog) {
			Dialog dialog = (Dialog) win;
			return dialog.isResizable();
		} else {
			return false;
		}
	}

	protected boolean isMaximizable() {
		Window win = getWindow();
		if(win == null) {
			return false;
		} else if(win instanceof Frame) {
			Frame frame = (Frame) win;
			return frame.isResizable();
		} else if(win instanceof Dialog) {
			return false;
		} else {
			return false;
		}
	}

	protected boolean isIconifiable() {
		Window win = getWindow();
		if(win == null) {
			return false;
		} else if(win instanceof Frame) {
			return true;
		} else if(win instanceof Dialog) {
			return false;
		} else {
			return false;
		}
	}

	private class CloseAction extends AbstractAction {
		public CloseAction() {
			super(UIManager.getString("InternalFrameTitlePane.closeButtonText", getLocale()));
		}

		public void actionPerformed(ActionEvent e) {
			close();
		}
	}

	/**
	 * Actions used to <code>iconfiy</code> the <code>Frame</code>.
	 */
	private class IconifyAction extends AbstractAction {
		public IconifyAction() {
			super(UIManager.getString("InternalFrameTitlePane.minimizeButtonText", getLocale()));
		}

		public void actionPerformed(ActionEvent e) {
			iconify();
		}
	}

	/**
	 * Actions used to <code>restore</code> the <code>Frame</code>.
	 */
	private class RestoreAction extends AbstractAction {
		public RestoreAction() {
			super(UIManager.getString("InternalFrameTitlePane.restoreButtonText", getLocale()));
		}

		public void actionPerformed(ActionEvent e) {
			restore();
		}
	}

	/**
	 * Actions used to <code>restore</code> the <code>Frame</code>.
	 */
	private class MaximizeAction extends AbstractAction {
		public MaximizeAction() {
			super(UIManager.getString("InternalFrameTitlePane.maximizeButtonText", getLocale()));
		}

		public void actionPerformed(ActionEvent e) {
			if((state & Frame.MAXIMIZED_BOTH) != 0) {
				restore();
			} else {
				maximize();
			}
		}
	}

	/**
	 * PropertyChangeListener installed on the Window. Updates the necessary state as the state of the Window
	 * changes.
	 */
	private class PropertyChangeHandler implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent pce) {
			String name = pce.getPropertyName();

			// Frame.state isn't currently bound.
			if("resizable".equals(name) || "state".equals(name)) {
				Frame frame = getFrame();

				if(frame != null) {
					setState(frame.getExtendedState(), true);
				}
				if("resizable".equals(name)) {
					getRootPane().repaint();
				}
			} else if("title".equals(name)) {
				repaint();
			} else if("componentOrientation" == name) {
				revalidate();
				repaint();
			} else if("iconImage" == name) {
				revalidate();
				repaint();
			}
		}
	}

	/**
	 * WindowListener installed on the Window, updates the state as necessary.
	 */
	private class WindowHandler extends WindowAdapter {
		@Override
		public void windowActivated(WindowEvent ev) {
			setActive(true);
		}

		@Override
		public void windowDeactivated(WindowEvent ev) {
			setActive(false);
		}
	}

	protected void assembleSystemMenu() {
		systemMenu = new JPopupMenu();
		if(getWindowDecorationStyle() == JRootPane.FRAME) {
			JMenuItem mi = systemMenu.add(restoreAction);
			mi.setMnemonic(getButtonMnemonic("restore"));
			// mi = systemMenu.add(moveAction);
			// mi.setMnemonic(getButtonMnemonic("move"));
			// mi = systemMenu.add(sizeAction);
			// mi.setMnemonic(getButtonMnemonic("size"));
			mi = systemMenu.add(iconifyAction);
			mi.setMnemonic(getButtonMnemonic("minimize"));
			mi = systemMenu.add(maximizeAction);
			mi.setMnemonic(getButtonMnemonic("maximize"));
			systemMenu.add(new JSeparator());
		}
		JMenuItem mi = systemMenu.add(closeAction);
		mi.setMnemonic(getButtonMnemonic("close"));

		systemButton = new SystemButton();
		systemButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				systemMenu.show(systemButton, 0, BUTTON_SIZE);

				// workaround for JDK bug which consumes the next mouse event after a popup menu became
				// visible. This results in a reset of the click count, which means 3 clicks would be
				// required to perform a "double click". This hack implements the double click timer
				// manually
				popupTime = e.getWhen();
			}
		});

		systemButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				long when = evt.getWhen();
				long dt = when - popupTime;
				int timeout = getMultiClickInterval();
				boolean dbl = dt < timeout && evt.getPoint().equals(lastPoint);
				lastPoint = evt.getPoint();
				if(evt.getClickCount() == 2 || dbl) {
					// mark button as not pressed, otherwise re-showing the frame will have a still
					// pressed button
					systemButton.getModel().setPressed(false);

					closeAction.actionPerformed(new ActionEvent(evt.getSource(),
							ActionEvent.ACTION_PERFORMED, null, evt.getWhen(), 0));
					systemMenu.setVisible(false);
				}
			}

			@Override
			public void mouseMoved(MouseEvent evt) {
				lastPoint = null;
			}

			@Override
			public void mouseExited(MouseEvent evt) {
				lastPoint = null;
			}
		});
	}

	private static int getMultiClickInterval() {
		Integer multiClickInterval = (Integer) Toolkit.getDefaultToolkit()
				.getDesktopProperty("awt.multiClickInterval");
		if(multiClickInterval != null) {
			return multiClickInterval;
		} else {
			return 300; // by default we use 300ms ... good enough if there is non better info
		}
	}

	private static int getButtonMnemonic(String button) {
		try {
			return Integer.parseInt(UIManager.getString(
					"InternalFrameTitlePane." + button + "Button.mnemonic"));
		} catch(NumberFormatException e) {
			return -1;
		}
	}

	protected void createButtons() {
		minimizeButton = new MinimizeButton();
		minimizeButton.addActionListener(iconifyAction);

		maximizeButton = new MaximizeButton();
		maximizeButton.addActionListener(maximizeAction);
	}

	protected void addSubComponents() {
		title = new Title(getTitle());
		title.setFont(getFont());

		add(systemButton);
		add(title);
		add(minimizeButton);
		add(maximizeButton);
	}

	@Override
	public void paintComponent(Graphics g) {
		if(getFrame() != null) {
			setState(getFrame().getExtendedState());
		}
	}

	public void setColors(Color c, Color h, Color s) {
		color = c;
		highlight = h;
		shadow = s;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if(JInternalFrame.IS_SELECTED_PROPERTY.equals(prop)) {
			repaint();
		} else if(prop.equals("maximizable")) {
			if((Boolean) evt.getNewValue() == Boolean.TRUE)
				add(maximizeButton);
			else
				remove(maximizeButton);
			revalidate();
			repaint();
		} else if(prop.equals("iconable")) {
			if((Boolean) evt.getNewValue() == Boolean.TRUE)
				add(minimizeButton);
			else
				remove(minimizeButton);
			revalidate();
			repaint();
		} else if(prop.equals(JInternalFrame.TITLE_PROPERTY)) {
			repaint();
		}
		// enableActions();
	}

	private class TitlePaneLayout implements LayoutManager {
		@Override
		public void addLayoutComponent(String name, Component c) {
		}

		@Override
		public void removeLayoutComponent(Component c) {
		}

		@Override
		public Dimension preferredLayoutSize(Container c) {
			return minimumLayoutSize(c);
		}

		@Override
		public Dimension minimumLayoutSize(Container c) {
			return new Dimension(100, BUTTON_SIZE);
		}

		@Override
		public void layoutContainer(Container c) {
			int w = getWidth();
			systemButton.setBounds(0, 0, BUTTON_SIZE, BUTTON_SIZE);
			int x = w - BUTTON_SIZE;

			if(isMaximizable()) {
				maximizeButton.setBounds(x, 0, BUTTON_SIZE, BUTTON_SIZE);
				x -= BUTTON_SIZE;
			} else if(maximizeButton.getParent() != null) {
				maximizeButton.getParent().remove(maximizeButton);
			}

			if(isIconifiable()) {
				minimizeButton.setBounds(x, 0, BUTTON_SIZE, BUTTON_SIZE);
				x -= BUTTON_SIZE;
			} else if(minimizeButton.getParent() != null) {
				minimizeButton.getParent().remove(minimizeButton);
			}

			title.setBounds(BUTTON_SIZE, 0, x, BUTTON_SIZE);
		}
	}

	protected void showSystemMenu() {
		systemMenu.show(systemButton, 0, BUTTON_SIZE);
	}

	protected void hideSystemMenu() {
		systemMenu.setVisible(false);
	}

	static Dimension buttonDimension = new Dimension(BUTTON_SIZE, BUTTON_SIZE);

	private abstract class FrameButton extends JButton {
		FrameButton() {
			super();
			setFocusPainted(false);
			setBorderPainted(false);
		}

		@SuppressWarnings("deprecation")
		@Override
		public boolean isFocusTraversable() {
			return false;
		}

		@Override
		public void requestFocus() {
			// ignore request.
		}

		@Override
		public Dimension getMinimumSize() {
			return buttonDimension;
		}

		@Override
		public Dimension getPreferredSize() {
			return buttonDimension;
		}

		@Override
		public void paintComponent(Graphics g) {
			Dimension d = getSize();
			int maxX = d.width - 1;
			int maxY = d.height - 1;

			// draw background
			g.setColor(color);
			g.fillRect(1, 1, d.width, d.height);

			// draw border
			boolean pressed = getModel().isPressed();
			g.setColor(pressed ? shadow : highlight);
			g.drawLine(0, 0, maxX, 0);
			g.drawLine(0, 0, 0, maxY);
			g.setColor(pressed ? highlight : shadow);
			g.drawLine(1, maxY, maxX, maxY);
			g.drawLine(maxX, 1, maxX, maxY);
		}
	}

	private class MinimizeButton extends FrameButton {
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(highlight);
			g.drawLine(7, 8, 7, 11);
			g.drawLine(7, 8, 10, 8);
			g.setColor(shadow);
			g.drawLine(8, 11, 10, 11);
			g.drawLine(11, 9, 11, 11);
		}
	}

	private class MaximizeButton extends FrameButton {
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			int max = BUTTON_SIZE - 5;
			boolean isMaxed = (state & Frame.MAXIMIZED_BOTH) != 0;
			g.setColor(isMaxed ? shadow : highlight);
			g.drawLine(4, 4, 4, max);
			g.drawLine(4, 4, max, 4);
			g.setColor(isMaxed ? highlight : shadow);
			g.drawLine(5, max, max, max);
			g.drawLine(max, 5, max, max);
		}
	}

	private class SystemButton extends FrameButton {
		@Override
		public boolean isFocusTraversable() {
			return false;
		}

		@Override
		public void requestFocus() {
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(highlight);
			g.drawLine(4, 8, 4, 11);
			g.drawLine(4, 8, BUTTON_SIZE - 5, 8);
			g.setColor(shadow);
			g.drawLine(5, 11, BUTTON_SIZE - 5, 11);
			g.drawLine(BUTTON_SIZE - 5, 9, BUTTON_SIZE - 5, 11);
		}
	}

	private class Title extends FrameButton {
		Title(String title) {
			super();
			setText(title);
			setHorizontalAlignment(SwingConstants.CENTER);
			setBorder(BorderFactory.createBevelBorder(
					BevelBorder.RAISED,
					UIManager.getColor("activeCaptionBorder"),
					UIManager.getColor("inactiveCaptionBorder")));

			// Forward mouse events to titlebar for moves.
			addMouseMotionListener(new MouseMotionListener() {
				@Override
				public void mouseDragged(MouseEvent e) {
					forwardEventToParent(e);
				}

				@Override
				public void mouseMoved(MouseEvent e) {
					forwardEventToParent(e);
				}
			});
			addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					forwardEventToParent(e);
				}

				@Override
				public void mousePressed(MouseEvent e) {
					forwardEventToParent(e);
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					forwardEventToParent(e);
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					forwardEventToParent(e);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					forwardEventToParent(e);
				}
			});
		}

		private Point getRootLocation() {
			int px = 0;
			int py = 0;
			for(Component c = this; c != null; c = c.getParent()) {
				if(c instanceof Window) {
					break;
				}
				Point p = c.getLocation();
				px += p.x;
				py += p.y;
			}
			return new Point(px, py);
		}

		void forwardEventToParent(MouseEvent e) {
			Window win = getWindow();
			if(win != null) {
				Point pos = getRootLocation();
				MouseEvent newEvent = new MouseEvent(
						win, e.getID(), e.getWhen(), e.getModifiersEx(),
						e.getX() + pos.x, e.getY() + pos.y, e.getXOnScreen(),
						e.getYOnScreen(), e.getClickCount(),
						e.isPopupTrigger(), e.getButton());
				MouseInputListener listener = rootPaneUI.getMouseListener();
				if(listener != null) {
					switch(e.getID()) {
					case MouseEvent.MOUSE_CLICKED:
						listener.mouseClicked(newEvent);
						break;
					case MouseEvent.MOUSE_PRESSED:
						listener.mousePressed(newEvent);
						break;
					case MouseEvent.MOUSE_RELEASED:
						listener.mouseReleased(newEvent);
						break;
					case MouseEvent.MOUSE_ENTERED:
						listener.mouseEntered(newEvent);
						break;
					case MouseEvent.MOUSE_EXITED:
						listener.mouseExited(newEvent);
						break;
					case MouseEvent.MOUSE_MOVED:
						listener.mouseMoved(newEvent);
						break;
					case MouseEvent.MOUSE_DRAGGED:
						listener.mouseDragged(newEvent);
						break;
					}
				}
			} else {
				MouseEvent newEvent = new MouseEvent(
						getParent(), e.getID(), e.getWhen(), e.getModifiersEx(),
						e.getX(), e.getY(), e.getXOnScreen(),
						e.getYOnScreen(), e.getClickCount(),
						e.isPopupTrigger(), e.getButton());
				// MouseEventAccessor meAccessor = AWTAccessor.getMouseEventAccessor();
				// meAccessor.setCausedByTouchEvent(newEvent, meAccessor.isCausedByTouchEvent(e));
				getParent().dispatchEvent(newEvent);
			}
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			boolean isSelected = (window == null) ? true : window.isActive();
			if(isSelected) {
				g.setColor(UIManager.getColor("activeCaptionText"));
			} else {
				g.setColor(UIManager.getColor("inactiveCaptionText"));
			}
			Dimension d = getSize();
			String frameTitle = getTitle();
			if(frameTitle != null) {
				MotifGraphicsUtils.drawStringInRect(rootPane, g, frameTitle, 0, 0, d.width, d.height,
						SwingConstants.CENTER);
			}
		}
	}
}
