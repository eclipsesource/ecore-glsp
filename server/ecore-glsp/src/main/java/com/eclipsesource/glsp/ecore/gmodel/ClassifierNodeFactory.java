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

import java.util.Collection;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;

import com.eclipsesource.glsp.ecore.enotation.Shape;
import com.eclipsesource.glsp.ecore.model.EcoreModelState;
import com.eclipsesource.glsp.ecore.util.EcoreConfig.CSS;
import com.eclipsesource.glsp.ecore.util.EcoreConfig.Types;
import com.eclipsesource.glsp.graph.GCompartment;
import com.eclipsesource.glsp.graph.GNode;
import com.eclipsesource.glsp.graph.builder.impl.GCompartmentBuilder;
import com.eclipsesource.glsp.graph.builder.impl.GLabelBuilder;
import com.eclipsesource.glsp.graph.builder.impl.GLayoutOptionsBuilder;
import com.eclipsesource.glsp.graph.builder.impl.GNodeBuilder;
import com.eclipsesource.glsp.graph.util.GConstants;
import com.eclipsesource.glsp.graph.util.GraphUtil;

public class ClassifierNodeFactory extends AbstractGModelFactory<EClassifier, GNode> {

	private GModelFactory parentFactory;

	public ClassifierNodeFactory(EcoreModelState modelState, GModelFactory parentFactory) {
		super(modelState);
		this.parentFactory = parentFactory;
	}

	@Override
	public GNode create(EClassifier classifier) {

		if (classifier instanceof EClass) {
			return create((EClass) classifier);
		} else if (classifier instanceof EEnum) {
			return create((EEnum) classifier);
		} else if (classifier instanceof EDataType) {
			return create((EDataType) classifier);
		}
		return null;
	}

	public GNode create(EClass eClass) {
		GNodeBuilder b = new GNodeBuilder(Types.ECLASS) //
				.id(toId(eClass)) //
				.layout(GConstants.Layout.VBOX) //
				.addCssClass(CSS.NODE) //
				.add(buildHeader(eClass))//
				.add(createLabeledChildrenCompartment(eClass.getEAttributes()));

		if (eClass.isAbstract()) {
			b.addCssClass(CSS.ABSTRACT);
		} else if (eClass.isInterface()) {
			b.addCssClass(CSS.INTERFACE);
		}
		applyShapeData(eClass, b);
		return b.build();
	}

	public GNode create(EEnum eEnum) {
		GNodeBuilder b = new GNodeBuilder(Types.ENUM) //
				.id(toId(eEnum)) //
				.layout(GConstants.Layout.VBOX) //
				.layoutOptions(new GLayoutOptionsBuilder().resizeContainer(true).build()) //
				.addCssClass(CSS.NODE) //
				.add(buildHeader(eEnum))//
				.add(createLabeledChildrenCompartment(eEnum.getELiterals()));
		applyShapeData(eEnum, b);

		return b.build();
	}

	public GNode create(EDataType eDataType) {
		GNodeBuilder b = new GNodeBuilder(Types.DATATYPE) //
				.id(toId(eDataType)) //
				.layout(GConstants.Layout.VBOX) //
				.layoutOptions(new GLayoutOptionsBuilder().resizeContainer(true).build()) //
				.addCssClass(CSS.NODE) //
				.add(buildHeader(eDataType))//
				.add(new GCompartmentBuilder(Types.COMP) //
						.layout(GConstants.Layout.VBOX) //
						.layoutOptions(new GLayoutOptionsBuilder() //
								.hAlign(GConstants.HAlign.CENTER) //
								.resizeContainer(true) //
								.build()) //
						.add(new GLabelBuilder(Types.LABEL_INSTANCE) //
								.addCssClass(CSS.ITALIC)//
								.id(toId(eDataType) + Types.LABEL_INSTANCE)//
								.text(eDataType.getInstanceClassName()) //
								.build())
						.build());

		applyShapeData(eDataType, b);
		return b.build();

	}

	private void applyShapeData(EClassifier classifier, GNodeBuilder builder) {
		modelState.getIndex().getNotation(classifier, Shape.class).ifPresent(shape -> {
			if (shape.getPosition() != null) {
				builder.position(GraphUtil.copy(shape.getPosition()));
			} else if (shape.getSize() != null) {
				builder.size(GraphUtil.copy(shape.getSize()));
			}
		});
	}

	private GCompartment buildHeader(EClassifier classifier) {
		return new GCompartmentBuilder(Types.COMP_HEADER) //
				.layout("hbox") //
				.add(new GCompartmentBuilder(getType(classifier)) //
						.build()) //
				.add(new GLabelBuilder(Types.LABEL_NAME) //
						.text(classifier.getName()) //
						.build()) //
				.build();
	}

	private GCompartment createLabeledChildrenCompartment(Collection<? extends EObject> children) {
		return new GCompartmentBuilder(Types.COMP) //
				.layout(GConstants.Layout.VBOX) //
				.layoutOptions(new GLayoutOptionsBuilder() //
						.hAlign(GConstants.HAlign.LEFT) //
						.resizeContainer(true) //
						.build()) //
				.addAll(children.stream() //
						.map(parentFactory::create) //
						.collect(Collectors.toList()))
				.build();
	}


	public static String getType(EClassifier classifier) {
		if (classifier instanceof EClass) {
			EClass eClass = (EClass) classifier;
			if (eClass.isAbstract()) {
				return Types.ICON_ABSTRACT;
			} else if (eClass.isInterface()) {
				return Types.ICON_INTERFACE;
			}
			return Types.ICON_CLASS;
		} else if (classifier instanceof EEnum) {
			return Types.ICON_ENUM;
		} else if (classifier instanceof EDataType) {
			return Types.ICON_DATATYPE;
		}

		return "Classifier not found";
	}
}
