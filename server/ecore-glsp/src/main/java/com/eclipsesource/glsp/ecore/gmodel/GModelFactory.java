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
package com.eclipsesource.glsp.ecore.gmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import com.eclipsesource.glsp.api.jsonrpc.GLSPServerException;
import com.eclipsesource.glsp.ecore.model.EcoreModelState;
import com.eclipsesource.glsp.ecore.util.EcoreConfig.CSS;
import com.eclipsesource.glsp.ecore.util.EcoreConfig.Types;
import com.eclipsesource.glsp.graph.GEdge;
import com.eclipsesource.glsp.graph.GGraph;
import com.eclipsesource.glsp.graph.GModelElement;
import com.eclipsesource.glsp.graph.GModelRoot;
import com.eclipsesource.glsp.graph.builder.impl.GEdgeBuilder;
import com.eclipsesource.glsp.graph.builder.impl.GEdgePlacementBuilder;
import com.eclipsesource.glsp.graph.builder.impl.GGraphBuilder;
import com.eclipsesource.glsp.graph.builder.impl.GLabelBuilder;
import com.eclipsesource.glsp.graph.util.GConstants;

public class GModelFactory extends AbstractGModelFactory<EObject, GModelElement> {

	private ClassifierNodeFactory classifierNodeFactory;
	private LabelFactory labelFactory;

	public GModelFactory(EcoreModelState modelState) {
		super(modelState);
		classifierNodeFactory = new ClassifierNodeFactory(modelState, this);
		labelFactory = new LabelFactory(modelState);
		getOrCreateRoot();

	}

	@Override
	public GModelElement create(EObject semanticElement) {
		GModelElement result = null;
		if (semanticElement instanceof EClassifier) {
			result = classifierNodeFactory.create((EClassifier) semanticElement);
		} else if (semanticElement instanceof EPackage) {
			result = create((EPackage) semanticElement);
		} else if (semanticElement instanceof EReference) {
			result = create((EReference) semanticElement);
		} else if (semanticElement instanceof ENamedElement) {
			result = labelFactory.create((ENamedElement) semanticElement);
		}
		if (result == null) {
			throw createFailed(semanticElement);
		}
		return result;
	}

	public GGraph create() {
		return create(modelState.getEditorContext().getEcoreFacade().getEPackage());
	}

	public GGraph create(EPackage ePackage) {
		GGraph graph = getOrCreateRoot();
		graph.setId(toId(ePackage));

		graph.getChildren().addAll(ePackage.getEClassifiers().stream()//
				.map(this::create)//
				.collect(Collectors.toList()));

		graph.getChildren().addAll(ePackage.getEClassifiers().stream() //
				.filter(EClass.class::isInstance) //
				.map(EClass.class::cast) //
				.flatMap(eClass -> createEdges(eClass).stream()) //
				.collect(Collectors.toList()));
		return graph;

	}

	private List<GModelElement> createEdges(EClass eClass) {
		List<GModelElement> children = new ArrayList<>();
		// create reference edges
		eClass.getEReferences().stream().map(this::create).forEach(children::add);
		// create inheritance edges
		eClass.getESuperTypes().stream().map(s -> create(eClass, s)).forEach(children::add);
		return children;
	}

	public GEdge create(EReference eReference) {
		String labelMultiplicity = String.format("[%s..%s]", eReference.getLowerBound(),
				eReference.getUpperBound() == -1 ? "*" : eReference.getUpperBound());
		String labelName = eReference.getName();

		String source = toId(eReference.getEContainingClass());

		String target = toId(eReference.getEReferenceType());

		String id = toId(eReference);
		return new GEdgeBuilder().type(eReference.isContainment() ? Types.COMPOSITION : Types.REFERENCE) //
				.id(id) //
				.addCssClass(CSS.ECORE_EDGE) //
				.addCssClass(eReference.isContainment() ? CSS.COMPOSITION : null) //
				.add(new GLabelBuilder(Types.LABEL_EDGE_MULTIPLICITY) //
						.edgePlacement(new GEdgePlacementBuilder()//
								.side(GConstants.EdgeSide.BOTTOM)//
								.position(0.5d)//
								.offset(2d) //
								.rotate(false) //
								.build())//
						.id(id + "_label_multiplicity") //
						.text(labelMultiplicity).build())
				.add(new GLabelBuilder(Types.LABEL_EDGE_NAME) //
					.edgePlacement(new GEdgePlacementBuilder()//
							.side(GConstants.EdgeSide.TOP)//
							.position(0.5d)//
							.offset(2d) //
							.rotate(false) //
							.build())//
					.id(id + "_label_name") //
					.text(labelName).build())
				.sourceId(source) //
				.targetId(target) //
				.routerKind(GConstants.RouterKind.MANHATTAN)//
				.build();
	}

	public GEdge create(EClass baseClass, EClass superClass) {
		String sourceId = toId(baseClass);
		String targetId = toId(superClass);
		if (sourceId.isEmpty() || sourceId.isEmpty()) {
			return null;
		}
		String id = sourceId + "_" + targetId;
		return new GEdgeBuilder(Types.INHERITANCE) //
				.id(id)//
				.addCssClass(CSS.ECORE_EDGE) //
				.addCssClass(CSS.INHERITANCE) //
				.sourceId(sourceId) //
				.targetId(targetId) //
				.routerKind(GConstants.RouterKind.MANHATTAN)//
				.build();
	}

	public static GLSPServerException createFailed(EObject semanticElement) {
		return new GLSPServerException("Error during model initialization!", new Throwable(
				"No matching GModelElement found for the semanticElement of type: " + semanticElement.getClass()));
	}

	private GGraph getOrCreateRoot() {
		GModelRoot existingRoot = modelState.getRoot();
		if (existingRoot != null && existingRoot instanceof GGraph) {
			GGraph graph = (GGraph) existingRoot;
			graph.getChildren().clear();
			return graph;
		} else {
			GGraph graph = new GGraphBuilder().build();
			modelState.setRoot(graph);
			return graph;
		}
	}
}
