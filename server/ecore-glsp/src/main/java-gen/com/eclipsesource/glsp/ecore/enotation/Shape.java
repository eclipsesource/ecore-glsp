/**
 *  Copyright (c) 2019 EclipseSource and others.
 * 
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v. 2.0 which is available at
 *  http://www.eclipse.org/legal/epl-2.0.
 * 
 *  This Source Code may also be made available under the following Secondary
 *  Licenses when the conditions for such availability set forth in the Eclipse
 *  Public License v. 2.0 are satisfied: GNU General Public License, version 2
 *  with the GNU Classpath Exception which is available at
 *  https://www.gnu.org/software/classpath/license.html.
 * 
 *  SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * 
 */
package com.eclipsesource.glsp.ecore.enotation;

import com.eclipsesource.glsp.graph.GDimension;
import com.eclipsesource.glsp.graph.GPoint;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Shape</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.eclipsesource.glsp.ecore.enotation.Shape#getPosition <em>Position</em>}</li>
 *   <li>{@link com.eclipsesource.glsp.ecore.enotation.Shape#getSize <em>Size</em>}</li>
 * </ul>
 *
 * @see com.eclipsesource.glsp.ecore.enotation.EnotationPackage#getShape()
 * @model
 * @generated
 */
public interface Shape extends NotationElement {
	/**
	 * Returns the value of the '<em><b>Position</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Position</em>' containment reference.
	 * @see #setPosition(GPoint)
	 * @see com.eclipsesource.glsp.ecore.enotation.EnotationPackage#getShape_Position()
	 * @model containment="true"
	 * @generated
	 */
	GPoint getPosition();

	/**
	 * Sets the value of the '{@link com.eclipsesource.glsp.ecore.enotation.Shape#getPosition <em>Position</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Position</em>' containment reference.
	 * @see #getPosition()
	 * @generated
	 */
	void setPosition(GPoint value);

	/**
	 * Returns the value of the '<em><b>Size</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Size</em>' containment reference.
	 * @see #setSize(GDimension)
	 * @see com.eclipsesource.glsp.ecore.enotation.EnotationPackage#getShape_Size()
	 * @model containment="true"
	 * @generated
	 */
	GDimension getSize();

	/**
	 * Sets the value of the '{@link com.eclipsesource.glsp.ecore.enotation.Shape#getSize <em>Size</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Size</em>' containment reference.
	 * @see #getSize()
	 * @generated
	 */
	void setSize(GDimension value);

} // Shape
