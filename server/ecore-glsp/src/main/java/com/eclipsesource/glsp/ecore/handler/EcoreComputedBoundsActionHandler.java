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
package com.eclipsesource.glsp.ecore.handler;

import java.util.List;
import java.util.Optional;

import org.eclipse.glsp.api.action.Action;
import org.eclipse.glsp.api.action.ActionMessage;
import org.eclipse.glsp.api.action.ActionProcessor;
import org.eclipse.glsp.api.action.kind.ComputedBoundsAction;
import org.eclipse.glsp.api.action.kind.LayoutAction;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.api.types.ElementAndBounds;
import org.eclipse.glsp.api.utils.LayoutUtil;
import com.eclipsesource.glsp.ecore.enotation.Shape;
import com.eclipsesource.glsp.ecore.model.EcoreModelState;
import org.eclipse.glsp.graph.GDimension;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.graph.GPoint;
import org.eclipse.glsp.server.actionhandler.ComputedBoundsActionHandler;
import com.google.inject.Inject;

public class EcoreComputedBoundsActionHandler extends ComputedBoundsActionHandler {

	@Inject
	private ActionProcessor actionProcessor;

	@Override
	public List<Action> execute(Action action, GraphicalModelState graphicalModelState) {
		ComputedBoundsAction computedBoundsAction = (ComputedBoundsAction) action;
		EcoreModelState modelState = EcoreModelState.getModelState(graphicalModelState);

		for (ElementAndBounds element : computedBoundsAction.getBounds()) {
			modelState.getIndex().getNotation(element.getElementId(), Shape.class)
					.ifPresent(notationElement -> changeElementBounds(notationElement, element.getNewSize(),
							element.getNewPosition()));
		}
		synchronized (submissionHandler.getModelLock()) {
			GModelRoot model = modelState.getRoot();
			if (model != null && model.getRevision() == computedBoundsAction.getRevision()) {
				LayoutUtil.applyBounds(model, computedBoundsAction, graphicalModelState);
				if (modelState.getEditorContext().getEcoreFacade().diagramNeedsAutoLayout()) {
					ActionMessage layoutMessage = new ActionMessage(clientId, new LayoutAction());
					actionProcessor.process(layoutMessage);
				}
				return submissionHandler.doSubmitModel(false, modelState);
			}
		}
		return List.of();

	}

	private void changeElementBounds(Shape element, GDimension dimension, GPoint position) {
		if (position != null) {
			element.setPosition(position);
		}
		if (dimension != null) {
			element.setSize(dimension);
		}
	}
}
