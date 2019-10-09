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
import {
    boundsFeature,
    deletableFeature,
    EditableLabel,
    editLabelFeature,
    fadeFeature,
    isEditableLabel,
    layoutableChildFeature,
    layoutContainerFeature,
    Nameable,
    nameFeature,
    popupFeature,
    RectangularNode,
    selectFeature,
    SLabel,
    SShapeElement,
    WithEditableLabel,
    withEditLabelFeature
} from "sprotty/lib";


export class LabeledNode extends RectangularNode implements WithEditableLabel, Nameable {

    get editableLabel() {
        const headerComp = this.children.find(element => element.type === 'comp:header');
        if (headerComp) {
            const label = headerComp.children.find(element => element.type === 'label:heading');
            if (label && isEditableLabel(label)) {
                return label;
            }
        }
        return undefined;
    }

    get name() {
        if (this.editableLabel) {
            return this.editableLabel.text;
        }
        return this.id;
    }
    hasFeature(feature: symbol) {
        return super.hasFeature(feature) || feature === nameFeature || feature === withEditLabelFeature;
    }

}

export class SEditableLabel extends SLabel implements EditableLabel {
    hasFeature(feature: symbol) {
        return feature === editLabelFeature || super.hasFeature(feature);
    }
}

export class Icon extends SShapeElement {
    size = {
        width: 32,
        height: 32
    };

    hasFeature(feature: symbol): boolean {
        return feature === boundsFeature || feature === layoutContainerFeature || feature === layoutableChildFeature || feature === fadeFeature;
    }
}

export class SLabelNode extends SLabel {
    hoverFeedback: boolean = false;

    hasFeature(feature: symbol): boolean {
        return (feature === selectFeature || feature === popupFeature || feature === deletableFeature || super.hasFeature(feature));
    }
}
