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

import java.util.Optional;

import com.eclipsesource.glsp.api.action.Action;
import com.eclipsesource.glsp.api.action.kind.ComputedBoundsAction;
import com.eclipsesource.glsp.api.model.GraphicalModelState;
import com.eclipsesource.glsp.api.types.ElementAndBounds;
import com.eclipsesource.glsp.api.utils.LayoutUtil;
import com.eclipsesource.glsp.ecore.enotation.Shape;
import com.eclipsesource.glsp.ecore.model.EcoreModelState;
import com.eclipsesource.glsp.graph.GDimension;
import com.eclipsesource.glsp.graph.GModelRoot;
import com.eclipsesource.glsp.graph.GPoint;
import com.eclipsesource.glsp.server.actionhandler.ComputedBoundsActionHandler;

public class EcoreComputedBoundsActionHandler extends ComputedBoundsActionHandler {
	@Override
	public Optional<Action> execute(Action action, GraphicalModelState graphicalModelState) {
		ComputedBoundsAction computedBoundsAction = (ComputedBoundsAction) action;
		EcoreModelState modelState = EcoreModelState.getModelState(graphicalModelState);

		for (ElementAndBounds element : computedBoundsAction.getBounds()) {
			modelState.getIndex().getNotation(element.getElementId(), Shape.class)
					.ifPresent(notationElement -> changeElementBounds(notationElement, element.getNewSize(), element.getNewPosition()));
		}
		synchronized (submissionHandler.getModelLock()) {
			GModelRoot model = modelState.getRoot();
			if (model != null && model.getRevision() == computedBoundsAction.getRevision()) {
				LayoutUtil.applyBounds(model, computedBoundsAction, graphicalModelState);
				return submissionHandler.doSubmitModel(false, modelState);
			}
		}
		return Optional.empty();

	}

	private void changeElementBounds(Shape element, GDimension dimension, GPoint position) {
		element.setPosition(position);
		element.setSize(dimension);
	}
}
