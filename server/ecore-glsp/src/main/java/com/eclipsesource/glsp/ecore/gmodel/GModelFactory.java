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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import com.eclipsesource.glsp.ecore.util.EcoreEdgeUtil;
import com.eclipsesource.glsp.api.jsonrpc.GLSPServerException;
import com.eclipsesource.glsp.ecore.model.EcoreModelState;
import com.eclipsesource.glsp.ecore.util.EcoreConfig.CSS;
import com.eclipsesource.glsp.ecore.util.EcoreConfig.Types;
import com.eclipsesource.glsp.graph.GEdge;
import com.eclipsesource.glsp.graph.GGraph;
import com.eclipsesource.glsp.graph.GLabel;
import com.eclipsesource.glsp.graph.GModelElement;
import com.eclipsesource.glsp.graph.GModelRoot;
import com.eclipsesource.glsp.graph.builder.impl.GEdgeBuilder;
import com.eclipsesource.glsp.graph.builder.impl.GEdgePlacementBuilder;
import com.eclipsesource.glsp.graph.builder.impl.GGraphBuilder;
import com.eclipsesource.glsp.graph.builder.impl.GLabelBuilder;
import com.eclipsesource.glsp.graph.util.GConstants;
import com.eclipsesource.glsp.server.operationhandler.DeleteOperationHandler;

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
		eClass.getEReferences().stream().map(this::create).filter(Objects::nonNull).forEach(children::add);
		// create inheritance edges
		eClass.getESuperTypes().stream().map(s -> create(eClass, s)).forEach(children::add);
		return children;
	}

	public GEdge create(EReference eReference) {
		String source = toId(eReference.getEContainingClass());
		String target = toId(eReference.getEReferenceType());
		String id = toId(eReference);

		GEdgeBuilder builder = new GEdgeBuilder().id(id) //
				.addCssClass(CSS.ECORE_EDGE) //
				.addCssClass(eReference.isContainment() ? CSS.COMPOSITION : null) //
				.sourceId(source) //
				.targetId(target) //
				.routerKind(GConstants.RouterKind.MANHATTAN);

		if (eReference.getEOpposite() != null) {
			return createBidirectionalEdge(eReference, builder);
		}

		String labelMultiplicity = createMultiplicity(eReference);
		String labelName = eReference.getName();
		return builder.type(eReference.isContainment() ? Types.COMPOSITION : Types.REFERENCE) //
				.add(createEdgeMultiplicityLabel(labelMultiplicity, id + "_label_multiplicity", 0.5d))
				.add(createEdgeNameLabel(labelName, id + "_label_name", 0.5d)).build();
	}

	private GEdge createBidirectionalEdge(EReference eReference, GEdgeBuilder builder) {
		Set<String> referenceSet = this.modelState.getIndex().getBidirectionalReferences();

		if (!eReference.isContainment()
				&& referenceSet.contains(EcoreEdgeUtil.getStringId(eReference.getEOpposite()))) {
			return null;
		}

		referenceSet.add(EcoreEdgeUtil.getStringId(eReference));

		String targetLabelMultiplicity = createMultiplicity(eReference.getEOpposite());
		String targetLabelName = eReference.getEOpposite().getName();
		String targetId = toId(eReference.getEOpposite());

		String sourceLabelMultiplicity = createMultiplicity(eReference);
		String sourceLabelName = eReference.getName();
		String sourceId = toId(eReference);

		return builder
				.type(eReference.isContainment() ? Types.BIDIRECTIONAL_COMPOSITION : Types.BIDIRECTIONAL_REFERENCE) //
				.add(createEdgeMultiplicityLabel(sourceLabelMultiplicity, sourceId + "_sourcelabel_multiplicity", 0.9d))//
				.add(createEdgeNameLabel(sourceLabelName, sourceId + "_sourcelabel_name", 0.9d))//
				.add(createEdgeMultiplicityLabel(targetLabelMultiplicity, targetId + "_targetlabel_multiplicity", 0.1d))//
				.add(createEdgeNameLabel(targetLabelName, targetId + "_targetlabel_name", 0.1d))//
				.build();
	}

	private String createMultiplicity(EReference eReference) {
		return String.format("[%s..%s]", eReference.getLowerBound(),
				eReference.getUpperBound() == -1 ? "*" : eReference.getUpperBound());
	}

	private GLabel createEdgeMultiplicityLabel(String value, String id, double position) {
		return createEdgeLabel(value, position, id, Types.LABEL_EDGE_MULTIPLICITY, GConstants.EdgeSide.BOTTOM);
	}

	private GLabel createEdgeNameLabel(String name, String id, double position) {
		return createEdgeLabel(name, position, id, Types.LABEL_EDGE_NAME, GConstants.EdgeSide.TOP);
	}

	private GLabel createEdgeLabel(String name, double position, String id, String type, String side) {
		return new GLabelBuilder(type) //
				.edgePlacement(new GEdgePlacementBuilder()//
						.side(side)//
						.position(position)//
						.offset(2d) //
						.rotate(false) //
						.build())//
				.id(id) //
				.text(name).build();
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
