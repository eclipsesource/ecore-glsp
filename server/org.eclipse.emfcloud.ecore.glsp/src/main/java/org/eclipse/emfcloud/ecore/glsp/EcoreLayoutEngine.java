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
package org.eclipse.emfcloud.ecore.glsp;

import org.eclipse.elk.alg.layered.options.EdgeLabelSideSelection;
import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.options.EdgeLabelPlacement;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.emfcloud.ecore.glsp.model.EcoreModelState;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.graph.DefaultTypes;
import org.eclipse.glsp.graph.GGraph;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.layout.ElkLayoutEngine;
import org.eclipse.glsp.layout.GLSPLayoutConfigurator;

public class EcoreLayoutEngine extends ElkLayoutEngine {

	@Override
	public void layout(GraphicalModelState modelState) {
		GModelElement root = modelState.getRoot();
		if (root instanceof GGraph) {
			GLSPLayoutConfigurator configurator = new GLSPLayoutConfigurator();
			configurator.configureByType(DefaultTypes.GRAPH)
					.setProperty(LayeredOptions.EDGE_LABELS_PLACEMENT, EdgeLabelPlacement.CENTER)
					.setProperty(LayeredOptions.EDGE_LABELS_SIDE_SELECTION, EdgeLabelSideSelection.ALWAYS_UP);
			this.layout((GGraph) root, configurator);
		}
		EcoreFacade facade = EcoreModelState.getEcoreFacade(modelState);

		root.getChildren().stream() //
				.forEach(facade::updateNotationElement);

	}

	@Override
	protected boolean shouldInclude(GModelElement element, GModelElement parent, ElkGraphElement elkParent,
			LayoutContext context) {

		if (element.getType().equals("label:icon")) {
			return false;
		}
//		} else if (element instanceof GLabel && parent instanceof GEdge) {
//			return false;
//		}
		return super.shouldInclude(element, parent, elkParent, context);
	}

}
