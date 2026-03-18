package com.unknown.plaf.windows.mixed;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.accessibility.AccessibleContext;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.UIResource;

import com.unknown.plaf.windows.mixed.TMSchema.Part;
import com.unknown.plaf.windows.mixed.TMSchema.Prop;
import com.unknown.plaf.windows.mixed.TMSchema.State;
import com.unknown.plaf.windows.mixed.XPStyle.Skin;
import com.unknown.util.ui.plaf.SunToolkit;
import com.unknown.util.ui.plaf.SwingUtilities2;

@SuppressWarnings("serial")
public class WindowsTitlePane extends JComponent {
	private static final int IMAGE_HEIGHT = 16;
	private static final int IMAGE_WIDTH = 16;

	private Action closeAction;
	private Action iconifyAction;
	private Action restoreAction;
	private Action maximizeAction;

	private PropertyChangeListener propertyChangeListener;

	private WindowListener windowListener;

	private Window window;

	private Image systemIcon;

	/**
	 * JRootPane rendering for.
	 */
	private JRootPane rootPane;

	private int state;
	private boolean active;

	private JButton iconButton;
	private JButton maxButton;
	private JButton closeButton;

	protected Icon maxIcon;
	protected Icon minIcon;
	protected Icon iconIcon;
	protected Icon closeIcon;

	private String closeButtonToolTip;
	private String iconButtonToolTip;
	private String restoreButtonToolTip;
	private String maxButtonToolTip;

	protected Color selectedTitleColor;
	protected Color selectedTextColor;
	protected Color notSelectedTitleColor;
	protected Color notSelectedTextColor;

	private WindowsRootPaneUI rootPaneUI;

	public WindowsTitlePane(JRootPane root, WindowsRootPaneUI rootPaneUI) {
		this.rootPane = root;
		this.rootPaneUI = rootPaneUI;

		state = -1;

		installDefaults();
		createActions();
		createButtons();
		assembleSystemMenu();
		addSubComponents();

		setLayout(createLayout());

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
			MouseEvent newEvent = new MouseEvent(getParent(), e.getID(), e.getWhen(), e.getModifiersEx(),
					e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(), e.getClickCount(),
					e.isPopupTrigger(), e.getButton());
			// MouseEventAccessor meAccessor = AWTAccessor.getMouseEventAccessor();
			// meAccessor.setCausedByTouchEvent(newEvent, meAccessor.isCausedByTouchEvent(e));
			getParent().dispatchEvent(newEvent);
		}
	}

	private Color selectedTitleGradientColor;
	private Color notSelectedTitleGradientColor;
	private JPopupMenu systemPopupMenu;
	private JLabel systemLabel;

	private Font titleFont;
	private int titlePaneHeight;
	private int buttonWidth, buttonHeight;
	private boolean hotTrackingOn;

	private void installListeners() {
		if(window != null) {
			windowListener = createWindowListener();
			window.addWindowListener(windowListener);
			propertyChangeListener = createWindowPropertyChangeListener();
			window.addPropertyChangeListener(propertyChangeListener);
		}
	}

	private void uninstallListeners() {
		if(window != null) {
			window.removeWindowListener(windowListener);
			window.removePropertyChangeListener(propertyChangeListener);
		}
	}

	private PropertyChangeListener createWindowPropertyChangeListener() {
		return new WindowsPropertyChangeHandler();
	}

	private WindowListener createWindowListener() {
		return new WindowHandler();
	}

	@Override
	public JRootPane getRootPane() {
		return rootPane;
	}

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
			updateSystemIcon();
		}
	}

	@Override
	public void removeNotify() {
		super.removeNotify();

		uninstallListeners();
		window = null;
	}

	private void close() {
		Window win = getWindow();

		if(win != null) {
			win.dispatchEvent(new WindowEvent(win, WindowEvent.WINDOW_CLOSING));
		}
	}

	private void iconify() {
		Frame frame = getFrame();
		if(frame != null) {
			frame.setExtendedState(state | Frame.ICONIFIED);
		}
	}

	private void maximize() {
		Frame frame = getFrame();
		if(frame != null) {
			frame.setExtendedState(state | Frame.MAXIMIZED_BOTH);
			setState(state | Frame.MAXIMIZED_BOTH);
		}
	}

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

	private void setActive(boolean isActive) {
		// Repaint the whole thing as the Borders that are used have
		// different colors for active vs inactive
		this.active = isActive;
		getRootPane().repaint();
	}

	private void setState(int state) {
		setState(state, false);
	}

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
					WindowsRootPaneUI.installBorder(root);
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
			setButtonIcons();
		}
	}

	private Frame getFrame() {
		Window w = getWindow();

		if(w instanceof Frame) {
			return (Frame) w;
		}
		return null;
	}

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

	protected boolean isClosable() {
		Window win = getWindow();
		if(win == null) {
			return false;
		} else if(win instanceof Frame) {
			return true;
		} else if(win instanceof Dialog) {
			return true;
		} else {
			return false;
		}
	}

	protected boolean isSelected() {
		return active;
	}

	protected boolean isMaximum() {
		return (state & Frame.MAXIMIZED_BOTH) != 0;
	}

	protected boolean isIcon() {
		return (state & Frame.ICONIFIED) != 0;
	}

	protected Icon getFrameIcon() {
		if(systemIcon == null) {
			return null;
		} else {
			return new ImageIcon(systemIcon);
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
			} else if("componentOrientation".equals(name)) {
				revalidate();
				repaint();
			} else if("iconImage".equals(name)) {
				updateSystemIcon();
				revalidate();
				repaint();
			}
		}
	}

	private void updateSystemIcon() {
		Window win = getWindow();
		if(win == null) {
			systemIcon = null;
			return;
		}
		List<Image> icons = win.getIconImages();
		assert icons != null;

		if(icons.size() == 0) {
			systemIcon = null;
		} else if(icons.size() == 1) {
			systemIcon = icons.get(0);
		} else {
			systemIcon = SunToolkit.getScaledIconImage(icons, IMAGE_WIDTH, IMAGE_HEIGHT);
		}

		if(systemLabel != null) {
			systemLabel.setIcon(getFrameIcon());
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

	protected void addSubComponents() {
		add(systemLabel);
		add(iconButton);
		add(maxButton);
		add(closeButton);
	}

	protected void installDefaults() {
		maxIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
		minIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
		iconIcon = UIManager.getIcon("InternalFrame.iconifyIcon");
		closeIcon = UIManager.getIcon("InternalFrame.closeIcon");

		selectedTitleColor = UIManager.getColor("InternalFrame.activeTitleBackground");
		selectedTextColor = UIManager.getColor("InternalFrame.activeTitleForeground");
		notSelectedTitleColor = UIManager.getColor("InternalFrame.inactiveTitleBackground");
		notSelectedTextColor = UIManager.getColor("InternalFrame.inactiveTitleForeground");
		setFont(UIManager.getFont("InternalFrame.titleFont"));
		closeButtonToolTip = UIManager.getString("InternalFrame.closeButtonToolTip");
		iconButtonToolTip = UIManager.getString("InternalFrame.iconButtonToolTip");
		restoreButtonToolTip = UIManager.getString("InternalFrame.restoreButtonToolTip");
		maxButtonToolTip = UIManager.getString("InternalFrame.maxButtonToolTip");

		selectedTitleColor = UIManager.getColor("InternalFrame.activeTitleBackground");
		selectedTextColor = UIManager.getColor("InternalFrame.activeTitleForeground");
		notSelectedTitleColor = UIManager.getColor("InternalFrame.inactiveTitleBackground");
		notSelectedTextColor = UIManager.getColor("InternalFrame.inactiveTitleForeground");

		titlePaneHeight = UIManager.getInt("InternalFrame.titlePaneHeight");
		buttonHeight = UIManager.getInt("InternalFrame.titleButtonHeight") - 4;

		Object obj = UIManager.get("InternalFrame.titleButtonToolTipsOn");
		hotTrackingOn = (obj instanceof Boolean) ? (Boolean) obj : true;

		if(XPStyle.getXP() != null) {
			// Fix for XP bug where sometimes these sizes aren't updated properly
			// Assume for now that height is correct and derive width from height
			buttonWidth = buttonHeight + 14;
		} else {
			buttonWidth = buttonHeight + 2;
		}
		// JDK-8039383: initialize these colors because getXP() may return null when theme is changed
		selectedTitleGradientColor = UIManager.getColor("InternalFrame.activeTitleGradient");
		notSelectedTitleGradientColor = UIManager.getColor("InternalFrame.inactiveTitleGradient");
	}

	protected void createButtons() {
		iconButton = new NoFocusButton("InternalFrameTitlePane.iconifyButtonAccessibleName",
				"InternalFrameTitlePane.iconifyButtonOpacity");
		iconButton.addActionListener(iconifyAction);
		if(iconButtonToolTip != null && iconButtonToolTip.length() != 0) {
			iconButton.setToolTipText(iconButtonToolTip);
		}

		maxButton = new NoFocusButton("InternalFrameTitlePane.maximizeButtonAccessibleName",
				"InternalFrameTitlePane.maximizeButtonOpacity");
		maxButton.addActionListener(maximizeAction);

		closeButton = new NoFocusButton("InternalFrameTitlePane.closeButtonAccessibleName",
				"InternalFrameTitlePane.closeButtonOpacity");
		closeButton.addActionListener(closeAction);
		if(closeButtonToolTip != null && closeButtonToolTip.length() != 0) {
			closeButton.setToolTipText(closeButtonToolTip);
		}

		setButtonIcons();

		if(XPStyle.getXP() != null) {
			iconButton.setContentAreaFilled(false);
			maxButton.setContentAreaFilled(false);
			closeButton.setContentAreaFilled(false);
		}
	}

	protected void setButtonIcons() {
		if(isIcon()) {
			if(minIcon != null) {
				iconButton.setIcon(minIcon);
			}
			if(restoreButtonToolTip != null && restoreButtonToolTip.length() != 0) {
				iconButton.setToolTipText(restoreButtonToolTip);
			}
			if(maxIcon != null) {
				maxButton.setIcon(maxIcon);
			}
			if(maxButtonToolTip != null && maxButtonToolTip.length() != 0) {
				maxButton.setToolTipText(maxButtonToolTip);
			}
		} else if(isMaximum()) {
			if(iconIcon != null) {
				iconButton.setIcon(iconIcon);
			}
			if(iconButtonToolTip != null && iconButtonToolTip.length() != 0) {
				iconButton.setToolTipText(iconButtonToolTip);
			}
			if(minIcon != null) {
				maxButton.setIcon(minIcon);
			}
			if(restoreButtonToolTip != null && restoreButtonToolTip.length() != 0) {
				maxButton.setToolTipText(restoreButtonToolTip);
			}
		} else {
			if(iconIcon != null) {
				iconButton.setIcon(iconIcon);
			}
			if(iconButtonToolTip != null && iconButtonToolTip.length() != 0) {
				iconButton.setToolTipText(iconButtonToolTip);
			}
			if(maxIcon != null) {
				maxButton.setIcon(maxIcon);
			}
			if(maxButtonToolTip != null && maxButtonToolTip.length() != 0) {
				maxButton.setToolTipText(maxButtonToolTip);
			}
		}
		if(closeIcon != null) {
			closeButton.setIcon(closeIcon);
		}

		if(!hotTrackingOn) {
			iconButton.setToolTipText(null);
			maxButton.setToolTipText(null);
			closeButton.setToolTipText(null);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		if(getFrame() != null) {
			setState(getFrame().getExtendedState());
		}

		XPStyle xp = XPStyle.getXP();

		paintTitleBackground(g);

		Window frame = getWindow();
		String title = getTitle();
		if(title != null) {
			boolean isSelected = isSelected();
			Font oldFont = g.getFont();
			Font newFont = (titleFont != null) ? titleFont : getFont();
			g.setFont(newFont);

			// Center text vertically.
			FontMetrics fm = SwingUtilities2.getFontMetrics(rootPane, g, newFont);
			int baseline = (getHeight() + fm.getAscent() - fm.getLeading() - fm.getDescent()) / 2;

			Rectangle lastIconBounds = new Rectangle(0, 0, 0, 0);
			if(isIconifiable()) {
				lastIconBounds = iconButton.getBounds();
			} else if(isMaximizable()) {
				lastIconBounds = maxButton.getBounds();
			} else if(isClosable()) {
				lastIconBounds = closeButton.getBounds();
			}

			int titleX;
			int titleW;
			int gap = 2;
			if(WindowsGraphicsUtils.isLeftToRight(frame)) {
				if(lastIconBounds.x == 0) { // There are no icons
					lastIconBounds.x = frame.getWidth() - frame.getInsets().right;
				}
				if(getFrameIcon() == null) {
					titleX = gap;
				} else {
					titleX = systemLabel.getX() + systemLabel.getWidth() + gap;
				}
				if(xp != null) {
					titleX += 2;
				}
				titleW = lastIconBounds.x - titleX - gap;
			} else {
				if(lastIconBounds.x == 0) { // There are no icons
					lastIconBounds.x = frame.getInsets().left;
				}
				titleW = SwingUtilities2.stringWidth(rootPane, fm, title);
				int minTitleX = lastIconBounds.x + lastIconBounds.width + gap;
				if(xp != null) {
					minTitleX += 2;
				}
				int availableWidth = systemLabel.getX() - gap - minTitleX;
				if(availableWidth > titleW) {
					titleX = systemLabel.getX() - gap - titleW;
				} else {
					titleX = minTitleX;
					titleW = availableWidth;
				}
			}
			// title = getTitle(getTitle(), fm, titleW);

			if(xp != null) {
				String shadowType = null;
				if(isSelected) {
					shadowType = XPStyle.getString(this, Part.WP_CAPTION, State.ACTIVE,
							Prop.TEXTSHADOWTYPE);
				}
				if("single".equalsIgnoreCase(shadowType)) {
					Point shadowOffset = XPStyle.getPoint(this, Part.WP_WINDOW, State.ACTIVE,
							Prop.TEXTSHADOWOFFSET);
					Color shadowColor = xp.getColor(this, Part.WP_WINDOW, State.ACTIVE,
							Prop.TEXTSHADOWCOLOR, null);
					if(shadowOffset != null && shadowColor != null) {
						g.setColor(shadowColor);
						SwingUtilities2.drawString(rootPane, g, title,
								titleX + shadowOffset.x,
								baseline + shadowOffset.y);
					}
				}
			}
			g.setColor(isSelected ? selectedTextColor : notSelectedTextColor);
			SwingUtilities2.drawString(rootPane, g, title, titleX, baseline);
			g.setFont(oldFont);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	@Override
	public Dimension getMinimumSize() {
		Dimension d = new Dimension(super.getMinimumSize());
		d.height = titlePaneHeight + 2;

		XPStyle xp = XPStyle.getXP();
		if(xp != null) {
			// Note: Don't know how to calculate height on XP,
			// the captionbarheight is 25 but native caption is 30 (maximized 26)
			if(isMaximum()) {
				d.height -= 1;
			} else {
				d.height += 3;
			}
		}
		return d;
	}

	protected void paintTitleBackground(Graphics g) {
		XPStyle xp = XPStyle.getXP();
		if(xp != null) {
			Part part = isIcon() ? Part.WP_MINCAPTION
					: (isMaximum() ? Part.WP_MAXCAPTION : Part.WP_CAPTION);
			State st = isSelected() ? State.ACTIVE : State.INACTIVE;
			Skin skin = xp.getSkin(this, part);
			skin.paintSkin(g, 0, 0, getWidth(), getHeight(), st);
		} else {
			Boolean gradientsOn = (Boolean) LookAndFeel.getDesktopPropertyValue(
					"win.frame.captionGradientsOn", Boolean.valueOf(false));
			if(gradientsOn.booleanValue() && g instanceof Graphics2D) {
				Graphics2D g2 = (Graphics2D) g;
				Paint savePaint = g2.getPaint();

				boolean isSelected = isSelected();
				int w = getWidth();

				if(isSelected) {
					GradientPaint titleGradient = new GradientPaint(0, 0, selectedTitleColor,
							(int) (w * .75), 0, selectedTitleGradientColor);
					g2.setPaint(titleGradient);
				} else {
					GradientPaint titleGradient = new GradientPaint(0, 0, notSelectedTitleColor,
							(int) (w * .75), 0, notSelectedTitleGradientColor);
					g2.setPaint(titleGradient);
				}
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.setPaint(savePaint);
			} else {
				boolean isSelected = isSelected();

				if(isSelected) {
					g.setColor(selectedTitleColor);
				} else {
					g.setColor(notSelectedTitleColor);
				}
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		}
	}

	protected void assembleSystemMenu() {
		systemPopupMenu = new JPopupMenu();
		addSystemMenuItems(systemPopupMenu);
		// enableActions();
		JLabel tmp = new JLabel(getFrameIcon()) {
			@Override
			protected void paintComponent(Graphics gx) {
				int x = 0;
				int y = 0;
				int w = getWidth();
				int h = getHeight();
				Graphics g = gx.create();  // Create scratch graphics
				if(isOpaque()) {
					g.setColor(getBackground());
					g.fillRect(0, 0, w, h);
				}
				Icon icon = getIcon();
				int iconWidth;
				int iconHeight;
				if(icon != null && (iconWidth = icon.getIconWidth()) > 0 &&
						(iconHeight = icon.getIconHeight()) > 0) {
					// Set drawing scale to make icon scale to our desired size
					double drawScale;
					if(iconWidth > iconHeight) {
						// Center icon vertically
						y = (h - w * iconHeight / iconWidth) / 2;
						drawScale = w / (double) iconWidth;
					} else {
						// Center icon horizontally
						x = (w - h * iconWidth / iconHeight) / 2;
						drawScale = h / (double) iconHeight;
					}
					((Graphics2D) g).translate(x, y);
					((Graphics2D) g).scale(drawScale, drawScale);
					icon.paintIcon(this, g, 0, 0);
				}
				g.dispose();
			}
		};
		systemLabel = tmp;
		systemLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2 && isClosable() && !isIcon()) {
					systemPopupMenu.setVisible(false);
					close();
				} else {
					super.mouseClicked(e);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				showSystemPopupMenu(e.getComponent());
			}
		});
	}

	protected void addSystemMenuItems(JPopupMenu menu) {
		if(getWindowDecorationStyle() == JRootPane.FRAME) {
			JMenuItem mi = menu.add(restoreAction);
			mi.setMnemonic(getButtonMnemonic("restore"));
			// mi = menu.add(moveAction);
			// mi.setMnemonic(getButtonMnemonic("move"));
			// mi = menu.add(sizeAction);
			// mi.setMnemonic(getButtonMnemonic("size"));
			mi = menu.add(iconifyAction);
			mi.setMnemonic(getButtonMnemonic("minimize"));
			mi = menu.add(maximizeAction);
			mi.setMnemonic(getButtonMnemonic("maximize"));
			menu.add(new JSeparator());
		}
		JMenuItem mi = menu.add(closeAction);
		mi.setMnemonic(getButtonMnemonic("close"));
	}

	private static int getButtonMnemonic(String button) {
		try {
			return Integer.parseInt(UIManager.getString(
					"InternalFrameTitlePane." + button + "Button.mnemonic"));
		} catch(NumberFormatException e) {
			return -1;
		}
	}

	protected void showSystemMenu() {
		showSystemPopupMenu(systemLabel);
	}

	private void showSystemPopupMenu(Component invoker) {
		Dimension dim = new Dimension();
		Border border = rootPane.getBorder();
		if(border != null) {
			dim.width += border.getBorderInsets(rootPane).left + border.getBorderInsets(rootPane).right;
			dim.height += border.getBorderInsets(rootPane).bottom + border.getBorderInsets(rootPane).top;
		}
		if(!isIcon()) {
			systemPopupMenu.show(invoker, getX() - dim.width, getY() + getHeight() - dim.height);
		} else {
			systemPopupMenu.show(invoker, getX() - dim.width,
					getY() - systemPopupMenu.getPreferredSize().height - dim.height);
		}
	}

	protected PropertyChangeListener createPropertyChangeListener() {
		return new WindowsPropertyChangeHandler();
	}

	protected LayoutManager createLayout() {
		return new WindowsTitlePaneLayout();
	}

	public final class WindowsTitlePaneLayout implements LayoutManager {
		private Insets captionMargin = null;
		private Insets contentMargin = null;
		private XPStyle xp = XPStyle.getXP();

		WindowsTitlePaneLayout() {
			if(xp != null) {
				Component c = WindowsTitlePane.this;
				captionMargin = XPStyle.getMargin(c, Part.WP_CAPTION, null, Prop.CAPTIONMARGINS);
				contentMargin = XPStyle.getMargin(c, Part.WP_CAPTION, null, Prop.CONTENTMARGINS);
			}
			if(captionMargin == null) {
				captionMargin = new Insets(0, 2, 0, 2);
			}
			if(contentMargin == null) {
				contentMargin = new Insets(0, 0, 0, 0);
			}
		}

		@Override
		public void addLayoutComponent(String name, Component c) {
		}

		@Override
		public void removeLayoutComponent(Component c) {
		}

		@Override
		public Dimension preferredLayoutSize(Container c) {
			int height = computeHeight();
			return new Dimension(height, height);
		}

		@Override
		public Dimension minimumLayoutSize(Container c) {
			return preferredLayoutSize(c);
		}

		private int computeHeight() {
			FontMetrics fm = rootPane.getFontMetrics(getFont());
			int fontHeight = fm.getHeight();
			fontHeight += 7;
			int iconHeight = 0;
			if(getWindowDecorationStyle() == JRootPane.FRAME) {
				iconHeight = IMAGE_HEIGHT;
			}

			int finalHeight = Math.max(fontHeight, iconHeight);
			return finalHeight;
		}

		private int layoutButton(JComponent button, int x, int y, int w, int h, boolean leftToRight) {
			int px = x;
			if(!leftToRight) {
				px -= w;
			}
			button.setBounds(px, y, w, h);
			if(leftToRight) {
				px += w + 2;
			} else {
				px -= 2;
			}
			return px;
		}

		@Override
		public void layoutContainer(Container c) {
			boolean leftToRight = WindowsGraphicsUtils.isLeftToRight(rootPane);
			int x, y;
			int w = getWidth();
			int h = getHeight();

			// System button
			// Note: this icon is square, but the buttons aren't always.
			int iconSize = (xp != null) ? (h - 2) * 6 / 10 : h - 4;
			if(xp != null) {
				x = (leftToRight) ? captionMargin.left + 2 : w - captionMargin.right - 2;
			} else {
				x = (leftToRight) ? captionMargin.left : w - captionMargin.right;
			}
			y = (h - iconSize) / 2;
			layoutButton(systemLabel, x, y, iconSize, iconSize, leftToRight);

			// Right hand buttons
			if(xp != null) {
				x = (leftToRight) ? w - captionMargin.right - 2 : captionMargin.left + 2;
				y = 1;  // XP seems to ignore margins and offset here
				if(isMaximum()) {
					y += 1;
				} else {
					y += 5;
				}
			} else {
				x = (leftToRight) ? w - captionMargin.right : captionMargin.left;
				y = (h - buttonHeight) / 2;
			}

			if(isClosable()) {
				x = layoutButton(closeButton, x, y, buttonWidth, buttonHeight, !leftToRight);
			}

			if(isMaximizable()) {
				x = layoutButton(maxButton, x, y, buttonWidth, buttonHeight, !leftToRight);
			}

			if(isIconifiable()) {
				layoutButton(iconButton, x, y, buttonWidth, buttonHeight, !leftToRight);
			}
		}
	} // end WindowsTitlePaneLayout

	public final class WindowsPropertyChangeHandler extends PropertyChangeHandler {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			String prop = evt.getPropertyName();

			System.out.println("prop=" + prop + ", systemLabel=" + systemLabel);
			// Update the internal frame icon for the system menu.
			if("iconImage".equals(prop) && systemLabel != null) {
				systemLabel.setIcon(getFrameIcon());
			}

			super.propertyChange(evt);
		}
	}

	/**
	 * A versatile Icon implementation which can take an array of Icon instances (typically <code>ImageIcon</code>s)
	 * and choose one that gives the best quality for a given Graphics2D scale factor when painting.
	 * <p>
	 * The class is public so it can be instantiated by UIDefaults.ProxyLazyValue.
	 * <p>
	 * Note: We assume here that icons are square.
	 */
	public static final class ScalableIconUIResource implements Icon, UIResource {
		// We can use an arbitrary size here because we scale to it in paintIcon()
		private static final int SIZE = 16;

		private Icon[] icons;

		/**
		 * @param objects
		 *                an array of Icon or UIDefaults.LazyValue
		 *                <p>
		 *                The constructor is public so it can be called by UIDefaults.ProxyLazyValue.
		 */
		public ScalableIconUIResource(Object[] objects) {
			this.icons = new Icon[objects.length];

			for(int i = 0; i < objects.length; i++) {
				if(objects[i] instanceof UIDefaults.LazyValue) {
					icons[i] = (Icon) ((UIDefaults.LazyValue) objects[i]).createValue(null);
				} else {
					icons[i] = (Icon) objects[i];
				}
			}
		}

		/**
		 * @return the <code>Icon</code> closest to the requested size
		 */
		protected Icon getBestIcon(int size) {
			if(icons != null && icons.length > 0) {
				int bestIndex = 0;
				int minDiff = Integer.MAX_VALUE;
				for(int i = 0; i < icons.length; i++) {
					Icon icon = icons[i];
					int iconSize;
					if(icon != null && (iconSize = icon.getIconWidth()) > 0) {
						int diff = Math.abs(iconSize - size);
						if(diff < minDiff) {
							minDiff = diff;
							bestIndex = i;
						}
					}
				}
				return icons[bestIndex];
			} else {
				return null;
			}
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2d = (Graphics2D) g.create();
			// Calculate how big our drawing area is in pixels
			// Assume we are square
			int size = getIconWidth();
			double scale = g2d.getTransform().getScaleX();
			Icon icon = getBestIcon((int) (size * scale));
			int iconSize;
			if(icon != null && (iconSize = icon.getIconWidth()) > 0) {
				// Set drawing scale to make icon act true to our reported size
				double drawScale = size / (double) iconSize;
				g2d.translate(x, y);
				g2d.scale(drawScale, drawScale);
				icon.paintIcon(c, g2d, 0, 0);
			}
			g2d.dispose();
		}

		@Override
		public int getIconWidth() {
			return SIZE;
		}

		@Override
		public int getIconHeight() {
			return SIZE;
		}
	}

	private static class NoFocusButton extends JButton {
		private String uiKey;

		public NoFocusButton(String uiKey, String opacityKey) {
			setFocusPainted(false);
			setMargin(new Insets(0, 0, 0, 0));
			this.uiKey = uiKey;

			Object opacity = UIManager.get(opacityKey);
			if(opacity instanceof Boolean) {
				setOpaque(((Boolean) opacity).booleanValue());
			}
		}

		@SuppressWarnings("deprecation")
		@Override
		public boolean isFocusTraversable() {
			return false;
		}

		@Override
		public void requestFocus() {
		}

		@Override
		public AccessibleContext getAccessibleContext() {
			AccessibleContext ac = super.getAccessibleContext();
			if(uiKey != null) {
				ac.setAccessibleName(UIManager.getString(uiKey));
				uiKey = null;
			}
			return ac;
		}
	}
}
