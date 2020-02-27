/********************************************************************************
 * Copyright (c) 2019-2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package org.eclipse.emfcloud.ecore.glsp.operationhandler;

import java.util.List;

import org.eclipse.emfcloud.ecore.enotation.Edge;
import org.eclipse.emfcloud.ecore.glsp.EcoreModelIndex;
import org.eclipse.emfcloud.ecore.glsp.model.EcoreModelState;
import org.eclipse.glsp.api.action.kind.AbstractOperationAction;
import org.eclipse.glsp.api.action.kind.ChangeRoutingPointsOperationAction;
import org.eclipse.glsp.api.handler.OperationHandler;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.api.types.ElementAndRoutingPoints;
import org.eclipse.glsp.graph.GPoint;

public class ChangeRoutingPointsOperationHandler implements OperationHandler {

    public ChangeRoutingPointsOperationHandler() {

    }

    @Override
    public Class<?> handlesActionType() {
        return ChangeRoutingPointsOperationAction.class;
    }

    @Override
    public void execute(AbstractOperationAction action, GraphicalModelState modelState) {
        EcoreModelIndex index = EcoreModelState.getModelState(modelState).getIndex();
        ChangeRoutingPointsOperationAction rerouteAction = (ChangeRoutingPointsOperationAction) action;
        rerouteEdge(rerouteAction, index);
    }

    private void rerouteEdge(ChangeRoutingPointsOperationAction action, EcoreModelIndex index) {
        for (ElementAndRoutingPoints element : action.getNewRoutingPoints()) {
            index.getNotation(element.getElementId(), Edge.class)
                    .ifPresent(notationElement -> changeEdgePoints(notationElement, element.getNewRoutingPoints()));
        };
    }

    private void changeEdgePoints(Edge element, List<GPoint> points) {
        if (points != null) {
            element.getBendPoints().clear();
            element.getBendPoints().addAll(points);
        }
    }

    @Override
    public String getLabel(AbstractOperationAction action) {
        return "Reroute ecore edge";
    }
}