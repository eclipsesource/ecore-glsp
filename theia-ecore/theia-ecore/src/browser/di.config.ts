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
import { DiagramConfiguration, TheiaKeyTool, SprottySelectionForwardingInitializer } from "theia-glsp/lib"
import { createEcoreDiagramContainer } from "sprotty-ecore/lib"
import { TYPES, KeyTool } from "glsp-sprotty/lib"
import { Container, injectable } from "inversify";
import { EcoreLanguage } from "../common/ecore-language";
import { GLSPTheiaDiagramServer } from 'glsp-theia-extension/lib/browser'

@injectable()
export class EcoreDiagramConfiguration implements DiagramConfiguration {
    diagramType: string = EcoreLanguage.DiagramType

    createContainer(widgetId: string): Container {
        const container = createEcoreDiagramContainer(widgetId);
        container.bind(TYPES.ModelSource).to(GLSPTheiaDiagramServer)
        container.rebind(KeyTool).to(TheiaKeyTool).inSingletonScope()
        container.bind(TYPES.IActionHandlerInitializer).to(SprottySelectionForwardingInitializer)
        return container;
    }

}