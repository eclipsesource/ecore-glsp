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
package org.eclipse.emfcloud.ecore.glsp.operationhandler;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.ecore.glsp.model.EcoreModelState;
import org.eclipse.emfcloud.ecore.glsp.util.EcoreEdgeUtil;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.graph.GModelIndex;
import org.eclipse.glsp.server.operationhandler.DeleteOperationHandler;

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
