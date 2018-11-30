package com.eclipsesource.glsp.ecore.model;

import java.util.ArrayList;
import java.util.List;

import io.typefox.sprotty.api.SEdge;

public class EcoreEdge extends SEdge {
	private List<String> cssClasses;
	private String multiplicitySource;
	private String multiplicityTarget;

	public EcoreEdge() {
		cssClasses= new ArrayList<>();
	}
	public String getMultiplicitySource() {
		return multiplicitySource;
	}

	public void setMultiplicitySource(String multiplicitySource) {
		this.multiplicitySource = multiplicitySource;
	}

	public String getMultiplicityTarget() {
		return multiplicityTarget;
	}

	public void setMultiplicityTarget(String multiplicityTarget) {
		this.multiplicityTarget = multiplicityTarget;
	}

	public List<String> getCssClasses() {
		return cssClasses;
	}

	public void setCssClasses(List<String> cssClasses) {
		this.cssClasses = cssClasses;
	}

	
}
