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
package org.eclipse.emfcloud.ecore.glsp.operationhandler;

import java.util.List;
import java.util.function.Function;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emfcloud.ecore.enotation.Diagram;
import org.eclipse.emfcloud.ecore.enotation.Shape;
import org.eclipse.emfcloud.ecore.glsp.EcoreEditorContext;
import org.eclipse.emfcloud.ecore.glsp.EcoreFacade;
import org.eclipse.emfcloud.ecore.glsp.model.EcoreModelState;
import org.eclipse.emfcloud.ecore.glsp.util.EcoreConfig.Types;
import org.eclipse.glsp.api.action.kind.AbstractOperationAction;
import org.eclipse.glsp.api.action.kind.CreateNodeOperationAction;
import org.eclipse.glsp.api.handler.OperationHandler;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.graph.GraphPackage;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class CreateClassifierNodeOperationHandler implements OperationHandler {

	private List<String> handledElementTypeIds = Lists.newArrayList(Types.ECLASS, Types.ENUM, Types.INTERFACE,
			Types.ABSTRACT, Types.DATATYPE);

	public CreateClassifierNodeOperationHandler() {
	}

	@Override
	public boolean handles(AbstractOperationAction execAction) {
		if (execAction instanceof CreateNodeOperationAction) {
			CreateNodeOperationAction action = (CreateNodeOperationAction) execAction;
			return handledElementTypeIds.contains(action.getElementTypeId());
		}
		return false;
	}

	@Override
	public void execute(AbstractOperationAction abstractAction, GraphicalModelState modelState) {
		Preconditions.checkArgument(abstractAction instanceof CreateNodeOperationAction);
		CreateNodeOperationAction action = (CreateNodeOperationAction) abstractAction;
		String elementTypeId = action.getElementTypeId();
		EcoreEditorContext context = EcoreModelState.getEditorContext(modelState);
		EcoreFacade facade = context.getEcoreFacade();
		EPackage ePackage = facade.getEPackage();
		EClassifier eClassifier = createClassifier(elementTypeId);

		setName(eClassifier, modelState);
		ePackage.getEClassifiers().add(eClassifier);
		Diagram diagram = facade.getDiagram();
		Shape shape = facade.initializeShape(eClassifier);
		if (action.getLocation() != null) {
			shape.setPosition(action.getLocation());
		}
		diagram.getElements().add(shape);
	}

	protected void setName(EClassifier classifier, GraphicalModelState modelState) {
		Function<Integer, String> nameProvider = i -> "New" + classifier.eClass().getName() + i;
		int nodeCounter = modelState.getIndex().getCounter(GraphPackage.Literals.GNODE, nameProvider);
		classifier.setName(nameProvider.apply(nodeCounter));
	}

	private EClassifier createClassifier(String elementTypeId) {
		if (elementTypeId.equals((Types.ENUM))) {
			return EcoreFactory.eINSTANCE.createEEnum();
		} else if (elementTypeId.equals(Types.DATATYPE)) {
			EDataType dataType = EcoreFactory.eINSTANCE.createEDataType();
			dataType.setInstanceClass(Object.class);
			dataType.setInstanceClassName("java.lang.Object");
			return dataType;
		} else {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			if (elementTypeId.equals(Types.ABSTRACT)) {
				eClass.setAbstract(true);
			} else if (elementTypeId.equals(Types.INTERFACE)) {
				eClass.setInterface(true);
			}
			return eClass;
		}
	}

	@Override
	public String getLabel(AbstractOperationAction action) {
		return "Create ecore edge";
	}

}
