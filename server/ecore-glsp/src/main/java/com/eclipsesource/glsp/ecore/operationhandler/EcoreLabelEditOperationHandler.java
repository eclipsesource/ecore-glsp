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

import static com.eclipsesource.glsp.api.jsonrpc.GLSPServerException.getOrThrow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;

import com.eclipsesource.glsp.api.action.Action;
import com.eclipsesource.glsp.api.action.kind.AbstractOperationAction;
import com.eclipsesource.glsp.api.action.kind.ApplyLabelEditOperationAction;
import com.eclipsesource.glsp.api.handler.OperationHandler;
import com.eclipsesource.glsp.api.jsonrpc.GLSPServerException;
import com.eclipsesource.glsp.api.model.GraphicalModelState;
import com.eclipsesource.glsp.ecore.EcoreFacade;
import com.eclipsesource.glsp.ecore.EcoreModelIndex;
import com.eclipsesource.glsp.ecore.ResourceManager;
import com.eclipsesource.glsp.ecore.enotation.Shape;
import com.eclipsesource.glsp.ecore.model.EcoreModelState;
import com.eclipsesource.glsp.graph.GEdge;
import com.eclipsesource.glsp.graph.GModelElement;
import com.eclipsesource.glsp.graph.GNode;
import com.eclipsesource.glsp.ecore.util.EcoreConfig.Types;

public class EcoreLabelEditOperationHandler implements OperationHandler {

	@Override
	public Class<? extends Action> handlesActionType() {
		return ApplyLabelEditOperationAction.class;
	}

	@Override
	public void execute(AbstractOperationAction action, GraphicalModelState graphicalModelState) {
		ApplyLabelEditOperationAction editLabelAction = (ApplyLabelEditOperationAction) action;
		EcoreFacade facade = EcoreModelState.getEcoreFacade(graphicalModelState);
		EcoreModelIndex index = EcoreModelState.getModelState(graphicalModelState).getIndex();
		Optional<String> type = index.findElementByClass(editLabelAction.getLabelId(), GModelElement.class).map(e -> e.getType());
		if (type.isPresent()) {
			switch (type.get()) {
				case Types.LABEL_NAME:
						GNode node = getOrThrow(index.findElementByClass(editLabelAction.getLabelId(), GNode.class), 
							"No parent Node for element with id " + editLabelAction.getLabelId() + " found");
						
						EObject node_semantic = getOrThrow(index.getSemantic(node),
							"No semantic element for labelContainer with id " + node.getId() + " found");
		
						Shape shape = getOrThrow(index.getNotation(node_semantic), Shape.class,
								"No shape element for label with id " + editLabelAction.getLabelId() + " found");
		
						if (node_semantic instanceof EClassifier) {
							((EClassifier) node_semantic).setName(editLabelAction.getText().trim());
							// nameChange== uri change so we have to recreate the proxy here
							shape.setSemanticElement(facade.createProxy(node_semantic));
						}
					break;
				case Types.LABEL_INSTANCE:
					node = getOrThrow(index.findElementByClass(editLabelAction.getLabelId(), GNode.class), 
							"No parent Node for element with id " + editLabelAction.getLabelId() + " found");

					node_semantic = getOrThrow(index.getSemantic(node),
							"No semantic element for labelContainer with id " + node.getId() + " found");
					if (node_semantic instanceof EClassifier) {
						((EClassifier) node_semantic).setInstanceClassName(editLabelAction.getText().trim());
					}
					break;
				case Types.ATTRIBUTE:
					EAttribute attribute_semantic = (EAttribute) getOrThrow(index.getSemantic(editLabelAction.getLabelId()),
						"No semantic element for label with id " + editLabelAction.getLabelId() + " found");

					String inputText = editLabelAction.getText();
					String attributeName;
					if (inputText.contains(":")) {
						String[] split = inputText.split(":");
						attributeName = split[0].trim();
		
						Optional<EClassifier> datatype = parseStringToEType(split[1].trim(),
								EcoreModelState.getResourceManager(graphicalModelState));
						if (datatype.isPresent()) {
							attribute_semantic.setEType(datatype.get());
						}
					} else {
						attributeName = inputText.trim();
					}
					if (!inputText.isEmpty()) {
						attribute_semantic.setName(attributeName);
					}
					break;

				case Types.ENUMLITERAL:
					EEnumLiteral literal_semantic = (EEnumLiteral) getOrThrow(index.getSemantic(editLabelAction.getLabelId()),
						"No semantic element for label with id " + editLabelAction.getLabelId() + " found");
					String text = editLabelAction.getText().trim();
					if (!text.isEmpty()) {
						literal_semantic.setName(text);
					}
					break;

				case Types.LABEL_EDGE_NAME:
					GEdge edge = getOrThrow(index.findElementByClass(editLabelAction.getLabelId(), GEdge.class),
						"No edge for label with id " + editLabelAction.getLabelId() + " found");
					EReference reference_semantic = (EReference) getOrThrow(index.getSemantic(edge),
						"No semantic element for labelContainer with id " + edge.getId() + " found");
					reference_semantic.setName(editLabelAction.getText().trim());
					break;

				case Types.LABEL_EDGE_MULTIPLICITY:
					edge = getOrThrow(index.findElementByClass(editLabelAction.getLabelId(), GEdge.class),
						"No edge for label with id " + editLabelAction.getLabelId() + " found");
					reference_semantic = (EReference) getOrThrow(index.getSemantic(edge),
						"No semantic element for labelContainer with id " + edge.getId() + " found");
					Pattern pattern = Pattern.compile("\\s*\\[\\s*(\\d+)\\s*\\.+\\s*(\\*|\\d+|\\-1)\\s*\\]\\s*");
						Matcher matcher = pattern.matcher(editLabelAction.getText());
						if (matcher.matches()) {
							String lowerBound = matcher.group(1);
							String upperBound = matcher.group(2);
							reference_semantic.setLowerBound((lowerBound.equals("*")) ? -1 : Integer.valueOf(lowerBound));
							reference_semantic.setUpperBound((upperBound.equals("*")) ? -1 : Integer.valueOf(upperBound));
						} else {
							throw new GLSPServerException("Multiplicity of reference with id " + editLabelAction.getLabelId() + " has a wrong input format", new IllegalArgumentException());
						}
					break;
			}
		}
	}

	private Optional<EClassifier> parseStringToEType(String name, ResourceManager resManager) {
		for (EClassifier type : getAllEAttributeTypes(resManager)) {
			if (type.getName().toLowerCase().equals(name.toLowerCase())) {
				return Optional.ofNullable(type);
			}
		}
		return Optional.empty();
	}

	public static List<EClassifier> getAllEAttributeTypes(ResourceManager resManager) {
		List<EClassifier> listOfTypes = new ArrayList<>(EcorePackage.eINSTANCE.getEClassifiers());
		listOfTypes.removeIf(e -> !(e instanceof EDataType));
		TreeIterator<Notifier> resourceSetContent = resManager.getEditingDomain().getResourceSet().getAllContents();
		while (resourceSetContent.hasNext()) {
			Notifier res = resourceSetContent.next();
			if (res instanceof EDataType) {
				listOfTypes.add((EClassifier) res);
			}
		}
		return listOfTypes;
	}

	@Override
	public String getLabel(AbstractOperationAction action) {
		return "Apply label";
	}
}
