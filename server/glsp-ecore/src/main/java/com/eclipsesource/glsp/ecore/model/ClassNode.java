package com.eclipsesource.glsp.ecore.model;

import java.util.ArrayList;
import java.util.List;

import io.typefox.sprotty.api.SNode;

public class ClassNode extends SNode {
	private List<String> cssClasses;

	public ClassNode() {
		cssClasses = new ArrayList<>();
	}

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

	public List<String> getCssClasses() {
		return cssClasses;
	}

	public void setCssClasses(List<String> cssClasses) {
		this.cssClasses = cssClasses;
	}

}