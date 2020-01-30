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

import java.util.List;

import org.eclipse.glsp.api.action.kind.AbstractOperationAction;
import org.eclipse.glsp.api.action.kind.ChangeRoutingPointsOperationAction;
import org.eclipse.glsp.api.handler.OperationHandler;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.api.types.ElementAndRoutingPoints;

import com.eclipsesource.glsp.ecore.EcoreModelIndex;
import com.eclipsesource.glsp.ecore.enotation.Edge;
import com.eclipsesource.glsp.ecore.model.EcoreModelState;
import org.eclipse.glsp.graph.GPoint;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

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
        EList<GPoint> ePoints = new BasicEList<GPoint>(points);
        if (points != null)
            element.setBendPoints(ePoints);
    }

    @Override
    public String getLabel(AbstractOperationAction action) {
        return "Reroute ecore edge";
    }
}