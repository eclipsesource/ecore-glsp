/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
package com.eclipsesource.glsp.ecore.gmodel;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.ENamedElement;

import com.eclipsesource.glsp.ecore.model.EcoreModelState;
import com.eclipsesource.glsp.ecore.util.EcoreConfig.Types;
import com.eclipsesource.glsp.graph.GLabel;
import com.eclipsesource.glsp.graph.builder.impl.GLabelBuilder;

public class LabelFactory extends AbstractGModelFactory<ENamedElement, GLabel> {

	public LabelFactory(EcoreModelState modelState) {
		super(modelState);
	}

	@Override
	public GLabel create(ENamedElement semanticElement) {
		if (semanticElement instanceof EAttribute) {
			return create((EAttribute) semanticElement);
		} else if (semanticElement instanceof EEnumLiteral) {
			return create((EEnumLiteral) semanticElement);
		}
		return null;
	}

	public GLabel create(EAttribute eAttribute) {
		String label = String.format("%s : %s", eAttribute.getName(), //
				eAttribute.getEAttributeType().getName());
		return new GLabelBuilder(Types.ATTRIBUTE) //
				.id(toId(eAttribute))//
				.text(label) //
				.build();
	}

	public GLabel create(EEnumLiteral eEnumLiteral) {
		String label = " - " + eEnumLiteral.getLiteral();

		return new GLabelBuilder(Types.LABEL_TEXT) //
				.id(toId(eEnumLiteral)) //
				.text(label) //
				.build();
	}

}
