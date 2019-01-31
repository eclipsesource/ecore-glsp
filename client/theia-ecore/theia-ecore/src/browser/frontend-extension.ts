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
import { FrontendApplicationContribution, OpenHandler } from "@theia/core/lib/browser";
import { GLSPClientContribution } from "glsp-theia-extension/lib/browser";
import { ContainerModule, interfaces } from "inversify";
import { DiagramConfiguration, DiagramManager, DiagramManagerProvider } from "theia-glsp/lib";
import { EcoreLanguage } from "../common/ecore-language";
import { EcoreDiagramConfiguration } from "./di.config";
import { EcoreDiagramManager } from "./ecore-diagram-manager.";
import { EcoreGLClientContribution } from "./ecore-glclient-contribution";


export default new ContainerModule((bind: interfaces.Bind, unbind: interfaces.Unbind, isBound: interfaces.IsBound, rebind: interfaces.Rebind) => {
    
    
    bind(EcoreGLClientContribution).toSelf().inSingletonScope()
    bind(GLSPClientContribution).toDynamicValue(ctx => ctx.container.get(EcoreGLClientContribution)).inSingletonScope();
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
})

