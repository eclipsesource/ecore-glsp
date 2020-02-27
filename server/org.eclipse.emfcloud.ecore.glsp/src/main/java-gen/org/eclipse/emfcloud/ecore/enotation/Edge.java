/**
 *  Copyright (c) 2019-2020 EclipseSource and others.
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
package org.eclipse.emfcloud.ecore.enotation;

import org.eclipse.emf.common.util.EList;

import org.eclipse.glsp.graph.GPoint;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Edge</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emfcloud.ecore.enotation.Edge#getBendPoints <em>Bend Points</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emfcloud.ecore.enotation.EnotationPackage#getEdge()
 * @model
 * @generated
 */
public interface Edge extends NotationElement {
	/**
	 * Returns the value of the '<em><b>Bend Points</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.glsp.graph.GPoint}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Bend Points</em>' containment reference list.
	 * @see org.eclipse.emfcloud.ecore.enotation.EnotationPackage#getEdge_BendPoints()
	 * @model containment="true"
	 * @generated
	 */
	EList<GPoint> getBendPoints();

} // Edge
