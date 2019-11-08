/*******************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 *   This program and the accompanying materials are made available under the
 *   terms of the Eclipse Public License v. 2.0 which is available at
 *   http://www.eclipse.org/legal/epl-2.0.
 *
 *   This Source Code may also be made available under the following Secondary
 *   Licenses when the conditions for such availability set forth in the Eclipse
 *   Public License v. 2.0 are satisfied: GNU General Public License, version 2
 *   with the GNU Classpath Exception which is available at
 *   https://www.gnu.org/software/classpath/license.html.
 *
 *   SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ******************************************************************************/
import { injectable } from "inversify";
import * as snabbdom from "snabbdom-jsx";
import { VNode } from "snabbdom/vnode";
import {
  getSubType,
  IView,
  Point,
  PolylineEdgeView,
  RectangularNodeView,
  RenderingContext,
  SEdge,
  setAttr,
  SLabelView,
  toDegrees
} from "sprotty/lib";

import { Icon, LabeledNode, SLabelNode } from "./model";

/** @jsx svg */
const JSX = { createElement: snabbdom.svg };
@injectable()
export class ClassNodeView extends RectangularNodeView {
  render(node: LabeledNode, context: RenderingContext): VNode {
    return <g class-node={true}>
      <rect class-sprotty-node={true} class-selected={node.selected} class-mouseover={node.hoverFeedback}
        x={0} y={0} rx={10} ry={10}
        width={Math.max(0, node.bounds.width)} height={Math.max(0, node.bounds.height)} />
      {context.renderChildren(node)}
    </g>;
  }
}
@injectable()
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
@injectable()
export class ArrowEdgeView extends PolylineEdgeView {
  protected renderAdditionals(edge: SEdge, segments: Point[], context: RenderingContext): VNode[] {
    const p1 = segments[segments.length - 2];
    const p2 = segments[segments.length - 1];
    return [
      <path class-sprotty-edge={true} d="M 10,-4 L 0,0 L 10,4"
        transform={`rotate(${angle(p2, p1)} ${p2.x} ${p2.y}) translate(${p2.x} ${p2.y})`} />,
    ];
  }

  static readonly TARGET_CORRECTION = Math.sqrt(1 * 1 + 2.5 * 2.5);

  protected getTargetAnchorCorrection(edge: SEdge): number {
    return ArrowEdgeView.TARGET_CORRECTION;
  }

}

@injectable()
export class InheritanceEdgeView extends ArrowEdgeView {
  protected renderAdditionals(edge: SEdge, segments: Point[], context: RenderingContext): VNode[] {
    const p1 = segments[segments.length - 2];
    const p2 = segments[segments.length - 1];
    return [
      <path class-sprotty-edge={true} class-triangle={true} d="M 10,-8 L 0,0 L 10,8 Z" class-inheritance={true}
        transform={`rotate(${angle(p2, p1)} ${p2.x} ${p2.y}) translate(${p2.x} ${p2.y})`} />,
    ];
  }

  static readonly TARGET_CORRECTION = Math.sqrt(1 * 1 + 2.5 * 2.5);

  protected getTargetAnchorCorrection(edge: SEdge): number {
    return ArrowEdgeView.TARGET_CORRECTION;
  }
}

@injectable()
abstract class DiamondEdgeView extends PolylineEdgeView {
  protected renderAdditionals(edge: SEdge, segments: Point[], context: RenderingContext): VNode[] {
    const p1 = segments[0];
    const p2 = segments[1];
    const r = 6;
    const rhombStr = "M 0,0 l" + r + "," + (r / 2) + " l" + r + ",-" + (r / 2) + " l-" + r + ",-" + (r / 2) + " l-" + r + "," + (r / 2) + " Z";
    const firstEdgeAngle = angle(p1, p2);
    return [
      <path class-sprotty-edge={true} class-diamond={true} class-composition={this.isComposition()} d={rhombStr}
        transform={`rotate(${firstEdgeAngle} ${p1.x} ${p1.y}) translate(${p1.x} ${p1.y})`} />
    ];
  }

  static readonly SOURCE_CORRECTION = Math.sqrt(1 * 1 + 2 * 2);

  protected getSourceAnchorCorrection(edge: SEdge): number {
    return CompositionEdgeView.SOURCE_CORRECTION;
  }
  protected isComposition(): boolean {
    return false;
  }
  protected isAggregation(): boolean {
    return false;
  }
}

@injectable()
export class CompositionEdgeView extends DiamondEdgeView {
  protected isComposition(): boolean {
    return true;
  }
}

@injectable()
export class AggregationEdgeView extends DiamondEdgeView {
  protected isAggregation(): boolean {
    return true;
  }
}

@injectable()
export class LabelNodeView extends SLabelView {
  render(labelNode: Readonly<SLabelNode>, context: RenderingContext): VNode {
    const vnode = (
      <g
        class-selected={labelNode.selected}
        class-mouseover={labelNode.hoverFeedback}
        class-sprotty-label-node={true}
      >
        <text class-sprotty-label={true}>{labelNode.text}</text>
      </g>
    );

    const subType = getSubType(labelNode);
    if (subType) setAttr(vnode, "class", subType);
    return vnode;
  }
}

export function angle(x0: Point, x1: Point): number {
  return toDegrees(Math.atan2(x1.y - x0.y, x1.x - x0.x));
}
