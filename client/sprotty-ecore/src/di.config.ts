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
import "@glsp/sprotty-client/css/glsp-sprotty.css";
import "sprotty/css/edit-label.css";

import {
    boundsModule,
    buttonModule,
    commandPaletteModule,
    configureModelElement,
    configureViewerOptions,
    ConsoleLogger,
    decorationModule,
    defaultGLSPModule,
    defaultModule,
    edgeLayoutModule,
    expandModule,
    exportModule,
    fadeModule,
    glspCommandPaletteModule,
    glspEditLabelValidationModule,
    GLSPGraph,
    glspMouseToolModule,
    glspSelectModule,
    hoverModule,
    HtmlRoot,
    HtmlRootView,
    labelEditModule,
    labelEditUiModule,
    layoutCommandsModule,
    LogLevel,
    modelHintsModule,
    modelSourceModule,
    openModule,
    paletteModule,
    PolylineEdgeView,
    RectangularNodeView,
    requestResponseModule,
    routingModule,
    saveModule,
    SCompartment,
    SCompartmentView,
    SEdge,
    SGraphView,
    SLabel,
    SLabelView,
    SNode,
    SRoutingHandle,
    SRoutingHandleView,
    toolFeedbackModule,
    TYPES,
    validationModule,
    viewportModule,
    zorderModule
} from "@glsp/sprotty-client/lib";
import executeCommandModule from "@glsp/sprotty-client/lib/features/execute/di.config";
import { Container, ContainerModule } from "inversify";
import {EditLabelUIAutocomplete} from "./features/edit-label-autocomplete";
import { EditLabelUI } from "sprotty/lib";
import { LabelSelectionFeedback } from "./feedback";
import { Icon, LabeledNode, SEditableLabel, SLabelNode } from "./model";
import { ArrowEdgeView, ClassNodeView, CompositionEdgeView, IconView, InheritanceEdgeView, LabelNodeView } from "./views";

export default (containerId: string) => {
    const classDiagramModule = new ContainerModule((bind, unbind, isBound, rebind) => {
        rebind(TYPES.ILogger).to(ConsoleLogger).inSingletonScope();
        rebind(TYPES.LogLevel).toConstantValue(LogLevel.info);
        rebind(EditLabelUI).to(EditLabelUIAutocomplete);

        const context = { bind, unbind, isBound, rebind };
        bind(TYPES.IVNodePostprocessor).to(LabelSelectionFeedback);
        configureModelElement(context, 'graph', GLSPGraph, SGraphView);
        configureModelElement(context, 'node:class', LabeledNode, ClassNodeView);
        configureModelElement(context, 'node:enum', LabeledNode, ClassNodeView);
        configureModelElement(context, 'node:datatype', LabeledNode, ClassNodeView);
        configureModelElement(context, 'label:name', SEditableLabel, SLabelView);
        configureModelElement(context, 'label:edge', SLabel, SLabelView);
        configureModelElement(context, 'node:attribute', SLabelNode, LabelNodeView);
        configureModelElement(context, 'node:enumliteral', SNode, RectangularNodeView);
        configureModelElement(context, 'node:operation', SNode, RectangularNodeView);
        configureModelElement(context, 'label:text', SLabel, SLabelView);
        configureModelElement(context, 'comp:comp', SCompartment, SCompartmentView);
        configureModelElement(context, 'comp:header', SCompartment, SCompartmentView);
        configureModelElement(context, 'icon', Icon, IconView);
        configureModelElement(context, 'label:icon', SLabel, SLabelView);
        configureModelElement(context, 'html', HtmlRoot, HtmlRootView);
        configureModelElement(context, 'routing-point', SRoutingHandle, SRoutingHandleView);
        configureModelElement(context, 'volatile-routing-point', SRoutingHandle, SRoutingHandleView);
        configureModelElement(context, 'edge:reference', SEdge, ArrowEdgeView);
        configureModelElement(context, 'edge:inheritance', SEdge, InheritanceEdgeView);
        configureModelElement(context, 'edge:composition', SEdge, CompositionEdgeView);
        configureModelElement(context, 'edge', SEdge, PolylineEdgeView);
        configureViewerOptions(context, {
            needsClientLayout: true,
            baseDiv: containerId
        });
    });

    const container = new Container();
    container.load(decorationModule, validationModule, defaultModule, glspMouseToolModule, defaultGLSPModule, glspSelectModule, boundsModule, viewportModule,
        hoverModule, fadeModule, exportModule, expandModule, openModule, buttonModule, modelSourceModule, labelEditModule, labelEditUiModule, glspEditLabelValidationModule,
        classDiagramModule, saveModule, executeCommandModule, toolFeedbackModule, modelHintsModule,
        commandPaletteModule, glspCommandPaletteModule, paletteModule, requestResponseModule, routingModule, edgeLayoutModule,
        layoutCommandsModule, zorderModule);
    return container;

};
