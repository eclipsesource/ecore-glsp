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
import { injectable, inject } from "inversify";
import { TheiaFileSaver, DiagramWidgetRegistry } from "theia-glsp/lib";
import { EcoreLanguage } from "../common/ecore-language";
import { GLSPTheiaSprottyConnector, GraphicalLanguageClientContribution, GLSPDiagramManager, GLSPPaletteContribution } from "glsp-theia-extension/lib/browser";
import { EcoreGLClientContribution } from "./ecore-glclient-contribution";
import { EditorManager } from "@theia/editor/lib/browser";
import URI from "@theia/core/lib/common/uri";
import { OpenerOptions } from "@theia/core/lib/browser";



@injectable()
export class EcoreDiagramManager extends GLSPDiagramManager {
    readonly diagramType = EcoreLanguage.DiagramType;
    readonly iconClass = "fa fa-project-diagram";
    readonly label = EcoreLanguage.Label + " Editor";

    private _diagramConnector: GLSPTheiaSprottyConnector;

    constructor(
        @inject(EcoreGLClientContribution)
        readonly languageClientContribution: GraphicalLanguageClientContribution,
        @inject(TheiaFileSaver)
        readonly theiaFileSaver: TheiaFileSaver,
        @inject(EditorManager)
        readonly editorManager: EditorManager,
        @inject(DiagramWidgetRegistry)
        readonly diagramWidgetRegistry: DiagramWidgetRegistry,
        @inject(GLSPPaletteContribution) readonly paletteContribution: GLSPPaletteContribution) {
        super();

    }

    canHandle(uri: URI, options?: OpenerOptions | undefined): number {	
        if (uri.path.ext.endsWith("ecorediagram"))	
            return 1001	
        return 10	
    }

    get diagramConnector() {
        if (!this._diagramConnector) {
            this._diagramConnector = new GLSPTheiaSprottyConnector(
                this.languageClientContribution,
                this.theiaFileSaver,
                this.editorManager,
                this.diagramWidgetRegistry,
                this.paletteContribution)

        }
        return this._diagramConnector
    }
}