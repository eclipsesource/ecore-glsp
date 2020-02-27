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
package org.eclipse.emfcloud.ecore.glsp.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.ecore.glsp.ResourceManager;
import org.eclipse.emfcloud.ecore.glsp.actions.AttributeTypesAction;
import org.eclipse.emfcloud.ecore.glsp.actions.ReturnAttributeTypesAction;
import org.eclipse.emfcloud.ecore.glsp.model.EcoreModelState;
import org.eclipse.emfcloud.ecore.glsp.operationhandler.EcoreLabelEditOperationHandler;
import org.eclipse.glsp.api.action.Action;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.server.actionhandler.AbstractActionHandler;

public class EcoreGetAttributeTypesActionHandler extends AbstractActionHandler {

    @Override
	public boolean handles(Action action) {
		return action instanceof AttributeTypesAction;
    }

    @Override
	protected List<Action> execute(Action action, GraphicalModelState modelState) {
		List<String> types = getEAttributeTypeList(EcoreModelState.getResourceManager(modelState));
        Collections.sort(types);

		return List.of(new ReturnAttributeTypesAction(types));
    }
    
	private List<String> getEAttributeTypeList(ResourceManager resManager) {
		List<String> list = new ArrayList<>();
		for (EObject obj : EcoreLabelEditOperationHandler.getAllEAttributeTypes(resManager)) {
			list.add(((ENamedElement) obj).getName());
		}
		return list;
	}
}