/*
 * Copyright (C) 2017 TypeFox and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */

/** @jsx svg */
import { svg }  from 'snabbdom-jsx';

import { RenderingContext, RectangularNodeView, IView, PolylineEdgeView, SEdge, Point, toDegrees, SLabelView} from "sprotty/lib";
import { VNode } from "snabbdom/vnode";
import { Icon, ClassNode, EdgeWithMultiplicty, Link } from './model';

export class ClassNodeView extends RectangularNodeView {
    render(node: ClassNode, context: RenderingContext): VNode {
        return <g class-node={true}>
            <rect class-sprotty-node={true} class-selected={node.selected} class-mouseover={node.hoverFeedback}
                x={0} y={0} rx={10} ry={10}
                width={Math.max(0, node.bounds.width)} height={Math.max(0, node.bounds.height)} />
            {context.renderChildren(node)}
        </g>;
    }
}
export class LinkView extends SLabelView {
    render(element: Link, context: RenderingContext): VNode {
    return <a href={element.target} target="_empty"><text class-sprotty-label={true}>{element.text}</text></a>
            }
}
export class IconView implements IView {

    render(element: Icon, context: RenderingContext): VNode {
        const radius = this.getRadius();
        return <g>
            <circle class-sprotty-icon={true} r={radius} cx={radius} cy={radius}></circle>
            {context.renderChildren(element)}
        </g>;
    }

    getRadius() {
        return 16;
    }
}

export class ArrowEdgeView extends PolylineEdgeView {
    protected renderAdditionals(edge: SEdge, segments: Point[], context: RenderingContext): VNode[] {
        const p1 = segments[segments.length - 2];
        const p2 = segments[segments.length - 1];
        return [
            <path class-sprotty-edge={true} d="M 10,-4 L 0,0 L 10,4"
                transform={`rotate(${angle(p2, p1)} ${p2.x} ${p2.y}) translate(${p2.x} ${p2.y})`} />,
        ]
    }

    static readonly TARGET_CORRECTION = Math.sqrt(1 * 1 + 2.5 * 2.5)

    protected getTargetAnchorCorrection(edge: SEdge): number {
        return ArrowEdgeView.TARGET_CORRECTION
    }

}
export class InheritanceEdgeView extends ArrowEdgeView {
    protected renderAdditionals(edge: SEdge, segments: Point[], context: RenderingContext): VNode[] {
        const p1 = segments[segments.length - 2];
        const p2 = segments[segments.length - 1];
        return [
            <path class-sprotty-edge={true} d="M 10,-4 L 0,0 L 10,4 Z" class-inheritance={true}
                transform={`rotate(${angle(p2, p1)} ${p2.x} ${p2.y}) translate(${p2.x} ${p2.y})`} />,
        ]
    }
    
    static readonly TARGET_CORRECTION = Math.sqrt(1 * 1 + 2.5 * 2.5)
    
    protected getTargetAnchorCorrection(edge: SEdge): number {
        return ArrowEdgeView.TARGET_CORRECTION
    }
}

abstract class DiamondEdgeView extends PolylineEdgeView {
    protected renderAdditionals(edge: EdgeWithMultiplicty, segments: Point[], context: RenderingContext): VNode[] {
        const p1 = segments[0]
        const p2 = segments[1]
        const r = 6;
        const rhombStr = "M 0,0 l" + r + "," + (r / 2) + " l" + r + ",-" + (r / 2) + " l-" + r + ",-" + (r / 2) + " l-" + r + "," + (r / 2) + " Z";
        const firstEdgeAngle = angle(p1, p2);
        const pn = segments[segments.length - 1];
        const pn_1 = segments[segments.length - 2];
        const lastEdgeAngle = angle(pn_1, pn);
        return [
            <path class-sprotty-edge={true} class-diamond={true} class-composition={this.isComposition()} class-aggregation={this.isAggregation()} d={rhombStr}
                transform={`rotate(${firstEdgeAngle} ${p1.x} ${p1.y}) translate(${p1.x} ${p1.y})`} />,
            <text class-sprotty-label={true} transform={`rotate(${firstEdgeAngle} ${p1.x} ${p1.y}) translate(${p1.x + 15} ${p1.y - 5})`}>{edge.multiplicitySource}</text>,
            <text class-sprotty-label={true} transform={`rotate(${lastEdgeAngle} ${pn.x} ${pn.y}) translate(${pn.x - 15} ${pn.y - 5})`}>{edge.multiplicityTarget}</text>
        ]
    }

    static readonly SOURCE_CORRECTION = Math.sqrt(1 * 1 + 2 * 2)

    protected getSourceAnchorCorrection(edge: SEdge): number {
        return CompositionEdgeView.SOURCE_CORRECTION
    }
    protected isComposition(): boolean {
        return false;
    }
    protected isAggregation(): boolean {
        return false;
    }
}
export class CompositionEdgeView extends DiamondEdgeView {
    protected isComposition(): boolean {
        return true;
    }
}
export class AggregationEdgeView extends DiamondEdgeView {
    protected isAggregation(): boolean {
        return true;
    }
}

export function angle(x0: Point, x1: Point): number {
    return toDegrees(Math.atan2(x1.y - x0.y, x1.x - x0.x))
}