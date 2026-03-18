/*
 * Copyright (c) 2006, 2026, Oracle and/or its affiliates. All rights reserved.
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

import static com.unknown.plaf.windows.mixed.TMSchema.State.UPDISABLED;
import static com.unknown.plaf.windows.mixed.TMSchema.State.UPHOT;
import static com.unknown.plaf.windows.mixed.TMSchema.State.UPNORMAL;
import static com.unknown.plaf.windows.mixed.TMSchema.State.UPPRESSED;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIClientPropertyKey;
import javax.swing.UIManager;

import com.unknown.plaf.windows.mixed.TMSchema.Part;
import com.unknown.plaf.windows.mixed.TMSchema.Prop;
import com.unknown.plaf.windows.mixed.TMSchema.State;
import com.unknown.plaf.windows.mixed.XPStyle.Skin;

/**
 * A class to help mimic Vista theme animations. The only kind of animation it handles for now is 'transition' animation
 * (this seems to be the only animation which Vista theme can do). This is when one picture fadein over another one in
 * some period of time. According to https://connect.microsoft.com/feedback/ViewFeedback.aspx?FeedbackID=86852&SiteID=4
 * The animations are all linear.
 *
 * This class has a number of responsibilities.
 * <ul>
 * <li>It trigger rapaint for the UI components involved in the animation
 * <li>It tracks the animation state for every UI component involved in the animation and paints {@code Skin} in new
 * {@code State} over the {@code Skin} in last {@code State} using {@code AlphaComposite.SrcOver.derive(alpha)} where
 * {@code alpha} depends on the state of animation
 * </ul>
 *
 * @author Igor Kushnirskiy
 */
final class AnimationController implements ActionListener, PropertyChangeListener {

	private static final boolean VISTA_ANIMATION_DISABLED = Boolean.getBoolean("swing.disablevistaanimation");

	private static AnimationController animationController;

	private final Map<JComponent, Map<Part, AnimationState>> animationStateMap = new WeakHashMap<>();

	// this timer is used to cause repaint on animated components
	// 30 repaints per second should give smooth animation affect
	private final Timer timer = new Timer(1000 / 30, this);

	private static synchronized AnimationController getAnimationController() {
		if(animationController == null) {
			animationController = new AnimationController();
		}
		return animationController;
	}

	private AnimationController() {
		timer.setRepeats(true);
		timer.setCoalesce(true);
		// we need to dispose the controller on l&f change
		UIManager.addPropertyChangeListener(this);
	}

	private static void triggerAnimation(JComponent c, Part part, State newState) {
		if(c instanceof javax.swing.JTabbedPane || part == Part.TP_BUTTON) {
			// idk: we can not handle tabs animation because
			// the same (component,part) is used to handle all the tabs
			// and we can not track the states
			// Vista theme might have transition duration for toolbar buttons
			// but native application does not seem to animate them
			return;
		}
		AnimationController controller = AnimationController.getAnimationController();
		State oldState = controller.getState(c, part);
		if(oldState != newState) {
			controller.putState(c, part, newState);
			if(newState == State.DEFAULTED) {
				// it seems for DEFAULTED button state Vista does animation from
				// HOT
				oldState = State.HOT;
			}
			if(oldState != null) {
				long duration;
				if(newState == State.DEFAULTED) {
					// Only button might have DEFAULTED state
					// idk: do not know how to get the value from Vista
					// one second seems plausible value
					duration = 1000;
				} else {
					XPStyle xp = XPStyle.getXP();
					duration = (xp != null)
							? XPStyle.getThemeTransitionDuration(c, part,
									normalizeState(oldState),
									normalizeState(newState),
									Prop.TRANSITIONDURATIONS)
							: 1000;
				}
				controller.startAnimation(c, part, oldState, newState, duration);
			}
		}
	}

	// for scrollbar up, down, left and right button pictures are
	// defined by states. It seems that theme has duration defined
	// only for up button states thus we doing this translation here.
	private static State normalizeState(State state) {
		State rv;
		switch(state) {
		case DOWNPRESSED:
			/* falls through */
		case LEFTPRESSED:
			/* falls through */
		case RIGHTPRESSED:
			rv = UPPRESSED;
			break;

		case DOWNDISABLED:
			/* falls through */
		case LEFTDISABLED:
			/* falls through */
		case RIGHTDISABLED:
			rv = UPDISABLED;
			break;

		case DOWNHOT:
			/* falls through */
		case LEFTHOT:
			/* falls through */
		case RIGHTHOT:
			rv = UPHOT;
			break;

		case DOWNNORMAL:
			/* falls through */
		case LEFTNORMAL:
			/* falls through */
		case RIGHTNORMAL:
			rv = UPNORMAL;
			break;

		default:
			rv = state;
			break;
		}
		return rv;
	}

	private synchronized State getState(JComponent component, Part part) {
		State rv = null;
		Object tmpObject = component.getClientProperty(PartUIClientPropertyKey.getKey(part));
		if(tmpObject instanceof State) {
			rv = (State) tmpObject;
		}
		return rv;
	}

	private synchronized void putState(JComponent component, Part part, State state) {
		component.putClientProperty(PartUIClientPropertyKey.getKey(part),
				state);
	}

	private synchronized void startAnimation(JComponent component, Part part, State startState, State endState,
			long millis) {
		boolean isForwardAndReverse = false;
		if(endState == State.DEFAULTED) {
			isForwardAndReverse = true;
		}
		Map<Part, AnimationState> map = animationStateMap.get(component);
		if(millis <= 0) {
			if(map != null) {
				map.remove(part);
				if(map.size() == 0) {
					animationStateMap.remove(component);
				}
			}
			return;
		}
		if(map == null) {
			map = new EnumMap<>(Part.class);
			animationStateMap.put(component, map);
		}
		map.put(part, new AnimationState(startState, millis, isForwardAndReverse));
		if(!timer.isRunning()) {
			timer.start();
		}
	}

	static void paintSkin(JComponent component, Skin skin, Graphics g, int dx, int dy, int dw, int dh,
			State state) {
		if(VISTA_ANIMATION_DISABLED) {
			skin.paintSkinRaw(g, dx, dy, dw, dh, state);
			return;
		}
		triggerAnimation(component, skin.part, state);
		AnimationController controller = getAnimationController();
		synchronized(controller) {
			AnimationState animationState = null;
			Map<Part, AnimationState> map = controller.animationStateMap.get(component);
			if(map != null) {
				animationState = map.get(skin.part);
			}
			if(animationState != null) {
				animationState.paintSkin(skin, g, dx, dy, dw, dh, state);
			} else {
				skin.paintSkinRaw(g, dx, dy, dw, dh, state);
			}
		}
	}

	@Override
	public synchronized void propertyChange(PropertyChangeEvent e) {
		if("lookAndFeel" == e.getPropertyName() && !(e.getNewValue() instanceof WindowsLookAndFeel)) {
			dispose();
		}
	}

	@Override
	public synchronized void actionPerformed(ActionEvent e) {
		List<JComponent> componentsToRemove = null;
		List<Part> partsToRemove = null;
		for(JComponent component : animationStateMap.keySet()) {
			component.repaint();
			if(partsToRemove != null) {
				partsToRemove.clear();
			}
			Map<Part, AnimationState> map = animationStateMap.get(component);
			if(!component.isShowing() || map == null || map.size() == 0) {
				if(componentsToRemove == null) {
					componentsToRemove = new ArrayList<>();
				}
				componentsToRemove.add(component);
				continue;
			}
			for(Part part : map.keySet()) {
				if(map.get(part).isDone()) {
					if(partsToRemove == null) {
						partsToRemove = new ArrayList<>();
					}
					partsToRemove.add(part);
				}
			}
			if(partsToRemove != null) {
				if(partsToRemove.size() == map.size()) {
					// animation is done for the component
					if(componentsToRemove == null) {
						componentsToRemove = new ArrayList<>();
					}
					componentsToRemove.add(component);
				} else {
					for(Part part : partsToRemove) {
						map.remove(part);
					}
				}
			}
		}
		if(componentsToRemove != null) {
			for(JComponent component : componentsToRemove) {
				animationStateMap.remove(component);
			}
		}
		if(animationStateMap.size() == 0) {
			timer.stop();
		}
	}

	private synchronized void dispose() {
		timer.stop();
		UIManager.removePropertyChangeListener(this);
		synchronized(AnimationController.class) {
			animationController = null;
		}
	}

	private static final class AnimationState {
		private final State startState;

		// animation duration in nanoseconds
		private final long duration;

		// animatin start time in nanoseconds
		private long startTime;

		// direction the alpha value is changing
		// forward - from 0 to 1
		// !forward - from 1 to 0
		private boolean isForward = true;

		// if isForwardAndReverse the animation continually goes
		// forward and reverse. alpha value is changing from 0 to 1 then
		// from 1 to 0 and so forth
		private boolean isForwardAndReverse;

		private float progress;

		AnimationState(final State startState, final long milliseconds, boolean isForwardAndReverse) {
			assert startState != null && milliseconds > 0;
			assert SwingUtilities.isEventDispatchThread();

			this.startState = startState;
			this.duration = milliseconds * 1000000;
			this.startTime = System.nanoTime();
			this.isForwardAndReverse = isForwardAndReverse;
			progress = 0f;
		}

		private void updateProgress() {
			assert SwingUtilities.isEventDispatchThread();

			if(isDone()) {
				return;
			}
			long currentTime = System.nanoTime();

			progress = ((float) (currentTime - startTime)) / duration;
			progress = Math.max(progress, 0); // in case time was reset
			if(progress >= 1) {
				progress = 1;
				if(isForwardAndReverse) {
					startTime = currentTime;
					progress = 0;
					isForward = !isForward;
				}
			}
		}

		void paintSkin(Skin skin, Graphics _g, int dx, int dy, int dw, int dh, State state) {
			assert SwingUtilities.isEventDispatchThread();

			updateProgress();
			if(!isDone()) {
				Graphics2D g = (Graphics2D) _g.create();
				if(skin.haveToSwitchStates()) {
					skin.paintSkinRaw(g, dx, dy, dw, dh, state);
					g.setComposite(AlphaComposite.SrcOver.derive(1 - progress));
					skin.paintSkinRaw(g, dx, dy, dw, dh, startState);
				} else {
					skin.paintSkinRaw(g, dx, dy, dw, dh, startState);
					float alpha;
					if(isForward) {
						alpha = progress;
					} else {
						alpha = 1 - progress;
					}
					g.setComposite(AlphaComposite.SrcOver.derive(alpha));
					skin.paintSkinRaw(g, dx, dy, dw, dh, state);
				}
				g.dispose();
			} else {
				skin.paintSkinRaw(_g, dx, dy, dw, dh, state);
				skin.switchStates(false);
			}
		}

		boolean isDone() {
			assert SwingUtilities.isEventDispatchThread();

			return progress >= 1;
		}
	}

	private static final class PartUIClientPropertyKey implements UIClientPropertyKey {
		private static final Map<Part, PartUIClientPropertyKey> map = new EnumMap<>(Part.class);

		static synchronized PartUIClientPropertyKey getKey(Part part) {
			PartUIClientPropertyKey rv = map.get(part);
			if(rv == null) {
				rv = new PartUIClientPropertyKey(part);
				map.put(part, rv);
			}
			return rv;
		}

		private final Part part;

		private PartUIClientPropertyKey(Part part) {
			this.part = part;
		}

		@Override
		public String toString() {
			return part.toString();
		}
	}
}
