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

import com.eclipsesource.glsp.api.action.kind.AbstractOperationAction;
import com.eclipsesource.glsp.api.action.kind.ChangeBoundsOperationAction;
import com.eclipsesource.glsp.api.handler.OperationHandler;
import com.eclipsesource.glsp.api.model.GraphicalModelState;
import com.eclipsesource.glsp.api.types.ElementAndBounds;
import com.eclipsesource.glsp.ecore.EcoreModelIndex;
import com.eclipsesource.glsp.ecore.enotation.Shape;
import com.eclipsesource.glsp.ecore.model.EcoreModelState;
import com.eclipsesource.glsp.graph.GBounds;
import com.eclipsesource.glsp.graph.util.GraphUtil;

public class EcoreChangeBoundsOperationHandler implements OperationHandler {

	@Override
	public Class<?> handlesActionType() {
		return ChangeBoundsOperationAction.class;
	}

	@Override
	public void execute(AbstractOperationAction action, GraphicalModelState graphicalModelState) {
		EcoreModelState modelState = EcoreModelState.getModelState(graphicalModelState);
		ChangeBoundsOperationAction changeBoundsAction = (ChangeBoundsOperationAction) action;
		applyBounds(changeBoundsAction, modelState.getIndex());
	}

	private void applyBounds(ChangeBoundsOperationAction action, EcoreModelIndex index) {
		for (ElementAndBounds element : action.getNewBounds()) {
			index.getNotation(element.getElementId(), Shape.class)
					.ifPresent(notationElement -> changeElementBounds(notationElement, element.getNewBounds()));
		}
	}

	private void changeElementBounds(Shape element, GBounds newBounds) {
		element.setPosition(GraphUtil.point(newBounds.getX(), newBounds.getY()));
		element.setSize(GraphUtil.dimension(newBounds.getWidth(), newBounds.getHeight()));
	}

	@Override
	public String getLabel(AbstractOperationAction action) {
		return "Change bounds";
	}
}