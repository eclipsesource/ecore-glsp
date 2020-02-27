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
package org.eclipse.emfcloud.ecore.glsp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emfcloud.ecore.glsp.actions.EcoreActionProvider;
import org.eclipse.emfcloud.ecore.glsp.handler.EcoreComputedBoundsActionHandler;
import org.eclipse.emfcloud.ecore.glsp.handler.EcoreGetAttributeTypesActionHandler;
import org.eclipse.emfcloud.ecore.glsp.handler.EcoreOperationActionHandler;
import org.eclipse.emfcloud.ecore.glsp.handler.EcoreSaveModelActionHandler;
import org.eclipse.emfcloud.ecore.glsp.handler.EcoreUndoRedoActionHandler;
import org.eclipse.emfcloud.ecore.glsp.model.EcoreModelFactory;
import org.eclipse.emfcloud.ecore.glsp.model.EcoreModelStateProvider;
import org.eclipse.emfcloud.ecore.glsp.operationhandler.ChangeRoutingPointsOperationHandler;
import org.eclipse.emfcloud.ecore.glsp.operationhandler.CreateClassifierChildNodeOperationHandler;
import org.eclipse.emfcloud.ecore.glsp.operationhandler.CreateClassifierNodeOperationHandler;
import org.eclipse.emfcloud.ecore.glsp.operationhandler.CreateEcoreEdgeOperationHandler;
import org.eclipse.emfcloud.ecore.glsp.operationhandler.EcoreChangeBoundsOperationHandler;
import org.eclipse.emfcloud.ecore.glsp.operationhandler.EcoreDeleteOperationHandler;
import org.eclipse.emfcloud.ecore.glsp.operationhandler.EcoreLabelEditOperationHandler;
import org.eclipse.glsp.api.configuration.ServerConfiguration;
import org.eclipse.glsp.api.diagram.DiagramConfiguration;
import org.eclipse.glsp.api.factory.ModelFactory;
import org.eclipse.glsp.api.handler.OperationHandler;
import org.eclipse.glsp.api.layout.ILayoutEngine;
import org.eclipse.glsp.api.model.ModelStateProvider;
import org.eclipse.glsp.api.provider.ActionProvider;
import org.eclipse.glsp.server.actionhandler.ComputedBoundsActionHandler;
import org.eclipse.glsp.server.actionhandler.OperationActionHandler;
import org.eclipse.glsp.server.actionhandler.SaveModelActionHandler;
import org.eclipse.glsp.server.actionhandler.UndoRedoActionHandler;
import org.eclipse.glsp.server.di.DefaultGLSPModule;

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
				add(ChangeRoutingPointsOperationHandler.class);
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
