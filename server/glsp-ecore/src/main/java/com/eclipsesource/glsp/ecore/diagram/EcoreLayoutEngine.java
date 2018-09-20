package com.eclipsesource.glsp.ecore.diagram;

import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeType;
import org.eclipse.elk.core.options.SizeConstraint;

import io.typefox.sprotty.api.SGraph;
import io.typefox.sprotty.api.SModelRoot;
import io.typefox.sprotty.layout.ElkLayoutEngine;
import io.typefox.sprotty.layout.SprottyLayoutConfigurator;

public class EcoreLayoutEngine extends ElkLayoutEngine {

	@Override
	public void layout(SModelRoot root) {
		if (root instanceof SGraph) {
			SprottyLayoutConfigurator configurator = new SprottyLayoutConfigurator();
			configurator.configureByType("graph")
			.setProperty(CoreOptions.DIRECTION, Direction.UP)
			.setProperty(CoreOptions.SPACING_EDGE_EDGE, 50.0)
			.setProperty(CoreOptions.SPACING_EDGE_NODE, 50.0)
			.setProperty(CoreOptions.SPACING_NODE_NODE, 50.0)
			.setProperty(LayeredOptions.SPACING_EDGE_NODE_BETWEEN_LAYERS, 40.0)
			.setProperty(LayeredOptions.SPACING_NODE_NODE_BETWEEN_LAYERS, 40.0)
			.setProperty(LayeredOptions.SPACING_EDGE_EDGE_BETWEEN_LAYERS, 40.0)
			
			;
			
			configurator.configureByType("edge:composition")
			.setProperty(CoreOptions.EDGE_TYPE, EdgeType.ASSOCIATION);
			
			configurator.configureByType("edge:aggregation")
			.setProperty(CoreOptions.EDGE_TYPE, EdgeType.ASSOCIATION);
			
			configurator.configureByType("edge:inheritance")
			.setProperty(CoreOptions.EDGE_TYPE, EdgeType.GENERALIZATION);
			
			
			configurator.configureByType("node:class")
					.setProperty(CoreOptions.NODE_SIZE_CONSTRAINTS, SizeConstraint.minimumSize())
					.setProperty(CoreOptions.NODE_SIZE_MINIMUM, new KVector(200, 200))
					;
			this.layout((SGraph) root, configurator);
		}
	}
	
}
