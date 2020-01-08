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
package com.eclipsesource.glsp.ecore.operationhandler;

import static com.eclipsesource.glsp.api.jsonrpc.GLSPServerException.getOrThrow;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;

import com.eclipsesource.glsp.api.action.kind.AbstractOperationAction;
import com.eclipsesource.glsp.api.action.kind.CreateConnectionOperationAction;
import com.eclipsesource.glsp.api.handler.OperationHandler;
import com.eclipsesource.glsp.api.model.GraphicalModelState;
import com.eclipsesource.glsp.ecore.EcoreEditorContext;
import com.eclipsesource.glsp.ecore.EcoreFacade;
import com.eclipsesource.glsp.ecore.EcoreModelIndex;
import com.eclipsesource.glsp.ecore.enotation.Diagram;
import com.eclipsesource.glsp.ecore.model.EcoreModelState;
import com.eclipsesource.glsp.ecore.util.EcoreConfig.Types;
import com.eclipsesource.glsp.graph.GEdge;
import com.google.common.collect.Lists;

public class CreateEcoreEdgeOperationHandler implements OperationHandler {
	private List<String> handledElementTypeIds = Lists.newArrayList(Types.REFERENCE, Types.COMPOSITION,
			Types.INHERITANCE, Types.BIDIRECTIONAL_REFERENCE, Types.BIDIRECTIONAL_COMPOSITION);

	public CreateEcoreEdgeOperationHandler() {

	}

	@Override
	public boolean handles(AbstractOperationAction action) {
		if (action instanceof CreateConnectionOperationAction) {
			CreateConnectionOperationAction connectAction = (CreateConnectionOperationAction) action;
			return this.handledElementTypeIds.contains(connectAction.getElementTypeId());
		}
		return false;
	}

	@Override
	public void execute(AbstractOperationAction operationAction, GraphicalModelState modelState) {
		CreateConnectionOperationAction action = (CreateConnectionOperationAction) operationAction;
		String elementTypeId = action.getElementTypeId();

		EcoreEditorContext context = EcoreModelState.getEditorContext(modelState);
		EcoreModelIndex modelIndex = context.getModelState().getIndex();
		EcoreFacade facade = context.getEcoreFacade();
		EClass sourceEclass = getOrThrow(modelIndex.getSemantic(action.getSourceElementId(), EClass.class),
				"No semantic EClass found for source element with id " + action.getSourceElementId());
		EClass targetEClass = getOrThrow(modelIndex.getSemantic(action.getTargetElementId(), EClass.class),
				"No semantic EClass found for target element with id" + action.getTargetElementId());

		if (elementTypeId.equals(Types.INHERITANCE)) {
			sourceEclass.getESuperTypes().add(targetEClass);
		} else {
			EReference reference = createReference(sourceEclass, targetEClass, 
					elementTypeId.equals(Types.BIDIRECTIONAL_COMPOSITION) ? Types.COMPOSITION :elementTypeId);
			
			if (elementTypeId.equals(Types.BIDIRECTIONAL_REFERENCE) 
					|| elementTypeId.equals(Types.BIDIRECTIONAL_COMPOSITION)) {
				EReference opposite = createReference(targetEClass, sourceEclass, elementTypeId);
				reference.setEOpposite(opposite);
				opposite.setEOpposite(reference);
			}
			GEdge edge = getOrThrow(context.getGModelFactory().create(reference, GEdge.class),
					" No viewmodel factory found for element: " + reference);
			Diagram diagram = facade.getDiagram();
			diagram.getElements().add(facade.initializeEdge(reference, edge));
		}
	}

	private EReference createReference(EClass source, EClass target, String elementTypeId) {
		EReference reference = EcoreFactory.eINSTANCE.createEReference();
		reference.setEType(target);
		reference.setName(target.getName().toLowerCase() + "s");
		if (elementTypeId.equals(Types.COMPOSITION)) {
			reference.setContainment(true);
		}
		source.getEStructuralFeatures().add(reference);
		return reference;

	}
	
	@Override
	public String getLabel(AbstractOperationAction action) {
		return "Create ecore edge";
	}

}
