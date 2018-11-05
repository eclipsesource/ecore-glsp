/*******************************************************************************
 * Copyright (c) 2018 EclipseSource Services GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 * 	Philip Langer - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.glsp.ecore;

import java.util.HashSet;
import java.util.Set;

import com.eclipsesource.glsp.api.handler.OperationHandler;
import com.eclipsesource.glsp.api.provider.OperationHandlerProvider;
import com.eclipsesource.glsp.server.operationhandler.DeleteHandler;
import com.eclipsesource.glsp.server.operationhandler.MoveNodeHandler;


public class EcoreOperationHandlerProvider implements OperationHandlerProvider {

	Set<OperationHandler> operationHandlers = new HashSet<>();

	@Override
	public Set<OperationHandler> getHandlers() {
		operationHandlers.add(new MoveNodeHandler());
		operationHandlers.add(new DeleteHandler());
		return operationHandlers;
	}
}
