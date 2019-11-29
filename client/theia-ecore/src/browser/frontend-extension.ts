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
import { GLSPClientContribution } from "@glsp/theia-integration/lib/browser";
import { CommandContribution } from "@theia/core";
import {
    FrontendApplicationContribution,
    OpenHandler,
    WebSocketConnectionProvider,
    WidgetFactory
} from "@theia/core/lib/browser";
import { ContainerModule, interfaces } from "inversify";
import { DiagramConfiguration, DiagramManager, DiagramManagerProvider } from "sprotty-theia/lib";

import { FILEGEN_SERVICE_PATH, FileGenServer } from "../common/generate-protocol";
import { EcoreDiagramConfiguration } from "./diagram/ecore-diagram-configuration";
import { EcoreDiagramManager } from "./diagram/ecore-diagram-manager.";
import { EcoreGLSPDiagramClient } from "./diagram/ecore-glsp-diagram-client";
import { EcoreGLSPClientContribution } from "./ecore-glsp--contribution";
import { EcoreCommandContribution } from "./EcoreCommandContribution";


export default new ContainerModule((bind: interfaces.Bind, unbind: interfaces.Unbind, isBound: interfaces.IsBound, rebind: interfaces.Rebind) => {
    bind(EcoreGLSPClientContribution).toSelf().inSingletonScope();
    bind(GLSPClientContribution).toService(EcoreGLSPClientContribution);
    bind(EcoreGLSPDiagramClient).toSelf().inSingletonScope();
    bind(DiagramConfiguration).to(EcoreDiagramConfiguration).inSingletonScope();
    bind(EcoreDiagramManager).toSelf().inSingletonScope();
    bind(FrontendApplicationContribution).toService(EcoreDiagramManager);
    bind(OpenHandler).toService(EcoreDiagramManager);
    bind(WidgetFactory).toService(EcoreDiagramManager);
    bind(DiagramManagerProvider).toProvider<DiagramManager>((context) => {
        return () => {
            return new Promise<DiagramManager>((resolve) => {
                const diagramManager = context.container.get<EcoreDiagramManager>(EcoreDiagramManager);
                resolve(diagramManager);
            });
        };
    });
    bind(CommandContribution).to(EcoreCommandContribution);
    bind(FileGenServer).toDynamicValue(ctx => {
        const connection = ctx.container.get(WebSocketConnectionProvider);
        return connection.createProxy<FileGenServer>(FILEGEN_SERVICE_PATH);
    }).inSingletonScope();
});
