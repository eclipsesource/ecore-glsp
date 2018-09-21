/*******************************************************************************
 * Copyright (c) 2018 EclipseSource Muenchen GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *   
 * Contributors:
 * 	EclipseSource Muenchen GmbH - initial API and implementation
 ******************************************************************************/
import { ContainerModule, interfaces } from "inversify";

import { EcoreGLClientContribution } from "./ecore-glclient-contribution";
import { DiagramConfiguration, DiagramManagerProvider, DiagramManager } from "theia-glsp/lib";
import { EcoreDiagramConfiguration } from "./di.config";
import { EcoreDiagramManager } from "./ecore-diagram-manager.";
import { EcoreLanguage } from "../common/ecore-language";
import { FrontendApplicationContribution, OpenHandler } from "@theia/core/lib/browser";
import { GraphicalLanguageClientContribution, GLSPPaletteContribution } from "glsp-theia-extension/lib/browser";
import { MenuContribution, CommandContribution } from "@theia/core";

export default new ContainerModule((bind: interfaces.Bind, unbind: interfaces.Unbind, isBound: interfaces.IsBound, rebind: interfaces.Rebind) => {
    
    
    bind(EcoreGLClientContribution).toSelf().inSingletonScope()
    bind(GraphicalLanguageClientContribution).toDynamicValue(ctx => ctx.container.get(EcoreGLClientContribution)).inSingletonScope();
    bind(DiagramConfiguration).to(EcoreDiagramConfiguration).inSingletonScope()
    bind(DiagramManagerProvider).toProvider<DiagramManager>(context => {
        return () => {
            return new Promise<DiagramManager>((resolve) =>
                resolve(context.container.get(EcoreDiagramManager))
            )
        }
    }).whenTargetNamed(EcoreLanguage.DiagramType)

    bind(EcoreDiagramManager).toSelf().inSingletonScope()
    bind(FrontendApplicationContribution).toDynamicValue(context =>
        context.container.get(EcoreDiagramManager))
    bind(OpenHandler).toDynamicValue(context => context.container.get(EcoreDiagramManager))
    bind(GLSPPaletteContribution).toSelf().inSingletonScope()
    bind(MenuContribution).toDynamicValue(ctx => ctx.container.get(GLSPPaletteContribution)).inSingletonScope()
    bind(CommandContribution).toDynamicValue(ctx => ctx.container.get(GLSPPaletteContribution)).inSingletonScope()
})

