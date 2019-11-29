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
package com.eclipsesource.glsp.ecore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.eclipsesource.glsp.api.configuration.ServerConfiguration;
import com.eclipsesource.glsp.api.diagram.DiagramConfiguration;
import com.eclipsesource.glsp.api.factory.ModelFactory;
import com.eclipsesource.glsp.api.handler.OperationHandler;
import com.eclipsesource.glsp.api.layout.ILayoutEngine;
import com.eclipsesource.glsp.api.model.ModelStateProvider;
import com.eclipsesource.glsp.api.provider.ActionProvider;
import com.eclipsesource.glsp.ecore.actions.EcoreActionProvider;
import com.eclipsesource.glsp.ecore.handler.EcoreComputedBoundsActionHandler;
import com.eclipsesource.glsp.ecore.handler.EcoreGetAttributeTypesActionHandler;
import com.eclipsesource.glsp.ecore.handler.EcoreOperationActionHandler;
import com.eclipsesource.glsp.ecore.handler.EcoreSaveModelActionHandler;
import com.eclipsesource.glsp.ecore.handler.EcoreUndoRedoActionHandler;
import com.eclipsesource.glsp.ecore.model.EcoreModelFactory;
import com.eclipsesource.glsp.ecore.model.EcoreModelStateProvider;
import com.eclipsesource.glsp.ecore.operationhandler.CreateClassifierChildNodeOperationHandler;
import com.eclipsesource.glsp.ecore.operationhandler.CreateClassifierNodeOperationHandler;
import com.eclipsesource.glsp.ecore.operationhandler.CreateEcoreEdgeOperationHandler;
import com.eclipsesource.glsp.ecore.operationhandler.EcoreChangeBoundsOperationHandler;
import com.eclipsesource.glsp.ecore.operationhandler.EcoreDeleteOperationHandler;
import com.eclipsesource.glsp.ecore.operationhandler.EcoreLabelEditOperationHandler;
import com.eclipsesource.glsp.server.actionhandler.ComputedBoundsActionHandler;
import com.eclipsesource.glsp.server.actionhandler.OperationActionHandler;
import com.eclipsesource.glsp.server.actionhandler.SaveModelActionHandler;
import com.eclipsesource.glsp.server.actionhandler.UndoRedoActionHandler;
import com.eclipsesource.glsp.server.di.DefaultGLSPModule;

public class EcoreGLSPModule extends DefaultGLSPModule {

	public EcoreGLSPModule() {
		rebind(SaveModelActionHandler.class, EcoreSaveModelActionHandler.class);
		rebind(ComputedBoundsActionHandler.class, EcoreComputedBoundsActionHandler.class);
		rebind(OperationActionHandler.class, EcoreOperationActionHandler.class);
		rebind(UndoRedoActionHandler.class, EcoreUndoRedoActionHandler.class);

		bindActionHandlers().add(EcoreGetAttributeTypesActionHandler.class);
	}

	@Override
	protected Class<? extends ActionProvider> bindActionProvider() {
		return EcoreActionProvider.class; // includes AttributeTypesAction & DefaultActionProvider
	}

	@Override
	public Class<? extends ModelFactory> bindModelFactory() {
		return EcoreModelFactory.class;
	}

	@Override
	protected Class<? extends ILayoutEngine> bindLayoutEngine() {
		return EcoreLayoutEngine.class;
	}

	@Override
	protected Collection<Class<? extends DiagramConfiguration>> bindDiagramConfigurations() {
		return List.of(EcoreDiagramConfiguration.class);
	}

	@SuppressWarnings("serial")
	@Override
	protected Collection<Class<? extends OperationHandler>> bindOperationHandlers() {
		return new ArrayList<Class<? extends OperationHandler>>() {
			{
				add(EcoreChangeBoundsOperationHandler.class);
				add(EcoreDeleteOperationHandler.class);
				add(CreateClassifierNodeOperationHandler.class);
				add(CreateEcoreEdgeOperationHandler.class);
				add(CreateClassifierChildNodeOperationHandler.class);
				add(EcoreLabelEditOperationHandler.class);
			}
		};
	}

	@Override
	protected Class<? extends ServerConfiguration> bindServerConfiguration() {
		return EcoreServerConfiguration.class;
	}

	@Override
	protected Class<? extends ModelStateProvider> bindModelStateProvider() {
		return EcoreModelStateProvider.class;
	}

}
