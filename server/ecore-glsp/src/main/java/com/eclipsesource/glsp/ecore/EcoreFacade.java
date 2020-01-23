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
package com.eclipsesource.glsp.ecore;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.eclipsesource.glsp.ecore.enotation.Diagram;
import com.eclipsesource.glsp.ecore.enotation.Edge;
import com.eclipsesource.glsp.ecore.enotation.EnotationFactory;
import com.eclipsesource.glsp.ecore.enotation.NotationElement;
import com.eclipsesource.glsp.ecore.enotation.SemanticProxy;
import com.eclipsesource.glsp.ecore.enotation.Shape;
import com.eclipsesource.glsp.ecore.model.EcoreModelState;
import org.eclipse.glsp.graph.GEdge;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.graph.GShapeElement;
import org.eclipse.glsp.graph.util.GraphUtil;
import com.google.common.base.Preconditions;

public class EcoreFacade {

	private final Resource semanticResource;
	private final Resource notationResource;
	private final EPackage ePackage;

	private boolean diagramIsNewlyCreated = false;

	private Diagram diagram;
	private EcoreModelIndex modelIndex;

	public EcoreFacade(Resource semanticResource, Resource notationResource, EcoreModelIndex modelIndex) {
		this.semanticResource = semanticResource;
		this.notationResource = notationResource;
		this.modelIndex = modelIndex;
		this.ePackage = semanticResource.getContents().stream().filter(EPackage.class::isInstance)
				.map(EPackage.class::cast).findFirst().orElseThrow();
		EcoreUtil.resolveAll(ePackage);
	}

	public Resource getSemanticResource() {
		return semanticResource;
	}

	public Resource getNotationResource() {
		return notationResource;
	}

	public EPackage getEPackage() {
		return this.ePackage;
	}

	public Diagram getDiagram() {
		if (diagram == null) {
			getOrCreateDiagram();
		}
		return diagram;
	}

	private Diagram getOrCreateDiagram() {
		Optional<Diagram> existingDiagram = findDiagram();
		diagram = existingDiagram.isPresent() ? existingDiagram.get() : createDiagram();
		findUnresolvedElements(diagram).forEach(e -> e.setSemanticElement(resolved(e.getSemanticElement())));
		modelIndex.indexNotation(diagram);
		return diagram;

	}

	public Diagram initialize(Diagram diagram, GModelRoot gRoot) {
		Preconditions.checkArgument(diagram.getSemanticElement().getResolvedElement() == ePackage);
		gRoot.getChildren().forEach(child -> {
			modelIndex.getNotation(child).ifPresentOrElse(n -> updateNotationElement(n, child),
					() -> initializeNotationElement(child).ifPresent(diagram.getElements()::add));

		});
		return diagram;
	}

	public boolean diagramNeedsAutoLayout() {
		boolean oldValue = this.diagramIsNewlyCreated;
		this.diagramIsNewlyCreated = false;
		return oldValue;
	}

	public Optional<? extends NotationElement> initializeNotationElement(GModelElement gModelElement) {
		Optional<? extends NotationElement> result = Optional.empty();
		if (gModelElement instanceof GNode) {
			result = initializeShape((GNode) gModelElement);
		} else if (gModelElement instanceof GEdge) {
			result = initializeEdge((GEdge) gModelElement);
		}
		return result;
	}

	public List<NotationElement> findUnresolvedElements(Diagram diagram) {
		return diagram.getElements().stream()
				.filter(element -> resolved(element.getSemanticElement()).getResolvedElement() == null)
				.collect(Collectors.toList());
	}

	private Diagram createDiagram() {
		Diagram diagram = EnotationFactory.eINSTANCE.createDiagram();
		diagram.setSemanticElement(createProxy(ePackage));
		notationResource.getContents().add(diagram);
		diagramIsNewlyCreated = true;
		return diagram;
	}

	public Optional<Shape> initializeShape(GShapeElement shapeElement) {
		return modelIndex.getSemantic(shapeElement)
				.map(semanticElement -> initializeShape(semanticElement, shapeElement));

	}

	public Shape initializeShape(EObject semanticElement) {
		return initializeShape(semanticElement, null);
	}

	public Shape initializeShape(EObject semanticElement, GShapeElement shapeElement) {
		Shape shape = EnotationFactory.eINSTANCE.createShape();
		shape.setSemanticElement(createProxy(semanticElement));
		if (shapeElement != null) {
			updateShape(shape, shapeElement);

		}
		modelIndex.indexNotation(shape);
		return shape;
	}

	public Optional<Edge> initializeEdge(GEdge gEdge) {
		return modelIndex.getSemantic(gEdge).map(semanticElement -> initializeEdge(semanticElement, gEdge));
	}

	public Edge initializeEdge(EObject semanticElement) {
		return initializeEdge(semanticElement, null);
	}

	public Edge initializeEdge(EObject semanticElement, GEdge gEdge) {
		Edge edge = EnotationFactory.eINSTANCE.createEdge();
		edge.setSemanticElement(createProxy(semanticElement));
		if (gEdge != null) {
			updateEdge(edge, gEdge);
		}
		modelIndex.indexNotation(edge);
		return edge;
	}

	public SemanticProxy createProxy(EObject eObject) {
		SemanticProxy proxy = EnotationFactory.eINSTANCE.createSemanticProxy();
		proxy.setResolvedElement(eObject);
		proxy.setUri(semanticResource.getURIFragment(eObject));
		return proxy;
	}

	public SemanticProxy resolved(SemanticProxy proxy) {
		if (proxy.getResolvedElement() != null) {
			return proxy;
		}
		return reResolved(proxy);
	}

	public SemanticProxy reResolved(SemanticProxy proxy) {
		proxy.setResolvedElement(semanticResource.getEObject(proxy.getUri()));
		return proxy;
	}

	public void updateNotationElement(NotationElement notation, GModelElement modelElement) {
		if (notation instanceof Shape && modelElement instanceof GShapeElement) {
			updateShape((Shape) notation, (GShapeElement) modelElement);
		} else if (notation instanceof Edge && modelElement instanceof GEdge) {
			updateEdge((Edge) notation, (GEdge) modelElement);
		}
	}

	public void updateShape(Shape shape, GShapeElement shapeElement) {
		if (shapeElement.getSize() != null) {
			shape.setSize(GraphUtil.copy(shapeElement.getSize()));

		}
		if (shapeElement.getPosition() != null) {
			shape.setPosition(GraphUtil.copy(shapeElement.getPosition()));
		}
	}

	public void updateEdge(Edge edge, GEdge gEdge) {
		edge.getBendPoints().clear();
		if (gEdge.getRoutingPoints() != null) {
			edge.getBendPoints().addAll(gEdge.getRoutingPoints());
		}
	}

	public void updateNotationElement(GModelElement modelElement) {
		modelIndex.getNotation(modelElement).ifPresent(notation -> updateNotationElement(notation, modelElement));
	}

	private Optional<Diagram> findDiagram() {
		return notationResource.getContents().stream().filter(eObject -> isDiagramForEPackage(eObject))
				.map(Diagram.class::cast).findFirst();
	}

	private boolean isDiagramForEPackage(EObject eObject) {
		if (eObject instanceof Diagram) {
			Diagram diagram = (Diagram) eObject;

			return resolved(diagram.getSemanticElement()).getResolvedElement() == ePackage;

		}
		return false;
	}

}
