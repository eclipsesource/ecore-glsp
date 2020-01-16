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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;

import com.eclipsesource.glsp.api.diagram.DiagramConfiguration;
import com.eclipsesource.glsp.api.operations.Group;
import com.eclipsesource.glsp.api.operations.Operation;
import com.eclipsesource.glsp.api.types.EdgeTypeHint;
import com.eclipsesource.glsp.api.types.ShapeTypeHint;
import com.eclipsesource.glsp.ecore.util.EcoreConfig.Types;
import com.eclipsesource.glsp.graph.DefaultTypes;
import com.eclipsesource.glsp.graph.GraphPackage;
import com.google.common.collect.Lists;

public class EcoreDiagramConfiguration implements DiagramConfiguration {

	@Override
	public String getDiagramType() {
		return "ecorediagram";
	}

	@Override
	public List<EdgeTypeHint> getEdgeTypeHints() {
		return Lists.newArrayList(createDefaultEdgeTypeHint(Types.REFERENCE),
				createDefaultEdgeTypeHint(Types.COMPOSITION), 
				createDefaultEdgeTypeHint(Types.INHERITANCE), 
				createDefaultEdgeTypeHint(Types.BIDIRECTIONAL_REFERENCE) , 
				createDefaultEdgeTypeHint(Types.BIDIRECTIONAL_COMPOSITION));
	}

	@Override
	public EdgeTypeHint createDefaultEdgeTypeHint(String elementId) {
		List<String> allowed = Lists.newArrayList(Types.ECLASS, Types.INTERFACE, Types.ABSTRACT);
		return new EdgeTypeHint(elementId, true, true, true, allowed, allowed);
	}

	@Override
	public List<ShapeTypeHint> getNodeTypeHints() {
		List<ShapeTypeHint> hints = new ArrayList<>();
		hints.add(new ShapeTypeHint(DefaultTypes.GRAPH, false, false, false, false,
				List.of(Types.ECLASS, Types.ABSTRACT, Types.INTERFACE, Types.ENUM, Types.DATATYPE)));
		hints.add(new ShapeTypeHint(Types.ECLASS, true, true, false, false, List.of(Types.ATTRIBUTE, Types.OPERATION)));
		hints.add(new ShapeTypeHint(Types.ENUM, true, true, false, false, List.of(Types.ENUMLITERAL)));
		hints.add(new ShapeTypeHint(Types.DATATYPE, true, true, false, true));
		hints.add(new ShapeTypeHint(Types.ATTRIBUTE, false, true, false, true));
		hints.add(new ShapeTypeHint(Types.OPERATION, false, true, false, true));
		hints.add(new ShapeTypeHint(Types.ENUMLITERAL, false, true, false, true));
		return hints;
	}

	@Override
	public List<Operation> getOperations() {
		Group classifierGroup = new Group("ecore.classifier", "Classifier");
		Group classGroup = new Group("ecore.class", "Class", classifierGroup);
		Operation createEClass = new Operation("Class", Types.ECLASS, Operation.Kind.CREATE_NODE, classGroup);
		Operation createAbstract = new Operation("Abstract", Types.ABSTRACT, Operation.Kind.CREATE_NODE, classGroup);
		Operation createInterface = new Operation("Interface", Types.INTERFACE, Operation.Kind.CREATE_NODE, classGroup);
		Operation createEnum = new Operation("Enum", Types.ENUM, Operation.Kind.CREATE_NODE, classifierGroup);
		Operation createDataType = new Operation("DataType", Types.DATATYPE, Operation.Kind.CREATE_NODE,
				classifierGroup);
		Group relationGroup = new Group("ecore.relation", "Relation");
		Operation createEcoreEdge = new Operation("Reference", Types.REFERENCE, Operation.Kind.CREATE_CONNECTION,
				relationGroup);
		Operation createComposition = new Operation("Containment", Types.COMPOSITION, Operation.Kind.CREATE_CONNECTION,
				relationGroup);
		Operation createInheritance = new Operation("Inheritance", Types.INHERITANCE, Operation.Kind.CREATE_CONNECTION,
				relationGroup);
		Operation createBiReference = new Operation("Bi-Directional Reference", Types.BIDIRECTIONAL_REFERENCE, Operation.Kind.CREATE_CONNECTION,
				relationGroup);
		Operation createBiComposition = new Operation("Bi-Directional Containment", Types.BIDIRECTIONAL_COMPOSITION, Operation.Kind.CREATE_CONNECTION,
				relationGroup);

		Group featureGroup = new Group("ecore.feature", "Feature");
		Operation createAttributeOperation = new Operation("Attribute", Types.ATTRIBUTE, Operation.Kind.CREATE_NODE,
				featureGroup);
		Operation createEnumLiteral = new Operation("Literal", Types.ENUMLITERAL, Operation.Kind.CREATE_NODE,
				featureGroup);
		List<Operation> operations = Lists.newArrayList(createEClass, createAbstract, createInterface, createEnum,
				createDataType, createAttributeOperation, createEnumLiteral, createEcoreEdge, createComposition,
				createInheritance, createBiReference, createBiComposition);
		return operations;
	}

	@Override
	public Map<String, EClass> getTypeMappings() {
		Map<String, EClass> mappings = DefaultTypes.getDefaultTypeMappings();

		mappings.put(Types.LABEL_NAME, GraphPackage.Literals.GLABEL);
		mappings.put(Types.LABEL_TEXT, GraphPackage.Literals.GLABEL);
		mappings.put(Types.LABEL_EDGE_NAME, GraphPackage.Literals.GLABEL);
		mappings.put(Types.LABEL_EDGE_MULTIPLICITY, GraphPackage.Literals.GLABEL);
		mappings.put(Types.COMP, GraphPackage.Literals.GCOMPARTMENT);
		mappings.put(Types.COMP_HEADER, GraphPackage.Literals.GCOMPARTMENT);
		mappings.put(Types.LABEL_ICON, GraphPackage.Literals.GCOMPARTMENT);
		mappings.put(Types.ICON_CLASS, GraphPackage.Literals.GCOMPARTMENT);
		mappings.put(Types.ICON_ABSTRACT, GraphPackage.Literals.GCOMPARTMENT);
		mappings.put(Types.ICON_INTERFACE, GraphPackage.Literals.GCOMPARTMENT);
		mappings.put(Types.ICON_ENUM, GraphPackage.Literals.GCOMPARTMENT);
		mappings.put(Types.ICON_DATATYPE, GraphPackage.Literals.GCOMPARTMENT);

		// ecore stuff
		mappings.put(Types.ECLASS, GraphPackage.Literals.GNODE);
		mappings.put(Types.ENUM, GraphPackage.Literals.GNODE);
		mappings.put(Types.DATATYPE, GraphPackage.Literals.GNODE);
		mappings.put(Types.REFERENCE, GraphPackage.Literals.GEDGE);
		mappings.put(Types.INHERITANCE, GraphPackage.Literals.GEDGE);
		mappings.put(Types.COMPOSITION, GraphPackage.Literals.GEDGE);
		mappings.put(Types.BIDIRECTIONAL_REFERENCE, GraphPackage.Literals.GEDGE);
		mappings.put(Types.BIDIRECTIONAL_COMPOSITION, GraphPackage.Literals.GEDGE);
		mappings.put(Types.ATTRIBUTE, GraphPackage.Literals.GLABEL);
		mappings.put(Types.OPERATION, GraphPackage.Literals.GLABEL);
		mappings.put(Types.ENUMLITERAL, GraphPackage.Literals.GLABEL);
		mappings.put(Types.LABEL_INSTANCE, GraphPackage.Literals.GLABEL);
		return mappings;
	}

}
