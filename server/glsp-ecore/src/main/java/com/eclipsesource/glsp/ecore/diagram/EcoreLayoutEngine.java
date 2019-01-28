package com.eclipsesource.glsp.ecore.diagram;

import org.eclipse.elk.alg.layered.options.LayeredOptions;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.options.Direction;
import org.eclipse.elk.core.options.EdgeType;
import org.eclipse.elk.graph.ElkGraphElement;
import org.eclipse.elk.graph.ElkShape;
import org.eclipse.sprotty.BoundsAware;
import org.eclipse.sprotty.SCompartment;
import org.eclipse.sprotty.SGraph;
import org.eclipse.sprotty.SLabel;
import org.eclipse.sprotty.SModelElement;
import org.eclipse.sprotty.SModelRoot;
import org.eclipse.sprotty.SNode;
import org.eclipse.sprotty.layout.ElkLayoutEngine;
import org.eclipse.sprotty.layout.SprottyLayoutConfigurator;


public class EcoreLayoutEngine extends ElkLayoutEngine {

	@Override
	public void layout(SModelRoot root) {
		if (root instanceof SGraph) {
			SprottyLayoutConfigurator configurator = new SprottyLayoutConfigurator();
			configurator.configureByType("graph")
			.setProperty(CoreOptions.DIRECTION, Direction.UP)
			.setProperty(CoreOptions.SPACING_EDGE_EDGE, 50.0)
			.setProperty(CoreOptions.SPACING_EDGE_NODE, 50.0)
			.setProperty(CoreOptions.SPACING_NODE_NODE, 70.0)
			.setProperty(LayeredOptions.SPACING_EDGE_NODE_BETWEEN_LAYERS, 50.0)
			.setProperty(LayeredOptions.SPACING_NODE_NODE_BETWEEN_LAYERS, 70.0)
			.setProperty(LayeredOptions.SPACING_EDGE_EDGE_BETWEEN_LAYERS, 50.0)
			;
			
			configurator.configureByType("edge:composition")
			.setProperty(CoreOptions.EDGE_TYPE, EdgeType.ASSOCIATION);
			
			configurator.configureByType("edge:aggregation")
			.setProperty(CoreOptions.EDGE_TYPE, EdgeType.ASSOCIATION);
			
			configurator.configureByType("edge:inheritance")
			.setProperty(CoreOptions.EDGE_TYPE, EdgeType.GENERALIZATION);
			
			
			configurator.configureByType("node:class")
			.setProperty(CoreOptions.DIRECTION, Direction.DOWN)	;
			this.layout((SGraph) root, configurator);
		}
	}

	// alot of magic number. The only other way would be to provide a complete own layout engine to convert the sprotty model to an elk model
	protected void applyBounds(BoundsAware bounds, ElkShape elkShape) {
		super.applyBounds(bounds, elkShape);
		if(bounds instanceof SLabel) {
			elkShape.setWidth(Math.max(elkShape.getWidth(), ((SLabel)bounds).getText().length()*11));
			if(((SLabel) bounds).getType().equals("label:heading")) {
				elkShape.setWidth(elkShape.getWidth()+100);	
			}
			elkShape.setHeight(Math.max(elkShape.getHeight(), 20));
		}
		if(bounds instanceof SNode) {
			elkShape.setHeight(getNodeHeight((SNode)bounds) + 100);
		}
	}
	private double getNodeHeight(SModelElement node) {
		double result = 20;
		for(SModelElement child:node.getChildren()) {
			if(child instanceof SCompartment && ((SCompartment)child).getLayout()=="vbox")
				result += getNodeHeight(child);
			else
				result += 20;
		}
		return result;
	}
	
	protected boolean shouldInclude(SModelElement element, SModelElement sParent, ElkGraphElement elkParent, LayoutContext context) {
		return true;
	}
	
}
