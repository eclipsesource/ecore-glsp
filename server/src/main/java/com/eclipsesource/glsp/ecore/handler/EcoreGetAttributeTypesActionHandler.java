/*******************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *  
 *   This program and the accompanying materials are made available under the
 *   terms of the Eclipse Public License v. 2.0 which is available at
 *   http://www.eclipse.org/legal/epl-2.0.
 *  
 *   This Source Code may also be made available under the following Secondary
 *   Licenses when the conditions for such availability set forth in the Eclipse
 *   Public License v. 2.0 are satisfied: GNU General Public License, version 2
 *   with the GNU Classpath Exception which is available at
 *   https://www.gnu.org/software/classpath/license.html.
 *  
 *   SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ******************************************************************************/
package com.eclipsesource.glsp.ecore.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.eclipsesource.glsp.api.action.Action;
import com.eclipsesource.glsp.api.model.GraphicalModelState;
import com.eclipsesource.glsp.ecore.ResourceManager;
import com.eclipsesource.glsp.ecore.actions.AttributeTypesAction;
import com.eclipsesource.glsp.ecore.actions.ReturnAttributeTypesAction;
import com.eclipsesource.glsp.ecore.model.EcoreModelState;
import com.eclipsesource.glsp.ecore.operationhandler.EcoreLabelEditOperationHandler;
import com.eclipsesource.glsp.server.actionhandler.AbstractActionHandler;

import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;

public class EcoreGetAttributeTypesActionHandler extends AbstractActionHandler {

    @Override
	public boolean handles(Action action) {
		return action instanceof AttributeTypesAction;
    }

    @Override
	protected Optional<Action> execute(Action action, GraphicalModelState modelState) {
		List<String> types = getEAttributeTypeList(EcoreModelState.getResourceManager(modelState));
        Collections.sort(types);

		return Optional.of(new ReturnAttributeTypesAction(types));
    }
    
	private List<String> getEAttributeTypeList(ResourceManager resManager) {
		List<String> list = new ArrayList<>();
		for (EObject obj : EcoreLabelEditOperationHandler.getAllEAttributeTypes(resManager)) {
			list.add(((ENamedElement) obj).getName());
		}
		return list;
	}
}