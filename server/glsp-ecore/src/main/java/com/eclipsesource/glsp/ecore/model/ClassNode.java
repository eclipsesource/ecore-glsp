package com.eclipsesource.glsp.ecore.model;

import io.typefox.sprotty.api.SNode;

public class ClassNode extends SNode {
	private boolean expanded;
	private double strokeWidth;
	public boolean isExpanded() {
		return expanded;
	}
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	public double getStrokeWidth() {
		return strokeWidth;
	}
	public void setStrokeWidth(double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
}