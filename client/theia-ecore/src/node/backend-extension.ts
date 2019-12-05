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
import { ConnectionHandler, JsonRpcConnectionHandler } from "@theia/core";
import { BackendApplicationContribution } from "@theia/core/lib/node";
import { LanguageServerContribution } from "@theia/languages/lib/node";
import { ContainerModule, injectable } from "inversify";

import { FILEGEN_SERVICE_PATH, FileGenServer } from "../common/generate-protocol";
import { EcoreFileGenServer } from "./ecore-file-generation";
import { EcoreGLServerContribution } from "./ecore-glsp-server-contribution";
import {GLSPLaunchOptions, GLSPLaunchOptionsSymb, GLSPServerLauncher} from "./glsp-server-launcher";
import { join, resolve } from 'path';


@injectable()
export class EcoreGlspLaunchOptions implements GLSPLaunchOptions {
    isRunning = false;
    hostname = 'localhost';
    jarPath = resolve(join(__dirname, '..', '..', 'build', 'glsp-ecore-0.0.2-SNAPSHOT-glsp.jar'));
    serverPort = 5007;
}

export default new ContainerModule(bind => {
    bind(GLSPLaunchOptionsSymb).to(EcoreGlspLaunchOptions).inSingletonScope();
    bind(LanguageServerContribution).to(EcoreGLServerContribution).inSingletonScope();
    bind(EcoreFileGenServer).toSelf().inSingletonScope();
    bind(BackendApplicationContribution).toService(EcoreFileGenServer);
    bind(ConnectionHandler).toDynamicValue(ctx =>
        new JsonRpcConnectionHandler(FILEGEN_SERVICE_PATH, () =>
            ctx.container.get<FileGenServer>(EcoreFileGenServer))
    ).inSingletonScope();
    bind(BackendApplicationContribution).to(GLSPServerLauncher);
});
