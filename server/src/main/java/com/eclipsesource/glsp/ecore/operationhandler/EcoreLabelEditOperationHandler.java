/*******************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *  
 *   This program and the accompanying materials are made available under the
 *   terms of the Eclipse Public License v. 2.0 which is available at
 *   http://www.eclipse.org/legal/epl-2.0.
 *  
 *   This Source Code may also be made available under the following Secondary
 *   Licenses when the conditions for such availability set forth in the Eclipse
 *   Public License v. 2.0 are satisfied: GNU General Public License, version 2
 *   with the GNU Classpath Exception which is available at
 *   https://www.gnu.org/software/classpath/license.html.
 *  
 *   SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ******************************************************************************/
package com.eclipsesource.glsp.ecore.operationhandler;

import static com.eclipsesource.glsp.api.jsonrpc.GLSPServerException.getOrThrow;

import java.util.Optional;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;

import com.eclipsesource.glsp.api.action.Action;
import com.eclipsesource.glsp.api.action.kind.AbstractOperationAction;
import com.eclipsesource.glsp.api.action.kind.ApplyLabelEditOperationAction;
import com.eclipsesource.glsp.api.handler.OperationHandler;
import com.eclipsesource.glsp.api.model.GraphicalModelState;
import com.eclipsesource.glsp.ecore.EcoreFacade;
import com.eclipsesource.glsp.ecore.EcoreModelIndex;
import com.eclipsesource.glsp.ecore.enotation.Shape;
import com.eclipsesource.glsp.ecore.model.EcoreModelState;
import com.eclipsesource.glsp.graph.GNode;

public class EcoreLabelEditOperationHandler implements OperationHandler {

	@Override
	public Class<? extends Action> handlesActionType() {
		return ApplyLabelEditOperationAction.class;
	}

	@Override
	public void execute(AbstractOperationAction action, GraphicalModelState graphicalModelState) {
		ApplyLabelEditOperationAction editLabelAction = (ApplyLabelEditOperationAction) action;
		EcoreFacade facade = EcoreModelState.getEcoreFacade(graphicalModelState);
		EcoreModelIndex index = EcoreModelState.getModelState(graphicalModelState).getIndex();
		GNode node = getOrThrow(index.findElementByClass(editLabelAction.getLabelId(), GNode.class),
				"No label container for label with id " + editLabelAction.getLabelId() + " found");

		EObject eObject = getOrThrow(index.getSemantic(node),
				"No semantic element for labelContainer with id " + node.getId() + " found");

		Shape shape = getOrThrow(index.getNotation(eObject), Shape.class,
				"No shape element for label with id " + editLabelAction.getLabelId() + " found");

		Optional<EObject> callingObject = index.getSemantic(editLabelAction.getLabelId());
		if (callingObject.isPresent() && callingObject.get() instanceof EAttribute) {
			String inputText = editLabelAction.getText();
			String attributeName;
			if (inputText.contains(":")) {
				String[] split = inputText.split(":");
				attributeName = split[0].trim();
			} else {
				attributeName = inputText;
			}
			((EAttribute) callingObject.get()).setName(attributeName);
			// nameChange== uri change so we have to recreate the proxy here
			shape.setSemanticElement(facade.createProxy(callingObject.get()));
		} else if (eObject instanceof EClassifier) {
			((EClassifier) eObject).setName(editLabelAction.getText());
			// nameChange== uri change so we have to recreate the proxy here
			shape.setSemanticElement(facade.createProxy(eObject));
		}
	}

	@Override
	public String getLabel(AbstractOperationAction action) {
		return "Apply label";
	}

}