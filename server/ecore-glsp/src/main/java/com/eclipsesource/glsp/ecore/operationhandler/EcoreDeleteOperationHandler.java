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
package com.eclipsesource.glsp.ecore.operationhandler;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.eclipsesource.glsp.api.model.GraphicalModelState;
import com.eclipsesource.glsp.ecore.model.EcoreModelState;
import com.eclipsesource.glsp.ecore.util.EcoreEdgeUtil;
import com.eclipsesource.glsp.graph.GModelIndex;
import com.eclipsesource.glsp.server.operationhandler.DeleteOperationHandler;

public class EcoreDeleteOperationHandler extends DeleteOperationHandler {
	@Override
	protected boolean delete(String elementId, GModelIndex index, GraphicalModelState graphicalModelState) {
		
		super.delete(elementId, index, graphicalModelState);
		EcoreModelState modelState = EcoreModelState.getModelState(graphicalModelState);
		
		modelState.getIndex().getSemantic(elementId).ifPresent(element -> {
			if(element instanceof EReference && ((EReference) element).getEOpposite() != null) {
				EcoreUtil.delete(((EReference) element).getEOpposite());
				modelState.getIndex().getBidirectionalReferences().remove(EcoreEdgeUtil.getStringId((EReference)element));
			}
		});
		modelState.getIndex().getSemantic(elementId).ifPresent(EcoreUtil::remove);
		modelState.getIndex().getNotation(elementId).ifPresent(EcoreUtil::remove);
		return true;
	}
	
}
