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
package com.eclipsesource.glsp.ecore.model;

import org.eclipse.glsp.api.action.kind.RequestModelAction;
import org.eclipse.glsp.api.factory.ModelFactory;
import org.eclipse.glsp.api.model.GraphicalModelState;
import com.eclipsesource.glsp.ecore.EcoreEditorContext;
import com.eclipsesource.glsp.ecore.EcoreFacade;
import com.eclipsesource.glsp.ecore.enotation.Diagram;
import org.eclipse.glsp.graph.DefaultTypes;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.graph.builder.impl.GGraphBuilder;

public class EcoreModelFactory implements ModelFactory {
	private static final String ROOT_ID = "sprotty";

	@Override
	public GModelRoot loadModel(RequestModelAction action, GraphicalModelState graphicalModelState) {
		EcoreModelState modelState = EcoreModelState.getModelState(graphicalModelState);
		graphicalModelState.setClientOptions(action.getOptions());

		EcoreEditorContext context = new EcoreEditorContext(modelState);

		modelState.setEditorContext(context);

		EcoreFacade ecoreFacade = context.getEcoreFacade();
		if (ecoreFacade == null) {
			return createEmptyRoot();
		}
		Diagram diagram = ecoreFacade.getDiagram();

		GModelRoot gmodelRoot = context.getGModelFactory().create(ecoreFacade.getEPackage());
		ecoreFacade.initialize(diagram, gmodelRoot);
		modelState.setRoot(gmodelRoot);
		return gmodelRoot;
	}

	private static GModelRoot createEmptyRoot() {
		return new GGraphBuilder(DefaultTypes.GRAPH)//
				.id(ROOT_ID) //
				.build();
	}

}
