package com.unknown.util.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class LabeledPairLayout implements LayoutManager {
	public static final String LABEL = "label";
	public static final String COMPONENT = "component";

	private Vector<Component> labels = new Vector<>();
	private Vector<Component> fields = new Vector<>();

	int yGap = 2;
	int xGap = 2;

	private LayoutGroup group;

	public LabeledPairLayout() {
		group = null;
	}

	public LabeledPairLayout(LayoutGroup group) {
		this.group = group;
		group.add(this);
	}

	public LabeledPairLayout(LabeledPairLayout other) {
		if(other != null && other.group != null) {
			group = other.group;
			group.add(this);
		} else {
			group = new LayoutGroup();
			group.add(this);
			if(other != null) {
				other.group = group;
				group.add(other);
			}
		}
	}

	private int getTotalLabelWidth() {
		if(group != null) {
			// this works because if there is a group, this layout will be part of that group
			int labelWidth = 0;
			for(LabeledPairLayout layout : group.getLayouts()) {
				labelWidth = Math.max(labelWidth, layout.getLabelWidth());
			}
			return labelWidth;
		} else {
			return getLabelWidth();
		}
	}

	private int getLabelWidth() {
		int labelWidth = 0;

		Enumeration<Component> labelIter = labels.elements();
		while(labelIter.hasMoreElements()) {
			Component comp = labelIter.nextElement();
			labelWidth = Math.max(labelWidth, comp.getPreferredSize().width);
		}

		return labelWidth;
	}

	@Override
	public void addLayoutComponent(String s, Component c) {
		if(s.equals(LABEL)) {
			labels.addElement(c);
		} else {
			fields.addElement(c);
		}
	}

	@Override
	public void layoutContainer(Container c) {
		Insets insets = c.getInsets();

		int labelWidth = getTotalLabelWidth();

		int yPos = insets.top;

		Enumeration<Component> labelIter = labels.elements();
		Enumeration<Component> fieldIter = fields.elements();
		while(labelIter.hasMoreElements() && fieldIter.hasMoreElements()) {
			Component label = labelIter.nextElement();
			Component field = fieldIter.nextElement();
			int height = Math.max(label.getPreferredSize().height, field.getPreferredSize().height);
			label.setBounds(insets.left, yPos, labelWidth, height);
			field.setBounds(insets.left + labelWidth + xGap, yPos,
					c.getSize().width - (labelWidth + xGap + insets.left + insets.right), height);
			yPos += (height + yGap);
		}

	}

	@Override
	public Dimension minimumLayoutSize(Container c) {
		Insets insets = c.getInsets();

		int labelWidth = getTotalLabelWidth();

		int yPos = insets.top;

		Enumeration<Component> labelIter = labels.elements();
		Enumeration<Component> fieldIter = fields.elements();
		while(labelIter.hasMoreElements() && fieldIter.hasMoreElements()) {
			Component label = labelIter.nextElement();
			Component field = fieldIter.nextElement();
			int height = Math.max(label.getPreferredSize().height, field.getPreferredSize().height);
			yPos += (height + yGap);
		}

		yPos += insets.bottom;

		return new Dimension(labelWidth * 3, yPos);
	}

	@Override
	public Dimension preferredLayoutSize(Container c) {
		Dimension d = minimumLayoutSize(c);
		d.width *= 2;
		return d;
	}

	@Override
	public void removeLayoutComponent(Component c) {
		int index = fields.indexOf(c);
		if(index != -1) {
			labels.remove(index);
			fields.remove(index);
		}
	}

	public static class LayoutGroup {
		private final List<LabeledPairLayout> layouts = new ArrayList<>();

		public void add(LabeledPairLayout layout) {
			layouts.add(layout);
		}

		public void remove(LabeledPairLayout layout) {
			layouts.remove(layout);
		}

		private List<LabeledPairLayout> getLayouts() {
			return layouts;
		}
	}
}
