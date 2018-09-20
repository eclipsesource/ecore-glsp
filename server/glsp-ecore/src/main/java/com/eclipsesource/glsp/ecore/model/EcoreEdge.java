package com.eclipsesource.glsp.ecore.model;

import io.typefox.sprotty.api.SEdge;

public class EcoreEdge extends SEdge {
	private String multiplicitySource;
	private String multiplicityTarget;

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

}
