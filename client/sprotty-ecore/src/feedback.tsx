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
import { IVNodeDecorator, SModelElement } from "sprotty";

import { SLabelNode } from "./model";

const JSX = { createElement: snabbdom.svg };

/**
 * A NodeDecorator to install visual feedback on selected NodeLabels
 */
@injectable()
export class LabelSelectionFeedback implements IVNodeDecorator {
  decorate(vnode: VNode, element: SModelElement): VNode {
    if (element instanceof SLabelNode && element.selected) {
      const vPadding = 3;
      const hPadding = 5;

      const feedback: VNode = (
        <rect
          x={-hPadding}
          y={-element.bounds.height / 2 - vPadding}
          width={element.bounds.width + 2 * hPadding}
          height={element.bounds.height + 2 * vPadding}
          class-selection-feedback={true}
        />
      );
      if (!vnode.children) {
        vnode.children = [];
      }
      vnode.children.push(feedback);
    }
    return vnode;
  }

  postUpdate(): void { }
}
