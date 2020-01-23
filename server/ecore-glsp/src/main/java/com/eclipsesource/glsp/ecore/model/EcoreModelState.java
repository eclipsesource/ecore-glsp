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
package com.eclipsesource.glsp.ecore.model;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.glsp.api.model.GraphicalModelState;
import com.eclipsesource.glsp.ecore.EcoreEditorContext;
import com.eclipsesource.glsp.ecore.EcoreFacade;
import com.eclipsesource.glsp.ecore.EcoreModelIndex;
import com.eclipsesource.glsp.ecore.ResourceManager;
import org.eclipse.glsp.server.model.ModelStateImpl;

public class EcoreModelState extends ModelStateImpl implements GraphicalModelState {

	private EcoreEditorContext editorContext;

	public static EcoreModelState getModelState(GraphicalModelState state) {
		if (!(state instanceof EcoreModelState)) {
			throw new IllegalArgumentException("Argument must be a EcoreModelState");
		}
		return ((EcoreModelState) state);
	}

	public static EcoreEditorContext getEditorContext(GraphicalModelState state) {
		return getModelState(state).getEditorContext();
	}

	public static ResourceManager getResourceManager(GraphicalModelState modelState) {
		return getEditorContext(modelState).getResourceManager();
	}

	public static EcoreFacade getEcoreFacade(GraphicalModelState modelState) {
		return getEditorContext(modelState).getEcoreFacade();
	}

	public EcoreEditorContext getEditorContext() {
		return editorContext;
	}

	public void setEditorContext(EcoreEditorContext editorContext) {
		this.editorContext = editorContext;
		setCommandStack((BasicCommandStack) editorContext.getResourceManager().getEditingDomain().getCommandStack());
	}

	@Override
	public EcoreModelIndex getIndex() {
		return EcoreModelIndex.get(getRoot());
	}

}
