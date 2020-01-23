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
import org.eclipse.glsp.api.action.kind.AbstractOperationAction;
import org.eclipse.glsp.api.action.kind.RequestBoundsAction;
import org.eclipse.glsp.api.handler.OperationHandler;
import org.eclipse.glsp.api.model.GraphicalModelState;
import com.eclipsesource.glsp.ecore.EcoreEditorContext;
import com.eclipsesource.glsp.ecore.EcoreRecordingCommand;
import com.eclipsesource.glsp.ecore.gmodel.GModelFactory;
import com.eclipsesource.glsp.ecore.model.EcoreModelState;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.server.actionhandler.OperationActionHandler;

public class EcoreOperationActionHandler extends OperationActionHandler {

	@Override
	public List<Action> doHandle(AbstractOperationAction action, GraphicalModelState graphicalModelState) {
		EcoreModelState modelState = EcoreModelState.getModelState(graphicalModelState);
		EcoreEditorContext context = modelState.getEditorContext();

		if (operationHandlerProvider.isHandled(action)) {
			OperationHandler handler = operationHandlerProvider.getHandler(action).get();
			String label = handler.getLabel(action);
			EcoreRecordingCommand command = new EcoreRecordingCommand(context, label,
					() -> handler.execute(action, modelState));
			modelState.execute(command);
			GModelRoot newRoot = new GModelFactory(modelState).create();

			return List.of(new RequestBoundsAction(newRoot));
		}
		return List.of();
	}

}