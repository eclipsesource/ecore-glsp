/*******************************************************************************
 * Copyright (c) 2018 EclipseSource Services GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 * 	Tobias Ortmayr - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.glsp.ecore;

import com.eclipsesource.glsp.api.factory.ModelFactory;
import com.eclipsesource.glsp.api.factory.PopupModelFactory;
import com.eclipsesource.glsp.api.listener.GraphicalModelExpansionListener;
import com.eclipsesource.glsp.api.listener.GraphicalModelSelectionListener;
import com.eclipsesource.glsp.api.listener.ModelElementOpenListener;
import com.eclipsesource.glsp.api.operations.OperationConfiguration;
import com.eclipsesource.glsp.api.operations.OperationHandlerProvider;
import com.eclipsesource.glsp.ecore.diagram.EcoreModelFactory;
import com.eclipsesource.glsp.server.di.ServerModule;


public class EcoreServerRuntimeModule extends ServerModule {
	
	@Override
	public Class<? extends ModelFactory> bindModelFactory() {
//		return EcoreSprottyFileModelFactory.class;
		return EcoreModelFactory.class;
	}

	@Override
	public Class<? extends PopupModelFactory> bindPopupModelFactory() {
		return EcorePopupFactory.class;
	}

	@Override
	public Class<? extends GraphicalModelSelectionListener> bindGraphicalModelSelectionListener() {
		return EcoreServerListener.class;
	}

	@Override
	public Class<? extends ModelElementOpenListener> bindModelElementOpenListener() {
		return EcoreServerListener.class;
	}

	@Override
	public Class<? extends GraphicalModelExpansionListener> bindGraphicalModelExpansionListener() {
		return EcoreServerListener.class;
	}

	@Override
	public Class<? extends OperationConfiguration> bindOperationConfiguration() {
		return EcoreOperationConfiguration.class;
	}
	
	@Override
	public Class<? extends OperationHandlerProvider> bindOperationHandlerProvider() {
		return EcoreOperationHandlerProvider.class;
	}
	
}
